package com.jwcq.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by luotuo on 17-8-15.
 */
public class MySimpleUrlLogoutSuccessHandler extends MyAbstractAuthenticationTargetUrlRequestHandler implements LogoutSuccessHandler {
    public MySimpleUrlLogoutSuccessHandler() {
    }

    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        super.handle(request, response, authentication);
    }
}
