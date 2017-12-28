package com.jwcq.config;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.jwcq.custom.UserInfo;
import com.jwcq.service.UserService;
import com.jwcq.user.entity.User;
import com.jwcq.utils.StringUtils;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by liuma on 2017/6/5.
 */
public class Global {
    public static final int DEFAULT_PAGE_SIZE=20;//分页使用，每页条数
    public static final int DEFAULT_PAGE_NUM=0;//分页使用，第几页

    //预定的角色
    public static final String SURPERADMIN="超级管理员";//超级管理员，能够对系统进行任意操作
    public static final String SYSTEMADMIN="系统管理员";//系统管理员，所有权限
    public static final String BUSINESSADMIN="业务管理员";//系统管理员，所有权限
    public static final String PROJECTMADMIN="项目管理员";//项目管理员，所有权限
    public static final String SCHEDULER="调度员";


    //预定的项目内容控制角色
    public static final String PROJECTMANAGER="项目经理";
    public static final String BUSINESSMANAGER="商务经理";
    public static final String ASSISTMANAGER="协同项目经理";
    public static final String AUDITLEADER="稽查组长";
    public static final String AUDITOR="稽查员";
    public static final String REVIEWER="评审老师";

    public static final String EXPERT="专家";


    //系统配置
    public static final String qiniuAccessKey = "qiniuaccesskey";
    public static final String qiniuSecretKey = "qiniuaccesssecretkey";
    public static final String userIconBucket = "audit-new";
    public static final String qiniuUrl = "qiniuurl";
    public static final String wechatAppId = "wechatappid";
    public static final String wechatSecretKey = "wechatsecretkey";
    public static final String defaultPassword = "111111";

    /*********************************/

    public static String uploadFileToQiNiu(MultipartFile file, String fileName) throws Exception {
        Configuration cfg = new Configuration(Zone.zone0());
        UploadManager uploadManager = new UploadManager(cfg);
        Auth auth = Auth.create(qiniuAccessKey, qiniuSecretKey);
        String upToken = auth.uploadToken(userIconBucket);
        try {
            InputStream inputStream = null;
            try {
                inputStream = file.getInputStream();
            } catch (IOException e) {
                throw e;
            }

            try {
                Response response = uploadManager.put(inputStream,fileName,upToken,null, null);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            }
        } catch (UnsupportedEncodingException ex) {
            //ignore
        }
        return qiniuUrl + "/" + fileName;
    }

    public static void deleteFileInQiNiu(String fileName) throws Exception {
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(Zone.zone0());
        Auth auth = Auth.create(qiniuAccessKey, qiniuSecretKey);
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            bucketManager.delete(userIconBucket, fileName);
        } catch (QiniuException ex) {
            throw ex;
        }
    }

    /* 将json字符串转换成List<Map>集合 */
    public static List convertJson2List(String json) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        List<LinkedHashMap<String, Object>> list = null;
        try {
            list = objectMapper.readValue(json, List.class);
        } catch (JsonParseException e) {
            throw e;
        } catch (JsonMappingException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        }
        return list;
    }

    public static Map<String, String> convertJson2Map(String json) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> map = null;
        try {
            map  = objectMapper.readValue(json, Map.class);
        } catch (JsonParseException e) {
            throw e;
        } catch (JsonMappingException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        }
        return map;
    }

    /* JavaBean(Entity/Model)转换成JSON */
    public static String writeEntityJSON(Object obj) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String res = null;
        try {
            res = objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            throw e;
        }
        return res;
    }

    public static Map<String, String> convertEntity2Map(Object obj) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = null;
        Map<String, String> map = null;
        try {
            json = objectMapper.writeValueAsString(obj);
            map  = objectMapper.readValue(json, Map.class);
        } catch (IOException e) {
            throw e;
        }
        return map;
    }

    /* 将Map集合转换成Json字符串 */
    public static String writeMapJSON(Map<String, String> map) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(map);
        return json;
    }

    public static Object jsonStr2Object(String content, Class cls) throws IOException {
        if(StringUtils.isBlank(content))return null;
        ObjectMapper objectMapper = new ObjectMapper();
        Object obj = objectMapper.readValue(content, cls);
        return obj;
    }

    /*将json字符串转换成List<T>集合*/
    public static TreeSet json2List(String json, Class cls) throws Exception {
        if (json == null || json.equals(""))
            return null;
        ObjectMapper objectMapper = new ObjectMapper();
        List<Object> list = objectMapper.readValue(json, new TypeReference<List<Object>>() {
        });
        TreeSet<Object> resList = new TreeSet<>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = (LinkedHashMap<String, Object>)list.get(i);
            Set<String> set = map.keySet();
            Object obj = cls.newInstance();
            for (Iterator<String> it = set.iterator();it.hasNext();) {
                String key = it.next();
                String keyOld = key;
                if (key.equals("content"))
                    key = "value";
                Field idF = cls.getDeclaredField(key);
                idF.setAccessible(true); //使用反射机制可以打破封装性，导致了java对象的属性不安全。
                if (idF.getType().toString().equals("class java.util.Date")){
                    if (map.get(key) != null)
                        idF.set(obj, new Date((Long)map.get(key))); //set
                }
                else {
                    if (keyOld.equals("content"))
                        idF.set(obj, map.get(keyOld)); //set
                    else idF.set(obj, map.get(key)); //set
                }
            }
            resList.add(obj);
        }
        return resList;
    }

    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static Long getCurrentUserId() {
        Object userInfo = null;
        try {
            userInfo = SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();
            if (userInfo instanceof Long)
                return (Long)userInfo;
        } catch (Exception e) {
            return 0L;
        }
        if ("anonymousUser".equals(userInfo.toString()))
            return 0L;
        return ((UserInfo)userInfo).getId();
    }
}
