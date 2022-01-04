package com.adolesce.consul.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient //激活DiscoveryClient 该注解在新版本可省略，但是不建议省略
public class ConsulConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsulConsumerApplication.class, args);
    }
}
