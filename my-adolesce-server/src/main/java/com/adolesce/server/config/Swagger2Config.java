package com.adolesce.server.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
//开启Knife4j
@EnableKnife4j
//开启swagger登录校验
@ConditionalOnExpression("${knife4j.enable}")
public class Swagger2Config {
    @Bean
    public Docket buildDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(buildApiInfo())
                .groupName("1.0")
                .select()  //选择那些路径和api会生成document
                //.apis(RequestHandlerSelectors.any())  //对所有api进行监控
                .apis(RequestHandlerSelectors.basePackage("com.adolesce.server.controller")) //对某个包下的api进行扫描
                /*.paths(Predicates.or(
                        PathSelectors.regex("/my_adolesce/es/.*"),
                        PathSelectors.regex("/my_adolesce/excel/.*")
                ))*/  //对某些特定路径的api进行监控并生成api文档
                .paths(PathSelectors.any())//// 对所有路径进行监控并生成api文档
                .build();
    }

    private ApiInfo buildApiInfo() {
        return new ApiInfoBuilder()
                .title("我的青春")
                .version("1.0")
                .description("Swagger使用演示接口文档")
                .termsOfServiceUrl("http://blog.csdn.net/myservice网址链接")
                .contact(new Contact("lwd", "http://blog.csdn.net", "liuweidong0008@163.com"))
                .build();
    }
}