package com.adolesce.common.advice;

import com.adolesce.common.annotation.Cache;
import com.adolesce.common.intercepter.GlobalCacheInterceptor;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.concurrent.TimeUnit;

/**
 * 全局缓存响应体增强
 */
@Slf4j
//@ControllerAdvice
public class GlobalCacheResponseBodyAdvice implements ResponseBodyAdvice {

    @Value("${cache.enable}")
    private Boolean enable;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        // 全局缓存开关处于开启状态&是get请求&包含了@Cache注解
        return enable && returnType.hasMethodAnnotation(GetMapping.class)
                && returnType.hasMethodAnnotation(Cache.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (ObjectUtils.isEmpty(body)) {
            return null;
        }
        try {
            //构建redisValue
            String redisValue;
            if (body instanceof String) {
                redisValue = (String) body;
            } else {
                redisValue = JSON.toJSONString(body);
            }
            //构建redisKey
            String redisKey = GlobalCacheInterceptor.createRedisKey(((ServletServerHttpRequest) request).getServletRequest());
            //获取Cache注解
            Cache cache = returnType.getMethodAnnotation(Cache.class);
            //存入redis，缓存的时间单位是秒
            this.redisTemplate.opsForValue().set(redisKey, redisValue, Long.valueOf(cache.time()), TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("全局缓存拦截器异常：{}",e);
            e.printStackTrace();
        }
        return body;
    }
}