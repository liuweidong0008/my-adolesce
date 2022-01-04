package com.adolesce.consul.consumer.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.adolesce.consul.consumer.domain.Goods;
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

    @GetMapping("/goods/{id}")
    public Goods findGoodsById(@PathVariable("id") int id) {
        System.out.println("findGoodsById..." + id);
        //远程调用Goods服务中的findOne接口
        String url  = "http://localhost:8000/consul-provider/goods/findOne/1";
        Goods goods = restTemplate.getForObject(url, Goods.class);

        /*动态从Eureka Server 中获取 provider 的 ip 和端口
        1. 注入 DiscoveryClient 对象.激活
        2. 调用方法*/

        //演示discoveryClient 使用（启动类上标注@EnableDiscoveryClient注解）
        List<ServiceInstance> instances = discoveryClient.getInstances("CONSUL-PROVIDER"); //Application name
        //判断集合是否有数据
        if (CollectionUtil.isEmpty(instances)) {
            //集合没有数据
            return null;
        }

        ServiceInstance instance = instances.get(0);
        String host = instance.getHost();//获取ip
        int port = instance.getPort();  //获取端口
        System.out.println(host);
        System.out.println(port);

        url = "http://"+host+":"+port+"/consul-provider/goods/findOne/"+id;
        // 3. 调用方法
        goods = restTemplate.getForObject(url, Goods.class);

        return goods;
    }
}
