package com.jwcq.service;

import com.jwcq.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextImpl;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by luotuo on 17-6-1.
 */
public abstract class BaseService {
    @Autowired
    protected UserService userService;


    public User getUserByRequest(HttpServletRequest request) {
        SecurityContextImpl securityContextImpl = (SecurityContextImpl) request
                .getSession().getAttribute("SPRING_SECURITY_CONTEXT");
        String userLoginName = securityContextImpl.getAuthentication().getName();
        return userService.getUserByLoginName(userLoginName);
    }

    public abstract  Iterable<Object> findAll(Pageable pageable);
    public abstract  Object findById(long id);
    public abstract  boolean deleteById(long id);
    public abstract Object save(Object object);
    public abstract String getNextId();
    public abstract Iterable<Object> findByParams(PageRequest pageRequest, String... params);
}
