package com.adolesce.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 互亿无线 短信发送配置
 */
@Configuration
@PropertySource("classpath:sms.properties")
@ConfigurationProperties(prefix = "sms.huyi")
@Data
public class HuyiSMSConfig {
    private String url;
    private String account;
    private String password;
}