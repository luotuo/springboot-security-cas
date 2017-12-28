package com.jwcq.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * Created by liuma on 2017/7/20.
 */
public class StringUtils {

    public static Long[] parseLongArray(String input) {
        if (input == null || input.length() == 0) return null;
        input = input.trim();
        input = input.replace("\"", "");
        input = input.replace("'", "");
        input = input.replace("，", ",");
        String[] idstr = input.split(",");
        Long[] idlongs = new Long[idstr.length];
        for (int i = 0; i < idlongs.length; i++) {
            idlongs[i] = Long.parseLong(idstr[i]);
        }
        return idlongs;
    }

    public static List<Long> StringToLongList(String inputs) {
        if(isBlank(inputs))return new ArrayList<>();
        inputs = inputs.trim();
        inputs = inputs.replace("\"", "");
        inputs = inputs.replace("'", "");
        inputs = inputs.replace("，",",");
        String[] idstr = inputs.split(",");
        List<Long> LongList = new ArrayList<>();
        for (int i = 0; i < idstr.length; i++) {
            LongList.add(Long.parseLong(idstr[i]));
        }
        return LongList;
    }

    //字符串
    public static boolean isNotBlank(String input) {
        if (input == null) return false;
        return (!input.isEmpty());
    }

    public static boolean isBlank(String input) {
        if (input == null) return true;
        return input.isEmpty();
    }


    public static String join(List<String> input, char port) {
        return org.apache.tomcat.util.buf.StringUtils.join(input, port);
    }

    public static String joinLong(List<Long> inputs, char port) {
        if(inputs==null||inputs.size()==0)return "";
        List<String>newInput=new ArrayList<>();
        for(Long input:inputs)newInput.add(""+input);
        return org.apache.tomcat.util.buf.StringUtils.join(newInput, port);
    }



    public static Long StringToLong(String id){
        try{
            return Long.valueOf(id);
        }catch (Exception e){
            e.printStackTrace();
            return 0L;
        }

    }

    //替换标签，<br>换行
    public static String stripHtml(String input){
        if(isBlank(input))return null;
        input =input.replaceAll("&nbsp;"," ");
        // <p>段落替换为换行
        input = input.replaceAll("<p .*?>", "\r\n");
        // <br><br/>替换为换行
        input = input.replaceAll("<br\\s*/?>", "\r\n");
        // 去掉其它的<>之间的东西
        input = input.replaceAll("\\<.*?>", "");
        // 还原HTML
        // content = HTMLDecoder.decode(content);
        return input;
    }
    //只保留<br>&nbsp;标签，其他的全部去掉
    public static String centerReportAbstractStripHtml(String input){
        if(isBlank(input))return null;
        input = input.replaceAll("\r", "");
        input = input.replaceAll("\n", "");
        // 去掉除<br>以外的其它的标签
        input = input.replaceAll("<(?!br).*?>", "");
        return input;
    }
    //用于更新数据库的标签，一次性使用，20171218
    public static String updateDataBaseAbstract(String input){
        if(isBlank(input))return null;
        // 去掉除<br>以外的其它的标签
        input = input.replaceAll("<(?!br).*?>", "");
        input = input.replaceAll("\r\n", "\n");
        input = input.replaceAll("\n\n", "\n");
        input = input.replaceAll("\n\n", "\n");
        input = input.replaceAll("\n", "<br>");
        input = input.replaceAll(" *(<br>) *", "<br>");
        input = input.replaceAll("(<br>)+", "<br>");
        return input;

    }

    //判断是否是电话号码
    public static boolean isPhoneNum(String phoneNumber){
        String PHONE_PATTERN="^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17([0,1,6,7,]))|(18[0-2,5-9]))\\d{8}$";
        return Pattern.compile(PHONE_PATTERN).matcher(phoneNumber).matches();
    }
    public static boolean isNotPhoneNum(String phoneNumber){
        return !isPhoneNum(phoneNumber);
    }
    //生成随机长度的验证码
    public static String randomCode(int length){
        Random random = new Random();
        String code="";
        while(length>0){
            length--;
            code+=random.nextInt(10);
        }
        return code;
    }

   public static void main(String[]arg){
        String abstractStr= "daf<br><br> <br> <br>得问   <br><br> <br>文档";
        String newStr =  updateDataBaseAbstract(abstractStr);
        System.out.println("abstractStr:"+abstractStr);
        System.out.println("newsTr:"+newStr);

   }
}
