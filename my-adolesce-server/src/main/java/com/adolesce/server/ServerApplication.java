package com.adolesce.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * 启动类
 */
@SpringBootApplication
@MapperScan("com.adolesce.common.mapper")
@ComponentScan(basePackages = "com.adolesce")
@EnableDiscoveryClient // 激活DiscoveryClient 该注解在新版本可省略，但是不建议省略
//@EnableEurekaClient  //该注解在新版本可省略，但是不建议省略
//@RibbonClient(name = "EUREKA-PROVIDER",configuration = RibbonRuleConfig.class)  //为指定的服务设置指定的负载均衡策略，name为服务提供者的应用名称
public class ServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }
}