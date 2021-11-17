package com.adolesce.cloud.dubbo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//https://maokun.blog.csdn.net/article/details/113764393
@SpringBootApplication/*(exclude = FeignAutoConfiguration.class)*/
public class CloudDubboConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudDubboConsumerApplication.class,args);
    }
}
