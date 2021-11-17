package com.adolesce.common.cachepack.impl;

import com.adolesce.common.cachepack.CachePack;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2021/9/12 16:33
 */
@NoArgsConstructor
@AllArgsConstructor
public class RedisCache implements CachePack {
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Override
    public void writeCacheData(String redisKey, String redisValue, Duration duration) {
        redisTemplate.opsForValue().set(redisKey,redisValue,duration);
    }

    @Override
    public String getCacheData(String redisKey) {
        return this.redisTemplate.opsForValue().get(redisKey);
    }
}
