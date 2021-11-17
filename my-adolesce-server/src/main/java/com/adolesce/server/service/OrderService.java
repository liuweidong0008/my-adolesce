package com.adolesce.server.service;

import com.adolesce.common.entity.Goods;
import com.adolesce.server.config.FeignLogConfig;
import com.adolesce.server.service.impl.OrderServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Administrator
 * @version 1.0
 * @description: Feign声明式接口
 * Feign 为声明式的Rest客户端，可以大大简化远程调用
 * @date 2021/7/14 13:13
 * <p>
 * String url = "http://EUREKA-PROVIDER/goods/findOne/"+id;
 * Goods goods = restTemplate.getForObject(url, Goods.class);
 * <p>
 * 步骤
 * pom引入Feign依赖
 * 主启动类上打上@EnableFeignClients注解，开启Feign功能
 * POM引入Feign依赖
 * 编写代码：
 * 1. 定义接口
 * 2. 接口上添加注解 @FeignClient,设置value属性为 服务提供者的 应用名称
 * 3. 编写调用接口，接口的声明规则 和 提供方接口保持一致(返回值和方法名可以不一致)。
 * 4. 使用类上注入该接口对象，调用接口方法完成远程调用
 * <p>
 * configuration用来注入配置，当前在配置Feign日志打印策略，如果configuration不指定，会将FeignLogCnfig中定义好的策略应用至所有服务
 * fallback属性用于指定服务的降级处理类
 */
@FeignClient(value = "EUREKA-PROVIDER", configuration = FeignLogConfig.class, fallback = OrderServiceFallback.class)
public interface OrderService {
    @GetMapping("eureka-provider/goods/findOne/{id}")
    public Goods findGoodsById(@PathVariable("id") int id);
}
