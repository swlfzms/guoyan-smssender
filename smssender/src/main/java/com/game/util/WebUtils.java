package com.game.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description:
 * @Author: Jason
 * @CreateDate: 2018/11/25 0:45
 */

public class WebUtils {
    public static String getIP(HttpServletRequest request){
        String ip=request.getHeader("X-Real-IP");
        if(ip==null || ip.length()==0 || "unknown".equalsIgnoreCase(ip)){
            ip=request.getHeader("Proxy-Client-IP");
        }
        if(ip==null || ip.length()==0 || "unknown".equalsIgnoreCase(ip)){
            ip=request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip==null || ip.length()==0 || "unknown".equalsIgnoreCase(ip)){
            ip=request.getHeader("x-forwarded-for");
        }
        if(ip==null || ip.length()==0 || "unknown".equalsIgnoreCase(ip)){
            ip=request.getRemoteAddr();
        }
        return ip;
    }

    public static void main(String[] args){
        JSONArray jsonArray = new JSONArray();
        for(int i=0;i<2;i++){
            JSONObject message = new JSONObject();
            message.put("phone","18825162236");
            message.put("templateCode","SMS_151785361");
            JSONObject content = new JSONObject();
            content.put("code", "io-17289-exec-3");
            message.put("content", content);
            jsonArray.add(message);
        }
        System.out.println(jsonArray.toJSONString());
    }
}
