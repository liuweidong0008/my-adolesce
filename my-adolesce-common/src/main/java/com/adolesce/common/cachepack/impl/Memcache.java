package com.adolesce.common.cachepack.impl;


import com.adolesce.common.cachepack.CachePack;

import java.time.Duration;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2021/9/12 16:34
 */
public class Memcache implements CachePack {
    @Override
    public void writeCacheData(String redisKey, String redisValue, Duration duration) {
        System.err.println("向memcache中添加缓存数据");

    }

    @Override
    public String getCacheData(String redisKey) {
        System.err.println("从memcache中获取缓存数据");
        return null;
    }
}
