package com.adolesce.server;

import com.adolesce.server.config.RibbonRuleConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 启动类
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.adolesce.common","com.adolesce.server"})
@EnableDiscoveryClient // 激活DiscoveryClient 该注解在新版本可省略，但是不建议省略
//@EnableEurekaClient  //Eureka 客户端,该注解在新版本可省略，但是不建议省略
@RibbonClient(name = "nacos-provider", configuration = RibbonRuleConfig.class) //为指定的服务设置指定的负载均衡策略，如果需要给所有服务指定该策略，直接不打这个注解就行，然后将RibbonRuleConfig配置放入spring容器即可,name为服务提供者的应用名称，当打上了这个注解，必须配置name或value属性，也就是为某个服务指定
@EnableFeignClients//开启Feign远程调用 (defaultConfiguration = FeignLogConfig.class)  配置Feign全局的日志
//@EnableHystrixDashboard // 开启Hystrix 图形化监控
@EnableCaching //开启SpringCache
public class ServerApplication {
    public static void main(String[] args) {
        //SpringApplication springApplication = new SpringApplication(ServerApplication.class);
        //springApplication.addInitializers(new MyApplicationContextInitializer());
        //springApplication.run(args);

        SpringApplication.run(ServerApplication.class, args);
    }

    /*@Bean
    public ServletRegistrationBean getServlet() {
        HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(streamServlet);
        registrationBean.setLoadOnStartup(1);
        registrationBean.addUrlMappings("/actuator/hystrix.stream");
        registrationBean.setName("HystrixMetricsStreamServlet");
        return registrationBean;
    }*/

}