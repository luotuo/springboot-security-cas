package com.jwcq.controller;

import com.jwcq.service.RolePrivilegeService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by luotuo on 17-7-13.
 */
@Controller
@RequestMapping("/rolePrivilege")
@Api(description = "角色权限管理")
public class RolePrivilegeController extends BaseController {

    @Autowired
    private RolePrivilegeService rolePrivilegeService;


}
