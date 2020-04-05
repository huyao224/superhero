package com.next.api.controller.interceptor;

import com.next.utils.JsonUtils;
import com.next.utils.NEXTJSONResult;
import com.next.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

public class UserTokenInterceper implements HandlerInterceptor {
    @Autowired
    public RedisOperator redis;

    public static  final String REDIS_USER_TOKEN = "redis-user-token";
    /**
     * 拦截请求，controller调用前请求
     * **/
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("headerUserId");
        String userToken = request.getHeader("headerUserToken");

        if(StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userToken)){
            String redisUserToken = redis.get(REDIS_USER_TOKEN+":"+userId);
            if(StringUtils.isBlank(redisUserToken)){
                returnErrorResponse(response,new NEXTJSONResult().errorTokenMsg("用户登陆过期"));
                return false;
            }else{
                if(!redisUserToken.equals(userToken)){
                    returnErrorResponse(response,new NEXTJSONResult().errorTokenMsg("用户异地登陆"));
                    return false;
                }
            }
        }else{
            returnErrorResponse(response,new NEXTJSONResult().errorTokenMsg("请登录"));
            return false;
        }

        /**
         * return true  请求放行
         * return false 请求拦截
         * **/
        return true;
    }

    /**
     * 拦截请求，controller调用后，视图渲染前
     * **/
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 拦截请求，controller调用后，视图渲染后
     * **/
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    /**
     * 构建一个返回json的方法
     * **/
    public  void  returnErrorResponse(HttpServletResponse response, NEXTJSONResult nextjsonResult) throws Exception{
        OutputStream out =null;
        try {
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json");
            out = response.getOutputStream();
            out.write(JsonUtils.objectToJson(nextjsonResult).getBytes("utf-8"));
            out.flush();
        }finally {
            if(null != out){
                out.close();
            }
        }
    }
}
