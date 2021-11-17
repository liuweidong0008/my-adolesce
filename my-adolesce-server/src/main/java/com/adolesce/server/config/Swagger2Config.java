package com.adolesce.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Profile({"dev", "debug",  "test"})
public class Swagger2Config {
    private ApiInfo buildApiInfo() {
        return new ApiInfoBuilder()
                .title("API接口文档")
                .description("Swagger使用演示")
                .termsOfServiceUrl("http://blog.csdn.net/myservice网址链接")
                .contact(new Contact("lwd", "http://blog.csdn.net", "liuweidong0008@163.com"))
                .version("1.0")
                .build();
    }

    @Bean
    public Docket buildDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(buildApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.adolesce.server.controller")) //controller类的包路径
                .paths(PathSelectors.any())
                .build();
    }
}