/*******************************************************************************
 * @(#)SnowFlakeIdUtils.java 2019年09月20日 10:35
 * Copyright 2019 明医众禾科技（北京）有限责任公司. All rights reserved.
 *******************************************************************************/
package com.adolesce.common.utils.ids;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

/**
 * <b>Application name：</b> SnowFlakeIdUtils.java <br>
 * <b>Application describing： </b> <br>
 * <b>Copyright：</b> Copyright &copy; 2019 明医众禾科技（北京）有限责任公司 版权所有。<br>
 * <b>Company：</b> 明医众禾科技（北京）有限责任公司 <br>
 * <b>@Date：</b> 2019年09月20日 10:35 <br>
 * <b>@author：</b> <a href="mailto:lwd@miyzh.com"> liuWeidong </a> <br>
 * <b>@version：</b>V2.0.0 <br>
 */
public class SnowFlakeIdUtils {
    private static Logger log = LoggerFactory.getLogger(BeanUtils.class);
    private static SnowFlakeIdUtils snowFlakeIdUtils = new SnowFlakeIdUtils(System.nanoTime() % 31L, System.nanoTime() % 30L);
    private long workerId = 100L;
    private long dataCenterId = 100L;
    private long startTime = 1288834974657L;
    private long workerIdBits = 5L;
    private long dataCenterIdBits = 5L;
    private long maxWorkerId;
    private long maxDataCenterId;
    private long sequenceBits;
    private long workerIdLeftShift;
    private long dataCenterIdLeftShift;
    private long timestampLeftShift;
    private long sequenceMask;
    private long sequence;
    private long lastTimestamp;

    private SnowFlakeIdUtils(long workerId, long dataCenterId) {
        this.maxWorkerId = ~(-1L << (int) this.workerIdBits);
        this.maxDataCenterId = ~(-1L << (int) this.dataCenterIdBits);
        this.sequenceBits = 12L;
        this.workerIdLeftShift = this.sequenceBits;
        this.dataCenterIdLeftShift = this.sequenceBits + this.workerIdBits;
        this.timestampLeftShift = this.sequenceBits + this.workerIdBits + this.dataCenterIdBits;
        this.sequenceMask = ~(-1L << (int) this.sequenceBits);
        this.sequence = 0L;
        this.lastTimestamp = -1L;
        log.info("实例化SnowFlakeId实例，workerId={} dataCenterId={}", workerId, dataCenterId);
        if (workerId <= this.maxWorkerId && workerId >= 0L) {
            if (dataCenterId <= this.maxDataCenterId && dataCenterId >= 0L) {
                this.workerId = workerId;
                this.dataCenterId = dataCenterId;
            } else {
                throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", this.maxDataCenterId));
            }
        } else {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", this.maxWorkerId));
        }
    }

    public synchronized long nextId() {
        long timestamp = this.timeGen();
        if (timestamp < this.lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", this.lastTimestamp - timestamp));
        } else {
            if (this.lastTimestamp == timestamp) {
                this.sequence = this.sequence + 1L & this.sequenceMask;
                if (this.sequence == 0L) {
                    timestamp = this.tilNextMillis(this.lastTimestamp);
                }
            } else {
                this.sequence = 0L;
            }

            this.lastTimestamp = timestamp;
            return timestamp - this.startTime << (int) this.timestampLeftShift | this.dataCenterId << (int) this.dataCenterIdLeftShift | this.workerId << (int) this.workerIdLeftShift | this.sequence;
        }
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp;
        for (timestamp = this.timeGen(); timestamp <= lastTimestamp; timestamp = this.timeGen()) {
            ;
        }

        return timestamp;
    }

    protected long timeGen() {
        return System.currentTimeMillis();
    }

    public static long getSnowFlakeId() {
        return snowFlakeIdUtils.nextId();
    }
}