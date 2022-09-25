package com.adolesce.common.aop;

import cn.hutool.crypto.SecureUtil;
import com.adolesce.common.annotation.Cache;
import com.adolesce.common.cachepack.CachePack;
import com.adolesce.common.cachepack.impl.Memcache;
import com.adolesce.common.cachepack.impl.RedisCache;
import com.adolesce.common.vo.Response;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;

/**
 * 全局缓存环绕通知
 */
//@Component
//@Aspect
@Order(1)
@Slf4j
public class GlobalCacheAopAround {
    @Value("${cache.enable}")
    private Boolean enable;
    @Value("${cache.pack}")
    private String cachePackName;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    //指定切入点
    @Pointcut("@annotation(com.adolesce.common.annotation.Cache) && " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void doAspect() {
    }

    // 切入点为：com.adolesce.server.service.impl 包下的任何类上的任何方法，参数不限，另外将目标方法上的@Cache注解注入到参数中
    // @Around(value = "execution(* com.adolesce.server.service.impl.*.*(..)) && @annotation(cache)")
    //环绕通知
    @Around("doAspect() && @annotation(cache)")
    public Object around(ProceedingJoinPoint joinPoint, Cache cache) {
        //另一种获取注解方式
		/*MethodSignature methodSignature =(MethodSignature) joinPoint.getSignature();
		Cache cache = methodSignature.getMethod().getDeclaredAnnotation(Cache.class);*/

        //目标方法参数
        Object[] args = joinPoint.getArgs();
        //目标方法返回值
        Object object = null;
        //缓存Key
        String cacheKey = null;
        //缓存组件
        CachePack cachePack = null;
        try {
            //缓存全局开关如果为关闭状态则直接放行
            if (!enable) {
                object = joinPoint.proceed(args);
            } else {
                //构建缓存key
                //cacheKey = createCacheKey();
                cacheKey = getCacheKey(cache,joinPoint);

                //缓存命中
                //String cacheData = this.redisTemplate.opsForValue().get(cacheKey);
                if ("redis".equals(cachePackName)) {
                    cachePack = new RedisCache(redisTemplate);
                } else if ("memcache".equals(cachePackName)) {
                    cachePack = new Memcache();
                }
                String cacheData = cachePack.getCacheData(cacheKey);

                if (ObjectUtils.isEmpty(cacheData)) {
                    //缓存未命中，放行
                    object = joinPoint.proceed(args);
                } else {
                    //缓存命中，返回数据，截断请求
                    return JSON.parseObject(cacheData, Response.class);
                }
            }
            //以上代码为目标方法返回前增强
            //------------------------------------------------------------------------------------------------
            //以下代码为目标方法返回后增强
            //返回结果为空或者缓存开关为关闭状态直接返回数据
            if (null == object || !enable) {
                return object;
            }
            String cacheData;
            if (object instanceof String) {
                cacheData = (String) object;
            } else {
                cacheData = JSON.toJSONString(object);
            }
            //设置缓存，缓存的时间单位是秒
            //this.redisTemplate.opsForValue().set(cacheKey, cacheData, Long.valueOf(cache.time()), TimeUnit.SECONDS);
            cachePack.writeCacheData(cacheKey, cacheData, Duration.ofSeconds(Long.valueOf(cache.time())));
        } catch (Throwable e) {
            log.error("全局缓存AOP异常：{}", e);
            e.printStackTrace();
        }
        return object;
    }

    /*
     * 获取缓存 key
     * @param cache
     * @return
     * @throws Exception
     */
    public static String getCacheKey(Cache cache,ProceedingJoinPoint joinPoint) throws Exception {
        //解析SpringEL获取动态参数
        /*MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] expressionArr = cache.key().split(",");
        String userName = parse(expressionArr[0], signature.getParameterNames(), joinPoint.getArgs());
        String sex = parse(expressionArr[1], signature.getParameterNames(), joinPoint.getArgs());
        String age = parse(expressionArr[2], signature.getParameterNames(), joinPoint.getArgs());
        String ageIsLt = parse(expressionArr[4], signature.getParameterNames(), joinPoint.getArgs());
        String nameIsEquals = parse(expressionArr[5], signature.getParameterNames(), joinPoint.getArgs());*/

        //如果没有指定缓存的组和key，就自动生成缓存key
        if (StringUtils.isEmpty(cache.group()) &&
                StringUtils.isEmpty(cache.key())) {
            return createCacheKey();
        }

        StringBuffer cacheKeyBuffer = new StringBuffer();
        String cacheKey;
        boolean isHaveGroup = false;
        //如果指定了缓存组
        if(StringUtils.isNotEmpty(cache.group())){
            cacheKeyBuffer.append(cache.group());
            isHaveGroup = true;
        }
        //如果指定了缓存key
        if(StringUtils.isNotEmpty(cache.key())){
            //如果指定了缓存key
            if(isHaveGroup){
                cacheKeyBuffer.append("::");
            }
            //解析SpringEL获取动态参数
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String[] expressionArr = cache.key().split(",");
            for(String expression : expressionArr){
                String keyValue = parse(expression, signature.getParameterNames(), joinPoint.getArgs());
                if(StringUtils.isNotEmpty(keyValue)){
                    cacheKeyBuffer.append(keyValue).append("_");
                }
            }
        }
        cacheKey = cacheKeyBuffer.toString();
        if(cacheKey.endsWith("_")){
            cacheKey = cacheKey.substring(0,cacheKey.lastIndexOf("_"));
        }
        return cacheKey;
    }

    /**
     * 生成redis中的key，规则：SERVER_CACHE_DATA_MD5(url + param + token)
     *
     * @return
     */
    public static String createCacheKey() {
        //RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
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

    /**
     * Spring EL表达式解析
     *
     * @param expression  EL表达式
     * @param paramNames  目标方法列表参数 属性名集合
     * @param paramValues 目标方法列表参数 属性值结合
     * @return
     */
    private static String parse(String expression, String[] paramNames, Object[] paramValues) {
        if (StringUtils.isEmpty(expression)) {
            return "";
        }
        StandardEvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], paramValues[i]);
        }
        Expression exp = new SpelExpressionParser().parseExpression(expression);
        Object value = exp.getValue(context);
        return value == null ? "" : value.toString();
    }
}