package com.adolesce.common.intercepter;

import cn.hutool.crypto.SecureUtil;
import com.adolesce.common.annotation.Cache;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 全局缓存拦截器
 */
@Component
public class GlobalCacheInterceptor implements HandlerInterceptor {
    @Value("${cache.enable}")
    private Boolean enable;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //如果缓存的全局开关没开,放行
        if (!enable) {
            return true;
        }
        //如果不是HandlerMethod,放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        //如果是非GET请求,放行
        if (!((HandlerMethod) handler).hasMethodAnnotation(GetMapping.class)) {
            return true;
        }
        //如果请求方法未被@Cache标注,放行
        if (!((HandlerMethod) handler).hasMethodAnnotation(Cache.class)) {
            return true;
        }
        //缓存命中
        String redisKey = createRedisKey(request);
        String cacheData = this.redisTemplate.opsForValue().get(redisKey);
        //缓存未命中,放行
        if(ObjectUtils.isEmpty(cacheData)){
            return true;
        }
        //缓存命中,返回数据，截断请求
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write(cacheData);
        return false;
    }

    /**
     * 生成redis中的key，规则：SERVER_CACHE_DATA_MD5(url + param + token)
     *
     * @param request
     * @return
     */
    public static String createRedisKey(HttpServletRequest request) throws Exception {
        //请求url
        String url = request.getRequestURI();
        //请求参数
        String param = JSON.toJSONString(request.getParameterMap());
        //请求Token
        String token = request.getHeader("Authorization");

        String data = url + "_" + param + "_" + token;
        String keyLastFix = SecureUtil.md5(data);

        return "SERVER_CACHE_DATA_" + keyLastFix;
    }
}