package com.jwcq.custom;

import com.jwcq.security.SecurityConfig;
import com.jwcq.service.*;
import com.jwcq.user.entity.PrivilegeConfig;
import com.jwcq.user.entity.User;

import com.jwcq.user.entity.UserPrivilege;
import com.jwcq.user.entity.UserWechat;
import com.jwcq.utils.HttpUtils;
import com.jwcq.utils.StringUtils;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by luotuo on 17-6-26.
 */
@Service
public class CustomUserDetailsService implements AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {

    @Autowired
    private UserService userService;

    @Autowired
    private URIResourceService uriResourceService;

    @Autowired
    private UserPrivilegeService userPrivilegeService;

    @Autowired
    private PrivilegeConfigService privilegeConfigService;

    @Autowired
    private UserWechatService userWechatService;

    @Override
    public UserDetails loadUserDetails(CasAssertionAuthenticationToken token) throws UsernameNotFoundException {
        AttributePrincipal attributePrincipal = token.getAssertion().getPrincipal();
        System.out.println("attributePrincipal == " + attributePrincipal.getName());
        Map<String, Object> attributes = attributePrincipal.getAttributes();
        UserWechat userWechat = null;
        UserInfo userInfo = new UserInfo();
        if (attributePrincipal.getName().contains("WeiXinProfile")) {
            for(Map.Entry<String, Object> entry : attributes.entrySet()) {
                System.out.println("key == " + entry.getKey() + "  value == " + entry.getValue().toString());
            }
            userWechat = userWechatService.getByOpenId((String) attributes.get("openid"));
            if (userWechat == null) {
                userWechat = new UserWechat(attributes);
                userWechat = userWechatService.save(userWechat);
            }
        }
        User user = null;
        if (attributePrincipal.getName().contains("WeiXinProfile")) {
            // try to find user by openid
            user = userService.getUserByOpenId(userWechat.getOpenid());
        } else {
            user = userService.getUserByLoginName(token.getName());
        }

        if (user == null && userWechat == null)
            throw new UsernameNotFoundException("Admin: " + token.getName() + "do not exsit!");
        else if (user == null && userWechat != null) {
//            user = new User(userWechat);
//            user = userService.save(user);
            throw new UsernameNotFoundException("111");
        }
        /*这里我为了方便，就直接返回一个用户信息，实际当中这里修改为查询数据库或者调用服务什么的来获取用户信息*/
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        user.setLast_ip(HttpUtils.getClientIP(request));
        userService.save(user);
        userInfo.setUsername(token.getName());
        userInfo.setName(user.getName());
        userInfo.setId(user.getId());
        Set<AuthorityInfo> authorities = new HashSet<AuthorityInfo>();
        System.out.println("userInfo.getId == " + userInfo.getId());
        if (userInfo.getId() == 1) {
            // Admin, add all privileges
            List<String> uris = uriResourceService.getAll();
            for (String uri: uris) {
                AuthorityInfo authorityInfo = new AuthorityInfo(uri);
                authorities.add(authorityInfo);
            }
        } else {
            // Other users. Find user's privileges and add them
            List<UserPrivilege> userPrivileges = userPrivilegeService.findByUserId(user.getId());
            List<Long> privilegeIds = new ArrayList<>();
            for (UserPrivilege p : userPrivileges) {
                privilegeIds.add(p.getPrivilege_id());
            }
            if (!privilegeIds.isEmpty()) {
                List<PrivilegeConfig> privilegeConfigs = privilegeConfigService.getByIds(privilegeIds);
                for (PrivilegeConfig p : privilegeConfigs) {
                    if (StringUtils.isNotBlank(p.getUrl())) {
                        AuthorityInfo authorityInfo = new AuthorityInfo(p.getUrl());
                        authorities.add(authorityInfo);
                    }
                }
            }
        }
        userInfo.setAuthorities(authorities);
        return userInfo;
    }
}


//@Service
//public class CustomUserDetailsService implements UserDetailsService {
//
//    //实现UserDetailsService接口，实现loadUserByUsername方法
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private URIResourceService uriResourceService;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        System.out.println("当前的用户名是："+username);
//        User user = userService.getUserByLoginName(username);
//        if (user == null)
//            throw new UsernameNotFoundException("Admin: " + username + "do not exsit!");
//        /*这里我为了方便，就直接返回一个用户信息，实际当中这里修改为查询数据库或者调用服务什么的来获取用户信息*/
//        UserInfo userInfo = new UserInfo();
//        userInfo.setUsername(username);
//        userInfo.setName(user.getName());
//        userInfo.setId(user.getId());
//        Set<AuthorityInfo> authorities = new HashSet<AuthorityInfo>();
//        System.out.println("userInfo.getId == " + userInfo.getId());
//        if (userInfo.getId() == 1) {
//            // Admin, add all privileges
//            List<String> uris = uriResourceService.getAll();
//            for (String uri: uris) {
//                AuthorityInfo authorityInfo = new AuthorityInfo(uri);
//                authorities.add(authorityInfo);
//            }
//        } else {
//            // Other users. Find user's privileges and add them
//
//        }
//        userInfo.setAuthorities(authorities);
//        return userInfo;
//    }
//}
