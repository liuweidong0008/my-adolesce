package com.adolesce.cloud.db;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

/**
 * 启动类
 */
@SpringBootApplication(exclude = {MongoDataAutoConfiguration.class, MongoAutoConfiguration.class})
//1、扫描mybatis mapper接口
@MapperScan("com.adolesce.cloud.db.mapper")

//2、在Springboot应用开发中使用JPA时，通常在主应用程序所在包或者其子包的某个位置定义我们的Entity和Repository，这样基于Springboot的自动配置，无需额外配置，我们定义的Entity和Repository即可被发现和使用。但有时候我们需要定义Entity和Repository不在应用程序所在包及其子包，那么这时候就需要使用@EntityScan和@EnableJpaRepositories了。
//2.1、@EntityScan用来使用JPA时扫描和发现指定包及其子包中的Entity定义。如果多处使用@EntityScan，它们的basePackages集合能覆盖所有被Repository使用的Entity即可，集合有交集也没有关系。但是如果不能覆盖被Repository使用的Entity，应用程序启动是会出错,比如：Not a managed type: com.customer.entities.Customer
//2.2、@EnableJpaRepositories用来扫描和发现指定包及其子包中的Repository定义。如果多处使用@EnableJpaRepositories，它们的basePackages集合不能有交集，并且要能覆盖所有需要的Repository定义。如果有交集，相应的Repository会被尝试反复注册，从而遇到如下错误:The bean ‘OrderRepository’, defined in xxx, could not be registered. A bean with that name has already been defined in xxx and overriding is disabled。而如果不能覆盖所有需要的Repository定义，会遇到启动错误：Parameter 0 of method setCustomerRepository in com.service.CustomerService required a bean of type ‘come.repo.OrderRepository’ that could not be found.
@EntityScan("com.adolesce.cloud.dubbo.domain")

public class CloudDubboDBApplication {
    public static void main(String[] args) {
        SpringApplication.run(CloudDubboDBApplication.class, args);
    }
}