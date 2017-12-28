package com.jwcq.controller;

import com.jwcq.entity.PlatformAndMenu;
import com.jwcq.service.PrivilegeConfigService;
import com.jwcq.user.entity.PrivilegeConfig;
import com.jwcq.global.result.Response;
import com.jwcq.service.URIResourceService;

import com.jwcq.user.entity.PrivilegeConfig1;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by luotuo on 17-7-3.
 */
@Controller
@RequestMapping("/privilege")
@Api(description = "权限管理")
public class PrivilegeConfigController extends BaseController {
    @Autowired
    private PrivilegeConfigService privilegeConfigService;
    @Autowired
    private URIResourceService uriResourceService;

    @ApiOperation(value = "权限分页列表", notes = "返回所有权限列表")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/getAll1")
    @ResponseBody
    public Response getAll(@RequestParam(value = "page", required = false, defaultValue = "0") String page,
                           @RequestParam(value = "number", required = false, defaultValue = "20") String number,
                           HttpServletRequest request) {

        Iterable<PrivilegeConfig> result;
        int p = 0;
        int pageSize = 20;
        try {
            p = Integer.valueOf(page);
            pageSize = Integer.valueOf(number);
        } catch (Exception e) {
            return errorResponse("用户输入page或者number有问题：" + e.getMessage(), e.toString());
        } finally {
            result = privilegeConfigService.findAll(p, pageSize);
        }
        return successResponse("处理成功", result);
    }

    @ApiOperation(value = "权限列表排版后结果", notes = "权限列表排版后结果")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/getAll")
    @ResponseBody
    public Response getAll(HttpServletRequest request) {
        Iterable<PrivilegeConfig> result = privilegeConfigService.findAllTree();
        return successResponse("处理成功", result);
    }

    @ApiOperation(value = "获取所有url列表", notes = "返回所有url列表")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/getUrls")
    @ResponseBody
    public Response getUrls(HttpServletRequest request) {
        Iterable<String> result = uriResourceService.getAllForWeb();
        return successResponse("处理成功", result);
    }

    @ApiOperation(value = "新增权限", notes = "新增权限")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/add")
    @ResponseBody
    public Response add(HttpServletRequest request) {
        PrivilegeConfig privilege = null;
        try {
            privilege = privilegeConfigService.save(request);
        } catch (Exception e) {
            return errorResponse("添加失败：" + e.getMessage(), e.toString());
        }
        return successResponse("添加成功", privilege);
    }

    @ApiOperation(value = "编辑权限", notes = "编辑权限")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/edit")
    @ResponseBody
    public Response edit(@RequestParam(value = "id", required = true) String i,
                         HttpServletRequest request) {
        Long id = -1L;
        PrivilegeConfig privilegeConfig = null;
        try {
            id = Long.valueOf(i);
            privilegeConfig = privilegeConfigService.findById(id);
        } catch (Exception e) {
            return errorResponse("选择失败：" + e.getMessage(), e.toString());
        }
        return successResponse("选择成功", privilegeConfig);
    }

    @ApiOperation(value = "通过id删除权限", notes = "通过id删除权限")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/delete")
    @ResponseBody
    public Response delete(@RequestParam(value = "id", required = true) String i,
                           HttpServletRequest request) {
        Long id = -1L;
        try {
            id = Long.valueOf(i);
            privilegeConfigService.deleteById(id);
        } catch (Exception e) {
            return errorResponse("删除失败：" + e.getMessage(), e.toString());
        }
        return successResponse("删除成功", null);
    }

    @ApiOperation(value = "更新权限", notes = "更新权限")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/update")
    @ResponseBody
    public Response update(@RequestParam(value = "id", required = true) String i,
                           HttpServletRequest request) {
        PrivilegeConfig privilegeConfig = null;
        long id = -1;
        try {
            id = Long.valueOf(i);
            privilegeConfig = privilegeConfigService.update(request, id);
        } catch (Exception e) {
            return errorResponse("数据类型有误：" + e.getMessage(), e.toString());
        }
        if (privilegeConfig != null)
            return successResponse("更新成功！", privilegeConfig);
        else
            return errorResponse("更新失败！", null);
    }

    @ApiOperation(value = "获取所有平台和菜单", notes = "获取所有平台和菜单")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/getPlatformsAndMenus")
    @ResponseBody
    public Response getPlatformsAndMenus(HttpServletRequest request) {
        List<PlatformAndMenu> res = null;
        try {
            res = privilegeConfigService.findPlatformsAndMenus();
        } catch (Exception e) {
            return errorResponse("数据类型有误：" + e.getMessage(), e.toString());
        }
        if (res != null)
            return successResponse("获取成功！", res);
        else
            return errorResponse("获取失败！", null);
    }

    @ApiOperation(value = "获取pid下某个用户的所有权限列表", notes = "获取pid下某个用户的所有权限列表")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/getPrivilegesByPidAndUserId")
    @ResponseBody
    public Response getPrivilegesByPidAndUserId(@RequestParam(value = "pid", required = true) String p,
                                       @RequestParam(value = "userId", required = true) String i,
                                        HttpServletRequest request) {
        List<PrivilegeConfig1> privilegeConfigs = null;
        long pid = -1;
        long userId = -1;
        try {
            pid = Long.valueOf(p);
            userId = Long.valueOf(i);
            if (userId == 1)
                return errorResponse("无法获取超级管理员权限", null);
            privilegeConfigs = privilegeConfigService.getByPidAndUserId(pid, userId);
        } catch (Exception e) {
            return errorResponse("数据类型有误：" + e.getMessage(), e.toString());
        }
        if (privilegeConfigs != null)
            return successResponse("获取成功！", privilegeConfigs);
        else
            return errorResponse("获取失败！", null);
    }

    @ApiOperation(value = "获取pid下某个用户的所有权限列表", notes = "获取pid下某个用户的所有权限列表")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/getPrivilegesByPidAndRoleId")
    @ResponseBody
    public Response getPrivilegesByPidAndRoleId(@RequestParam(value = "pid", required = true) String p,
                                                @RequestParam(value = "roleId", required = true) String i,
                                                HttpServletRequest request) {
        List<PrivilegeConfig1> privilegeConfigs = null;
        long pid = -1;
        long roleId = -1;
        try {
            pid = Long.valueOf(p);
            roleId = Long.valueOf(i);
//            if (roleId == 1)
//                return errorResponse("无法获取超级管理员权限", null);
            privilegeConfigs = privilegeConfigService.getByPidAndRoleId(pid, roleId);
        } catch (Exception e) {
            return errorResponse("数据类型有误：" + e.getMessage(), e.toString());
        }
        if (privilegeConfigs != null)
            return successResponse("获取成功！", privilegeConfigs);
        else
            return errorResponse("获取失败！", null);
    }
}
