package com.jwcq.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwcq.global.result.Response;
import com.jwcq.utils.SystemConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by luotuo on 17-8-21.
 */
public class MySimpleRedirectInvalidSessionStrategy implements InvalidSessionStrategy {
    private final Log logger = LogFactory.getLog(this.getClass());
    private final String destinationUrl;
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private boolean createNewSession = true;

    public MySimpleRedirectInvalidSessionStrategy(String invalidSessionUrl) {
        Assert.isTrue(UrlUtils.isValidRedirectUrl(invalidSessionUrl), "url must start with '/' or with 'http(s)'");
        this.destinationUrl = invalidSessionUrl;
    }

    public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.logger.debug("Starting new session (if required) and redirecting to '" + this.destinationUrl + "'");
        if(this.createNewSession) {
            request.getSession();
        }
        ObjectMapper mapper = new ObjectMapper();
        response.setHeader("Access-Control-Allow-Origin",request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "X-Request-With, JWCQ, Origin,Content-Type");
        response.setContentType("text/plain;charset='utf-8'");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(203);
        Response response1 = new Response();

        String url = SystemConfig.getProperty("cas.server.host.url")+"/login?service=";

        String referer = URLDecoder.decode(request.getHeader("referer"), "UTF-8");
        if (referer.contains("/html/"))
            url += URLEncoder.encode(SystemConfig.getProperty("app.server.host.url") + "/login?platform=task", "UTF-8");
        else
            url += URLEncoder.encode(SystemConfig.getProperty("app.server.host.url") + "/login?platfrom=project", "UTF-8");

        response1.setMessage("session过期，请重新登录！");
        response1.setResult(url);

        response1.setSuccess(1);

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

    public void setCreateNewSession(boolean createNewSession) {
        this.createNewSession = createNewSession;
    }
}
