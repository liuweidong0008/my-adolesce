package com.adolesce.server.controller;


import cn.hutool.core.collection.CollectionUtil;
import com.adolesce.common.entity.Goods;
import com.adolesce.server.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
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
    @Resource
    private OrderService orderService;

    @GetMapping("/goods/{id}")
    public Goods findGoodsById(@PathVariable("id") int id) {
        System.out.println("findGoodsById..." + id);
        //远程调用Goods服务中的findOne接口
        /*使用RestTemplate
        1. 定义Bean  restTemplate
        2. 注入Bean
        3. 调用方法*/

        /*动态从Eureka Server 中获取 provider 的 ip 和端口
        1. 注入 DiscoveryClient 对象.激活
        2. 调用方法*/

        //演示discoveryClient 使用（启动类上标注@EnableDiscoveryClient注解）
        //List<ServiceInstance> instances = discoveryClient.getInstances("EUREKA-PROVIDER"); //Application name
        //List<ServiceInstance> instances = discoveryClient.getInstances("CONSUL-PROVIDER"); //Application name
        List<ServiceInstance> instances = discoveryClient.getInstances("nacos-provider"); //Application name
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

        //String url = "http://"+host+":"+port+"/eureka-provider/goods/findOne/"+id;
        //String url = "http://"+host+":"+port+"/consul-provider/goods/findOne/"+id;
        String url = "http://" + host + ":" + port + "/nacos-provider/goods/findOne/" + id;
        // 3. 调用方法
        Goods goods = restTemplate.getForObject(url, Goods.class);

        return goods;
    }

    /**
     * NetFLix Ribbon：基于客户端的负载均衡器
     * 作用：
     * 1、简化服务调用
     * 2、实现负载均衡
     * <p>
     * 使用 Ribbon简化RestTemplate调用
     * 1、在声明RestTemplate Bean的时候，添加一个注解：@LoadBalance
     * 2、在使用RestTemplate发起请求时，需要定义url时，host:port 可以替换成服务提供方的应用名称
     * 3、开启@LoadBalance后，就不能以discoveryClient.getInstances去获取实例了，restTemplate调用时会报找不到实例
     * <p>
     * Ribbon负载均衡策略一共七种：
     * 随机：RandomRule                   随机调用节点
     * 轮询：RoundRobinRule               依次调用每个节点（默认）
     * 最小并发：BestAvailableRule         选取并发量最小，最闲的节点
     * 过滤：AvailabilityFilteringRule    过滤掉故障节点、并发高的节点
     * 响应时间：WeightedResponseTimeRule  选取对发送的小数据包响应最快的节点
     * 轮询重试：RetryRule                 轮询默认为十次，超过十次进行再次的重试轮询
     * 性能可用性：ZoneAvoidanceRule        综合节点的性能、可用性进行综合评估调用
     * <p>
     * 负载均衡策略设置有以下两种方式：
     * 1、通过编码方式：主启动类上@RibbonClient注解
     * 2、通过配置文件的方式
     *
     * @param id
     * @return
     */
    @GetMapping("/goods2/{id}")
    public Goods findGoodsById2(@PathVariable("id") int id) {
        //String url = "http://EUREKA-PROVIDER/eureka-provider/goods/findOne/"+id;
        //String url = "http://CONSUL-PROVIDER/goods/findOne/"+id;        //consul 整合rabbion进行调用时加上自定义的项目路径会报错,目前只能用根路径（待解决）
        String url = "http://nacos-provider/nacos-provider/goods/findOne/" + id;
        // 3. 调用方法
        Goods goods = restTemplate.getForObject(url, Goods.class);
        return goods;
    }

    /**
     * 通过Feign声明式rest客户端进行调用
     * 1、Feign底层依赖Ribbon实现负载均衡和远程调用
     * 2、使用feign，Ribbon的一切功能都能使用
     *
     * @param id
     * @return
     */
    @GetMapping("/goods3/{id}")
    public Goods findGoodsById3(@PathVariable("id") int id) {
        RestTemplateBuilder builder = new RestTemplateBuilder();
        builder.build();
        HashMap hashMap = new HashMap();
        return orderService.findGoodsById(id);
    }
}
