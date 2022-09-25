package com.adolesce.consul.consumer.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.adolesce.consul.consumer.domain.Goods;
import com.adolesce.consul.consumer.domain.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * 服务的调用方
 */
@RestController
@RequestMapping("/order")
@RefreshScope
public class OrderController {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private DiscoveryClient discoveryClient;

    @Value("${consul.myconfig}")
    private String consulConfig;

    @GetMapping("/printConfig")
    public String printConfig(){
        return consulConfig;
    }

    @GetMapping("/{id}")
    public Order findOrderById(@PathVariable("id") int id) {
        System.out.println("findOrderById..." + id);
        Order order = new Order();
        order.setOrderNo("10001L");
        order.setOrderName("小明的订单");

        //远程调用Goods服务中的findOne接口
        String url  = "http://localhost:4000/goods/findOne/" + id;
        Goods goods = restTemplate.getForObject(url, Goods.class);

        //动态从Eureka Server 中获取 provider 的 ip 和端口
            //1. 注入 DiscoveryClient 对象.激活
            //2. 调用方法
        List<ServiceInstance> instances = discoveryClient.getInstances("CONSUL-PROVIDER");
        //判断集合是否有数据
        if (CollectionUtil.isEmpty(instances)) {
            //集合没有数据
            return null;
        }

        ServiceInstance instance = instances.get(0);
        String host = instance.getHost();
        int port = instance.getPort();
        System.out.println("IP 端口：" + host + ":" +port);

        url = "http://"+host+":"+port+"/goods/findOne/" + id;
        goods = restTemplate.getForObject(url, Goods.class);
        order.setGoods(goods);
        return order;
    }

    /**
     * 二、利用ribbon进行负载均衡调用
     *      1、在RestTemplat上标注@LoadBalanced注解
     *      2、将url上的ip、端口换成服务名
     */
    @GetMapping("/ribbon/{id}")
    public Order findOrderById2(@PathVariable("id") int id) {
        System.out.println("findOrderById2..." + id);
        Order order = new Order();
        order.setOrderNo("10002L");
        order.setOrderName("小本的订单");
        // 1.定义url
        String url = "http://consul-provider/goods/findOne/" + id;
        // 2. 调用方法
        Goods goods = restTemplate.getForObject(url, Goods.class);
        order.setGoods(goods);
        return order;
    }

}
