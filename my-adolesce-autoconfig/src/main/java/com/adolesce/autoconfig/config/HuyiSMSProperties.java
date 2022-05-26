package com.adolesce.autoconfig.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 互亿无线 短信发送配置
 */
//@Component
//@PropertySource("classpath:sms.properties")
@ConfigurationProperties(prefix = "sms.huyi")
@Data
public class HuyiSMSProperties {
    private String url;
    private String account;
    private String password;
}