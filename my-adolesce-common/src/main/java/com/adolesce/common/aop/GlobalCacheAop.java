package com.adolesce.common.aop;

import cn.hutool.crypto.SecureUtil;
import com.adolesce.common.annotation.Cache;
import com.adolesce.common.exception.BusinessException;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * 全局缓存前置 + 返回后通知
 */
//@Component
//@Aspect
@Slf4j
public class GlobalCacheAop {
    @Value("${cache.enable}")
    private Boolean enable;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    //指定切入点
    @Pointcut("@annotation(com.adolesce.common.annotation.Cache) && " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void doAspect() {}

    //前置通知
    @Before("doAspect()")
    public void doBefore(JoinPoint point) throws Throwable {
        //缓存的全局开关的校验，如果关闭直接放行
        if (!enable) {
            return;
        }
        //缓存命中
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String redisKey = createRedisKey(request);
        String cacheData = this.redisTemplate.opsForValue().get(redisKey);
        if (StringUtils.isEmpty(cacheData)) {
            //缓存未命中，直接放行
            return;
        }
        //将数据放入request
        request.setAttribute("resultData", JSON.parse(cacheData));
        throw new BusinessException();
    }

    //返回后通知
    @AfterReturning(pointcut = "doAspect() && @annotation(cache)", returning = "responseData")
    public void doAfterReturning(JoinPoint point, Cache cache, Object responseData) {
        //缓存的全局开关的校验
        if (!enable || null == responseData) {
            return;
        }
        try {
            String redisValue;
            if (responseData instanceof String) {
                redisValue = (String) responseData;
            } else {
                redisValue = MAPPER.writeValueAsString(responseData);
            }
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = servletRequestAttributes.getRequest();
            String redisKey = createRedisKey(request);

            //缓存的时间单位是秒
            this.redisTemplate.opsForValue().set(redisKey, redisValue, Long.valueOf(cache.time()), TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成redis中的key，规则：SERVER_CACHE_DATA_MD5(url + param + token)
     *
     * @return
     */
    public static String createRedisKey(HttpServletRequest request) throws Exception {
        String url = request.getRequestURI();
        String param = MAPPER.writeValueAsString(request.getParameterMap());
        String token = request.getHeader("Authorization");
        String data = url + "_" + param + "_" + token;
        String keyLastFix = SecureUtil.md5(data);
        return "SERVER_CACHE_DATA_" + keyLastFix;
    }
}