package com.adolesce.common.intercepter;

import com.adolesce.common.entity.User;
import com.adolesce.common.threadLocal.UserHolder;
import com.adolesce.common.utils.JwtUtils;
import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Component
public class TokenInterceptor implements HandlerInterceptor {
    @Value("${adolesce.jwt.secret}")
    private String secret;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        System.err.println("当前访问URL："+ request.getRequestURI() + "参数：【" + JSON.toJSONString(request.getParameterMap())+"】");
        System.out.println("访问了："+request.getRequestURI());
        //1、获取请求头
        String token = request.getHeader("Authorization");

        //2、使用工具类，判断token是否有效
        boolean verifyToken = JwtUtils.verifyToken(token,secret);
        //3、如果token失效，返回状态码401，拦截
        if(!verifyToken) {
            response.setStatus(401);
            return false;
        }
        //4、如果token正常可用，放行

        //解析token，获取id和手机号码，构造User对象，存入Threadlocal
        Claims claims = JwtUtils.getClaims(token,secret);
        String phone = (String) claims.get("phone");
        Integer id = (Integer) claims.get("id");
        User user = new User();
        user.setId(Long.valueOf(id));
        user.setMobile(phone);
        UserHolder.set(user);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.remove();
    }
}
