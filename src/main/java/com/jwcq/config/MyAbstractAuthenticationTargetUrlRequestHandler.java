package com.jwcq.config;

import com.jwcq.custom.UserInfo;
import com.jwcq.entity.Resource;
import com.jwcq.entity.ResourceLock;
import com.jwcq.global.result.Response;
import com.jwcq.service.UserService;
import com.jwcq.user.entity.User;
import com.jwcq.utils.JsonUtils;
import com.jwcq.utils.SystemConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Enumeration;

/**
 * Created by luotuo on 17-8-15.
 */
public abstract class MyAbstractAuthenticationTargetUrlRequestHandler {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private String targetUrlParameter = null;
    private String defaultTargetUrl = "/";
    private boolean alwaysUseDefaultTargetUrl = false;
    private boolean useReferer = false;
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    @Autowired
    private UserService userService;

    protected MyAbstractAuthenticationTargetUrlRequestHandler() {
    }

    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetUrl = this.determineTargetUrl(request, response);
        if(response.isCommitted()) {
            this.logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
        } else {
            // Release all locks
            SecurityContextImpl securityContextImpl = (SecurityContextImpl) request
                    .getSession().getAttribute("SPRING_SECURITY_CONTEXT");
            User user = null;
            if (securityContextImpl != null) {
                String userLoginName = securityContextImpl.getAuthentication().getName();
                user= userService.getUserByLoginName(userLoginName);
            }
            if (user != null)
                ResourceLock.releaseAllLocksByUserId(user.getId());

            // Here, check header first, and then response
            if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
                response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
                response.setHeader("Access-Control-Allow-Credentials", "true");
                response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
                response.setHeader("Access-Control-Max-Age", "3600");
                response.setHeader("Access-Control-Allow-Headers", "X-Request-With, JWCQ, Origin,Content-Type, jwcq");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(202);
            } else {
                String header = request.getHeader("jwcq");
                if (header == null || header.equals("")) {
                    this.redirectStrategy.sendRedirect(request, response, targetUrl);
                } else if (header.equalsIgnoreCase("jwcq")) {
                    response.setHeader("Access-Control-Allow-Origin",request.getHeader("Origin"));
                    response.setHeader("Access-Control-Allow-Credentials", "true");
                    response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
                    response.setHeader("Access-Control-Max-Age", "3600");
                    response.setHeader("Access-Control-Allow-Headers", "X-Request-With, JWCQ, Origin,Content-Type");
                    response.setCharacterEncoding("UTF-8");
                    response.setStatus(203);
                    String url = targetUrl + "?service=";

                    Enumeration headerNames = request.getHeaderNames();
                    try {
                        while (headerNames.hasMoreElements()) {
                            String key = (String) headerNames.nextElement();
                            String value = key.equals("referer") ? URLDecoder.decode(request.getHeader(key), "UTF-8") : request.getHeader(key);
                            System.out.println("hahaHeader == " + key + "  value ==" + value);
                        }
                    } catch (Exception e) {
                    }

                    String referer = URLDecoder.decode(request.getHeader("referer"), "UTF-8");
                    if (referer.contains("/html/"))
                        url += URLEncoder.encode(SystemConfig.getProperty("app.server.host.url") + "/login?platform=task", "UTF-8");
                    else
                        url += URLEncoder.encode(SystemConfig.getProperty("app.server.host.url") + "/login?platform=project", "UTF-8");
                    // Our ajax request, redirect it to login web page
                    Response response1 = new Response();
                    response1.setSuccess(1);
                    response1.setMessage("success");
                    response1.setResult(url);
                    String responseStr = "";
                    responseStr = JsonUtils.writeEntityJSON(response1);
                    PrintWriter out = response.getWriter();
                    out.append(responseStr);
                    out.close();
                } else {
                    this.redirectStrategy.sendRedirect(request, response, targetUrl);
                }
            }
        }
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        if(this.isAlwaysUseDefaultTargetUrl()) {
            return this.defaultTargetUrl;
        } else {
            String targetUrl = null;
            if(this.targetUrlParameter != null) {
                targetUrl = request.getParameter(this.targetUrlParameter);
                if(StringUtils.hasText(targetUrl)) {
                    this.logger.debug("Found targetUrlParameter in request: " + targetUrl);
                    return targetUrl;
                }
            }

            if(this.useReferer && !StringUtils.hasLength(targetUrl)) {
                targetUrl = request.getHeader("Referer");
                this.logger.debug("Using Referer header: " + targetUrl);
            }

            if(!StringUtils.hasText(targetUrl)) {
                targetUrl = this.defaultTargetUrl;
                this.logger.debug("Using default Url: " + targetUrl);
            }

            return targetUrl;
        }
    }

    protected final String getDefaultTargetUrl() {
        return this.defaultTargetUrl;
    }

    public void setDefaultTargetUrl(String defaultTargetUrl) {
        Assert.isTrue(UrlUtils.isValidRedirectUrl(defaultTargetUrl), "defaultTarget must start with '/' or with 'http(s)'");
        this.defaultTargetUrl = defaultTargetUrl;
    }

    public void setAlwaysUseDefaultTargetUrl(boolean alwaysUseDefaultTargetUrl) {
        this.alwaysUseDefaultTargetUrl = alwaysUseDefaultTargetUrl;
    }

    protected boolean isAlwaysUseDefaultTargetUrl() {
        return this.alwaysUseDefaultTargetUrl;
    }

    public void setTargetUrlParameter(String targetUrlParameter) {
        if(targetUrlParameter != null) {
            Assert.hasText(targetUrlParameter, "targetUrlParameter cannot be empty");
        }

        this.targetUrlParameter = targetUrlParameter;
    }

    protected String getTargetUrlParameter() {
        return this.targetUrlParameter;
    }

    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

    protected RedirectStrategy getRedirectStrategy() {
        return this.redirectStrategy;
    }

    public void setUseReferer(boolean useReferer) {
        this.useReferer = useReferer;
    }
}

