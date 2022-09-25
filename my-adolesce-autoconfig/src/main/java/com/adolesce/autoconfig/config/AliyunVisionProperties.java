package com.adolesce.autoconfig.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("aliyun.content-security.vision")
public class AliyunVisionProperties {
    /**
     * 账号
     */
    private String accessKeyId;
    /**
     * 密钥
     */
    private String accessKeySecret;
    private String endpoint;
    private String imgScenes;
    private String txtLabels;
}