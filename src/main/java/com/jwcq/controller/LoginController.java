package com.jwcq.controller;

import com.jwcq.utils.SystemConfig;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.Enumeration;

/**
 * Created by luotuo on 17-6-26.
 */
@Controller
public class LoginController {
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, value = "/login")
    @ResponseBody
    public String login(HttpServletRequest request) {
        String responseStr = "<html><head><meta http-equiv=\"refresh\" content=\"1;url=%s\"></head><body></body></html>";
        String param = SystemConfig.getProperty("callback.path.project");
        Enumeration<String> p = request.getParameterNames();
        String clientParams = "";
        try {
            while (p.hasMoreElements()) {
                String key = p.nextElement();
                String value = request.getParameter(key);
                System.out.println("param == " + key + "  value ==" + value);
                if (key.equals("platform") || key.equals("ticket"))
                    continue;
                clientParams += key + "=" + value + "&";
                }
        } catch (Exception e) {
        }

        String platform = request.getParameter("platform");
        if (platform.equals("task")) {
            param = SystemConfig.getProperty("callback.path.task");
        } else if (platform.equals("project")) {
        } else if (platform.equals("wechat")) {
          param = SystemConfig.getProperty("callback.path.wechat") + "?" + clientParams + "online=1";
        }
        System.out.println("url == " + request.getRequestURL().toString());
        responseStr = String.format(responseStr, param);
        System.out.println("responseStr == " + responseStr);
        return responseStr;
    }
}
