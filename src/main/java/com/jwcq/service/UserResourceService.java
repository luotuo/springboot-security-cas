package com.jwcq.service;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by luotuo on 17-6-7.
 */
public interface UserResourceService {
    public boolean checkAuthorization(HttpServletRequest request, String controllerType);
}
