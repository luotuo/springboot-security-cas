package com.jwcq.controller;

import com.google.common.io.Files;
import com.jwcq.MyExceptions.NullException;
import com.jwcq.config.Global;
import com.jwcq.entity.AllUsers;
import com.jwcq.service.PrivilegeConfigService;
import com.jwcq.service.UserPrivilegeService;
import com.jwcq.service.UserRoleService;
import com.jwcq.user.entity.*;
import com.jwcq.global.result.Response;
import com.jwcq.service.UserService;
import com.jwcq.MyExceptions.AlreadyExistException;

import com.jwcq.utils.SystemConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;

/**
 * Created by luotuo on 17-7-4.
 */
@Controller
@RequestMapping("/user")
@Api(description = "用户管理")
public class UserController extends BaseController {
    @Autowired
    private UserService userService;

    /**
     * 用户当前信息
     * 包括用户姓名，头像
     * */
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/currentUserInfo")
    @ResponseBody
    public Response getAll(HttpServletRequest request) {
        User user = getUser();
        if (user == null)
            return errorResponse("获取用户信息失败", "获取用户信息失败");
//        user.setPassword("");
        UserResponse userResponse = null;
        try {
            userResponse = userService.getUserInfo(user);
        } catch (Exception e) {
            return errorResponse("获取登录用户信息失败：" + e.getMessage(), e.toString());
        }
        return successResponse("登录用户信息", userResponse);
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/allUsers")
    @ResponseBody
    public Response getAllUsers(HttpServletRequest request) {
        List<AllUsers> allUsers = null;
        try {
            allUsers = userService.getAllUsers();
        } catch (Exception e) {
            return errorResponse("获取所有用户失败：" + e.getMessage(), e.toString());
        }
        return successResponse("获取所有用户成功", allUsers);
    }

    @ApiOperation(value = "用户分页列表", notes = "返回所有用户列表")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/getAll")
    @ResponseBody
    public Response getAll(@RequestParam(value = "page", required = false, defaultValue = "0") String page,
                           @RequestParam(value = "number", required = false, defaultValue = "20") String number,
                           HttpServletRequest request) {
        Iterable<User> result;
        int p = 0;
        int pageSize = 20;
        try {
            p = Integer.valueOf(page);
            pageSize = Integer.valueOf(number);
        } catch (Exception e) {
            return errorResponse("用户输入page或者number有问题：" + e.getMessage(), e.toString());
        } finally {
            result = userService.findAll(p, pageSize);
        }
        return successResponse("处理成功", result);
    }

    @ApiOperation(value = "通过部门获取用户", notes = "通过部门获取用户")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/getUsersByDepartment")
    @ResponseBody
    public Response getUsersByDepartment(@RequestParam(value = "department", required = false, defaultValue = "稽查部") String department,
                                         HttpServletRequest request) {
        List<User> result = null;
        try {
            result = userService.getUserByDepartment(department);
        } catch (Exception e) {
            return errorResponse(e.getMessage(), null);
        }
        return successResponse("处理成功", result);
    }

    @ApiOperation(value = "获取用户头像", notes = "返回指定用户头像-暂未实现")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/getUserIcon")
    @ResponseBody
    public Response getUserIcon(@RequestParam(value = "id", required = true, defaultValue = "0") String id,
                                HttpServletRequest request) {

        return successResponse("处理成功", null);
    }

    @ApiOperation(value = "上传用户头像", notes = "上传用户头像")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/uploadUserIcon")
    @ResponseBody
    public Response uploadUserIcon(@RequestParam("file") MultipartFile file,
                                   HttpServletRequest request) {
        if (file.isEmpty() || file == null) {
            return errorResponse("文件不能为空",null);
        }
        // 获取文件名
        String fileName = file.getOriginalFilename();
        System.out.println("上传的文件名为：" + fileName);
        String extension = Files.getFileExtension(fileName);
        Matcher matcher = pattern.matcher(extension.toLowerCase());
        boolean match = matcher.matches();
        if (!match && !contentType.containsKey(file.getContentType())) {
            return errorResponse("图片后缀或者类型不合法", null);
        }

        try {
            if (file.getBytes().length / 1024 / 1024 > 10)
                return errorResponse("图片文件大于10兆,请缩小图片大小", null);
            System.out.println(file.getBytes().length / 1024);
        } catch (Exception e) {
            return errorResponse("头像上产失败：" + e.getMessage(), e.toString());
        }

        // 获取文件的后缀名
        //String suffixName = fileName.substring(fileName.lastIndexOf("."));

        // 解决中文问题，liunx下中文路径，图片显示问题
        String res = "";
        try {
            res = userService.uploadUserIcon(file);
        } catch (Exception e) {
            return errorResponse(e.getMessage(), null);
        }
        return successResponse("头像上产成功", res);
    }

    @ApiOperation(value = "更新用户头像", notes = "更新用户头像")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/updateUserIcon")
    @ResponseBody
    public Response updateUserIcon(@RequestParam("id") String i,
                                   @RequestParam("file") MultipartFile file,
                                   HttpServletRequest request) {

        long id = -1;
        try {
            id = Long.valueOf(i);
            if (id == 1)
                return errorResponse("超级管理员头像暂时不允许修改", null);
        } catch (Exception e) {
            return errorResponse("更新用户头像失败", e.toString());
        }
        if (file.isEmpty() || file == null) {
            return errorResponse("文件不能为空", null);
        }
        // 获取文件名
        String fileName = file.getOriginalFilename();
        System.out.println("上传的文件名为：" + fileName);
        String extension = Files.getFileExtension(fileName);
        Matcher matcher = pattern.matcher(extension.toLowerCase());
        boolean match = matcher.matches();
        if (!match && !contentType.containsKey(file.getContentType())) {
            return errorResponse("图片后缀或者类型不合法", null);
        }
        try {
            if (file.getBytes().length / 1024 / 1024 > 10)
                return errorResponse("图片文件大于10兆,请缩小图片大小", null);
            System.out.println(file.getBytes().length / 1024);
        } catch (Exception e) {
            return errorResponse("更新用户头像失败：" + e.getMessage(), e.toString());
        }
        String res = "";
        try {
            res = userService.updateUserIcon(file, id);
        } catch (Exception e) {
            return errorResponse(e.getMessage(), null);
        }
        return successResponse("头像上产成功", res);
    }

    @ApiOperation(value = "添加用户", notes = "添加一个用户")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/add")
    @ResponseBody
    public Response add(HttpServletRequest request) {
        UserResponse user = null;
        try {
            user = userService.save(request);
        } catch (AlreadyExistException ue) {
            return errorResponse("用户已经存在：" + ue.getMessage(), ue.toString());
        } catch (NullException e) {
            return errorResponse("保存失败：" + e.getMessage(), e.toString());
        }

        return successResponse("保存成功", user);
    }

    @ApiOperation(value = "编辑用户", notes = "编辑一个用户")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/edit")
    @ResponseBody
    public Response edit(@RequestParam(value = "id", required = true) String i,
                         HttpServletRequest request) {
        Long id = -1L;
        UserResponse user = null;
        try {
            id = Long.valueOf(i);
            if (id == 1)
                return errorResponse("超级管理员不允许编辑", null);
            user = userService.findUserById(id);
        } catch (Exception e) {
            return errorResponse("选择失败：" + e.getMessage(), e.toString());
        }
        return successResponse("选择成功", user);
    }

    @ApiOperation(value = "通过用户id删除用户", notes = "删除一个用户")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/deleteById")
    @ResponseBody
    public Response deleteById(@RequestParam(value = "id", required = true) String i,
                               HttpServletRequest request) {
        Long id = -1L;
        try {
            id = Long.valueOf(i);
            if (id == 1)
                return errorResponse("超级管理员不允许删除", null);
            userService.deleteUserById(id);
        } catch (Exception e) {
            return errorResponse("删除失败：" + e.getMessage(), e.toString());
        }
        return successResponse("删除成功", null);
    }

    @ApiOperation(value = "通过用户手机号删除用户", notes = "删除一个用户")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/deleteByPhone")
    @ResponseBody
    public Response deleteByPhone(@RequestParam(value = "phone", required = true, defaultValue = "") String phone,
                                 HttpServletRequest request) {
        if (phone.equals(""))
            return errorResponse("号码不能为空", null);
        User user = userService.findUserByPhone(phone);
        if (user == null)
            return errorResponse("用户不存在", null);
        if (user.getId() == 1)
            return errorResponse("超级管理员不能删除", null);
        userService.deleteUserByPhone(phone);
        return successResponse("删除成功", null);
    }

    @ApiOperation(value = "更新用户", notes = "更新一个用户")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/update")
    @ResponseBody
    public Response update(@RequestParam(value = "id", required = true) String i,
                           HttpServletRequest request) {
        UserResponse user = null;
        long id = -1;
        try {
            id = Long.valueOf(i);
        } catch (Exception e) {
            return errorResponse("id有问题：" + e.getMessage(), e.toString());
        }
        try {
            user = userService.update(request, id);
        } catch (NullException e) {
            return errorResponse("保存失败：" + e.getMessage(), e.toString());
        }

        return successResponse("保存成功", user);
    }

    @ApiOperation(value = "更新用户状态", notes = "更新一个用户的状态")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/changeState")
    @ResponseBody
    public Response changeState(@RequestParam(value = "id", required = true, defaultValue = "0") String i,
                                @RequestParam(value = "state", required = true, defaultValue = "1") String s,
                                HttpServletRequest request) {
        User user = null;
        long id = 0;
        //int state = 1;
        try {
            id = Long.valueOf(i);
            //state = Integer.valueOf(s);
        } catch (Exception e) {
            System.out.println("id有问题");
            id = 0;
        }
        if (id == 0)
            return errorResponse("id有问题", null);
        if (id == 1)
            return errorResponse("超级管理员不允许修改", null);
        try {
            user = userService.changeState(s, id);
        } catch (NullException e) {
            return errorResponse("处理失败：" + e.getMessage(), e.toString());
        }
        return successResponse("处理成功", user);
    }

//    @ApiOperation(value = "设置用户权限", notes = "设置用户权限")
//    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/setPrivileges")
//    @ResponseBody
//    public Response setPrivileges(@RequestParam(value = "privileges", required = true, defaultValue = "") String privileges,
//                                 @RequestParam(value = "id", required = true, defaultValue = "") String id,
//                                 HttpServletRequest request) {
//        long userId = 0;
//        User user = null;
//        try {
//            userId = Long.valueOf(id);
//        } catch (Exception e) {
//            return errorResponse("id有错误", null);
//        }
//        if (userId == 0)
//            return errorResponse("id有问题", null);
//        if (userId == 1)
//            return errorResponse("超级管理员不允许修改", null);
//
//        try {
//            user = userService.editPrivilege(userId, privileges);
//        } catch (Exception e) {
//            return errorResponse(e.toString(), null);
//        }
//        return successResponse("编辑成功", user);
//    }

    @ApiOperation(value = "设置用户权限", notes = "设置用户权限")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/setPrivileges")
    @ResponseBody
    public Response setPrivileges(@RequestParam(value = "privileges", required = true, defaultValue = "") String privileges,
                                  @RequestParam(value = "id", required = true, defaultValue = "") String id,
                                  @RequestParam(value = "add", required = true, defaultValue = "") String add,
                                  HttpServletRequest request) {
        long userId = 0;
        User user = null;
        try {
            userId = Long.valueOf(id);
        } catch (Exception e) {
            return errorResponse("id有错误", null);
        }
        if (userId == 0)
            return errorResponse("id有问题", null);
        if (userId == 1)
            return errorResponse("超级管理员不允许修改", null);

        try {
            user = userService.editPrivilege(userId, privileges, add);
        } catch (Exception e) {
            return errorResponse("编辑失败：" + e.getMessage(), e.toString());
        }
        return successResponse("编辑成功", user);
    }


    @ApiOperation(value = "获取用户权限", notes = "获取用户的所有权限")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/getPrivileges")
    @ResponseBody
    public Response getPrivileges(@RequestParam(value = "id", required = true, defaultValue = "") String id,
                                 HttpServletRequest request) {
        long userId = 0;
        Iterable<UserPrivilege> userPrivileges = null;
        try {
            userId = Long.valueOf(id);
        } catch (Exception e) {
            return errorResponse("id有错误", null);
        }
        try {
            userPrivileges = userService.getUserPrivileges(userId);
        } catch (Exception e) {
            return errorResponse("获取失败：" + e.getMessage(), e.toString());
        }
        return successResponse("获取成功", userPrivileges);
    }


    @ApiOperation(value = "设置用户角色", notes = "设置用户角色")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/setRoles")
    @ResponseBody
    public Response setRoles(@RequestParam(value = "roles", required = true, defaultValue = "") String roles,
                                 @RequestParam(value = "id", required = true, defaultValue = "") String id,
                                 HttpServletRequest request) {
        long userId = 0;
        User user = null;
        try {
            userId = Long.valueOf(id);
        } catch (Exception e) {
            return errorResponse("id有错误", null);
        }
        if (userId == 0)
            return errorResponse("id有问题", null);
        if (userId == 1)
            return errorResponse("超级管理员不允许设置", null);
        try {
            user = userService.editRoles(userId, roles);
        } catch (Exception e) {
            return errorResponse("编辑失败：" + e.getMessage(), e.toString());
        }
        return successResponse("编辑成功", user);
    }


    @ApiOperation(value = "获取用户角色", notes = "获取用户的所有角色")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/getRoles")
    @ResponseBody
    public Response getRoles(@RequestParam(value = "id", required = true, defaultValue = "") String id,
                                 HttpServletRequest request) {
        long userId = 0;
        Iterable<UserRole> userRoles = null;
        try {
            userId = Long.valueOf(id);
        } catch (Exception e) {
            return errorResponse("id有错误", null);
        }
        try {
            userRoles = userService.getUserRoles(userId);
        } catch (Exception e) {
            return errorResponse("获取失败：" + e.getMessage(), e.toString());
        }
        return successResponse("获取成功", userRoles);
    }


    @ApiOperation(value = "查询用户分页列表", notes = "返回查询用户列表")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/search")
    @ResponseBody
    public Response search(@RequestParam(value = "page", required = false, defaultValue = "0") String page,
                           @RequestParam(value = "number", required = false, defaultValue = "20") String number,
                           @RequestParam(value = "name", required = false, defaultValue = "") String name,
                           @RequestParam(value = "phone", required = false, defaultValue = "") String phone,
                           @RequestParam(value = "department", required = false, defaultValue = "") String department,
                           @RequestParam(value = "role", required = false, defaultValue = "") String role,
                           @RequestParam(value = "state", required = false, defaultValue = "") String state,
                           HttpServletRequest request) {
        PageResponse result;
        int p = 0;
        int pageSize = 20;
        try {
            p = Integer.valueOf(page);
            pageSize = Integer.valueOf(number);
        } catch (Exception e) {
            return errorResponse("用户输入page或者number有问题", e.toString());
        }
        try {
            result = userService.search(p, pageSize, name, phone, department, role, state);
        } catch (Exception e) {
            return errorResponse("处理失败：" + e.getMessage(), e.toString());
        }
        return successResponse("处理成功", result);
    }


    @ApiOperation(value = "通过用户角色获取用户", notes = "通过用户角色获取用户")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/getUserByRoleName")
    @ResponseBody
    public Response getUserByRoleName(@RequestParam(value = "roleName", required = false, defaultValue = "") String roleName,
                           HttpServletRequest request) {
        List<User> result = null;
        try {
            result = userService.getUserByRoleName(roleName);
        } catch (Exception e) {
            return errorResponse("处理失败：" + e.getMessage(), e.toString());
        }
        return successResponse("处理成功", result);
    }

    @ApiOperation(value = "修改用户密码", notes = "修改用户密码")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/setPassword")
    @ResponseBody
    public Response setPassword(@RequestParam(value = "oldPassword") String oldPassword,
                                @RequestParam(value = "newPassword") String newPassword,
                                HttpServletRequest request) {
        User user = getUser();
        try {
            if (oldPassword.equals(newPassword))
                return errorResponse("新旧密码必须不同", null);
            userService.setPassword(oldPassword, newPassword, user);
        } catch (Exception e) {
            return errorResponse("设置失败：" + e.getMessage(), e.toString());
        }
        return successResponse("设置成功", null);
    }

    @ApiOperation(value = "重置用户密码", notes = "重置用户密码")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/resetPassword")
    @ResponseBody
    public Response setPassword(@RequestParam(value = "userId") String userId,
                                HttpServletRequest request) {
        try {
            userService.resetPassword(Long.valueOf(userId));
        } catch (Exception e) {
            return errorResponse("重置失败：" + e.getMessage(), e.toString());
        }
        return successResponse("重置成功", null);
    }

    @ApiOperation(value = "绑定微信接口", notes = "绑定微信接口")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/bindWechat")
    @ResponseBody
    public String bindWechat(HttpServletRequest request) {
        UserResponse userResponse = null;
        try {
            User user = userService.bindWechat(request);
            userResponse = userService.getUserInfo(user);
            userResponse.setPassword("");
        } catch (Exception e) {
            return e.getMessage();
        }
        String userName = request.getParameter("state");
        String param = SystemConfig.getProperty("callback.path.project");
        String responseStr = "<html><head><meta http-equiv=\"refresh\" content=\"1;url=%s\"></head><body></body></html>";
        responseStr = String.format(responseStr, param);
        return responseStr;
    }

    @ApiOperation(value = "用户注册接口", notes = "用户注册接口")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/join")
    @ResponseBody
    public Response joinOpen(@RequestParam(value = "name") String name,
                             @RequestParam(value = "phone") String phone,
                             @RequestParam(value = "vCode") String vCode,
                             @RequestParam(value = "password") String password,
                             HttpServletRequest request) {
        UserResponse userResponse = null;
        try {
            User user = userService.join(name, phone, vCode, password, request);
            userResponse = userService.getUserInfo(user);
        } catch (Exception e) {
            return errorResponse("注册失败：" + e.getMessage(), e.toString());
        }
        return successResponse("注册成功", userResponse);
    }

    @ApiOperation(value = "微信扫描后用户注册接口", notes = "微信扫描后用户注册接口")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/wechatJoin")
    @ResponseBody
    public Response wechatJoin(@RequestParam(value = "name") String name,
                               @RequestParam(value = "phone") String phone,
                               @RequestParam(value = "vCode") String vCode,
                               @RequestParam(value = "password") String password,
                               HttpServletRequest request) {
        UserResponse userResponse = null;
        try {
            User user = userService.wechatJoin(name, phone, vCode, password, getUser());
            userResponse = userService.getUserInfo(user);
        } catch (Exception e) {
            return errorResponse("注册失败：" + e.getMessage(), e.toString());
        }
        return successResponse("注册成功", userResponse);
    }

    @ApiOperation(value = "用户解绑微信", notes = "用户解绑微信")
    @RequestMapping(method = {RequestMethod.POST}, value = "/unbind")
    @ResponseBody
    public Response unbind(HttpServletRequest request) {
        try {
            userService.unbindWechat(getUser());
        } catch (Exception e) {
            return errorResponse("解绑失败：" + e.getMessage(), e.toString());
        }
        return successResponse("解绑成功", "");
    }

    @ApiOperation(value = "管理员解绑用户微信", notes = "管理员解绑用户微信")
    @RequestMapping(method = {RequestMethod.POST}, value = "/unbindByAdmin")
    @ResponseBody
    public Response unbind(@RequestParam(value = "id") String id,
                           HttpServletRequest request) {
        try {
            userService.unbindWechatByAdmin(Long.valueOf(id));
        } catch (Exception e) {
            return errorResponse("解绑失败：" + e.getMessage(), e.toString());
        }
        return successResponse("解绑成功", "");
    }
}
