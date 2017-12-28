package com.jwcq.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.jwcq.custom.UserInfo;
import com.jwcq.global.result.Response;
import com.jwcq.utils.HttpUtils;
import com.jwcq.utils.JsonUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;


/**
 * Created by luotuo on 17-7-13.
 */
@Aspect
@Component
public class ControllerAop {
    public final Logger logger = LoggerFactory.getLogger(this.getClass());
    ThreadLocal<Long> startTime = new ThreadLocal<>();
    ThreadLocal<String> logStr = new ThreadLocal<>();

    @Pointcut("execution(* com.jwcq.controller..*.*(..))")
    public void executeController() {
    }

    @Before("executeController()")
    public void doBeforeController(JoinPoint joinPoint) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        //从获取RequestAttributes中获取HttpServletRequest的信息
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        //如果要获取Session信息的话，可以这样写：
        //HttpSession session = (HttpSession) requestAttributes.resolveReference(RequestAttributes.REFERENCE_SESSION);

//        Enumeration headerNames = request.getHeaderNames();
//        try {
//            while (headerNames.hasMoreElements()) {
//                String key = (String) headerNames.nextElement();
//                String value = key.equals("referer") ? URLDecoder.decode(request.getHeader(key), "UTF-8") : request.getHeader(key);
//                System.out.println("hahaHeader == " + key + "  value ==" + value);
//            }
//        } catch (Exception e) {
//        }

        Enumeration<String> enumeration = request.getParameterNames();
        Map<String, String> parameterMap = Maps.newHashMap();

        while (enumeration.hasMoreElements()) {
            String parameter = enumeration.nextElement();
            parameterMap.put(parameter, request.getParameter(parameter));
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String str = "";
        try {
            str = objectMapper.writeValueAsString(parameterMap);
        } catch (Exception e) {

        }
        if (str.length() > 0) {
            //System.out.println("请求的参数信息为：" + str);
        }
    }

    @After("executeController()")
    public void doAfterController(JoinPoint joinPoint) {

    }

    @AfterReturning("executeController()")
    public void doAfterControllerReturning(JoinPoint joinPoint) {

    }



    @Pointcut("execution(* com.jwcq.controller..*.*(..))")
    public void webLog() {
    }

    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        startTime.set(System.currentTimeMillis());
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        int sessionTimeout = request.getSession().getMaxInactiveInterval();
        logger.info("sessionTimeout == " + sessionTimeout);
        try {
            UserInfo userInfo = (UserInfo) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            String ip = HttpUtils.getClientIP(request);
            String user = userInfo.getName();
            String url = request.getRequestURL().toString();
            String httpMethod = request.getMethod();
            //String param = Arrays.toString(joinPoint.getArgs());
            Enumeration<String> enumeration = request.getParameterNames();
            Map<String, String> parameterMap = Maps.newHashMap();

            while (enumeration.hasMoreElements()) {
                String parameter = enumeration.nextElement();
                parameterMap.put(parameter, request.getParameter(parameter));
            }
            String param = JsonUtils.writeEntityJSON(parameterMap);
            String className = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
            String o = "IP: " + ip + " USER: " + user + " URL: " + url + " HTTPMETHOD: " + httpMethod + " CLASSNAME: " + className + " PARAM: " + param;
            logStr.set(o);
        } catch (Exception e) {

        }
    }

    @AfterReturning(returning = "ret", pointcut = "webLog()")
    public void doAfterReturning(Response ret) throws Throwable {
        Long spendTime = System.currentTimeMillis() - startTime.get();
        String response = JsonUtils.writeEntityJSON(ret.getResult());
        String temp = logStr.get() + " RESPONSE: " + response + " SPENDTIME: " + spendTime.toString() + "MS";
        logger.info(temp);
    }
}
