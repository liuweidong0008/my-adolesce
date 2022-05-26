package com.adolesce.nacos.consumer;

import com.adolesce.nacos.consumer.config.RibbonRuleConfig;
import com.adolesce.nacos.feign.config.DefaultFeignConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient //激活DiscoveryClient 该注解在新版本可省略，但是不建议省略
@RibbonClient(name = "nacos-provider", configuration = RibbonRuleConfig.class) //为指定的服务设置指定的负载均衡策略，如果需要给所有服务指定该策略，直接不打这个注解就行，然后将RibbonRuleConfig配置放入spring容器即可,name为服务提供者的应用名称，当打上了这个注解，必须配置name或value属性，也就是为某个服务指定
@EnableFeignClients(basePackages = {"com.adolesce.nacos.feign.clients"},defaultConfiguration = DefaultFeignConfiguration.class)
/*clients = GoodsFeignClient.class*/
public class NacosConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(NacosConsumerApplication.class, args);
    }
}


/*ribbon负载均衡
    1、通过@RibbonClient指定某个微服务的负载均衡配置
    2、通过@Bean的方式注入IRule，实现全局配置
    3、通过配置文件的方式实现某个微服务的负载均衡配置

feign日志
    1、通过启动类@EnableFeignClients属性defaultConfiguration指定全局日志级别配置
    2、通过配置文件指定全局或者单个微服务日志级别
    3、通过在@FeignClient 上指定当前微服务的日志级别*/
