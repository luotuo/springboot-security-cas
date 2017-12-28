package com.jwcq.entity;

import com.jwcq.global.EncryptionAlgs;
import com.jwcq.user.entity.User;
import org.apache.log4j.Logger;
import org.omg.PortableInterceptor.DISCARDING;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * Created by luotuo on 17-6-2.
 */
public class ResourceLock {
    public static final String RESOURCE_TYPE_TASK_MODULE = "任务模块";
    public static final String RESOURCE_TYPE_DISCOVERY = "稽查发现";
    public static final String RESOURCE_TYPE_CENTER_REPORT = "单中心报告";

    private static final int LOCKTIME = 1800;//锁定时间为1800s，30分钟

    public static final org.slf4j.Logger logger = LoggerFactory.getLogger(ResourceLock.class);

    static Map<String, Resource> resourceLock = new HashMap<>();
    private static ReadWriteLock lock = new ReentrantReadWriteLock();

    //获取所有资源锁
    public static Map<String, Resource> getResourceLock() {
        lock.readLock().lock();
        Map<String, Resource> locks = resourceLock;
        lock.readLock().unlock();
        return locks;
    }

    //添加资源锁
    /**
     * 1. 尝试添加锁
     * 2. 如果添加成功，则返回true；如果添加失败，则返回false
     * 3. 如果同一个用户之前添加过某个锁，再添加的时候添加成功，更新时间，并返回true
     * resourceCode = T.class + obj.id
     * resourceName = RESOURCE_TYPE_TASK_MODULE 或者 RESOURCE_TYPE_DISCOVERY 或者 RESOURCE_TYPE_CENTER_REPORT
     */
    private static boolean addLock(String resourceCode,
                                  String resourceName,
                                  User user,
                                  String sessionId,
                                  String taskCode, String name) throws Exception {
        boolean res = true;
        String resourceId = EncryptionAlgs.getMD5(resourceCode + resourceName);
        //lock.writeLock().lock();
        Resource resource = resourceLock.get(resourceId);
        if (resource == null) {
            resource = new Resource(resourceCode, resourceName, user, sessionId, taskCode, name);
            resourceLock.put(resourceId, resource);//新增资源
        } else {
            if (resource.getUserId().equals(user.getId())) {
                resource.updateTimestamp();//更新时间
                res = true;
            } else {
                res = false;//其他用户访问
                //lock.writeLock().unlock();
                throw new Exception(String.format("资源被[%s]占用", resource.getUserName()));
            }
        }
        //lock.writeLock().unlock();
        return res;
    }

    //释放资源锁,释放某个用户的锁资源
    private static boolean releaseLockByUser(String resourceId, User user) throws Exception {
        //lock.writeLock().lock();
        Resource resource = resourceLock.get(resourceId);
        if (resource == null)
            return true;
        if (!resource.getUserId().equals(user.getId()))
            throw new Exception("此资源不属于你");
        resourceLock.remove(resourceId);
        //lock.writeLock().unlock();
        return true;
    }

    //释放某个用户的所有资源锁
    public static void releaseAllLocksByUser(User user) {
        //lock.writeLock().lock();
        for (Iterator iterator = resourceLock.keySet().iterator(); iterator.hasNext(); ) {
            String resourceId = (String) iterator.next();
            Resource resource = resourceLock.get(resourceId);
            if (resource.getUserId() == user.getId())
                resourceLock.remove(resourceId);
        }
        //lock.writeLock().unlock();
    }

    //释放某个用户的所有资源锁
    public static void releaseAllLocksByUserId(Long userId) {
        //lock.writeLock().lock();
        for (Iterator iterator = resourceLock.keySet().iterator(); iterator.hasNext(); ) {
            String resourceId = (String) iterator.next();
            Resource resource = resourceLock.get(resourceId);
            if (resource.getUserId() == userId)
                resourceLock.remove(resourceId);
        }
        //lock.writeLock().unlock();
    }

    //释放某个Session所有资源锁
    public static void releaseAllLocksBySessionId(String sessionId) {
        //lock.writeLock().lock();
        for (Iterator iterator = resourceLock.keySet().iterator(); iterator.hasNext(); ) {
            String resourceId = (String) iterator.next();
            Resource resource = resourceLock.get(resourceId);
            if (resource.getSessionId().equals(sessionId))
                resourceLock.remove(resourceId);
        }
        //lock.writeLock().unlock();
    }

    //获得锁定某个资源的用户Id
    //锁定资源之前进行检查，判断过期时间。如果过期，则清空该锁，返回null
    public static Long getResourceUserByResourceId(String resourceId) {
        Long userId = 0L;
        //lock.readLock().lock();
        Resource resource = resourceLock.get(resourceId);
        if (resource != null) {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            Long timeDiff = (now.getTime() - resource.getLastUpdateTime().getTime()) / 1000; // Unit: second
            if (timeDiff < LOCKTIME)
                userId = resource.getUserId();
            else {
                releaseLockByResourceId(resourceId);
                return null;
            }
        }
        //lock.readLock().unlock();
        return userId;
    }


    //管理员解锁
    public static boolean releaseLockByResourceId(String resourceId) {
        //lock.writeLock().lock();
        Resource resource = resourceLock.get(resourceId);
        if (resource != null) {
            resourceLock.remove(resourceId);
        }
        //lock.writeLock().unlock();
        return true;
    }

    //更新用户锁，过期锁进行清空
    public static void checkForAlive() {
        System.out.println("checkForAlive");

        Timestamp now;
        long diff = 0;
        //lock.writeLock().lock();
        logger.info("**********************");
        List<String> ids = new ArrayList<>();
        for (Iterator iterator = resourceLock.keySet().iterator(); iterator.hasNext(); ) {
            String id = (String) iterator.next();
            Resource r = resourceLock.get(id);
            now = new Timestamp(System.currentTimeMillis());
            diff = (now.getTime() - r.getLastUpdateTime().getTime()) / 1000; // Unit: second
            if (diff > LOCKTIME) {
                //resourceLock.remove(id);
                logger.info("Add resource id == " + id + " diff == " + diff);
                ids.add(id);
            }
        }
        for (String i : ids) {
            logger.info("Release resource id == " + i);
            resourceLock.remove(i);
        }
        //lock.writeLock().unlock();
        //System.out.println("unlock");

    }
}
