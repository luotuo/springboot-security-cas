package com.jwcq.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwcq.global.result.Response;
import com.jwcq.utils.SystemConfig;
import org.jasig.cas.client.util.CommonUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by luotuo on 17-8-21.
 */
public class MyCasAuthenticationEntryPoint implements AuthenticationEntryPoint, InitializingBean {
    private ServiceProperties serviceProperties;
    private String loginUrl;
    private boolean encodeServiceUrlWithSessionId = true;

    public MyCasAuthenticationEntryPoint() {
    }

    public void afterPropertiesSet() throws Exception {
        Assert.hasLength(this.loginUrl, "loginUrl must be specified");
        Assert.notNull(this.serviceProperties, "serviceProperties must be specified");
        Assert.notNull(this.serviceProperties.getService(), "serviceProperties.getService() cannot be null.");
    }

    public final void commence(HttpServletRequest servletRequest, HttpServletResponse response, AuthenticationException authenticationException) throws IOException, ServletException {
        String urlEncodedService = this.createServiceUrl(servletRequest, response);
        String redirectUrl = this.createRedirectUrl(urlEncodedService);
        this.preCommence(servletRequest, response);
        //response.sendRedirect(redirectUrl);
        ObjectMapper mapper = new ObjectMapper();
        response.setHeader("Access-Control-Allow-Origin",servletRequest.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "X-Request-With, JWCQ, Origin,Content-Type");
        response.setContentType("text/plain;charset='utf-8'");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(203);
        Response response1 = new Response();
        response1.setSuccess(1);
        String url = SystemConfig.getProperty("cas.server.host.url")+"/login?service=";

        String encodeReferer = servletRequest.getHeader("referer");
        if (encodeReferer == null) {
            url += URLEncoder.encode(SystemConfig.getProperty("app.server.host.url") + "/login?platform=project", "UTF-8");
        } else {
            String referer = URLDecoder.decode(encodeReferer, "UTF-8");
            if (referer.contains("/html/"))
                url += URLEncoder.encode(SystemConfig.getProperty("app.server.host.url") + "/login?platform=task", "UTF-8");
            else
                url += URLEncoder.encode(SystemConfig.getProperty("app.server.host.url") + "/login?platform=project", "UTF-8");
        }
        response1.setMessage("请登录");
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

    protected String createServiceUrl(HttpServletRequest request, HttpServletResponse response) {
        return CommonUtils.constructServiceUrl((HttpServletRequest)null, response, this.serviceProperties.getService(), (String)null, this.serviceProperties.getArtifactParameter(), this.encodeServiceUrlWithSessionId);
    }

    protected String createRedirectUrl(String serviceUrl) {
        return CommonUtils.constructRedirectUrl(this.loginUrl, this.serviceProperties.getServiceParameter(), serviceUrl, this.serviceProperties.isSendRenew(), false);
    }

    protected void preCommence(HttpServletRequest request, HttpServletResponse response) {
    }

    public final String getLoginUrl() {
        return this.loginUrl;
    }

    public final ServiceProperties getServiceProperties() {
        return this.serviceProperties;
    }

    public final void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public final void setServiceProperties(ServiceProperties serviceProperties) {
        this.serviceProperties = serviceProperties;
    }

    public final void setEncodeServiceUrlWithSessionId(boolean encodeServiceUrlWithSessionId) {
        this.encodeServiceUrlWithSessionId = encodeServiceUrlWithSessionId;
    }

    protected boolean getEncodeServiceUrlWithSessionId() {
        return this.encodeServiceUrlWithSessionId;
    }
}

