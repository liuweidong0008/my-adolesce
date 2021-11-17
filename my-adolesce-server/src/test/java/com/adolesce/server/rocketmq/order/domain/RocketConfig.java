package com.adolesce.server.rocketmq.order.domain;

import lombok.Data;

@Data
public class RocketConfig {
    public static String NAMESRVADDR = "127.0.0.1:9876";
    public static String GROUP1 = "group1";
    public static String GROUP2 = "group2";
}
