package com.adolesce.nacos.consumer.config;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.context.annotation.Bean;

/**
 * 负载均衡策略配置类（除了这种配置方式，还可通过配置文件指定Ribbon负载均衡策略）
 * 1、该种配置Ribbon负载均衡方式还需在启动类上指定需要对哪个服务进行负载均衡策略配置（指定服务的情况下，不能将该类放入容器，否则对所有服务生效）
 *          @RibbonClient(name = "EUREKA-PROVIDER",configuration = RibbonRuleConfig.class)
 * 2、如果启动类没有通过@RibbonClient注解配置负载均衡策略（默认相当于打了没有指定属性的注解），会将当前config类注册到spring容器，会将该策略应用至所有服务
 */
//@Configuration
public class RibbonRuleConfig {
    @Bean
    public IRule rule() {
        return new RandomRule();
    }
}
