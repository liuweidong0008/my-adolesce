package com.adolesce.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 亿美 短信发送 配置
 */
@Configuration
@PropertySource("classpath:sms.properties")
@ConfigurationProperties(prefix = "sms.yimei")
@Data
public class YiMeiSMSConfig {

    private String appId;
    private String secretKey;
    private String signName;
    private String singleUrl;
    private String batchUrl;

}