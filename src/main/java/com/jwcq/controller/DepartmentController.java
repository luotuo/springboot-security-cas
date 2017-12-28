package com.jwcq.controller;

import com.jwcq.service.DepartmentService;
import com.jwcq.user.entity.Department;

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
import java.util.List;

/**
 * Created by luotuo on 17-7-4.
 */
@Controller
@RequestMapping("/department")
@Api(description = "部门管理")
public class DepartmentController extends BaseController {
    @Autowired
    private DepartmentService departmentService;

    @ApiOperation(value = "返回排版后部门结构", notes = "返回排版后部门结构")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/getAll")
    @ResponseBody
    public Response getAll(HttpServletRequest request) {
        List result = departmentService.findAllTree();
        return successResponse("处理成功", result);
    }

    @ApiOperation(value = "返回所有部门列表", notes = "返回部门列表")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/getAllList")
    @ResponseBody
    public Response getAllList(HttpServletRequest request) {
        List result = departmentService.findAllList();
        return successResponse("处理成功", result);
    }

    @ApiOperation(value = "添加部门", notes = "添加部门")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/add")
    @ResponseBody
    public Response add(@RequestParam(value = "pid", required = true) String pid,
                        @RequestParam(value = "name", required = true) String name,
                        @RequestParam(value = "level", required = true) String l,
                        HttpServletRequest request) {
        int pId = -1;
        int level = -1;
        try {
            pId = Integer.valueOf(pid);
            level = Integer.valueOf(l);
        } catch (Exception e) {
            System.out.println(e.toString());
            return errorResponse("数据类型有误！", e.toString());
        }
        Department res = departmentService.save(pId, name, level);
        if (res != null)
            return successResponse("添加成功！", res);
        else
            return errorResponse("数据类型有误！", null);
    }

    @ApiOperation(value = "编辑部门", notes = "编辑部门")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/edit")
    @ResponseBody
    public Response edit(@RequestParam(value = "id", required = true) String i,
                         HttpServletRequest request) {
        Long id = -1L;
        Department department = null;
        try {
            id = Long.valueOf(i);
            department = departmentService.findById(id);
        } catch (Exception e) {
            return errorResponse("选择失败", e.toString());
        }
        return successResponse("选择成功", department);
    }

    @ApiOperation(value = "更新部门", notes = "更新部门")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/update")
    @ResponseBody
    public Response update(@RequestParam(value = "id", required = true) String i,
                           @RequestParam(value = "pid", required = true) String pid,
                           @RequestParam(value = "name", required = true) String name,
                           @RequestParam(value = "level", required = true) String l,
                         HttpServletRequest request) {
        Long id = -1L;
        long pId = -1;
        int level = -1;
        try {
            id = Long.valueOf(i);
            pId = Long.valueOf(pid);
            level = Integer.valueOf(l);
        } catch (Exception e) {
            return errorResponse("选择失败", e.toString());
        }
        Department department = departmentService.update(id, pId, name, level);
        if (department != null)
            return successResponse("修改成功！", department);
        else
            return errorResponse("数据类型有误！", null);
    }

    @ApiOperation(value = "删除部门", notes = "删除部门")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/delete")
    @ResponseBody
    public Response delete(@RequestParam(value = "id", required = true) String i,
                           HttpServletRequest request) {
        Long id = -1L;
        try {
            id = Long.valueOf(i);
            departmentService.deleteById(id);
        } catch (Exception e) {
            System.out.println(e.toString());
            return errorResponse("删除失败", e.toString());
        }
        return successResponse("删除成功", null);
    }

    @ApiOperation(value = "通过pid获取部门", notes = "通过pid获取部门")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/getByPid")
    @ResponseBody
    public Response getByPid(@RequestParam(value = "pid", required = true) String i,
                           HttpServletRequest request) {
        Long pid = -1L;
        List<Department> res = null;
        try {
            pid = Long.valueOf(i);
            res = departmentService.getByPid(pid);
        } catch (Exception e) {
            System.out.println(e.toString());
            return errorResponse("获取失败", e.toString());
        }
        return successResponse("获取成功", res);
    }
}
