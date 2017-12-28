package com.jwcq.service;


import com.jwcq.user.entity.User;

import java.util.List;

/**
 * Created by luotuo on 17-6-2.
 */
public interface ResourceLockService {

    //新增或者更新锁
    public boolean addLock(Object obj, User user, String taskCode) throws Exception;

    //释放资源锁
    public boolean releaseLock(Object obj, User user) throws Exception;

    //释放某个用户的所有资源锁
    public void releaseAllLocksByUser(User user);

    //释放某个Session所有资源锁
    public void releaseAllLocksBySessionId(String sessionId);

    //获得锁定某个资源的用户Id
    public Long getResourceLocker(String resourceId);

    //管理员解锁
    public boolean releaseLockByResourceId(String resourceId);

    //获取所有锁
    public List getLocks();

    //获取当前用户的所有锁
    public List getLocks(User user);
}
