package com.adolesce.common.cachepack;

import java.time.Duration;

public interface CachePack {
    /**
     * 向缓存写入数据
     * @param redisKey
     * @param redisValue
     * @param duration 缓存时间
     */
    void writeCacheData(String redisKey, String redisValue, Duration duration);

    /**
     * 缓存获取数据
     * @param redisKey
     * @return
     */
    String getCacheData(String redisKey);
}
