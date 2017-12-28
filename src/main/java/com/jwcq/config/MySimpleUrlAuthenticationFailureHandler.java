package com.jwcq.config;

/**
 * Created by luotuo on 17-6-22.
 */

import com.jwcq.global.result.Response;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwcq.properties.CasProperties;
import com.jwcq.utils.SystemConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

public class MySimpleUrlAuthenticationFailureHandler implements AuthenticationFailureHandler {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private String defaultFailureUrl;
    private boolean forwardToDestination = false;
    private boolean allowSessionCreation = true;
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    public MySimpleUrlAuthenticationFailureHandler() {
    }

    public MySimpleUrlAuthenticationFailureHandler(String defaultFailureUrl) {
        this.setDefaultFailureUrl(defaultFailureUrl);
    }

    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if (exception.getMessage().equals("111")) {
            response.setHeader("Refresh",  "1;  URL=http://118.190.132.68:8080/wechat/wechatloginfailed.html");
            return;
        }
        if(this.defaultFailureUrl == null) {
            if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
                response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
                response.setHeader("Access-Control-Allow-Credentials", "true");
                response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
                response.setHeader("Access-Control-Max-Age", "3600");
                response.setHeader("Access-Control-Allow-Headers", "X-Request-With, JWCQ, Origin,Content-Type, jwcq");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(202);
            } else {
                this.logger.debug("No failure URL set, sending 401 Unauthorized error");
                System.out.println("onAuthenticationFailure======================================");
                String header = request.getHeader("jwcq");
                ObjectMapper mapper = new ObjectMapper();
                response.setHeader("Access-Control-Allow-Origin",request.getHeader("Origin"));
                response.setHeader("Access-Control-Allow-Credentials", "true");
                response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
                response.setHeader("Access-Control-Max-Age", "3600");
                response.setHeader("Access-Control-Allow-Headers", "X-Request-With, JWCQ, Origin,Content-Type");
                response.setContentType("text/plain;charset='utf-8'");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(203);
                if (header != null) {
                    //http://118.190.132.68
                    Enumeration headerNames = request.getHeaderNames();
                    try {
                        while (headerNames.hasMoreElements()) {
                            String key = (String) headerNames.nextElement();
                            String value = key.equals("referer") ? URLDecoder.decode(request.getHeader(key), "UTF-8") : request.getHeader(key);
                            System.out.println("hahaHeader == " + key + "  value ==" + value);
                        }
                    } catch (Exception e) {
                    }

                    String url = SystemConfig.getProperty("cas.server.host.url")+"/login?service=";

                    //url += URLEncoder.encode(request.getRequestURL().toString(), "UTF-8");
                    String referer = URLDecoder.decode(request.getHeader("referer"), "UTF-8");
                    if (referer.contains("/html/"))
                        url += URLEncoder.encode(SystemConfig.getProperty("app.server.host.url") + "/login?platform=task", "UTF-8");
                    else
                        url += URLEncoder.encode(SystemConfig.getProperty("app.server.host.url") + "/login?platform=project", "UTF-8");
                    if (header.equalsIgnoreCase("jwcq")) {
                        // Our ajax request, redirect it to login web page
                        Response response1 = new Response();
                        response1.setSuccess(1);
                        response1.setMessage("success");
                        response1.setResult(url);
                        String responseStr = "";
                        PrintWriter out = response.getWriter();
                        try {
                            responseStr = mapper.writeValueAsString(response1);
                            out.append(responseStr);
                        } catch (IOException ioe) {
                            // FIXME: Add log here!
                            out.append(ioe.toString());
                        }
                        out.close();
                    }
                    System.out.println("Return our response!");
                } else {
                    response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
                    response.setHeader("Access-Control-Allow-Credentials", "true");
                    response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
                    response.setHeader("Access-Control-Max-Age", "3600");
                    response.setHeader("Access-Control-Allow-Headers", "X-Request-With, JWCQ, Origin,Content-Type, Accept");
                    response.setCharacterEncoding("UTF-8");
                    response.setStatus(200);
                    if (exception instanceof SessionAuthenticationException && exception.getMessage().equals("Maximum sessions of 1 for this principal exceeded")) {
                        Response response1 = new Response();
                        response1.setSuccess(0);
                        response1.setMessage("该用户已登录");
                        response1.setResult(0);
                        String responseStr = "";
                        PrintWriter out = response.getWriter();
                        try {
                            responseStr = mapper.writeValueAsString(response1);
                            out.append(responseStr);
                        } catch (IOException ioe) {
                            // FIXME: Add log here!
                            out.append(ioe.toString());
                        }
                        out.close();
                    } else {
                        String url = SystemConfig.getProperty("cas.server.host.url")+"/login?service=";
                        url += URLEncoder.encode(request.getRequestURL().toString(), "UTF-8");
                        this.redirectStrategy.sendRedirect(request, response, url);
                    }
                }
            }
        } else {
            this.saveException(request, exception);
            if(this.forwardToDestination) {
                this.logger.debug("Forwarding to " + this.defaultFailureUrl);
                request.getRequestDispatcher(this.defaultFailureUrl).forward(request, response);
            } else {
                this.logger.debug("Redirecting to " + this.defaultFailureUrl);
                this.redirectStrategy.sendRedirect(request, response, this.defaultFailureUrl);
            }
        }

    }

    protected final void saveException(HttpServletRequest request, AuthenticationException exception) {
        if(this.forwardToDestination) {
            request.setAttribute("SPRING_SECURITY_LAST_EXCEPTION", exception);
        } else {
            HttpSession session = request.getSession(false);
            if(session != null || this.allowSessionCreation) {
                request.getSession().setAttribute("SPRING_SECURITY_LAST_EXCEPTION", exception);
            }
        }

    }

    public void setDefaultFailureUrl(String defaultFailureUrl) {
        Assert.isTrue(UrlUtils.isValidRedirectUrl(defaultFailureUrl), "'" + defaultFailureUrl + "' is not a valid redirect URL");
        this.defaultFailureUrl = defaultFailureUrl;
    }

    protected boolean isUseForward() {
        return this.forwardToDestination;
    }

    public void setUseForward(boolean forwardToDestination) {
        this.forwardToDestination = forwardToDestination;
    }

    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

    protected RedirectStrategy getRedirectStrategy() {
        return this.redirectStrategy;
    }

    protected boolean isAllowSessionCreation() {
        return this.allowSessionCreation;
    }

    public void setAllowSessionCreation(boolean allowSessionCreation) {
        this.allowSessionCreation = allowSessionCreation;
    }
}

