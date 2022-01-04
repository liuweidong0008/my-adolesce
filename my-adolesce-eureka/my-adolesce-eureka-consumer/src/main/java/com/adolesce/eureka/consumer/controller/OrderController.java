package com.adolesce.eureka.consumer.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.adolesce.eureka.consumer.domain.Goods;
import com.adolesce.eureka.consumer.service.GoodsFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
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
public class OrderController {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private GoodsFeignClient goodsFeignClient;

    /**
     * 一、DiscoveryClient结合RestTemplate发起远程调用
     * 1、利用DiscoveryClient动态获取服务实例的IP和端口
     * 2、利用restTemplate远程调用Goods服务中的findOne接口
     */
    @GetMapping("/goods/{id}")
    public Goods findGoodsById(@PathVariable("id") int id) {
        System.out.println("findGoodsById..." + id);
        String url  = "http://localhost:8001/eureka-provider/goods/findOne/1";
        //Goods goods = restTemplate.getForObject(url, Goods.class);

        //演示discoveryClient使用（启动类上标注@EnableDiscoveryClient注解）
        List<ServiceInstance> instances = discoveryClient.getInstances("eureka-provider");
        //判断集合是否有数据
        if (CollectionUtil.isEmpty(instances)) {
            //集合没有数据
            return null;
        }

        ServiceInstance instance = instances.get(0);
        //获取ip
        String host = instance.getHost();
        //获取端口
        int port = instance.getPort();
        System.out.println(host);
        System.out.println(port);

        url = "http://"+host+":"+port+"/eureka-provider/goods/findOne/"+id;
        // 3. 调用方法
        Goods goods = restTemplate.getForObject(url, Goods.class);
        return goods;
    }

    /**
     * 二、利用ribbon进行负载均衡调用
     * 1、在RestTemplat上标注@LoadBalanced注解
     * 2、将url上的ip、端口换成服务名
     */
    @GetMapping("/goods/ribbon/{id}")
    public Goods queryGoodsById(@PathVariable("id") int id) {
        // 1.定义url
        String url = "http://EUREKA-PROVIDER/eureka-provider/goods/findOne/" + id;
        // 2. 调用方法
        Goods goods = restTemplate.getForObject(url, Goods.class);
        return goods;
    }

    /**
     * 三、通过Feign声明式rest客户端进行调用
     * 1、引入依赖
     * 2、主启动类添加@EnableFeignClients注解
     * 3、编写FeignClient接口
     * 4、使用FeignClient中定义的方法代替RestTemplate
     * 5、Feign底层依赖Ribbon实现负载均衡和远程调用，使用feign，Ribbon的一切功能都能使用
     */
    @GetMapping("/goods/feign/{id}")
    public Goods findGoodsById3(@PathVariable("id") int id) {
        return goodsFeignClient.findGoodsById(id);
    }
}
