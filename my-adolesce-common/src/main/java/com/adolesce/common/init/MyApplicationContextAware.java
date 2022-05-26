package com.adolesce.common.init;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2022/1/23 22:26
 */
@Component
public class MyApplicationContextAware implements ApplicationContextAware {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;//have

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.err.println("3、通过ApplicationContextAware初始化执行....");
        RedisTemplate redisTemplate = (RedisTemplate<String,String>) applicationContext.getBean(StringRedisTemplate.class);
        redisTemplate.opsForValue().get("name");
    }
}
