package com.adolesce.common.config;

import lombok.Data;

/**
 * RocketMQConfig相关配置
 */
@Data
public class RocketConfig {
    public static String NAMESRVADDR = "127.0.0.1:9876";
    public static String GROUP1 = "group1";
    public static String GROUP2 = "group2";
}
