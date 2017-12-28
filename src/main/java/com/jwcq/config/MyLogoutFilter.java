package com.jwcq.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.CompositeLogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by luotuo on 17-8-15.
 */
public class MyLogoutFilter extends GenericFilterBean {
    private RequestMatcher logoutRequestMatcher;
    private final LogoutHandler handler;
    private final LogoutSuccessHandler logoutSuccessHandler;

    public MyLogoutFilter(LogoutSuccessHandler logoutSuccessHandler, LogoutHandler... handlers) {
        this.handler = new CompositeLogoutHandler(handlers);
        Assert.notNull(logoutSuccessHandler, "logoutSuccessHandler cannot be null");
        this.logoutSuccessHandler = logoutSuccessHandler;
        this.setFilterProcessesUrl("/logout");
    }

    public MyLogoutFilter(String logoutSuccessUrl, LogoutHandler... handlers) {
        this.handler = new CompositeLogoutHandler(handlers);
        Assert.isTrue(!StringUtils.hasLength(logoutSuccessUrl) || UrlUtils.isValidRedirectUrl(logoutSuccessUrl), logoutSuccessUrl + " isn't a valid redirect URL");
        MySimpleUrlLogoutSuccessHandler urlLogoutSuccessHandler = new MySimpleUrlLogoutSuccessHandler();
        if(StringUtils.hasText(logoutSuccessUrl)) {
            urlLogoutSuccessHandler.setDefaultTargetUrl(logoutSuccessUrl);
        }

        this.logoutSuccessHandler = urlLogoutSuccessHandler;
        this.setFilterProcessesUrl("/logout");
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)res;
        if(this.requiresLogout(request, response)) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if(this.logger.isDebugEnabled()) {
                this.logger.debug("Logging out user '" + auth + "' and transferring to logout destination");
            }

            this.handler.logout(request, response, auth);
            this.logoutSuccessHandler.onLogoutSuccess(request, response, auth);
        } else {
            chain.doFilter(request, response);
        }
    }

    protected boolean requiresLogout(HttpServletRequest request, HttpServletResponse response) {
        return this.logoutRequestMatcher.matches(request);
    }

    public void setLogoutRequestMatcher(RequestMatcher logoutRequestMatcher) {
        Assert.notNull(logoutRequestMatcher, "logoutRequestMatcher cannot be null");
        this.logoutRequestMatcher = logoutRequestMatcher;
    }

    public void setFilterProcessesUrl(String filterProcessesUrl) {
        this.logoutRequestMatcher = new AntPathRequestMatcher(filterProcessesUrl);
    }
}

