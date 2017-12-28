package com.jwcq.controller;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.collect.Maps;
import com.jwcq.config.Global;
import com.jwcq.custom.UserInfo;
import com.jwcq.entity.*;
import com.jwcq.global.result.Response;
import com.jwcq.properties.CasProperties;
import com.jwcq.service.BaseService;
import com.jwcq.global.result.Response;
import com.jwcq.service.UserRoleService;
import com.jwcq.service.UserService;
import com.jwcq.service.impl.NativeSqlServiceImpl;

import com.jwcq.user.entity.User;
import com.jwcq.user.entity.UserResponse;
import com.jwcq.user.entity.UserRole;
import com.jwcq.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.ServletRequestUtils;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by luotuo on 17-6-1.
 */
@Controller
public class BaseController {
    //默认请求的分页信息，具体的值在Global中设置。缺省值是20项每页，第0页。
    Sort sort = new Sort(Sort.Direction.DESC, "id");
    public PageRequest pageRequest = new PageRequest(Global.DEFAULT_PAGE_NUM, Global.DEFAULT_PAGE_SIZE, sort);
    //日志系统
    public final Logger mlogger = LoggerFactory.getLogger(this.getClass());

    protected static Pattern pattern = Pattern.compile("^(jpg)|(jpeg)|(png)|(bmp)$");
    protected static Map<String, String> contentType;
    protected String iconPath;

    static {
        contentType = Maps.newConcurrentMap();
        contentType.put("image/x-icon", ".ico");
        contentType.put("image/jpeg", ".jpg");
        contentType.put("image/png", ".png");
    }

    @Autowired
    public NativeSqlServiceImpl nativeSqlService;

    @Autowired
    UserService userService;

    @Autowired
    private UserRoleService userRoleService;


    public Long getUserId() {
        Object userInfo = null;
        try {
            userInfo = SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            if (userInfo instanceof Long)
                return (Long)userInfo;
        } catch (Exception e) {
            return 0L;
        }
        if ("anonymousUser".equals(userInfo.toString()))
            return 0L;
        return ((UserInfo)userInfo).getId();
    }

    //返回请求中的参数值
    public String getParam(HttpServletRequest request, String name) {
        if (name == null || name == "") return null;
        String para = ServletRequestUtils.getStringParameter(request, name, null);
        return para;
    }

    //获取正确
    public Response successResponse(String message, Object result) {
        if (message == null) message = "处理成功";
        Response response = new Response();
        response.setSuccess(Response.SUCCEED);
        response.setMessage(message);
        if (result == null)
            result = 1;
        response.setResult(result);
        return response;
    }

    //出现错误
    public Response errorResponse(String message, Object result) {
        if (message == null) message = "处理失败";
        Response response = new Response();
        response.setSuccess(Response.ERROR);
        response.setMessage(message);
        if (result == null)
            result = 0;
        response.setResult(result);
        return response;
    }

    public Response lockedResponse(String message, Object result) {
        if (message == null) message = "资源正在被占用";
        Response response = new Response();
        response.setSuccess(Response.RESOURCELOCKED);
        response.setMessage(message);
        if (result == null) result = 0;
        response.setResult(result);
        return response;
    }

    //Log 日志
    public void Logger(String message) {
    }
    //时间 操作者 ip 消息内容

    /**
     * @param page      页数
     * @param number    一页的数量
     * @param direction 排序的方向
     * @return Response 返回的数据
     * @Author liuma
     * @Function List
     * @Version 1.0
     **/
    public Response baseList(String page, String number, String direction, BaseService baseService) {
        Iterable<Object> result;
        try {
            Sort.Direction direc = Sort.Direction.DESC;//0 表示逆序，1表示顺序
            if (Integer.valueOf(direction) == 1) direc = Sort.Direction.ASC;
            Sort sort = new Sort(direc, "id");
            PageRequest pageRequest = new PageRequest(Integer.valueOf(page), Integer.valueOf(number), sort);
            result = baseService.findAll(pageRequest);
        } catch (Exception e) {
            System.out.println("用户输入page或者number有问题");
            result = baseService.findAll(pageRequest);
        }
        return successResponse("返回成功", result);

    }

    //edit 获取某个
    public Response baseEdit(String id, BaseService service) {
        if (id == null || id.equals("")) {
            return errorResponse("id 不能为空", null);
        }
        Object object;
        try {
            object = service.findById(Long.valueOf(id));
            if (object == null) return errorResponse("查询id存在问题，请输入正确id", null);
        } catch (Exception e) {
            return errorResponse("查询id存在问题，请输入正确id", null);
        }
        return successResponse("查询成功", object);

    }

    /**
     * @param id      需要删除项对应id
     * @param service 需要删除项执行的service
     * @return Response 返回的数据
     * @Author liuma
     * @Function 删除
     * @Version 1.0
     **/
    public Response baseDelete(String id, BaseService service) {
        if (id == null) {
            return errorResponse("id 不能为空", null);
        }
        Object object = null;
        try {
            object = service.findById(Long.valueOf(id));
        } catch (Exception e) {
            System.out.println("id 输入错误,删除失败");
        }
        if (object == null) errorResponse("id 输入错误,删除失败", null);
        if (service.deleteById(Long.valueOf(id))) {
            return successResponse("删除成功", null);
        } else
            return errorResponse("删除失败，请联系管理员", null);
    }

