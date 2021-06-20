package com.adolesce.server.rocketmq.order.domain;

import lombok.Data;

@Data
public class RocketConfig {
    public static String NAMESRVADDR = "192.168.31.82:9876";
    public static String GROUP1 = "group1";
    public static String GROUP2 = "group2";
}
