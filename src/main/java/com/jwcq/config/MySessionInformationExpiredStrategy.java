package com.jwcq.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwcq.global.result.Response;
import com.jwcq.utils.SystemConfig;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by luotuo on 17-8-22.
 */
public class MySessionInformationExpiredStrategy implements SessionInformationExpiredStrategy {
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException{
        HttpServletRequest request = event.getRequest();
        HttpServletResponse response = event.getResponse();
        SessionInformation info = event.getSessionInformation();
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
        response1.setSuccess(1);
        String url = SystemConfig.getProperty("cas.server.host.url")+"/login?service=";

        String referer = URLDecoder.decode(request.getHeader("referer"), "UTF-8");
        if (referer.contains("/html/"))
            url += URLEncoder.encode(SystemConfig.getProperty("app.server.host.url") + "/login?platform=task", "UTF-8");
        else
            url += URLEncoder.encode(SystemConfig.getProperty("app.server.host.url") + "/login?platform=project", "UTF-8");

        response1.setMessage("session过期，请重新登录！");
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
}
