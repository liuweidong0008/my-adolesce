package com.adolesce.common.init;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MyApplicationListener implements ApplicationListener<ApplicationEvent> {
    @Autowired
    private StringRedisTemplate redisTemplate; //have

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            System.err.println("4、通过ApplicationListener初始化执行....启动会执行多次");
            ApplicationContext applicationContext = ((ContextRefreshedEvent) event).getApplicationContext();
            RedisTemplate redisTemplate = applicationContext.getBean(StringRedisTemplate.class);
            redisTemplate.opsForValue().get("name");
        }
    }
}
