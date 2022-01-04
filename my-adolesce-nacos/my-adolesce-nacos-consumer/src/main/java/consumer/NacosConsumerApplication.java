package consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient //激活DiscoveryClient 该注解在新版本可省略，但是不建议省略
//@RibbonClient(name = "EUREKA-PROVIDER", configuration = RibbonRuleConfig.class) //为指定的服务设置指定的负载均衡策略，如果需要给所有服务指定该策略，直接不打这个注解就行，然后将RibbonRuleConfig配置放入spring容器即可,name为服务提供者的应用名称，当打上了这个注解，必须配置name或value属性，也就是为某个服务指定
public class NacosConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(NacosConsumerApplication.class, args);
    }
}
