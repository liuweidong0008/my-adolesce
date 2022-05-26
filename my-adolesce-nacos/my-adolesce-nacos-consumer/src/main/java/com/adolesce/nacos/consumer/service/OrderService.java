package com.adolesce.nacos.consumer.service;

import com.adolesce.nacos.feign.clients.GoodsFeignClient;
import com.adolesce.nacos.feign.domain.Goods;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2022/1/12 21:07
 */
@Service
public class OrderService {
    @Autowired
    private GoodsFeignClient goodsFeignClient;

    public Goods findGoodsById(int id) {
        return goodsFeignClient.findGoodsById(id);
    }

    /**
     * 1、sentinel默认只标记controller中的方法为资源，如果要标记其他方法，需要利用@SentinelResource注解
     * 2、sentinel默认会将controller的方法做context整合，导致链路模式的流控失效，需要修改application.yml配置，添加：
     *      spring:
     *        cloud:
     *          sentinel:
     *            web-context-unify: false # 关闭context整合
     */
    @SentinelResource("goods")
    public void queryGoods() {
        System.err.println("查询商品...");
    }
}