    /**
     * @param object  需要保存的项目
     * @param service 需要保存项执行的service
     * @return Response 返回的数据
     * @Author liuma
     * @Function 保存
     * @Version 1.0
     **/
    public Response baseSave(Object object, BaseService service) {
        Object newobject = service.save(object);
        if (newobject != null) {
            return successResponse("保存成功", newobject);
        } else {
            return errorResponse("保存失败", null);
        }
    }

    /**
     * @param service 需要保存项执行的service
     * @param service 需要保存项执行的service
     * @return Response 返回的数据
     * @Author liuma
     * @Function 保存
     * @Version 1.0
     **/
    public Response baseSearch(BaseService service, String page, String number, String direction, String... params) {
        for (int i = 0; i < params.length; i++) {
            System.out.println("para[" + i + "]=" + params[i]);
        }
        Iterable<Object> result;
        try {
            Sort.Direction direc = Sort.Direction.DESC;//0 表示逆序，1表示顺序
            if (Integer.valueOf(direction) == 1) direc = Sort.Direction.ASC;
            Sort sort = new Sort(direc, "id");
            PageRequest pageRequest = new PageRequest(Integer.valueOf(page), Integer.valueOf(number), sort);
            result = service.findByParams(pageRequest, params);
        } catch (Exception e) {
            System.out.println("用户输入page或者number有问题");
            result = service.findByParams(pageRequest, params);
        }
        if (result == null || (!result.iterator().hasNext())) return successResponse("查询条件为空，请重新查询", result);
        else return successResponse("查询成功", result);
    }


    /**
     * 获取当前登录的用户信息
     */
    public User getUser() {
        long userId = getUserId();
        if (userId == 0)
            return null;
        User user = userService.getUserById(userId);
        return user;
    }

    /**
     * 检测当前用户最高权限信息中是否包含Admin
     * 如果最高权限是Admin
     */
    public boolean isAdmin() {
        String roles = userRoleService.getUserRoleNamesByUserId(getUserId());
        if (StringUtils.isBlank(roles)) return false;
        //超级管理员 项目管理员 业务管理员
        else if (roles.contains(Global.SURPERADMIN) || roles.contains(Global.PROJECTMADMIN)||roles.contains(Global.BUSINESSADMIN)) return true;//全内容管理
        else return false;
    }

    /**
     * 检测当前用户是否是评审人员
     */
    public boolean isReviewer() {
        String roles = userRoleService.getUserRoleNamesByUserId(getUserId());
        if (StringUtils.isBlank(roles)) return false;
        //超级管理员 项目管理员 评审老师 项目经理 协同项目经理 评审老师
        else if (roles.contains(Global.SURPERADMIN) || roles.contains(Global.PROJECTMADMIN) || roles.contains(Global.REVIEWER)) return true;//全内容管理
        else return false;
    }

    /**
     * 检测当前用户是否是项目经理和协同项目经理
     */
    public boolean isProjectManager() {
        String roles = userRoleService.getUserRoleNamesByUserId(getUserId());
        if (StringUtils.isBlank(roles)) return false;
            //超级管理员 项目管理员 评审老师 项目经理 协同项目经理 评审老师
        else if (roles.contains(Global.SURPERADMIN) || roles.contains(Global.PROJECTMADMIN) || roles.contains(Global.PROJECTMANAGER)|| roles.contains(Global.ASSISTMANAGER)) return true;//全内容管理
        else return false;
    }




    /**
     * 检测当前用户是否是商务经理
     * **/
    public boolean isBusiness() {
        String roles = userRoleService.getUserRoleNamesByUserId(getUserId());
        if (StringUtils.isBlank(roles)) return false;
        //超级管理员 项目管理员 商务经理
        else if (roles.contains(Global.SURPERADMIN) || roles.contains(Global.PROJECTMADMIN) ||roles.contains(Global.BUSINESSMANAGER)) return true;//全内容管理
        else return false;
    }

    /**
     * 检测当前用户是否是调度员老师
     */
    public boolean isScheduler() {
        String roles = userRoleService.getUserRoleNamesByUserId(getUserId());
        if (StringUtils.isBlank(roles)) return false;
        //超级管理员 项目管理员 调度员
        else if (roles.contains(Global.SURPERADMIN) || roles.contains(Global.PROJECTMADMIN) || roles.contains(Global.SCHEDULER)) return true;//全内容管理
        else return false;
    }

    /**
     * 根据输入的值进行排序分页
     *
     * @param page      第几页从0开始
     * @param number    一页几条
     * @param direction 排序方向，0 表示逆序，1表示顺序
     * @param key       排序关键字
     */
    public PageRequest getPageRequest(String page, String number, String direction, String key) {
        PageRequest mpageRequest;
        try {
            Sort.Direction direc = Sort.Direction.DESC;//0 表示逆序，1表示顺序
            if (Integer.valueOf(direction) == 1) direc = Sort.Direction.ASC;
            Sort sort = new Sort(direc, key);
            mpageRequest = new PageRequest(Integer.valueOf(page), Integer.valueOf(number), sort);

        } catch (Exception e) {
            System.out.println("用户输入page或者number有问题");
            return pageRequest;
        }
        return mpageRequest;
    }

    /**
     * 根据输入的值进行排序分页,默认按照id排序
     *
     * @param page      第几页从0开始
     * @param number    一页几条
     * @param direction 排序方向，0 表示逆序，1表示顺序     *
     */
    public PageRequest getPageRequest(String page, String number, String direction) {
        return getPageRequest(page, number, direction, "id");
    }
}





