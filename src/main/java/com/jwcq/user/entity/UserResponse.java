package com.jwcq.user.entity;

import lombok.Data;

/**
 * Created by luotuo on 17-7-21.
 */
@Data
public class UserResponse {
    private long id;
    private String name;
    private String department;
    private String phone;
    private String email;
    private int state;
    private String state_str;
    private String icons;
    private String password;
    private String wechat;
    private String roles;
    private Object privileges;
    private String lastIp;
    private int hasLogin;
    private int isBindWechat;
    private String wechatHeadImgUrl;

    public void setUserResponse(User user, String roles) {
        this.roles = roles;
        this.id = user.getId();
        this.department = user.getDepartment();
        this.email = user.getEmail();
        this.icons = user.getIcons();
        this.name = user.getName();
        this.phone = user.getPhone();
        this.password = "";
        this.state = user.getState();
        this.state_str = user.getState_str();
        this.wechat = user.getWechat();
        this.lastIp = user.getLast_ip();
        this.hasLogin = user.getHas_login();
        this.isBindWechat = user.getBind_wechat();
        this.wechatHeadImgUrl = user.getWechat_headimgurl();
    }

    public UserResponse() {}

    public UserResponse(User user) {
        this.id = user.getId();
        this.department = user.getDepartment();
        this.email = user.getEmail();
        this.icons = user.getIcons();
        this.name = user.getName();
        this.phone = user.getPhone();
        this.password = "";
        this.state = user.getState();
        this.state_str = user.getState_str();
        this.wechat = user.getWechat();
        this.lastIp = user.getLast_ip();
        this.hasLogin = user.getHas_login();
        this.isBindWechat = user.getBind_wechat();
        this.wechatHeadImgUrl = user.getWechat_headimgurl();
    }
}
