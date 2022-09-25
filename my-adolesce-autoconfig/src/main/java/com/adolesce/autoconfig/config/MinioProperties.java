package com.adolesce.autoconfig.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

@Data
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
    private String accessKey;
    private String secretKey;
    private String endpoint;

    @Bean
    public MinioClient minioClient(){
        MinioClient minioClient = MinioClient
                .builder()
                .credentials(accessKey, secretKey)
                .endpoint(endpoint)
                .build();

        return minioClient;
    }
}