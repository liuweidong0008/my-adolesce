package com.adolesce.common.aop;

import cn.hutool.crypto.SecureUtil;
import com.adolesce.common.annotation.Cache;
import com.adolesce.common.vo.Response;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * 全局缓存环绕通知
 */
//@Component
//@Aspect
@Slf4j
public class GlobalCacheAopAround {
	@Value("${cache.enable}")
	private Boolean enable;
	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	//指定切入点
	@Pointcut("@annotation(com.adolesce.common.annotation.Cache) && " +
			"@annotation(org.springframework.web.bind.annotation.GetMapping)")
	public void doAspect(){}

	//环绕通知
	@Around("doAspect() && @annotation(cache)")
	public Object around(ProceedingJoinPoint joinPoint, Cache cache){
		//另一种获取注解方式
		/*MethodSignature methodSignature =(MethodSignature) joinPoint.getSignature();
		Cache cache = methodSignature.getMethod().getDeclaredAnnotation(Cache.class);*/

		//目标方法参数
		Object[] args = joinPoint.getArgs();
		//目标方法返回值
		Object object = null;
		//缓存Key
		String redisKey;
		try {
			//缓存全局开关如果为关闭状态则直接放行
			if (!enable) {
				object = joinPoint.proceed(args);
				redisKey = createRedisKey();
			}else{
				redisKey = createRedisKey();
				String cacheData = this.redisTemplate.opsForValue().get(redisKey);
				if(ObjectUtils.isEmpty(cacheData)){
					//缓存未命中，放行
					object = joinPoint.proceed(args);
				}else{
					//缓存命中，返回数据，截断请求
					return JSON.parseObject(cacheData, Response.class);
				}
			}
			//以上代码为目标方法返回前增强
			//------------------------------------------------------------------------------------------------
			//以下代码为目标方法返回后增强
			//返回结果为空或者缓存开关为关闭状态直接返回数据
			if(null == object || !enable){
				return object;
			}
			String redisValue;
			if (object instanceof String) {
				redisValue = (String) object;
			} else {
				redisValue = JSON.toJSONString(object);
			}
			//缓存的时间单位是秒
			this.redisTemplate.opsForValue().set(redisKey, redisValue, Long.valueOf(cache.time()), TimeUnit.SECONDS);
		} catch (Throwable e) {
			log.error("全局缓存AOP异常：{}",e);
			e.printStackTrace();
		}
		return object;
	}

	/**
	 * 生成redis中的key，规则：SERVER_CACHE_DATA_MD5(url + param + token)
	 * @return
	 */
	public static String createRedisKey(){
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
}