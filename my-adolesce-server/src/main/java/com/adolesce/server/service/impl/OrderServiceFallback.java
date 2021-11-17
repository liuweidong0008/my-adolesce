package com.adolesce.server.service.impl;

import com.adolesce.common.entity.Goods;
import com.adolesce.server.service.OrderService;
import org.springframework.stereotype.Component;

/**
 * 什么时候降级：
 * 1. 出现异常
 * 2. 服务调用超时（默认1s超时）
 * <p>
 * 服务提供方降级引入步骤：
 * 1、首先 feign 组件已经集成了 hystrix 组件，客户端服务降级基于feign。
 * 2、定义类去实现feign的调用接口，复写调用方法，即降级方法
 * 3、在 接口中的@FeignClient 注解中使用 fallback 属性设置降级处理类。
 * 4、配置文件中添加配置开启客户端降级
 * feign.hystrix.enabled = true
 * <p>
 * Feign 客户端的降级处理类
 * 1. 定义类 实现 Feign 客户端接口
 * 2. 使用@Component注解将该类的Bean加入SpringIOC容器
 */
@Component
public class OrderServiceFallback implements OrderService {
    @Override
    public Goods findGoodsById(int id) {
        Goods goods = new Goods();
        goods.setTitle("通过客户端降级了~~~");
        return goods;
    }
}
