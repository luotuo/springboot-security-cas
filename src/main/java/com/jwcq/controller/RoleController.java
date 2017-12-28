package com.jwcq.controller;

import com.jwcq.user.entity.Role;
import com.jwcq.user.entity.RolePrivilege;
import com.jwcq.service.RoleService;

import com.jwcq.global.result.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by luotuo on 17-7-4.
 */
@Controller
@RequestMapping("/role")
@Api(description = "角色管理")
public class RoleController extends BaseController {
    @Autowired
    private RoleService rolesService;

    @ApiOperation(value = "角色分页列表", notes = "返回所有角色列表")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/getAll")
    @ResponseBody
    public Response getAll(@RequestParam(value = "page", required = false, defaultValue = "0") String page,
                           @RequestParam(value = "number", required = false, defaultValue = "20") String number,
                           HttpServletRequest request) {
        Iterable<Role> result;
        int p = 0;
        int pageSize = 20;
        try {
            p = Integer.valueOf(page);
            pageSize = Integer.valueOf(number);
        } catch (Exception e) {
            return errorResponse("用户输入page或者number有问题：" + e.getMessage(), e.toString());
        } finally {
            result = rolesService.findAll(p, pageSize);
        }
        return successResponse("处理成功", result);
    }

    @ApiOperation(value = "角色列表，不分页", notes = "返回所有角色列表")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/getAllNoPage")
    @ResponseBody
    public Response getAllNoPage(HttpServletRequest request) {
        Iterable<Role> result = rolesService.findAllNoPage();
        return successResponse("处理成功", result);
    }

    @ApiOperation(value = "添加角色", notes = "新增角色")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/add")
    @ResponseBody
    public Response add(@RequestParam(value = "code", required = true, defaultValue = "") String code,
                        @RequestParam(value = "name", required = true, defaultValue = "") String name,
                        HttpServletRequest request) {
        if (code.equals("") || name.equals("")) {
            return errorResponse("编号或名称为空", null);
        }
        Role r = rolesService.findByCode(code);
        if (r != null) {
            return errorResponse("编号已存在", r.getCode());
        }
        Role role = new Role();
        role.setCode(code);
        role.setName(name);
        role = rolesService.save(role);
        return successResponse("保存成功", role);
    }

    @ApiOperation(value = "编辑角色", notes = "编辑角色")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/edit")
    @ResponseBody
    public Response edit(@RequestParam(value = "id", required = true) String i,
                         HttpServletRequest request) {
        Long id = -1L;
        Role role = null;
        try {
            id = Long.valueOf(i);
            role = rolesService.findById(id);
        } catch (Exception e) {
            return errorResponse("选择失败：" + e.getMessage(), e.toString());
        }
        return successResponse("选择成功", role);
    }

    @ApiOperation(value = "通过角色id删除角色", notes = "删除角色")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/deleteById")
    @ResponseBody
    public Response deleteById(@RequestParam(value = "id", required = true) String i,
                               HttpServletRequest request) {
        Long id = -1L;
        try {
            id = Long.valueOf(i);
            rolesService.deleteById(id);
        } catch (Exception e) {
            return errorResponse("删除失败：" + e.getMessage(), e.toString());
        }
        return successResponse("删除成功", null);
    }

    @ApiOperation(value = "通过角色编号删除角色", notes = "删除角色")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/deleteByCode")
    @ResponseBody
    public Response deleteByCode(@RequestParam(value = "code", required = true, defaultValue = "") String code,
                                 HttpServletRequest request) {
        if (code.equals(""))
            return errorResponse("编号不能为空", null);
        rolesService.deleteByCode(code);
        return successResponse("删除成功", null);
    }

    @ApiOperation(value = "更新角色", notes = "更新角色")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/update")
    @ResponseBody
    public Response update(@RequestParam(value = "code", required = true, defaultValue = "") String code,
                           @RequestParam(value = "name", required = true, defaultValue = "") String name,
                           @RequestParam(value = "id", required = true, defaultValue = "") String i,
                           HttpServletRequest request) {
        if (code.equals("") || i.equals(""))
            return errorResponse("编号或id不能为空", null);
        Role role = new Role();
        try {
            Long id = Long.valueOf(i);
            role.setName(name);
            role.setCode(code);
            role.setId(id);
            rolesService.save(role);
        } catch (Exception e) {
            return errorResponse("id类型错误：" + e.getMessage(), null);
        }
        return successResponse("更新成功", role);
    }

//    @ApiOperation(value = "编辑角色权限", notes = "编辑角色权限")
//    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/setPrivileges")
//    @ResponseBody
//    public Response setPrivileges(@RequestParam(value = "privileges", required = true, defaultValue = "") String privileges,
//                                  @RequestParam(value = "id", required = true, defaultValue = "") String id,
//                                  HttpServletRequest request) {
//        long roleId = 0;
//        try {
//            roleId = Long.valueOf(id);
//        } catch (Exception e) {
//            return errorResponse("id有错误", null);
//        }
//        Role role = null;
//        try {
//            role = rolesService.editPrivilege(roleId, privileges);
//        } catch (Exception e) {
//            return errorResponse(e.toString(), null);
//        }
//        return successResponse("编辑成功", role);
//    }

    @ApiOperation(value = "编辑角色权限", notes = "编辑角色权限")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/setPrivileges")
    @ResponseBody
    public Response setPrivileges(@RequestParam(value = "privileges", required = true, defaultValue = "") String privileges,
                                  @RequestParam(value = "id", required = true, defaultValue = "") String id,
                                  @RequestParam(value = "add", required = true, defaultValue = "") String add,
                                  HttpServletRequest request) {
        long roleId = 0;
        try {
            roleId = Long.valueOf(id);
        } catch (Exception e) {
            return errorResponse("id有错误", null);
        }
        Role role = null;
        try {
            role = rolesService.editPrivilege(roleId, privileges, add);
        } catch (Exception e) {
            return errorResponse(e.getMessage(), null);
        }
        return successResponse("编辑成功", role);
    }

    @ApiOperation(value = "获取角色权限", notes = "获取角色的所有权限")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/getPrivileges")
    @ResponseBody
    public Response getPrivileges(@RequestParam(value = "id", required = true, defaultValue = "") String id,
                                  HttpServletRequest request) {
        long role_id = 0;
        Iterable<RolePrivilege> rolePrivileges = null;
        try {
            role_id = Long.valueOf(id);
        } catch (Exception e) {
            return errorResponse("id有错误：" + e.getMessage(), null);
        }
        try {
            rolePrivileges = rolesService.getRolePrivileges(role_id);
        } catch (Exception e) {
            return errorResponse(e.getMessage(), null);
        }
        return successResponse("编辑成功", rolePrivileges);
    }
}
