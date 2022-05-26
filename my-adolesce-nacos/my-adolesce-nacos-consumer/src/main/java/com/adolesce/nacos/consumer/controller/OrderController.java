package com.adolesce.nacos.consumer.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.adolesce.nacos.consumer.service.OrderService;
import com.adolesce.nacos.feign.domain.Goods;
import com.adolesce.nacos.feign.domain.Order;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
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
    private OrderService orderService;

    /**
     * 一、DiscoveryClient结合RestTemplate发起远程调用
     *      1、利用DiscoveryClient动态获取服务实例的IP和端口
     *      2、利用restTemplate远程调用Goods服务中的findOne接口
     */
    @GetMapping("/{id}")
    public Order findOrderById(@PathVariable("id") int id) {
        System.out.println("findOrderById..." + id);
        Order order = new Order();
        order.setOrderNo("10001L");
        order.setOrderName("小明的订单");

        String url  = "http://localhost:7000/nacos-provider/goods/findOne/1";
        Goods goods = restTemplate.getForObject(url, Goods.class);

        //演示discoveryClient 使用（启动类上标注@EnableDiscoveryClient注解）
        List<ServiceInstance> instances = discoveryClient.getInstances("nacos-provider"); //Application name
        //判断集合是否有数据
        if (CollectionUtil.isEmpty(instances)) {
            //集合没有数据
            return null;
        }

        ServiceInstance instance = instances.get(0);
        String host = instance.getHost();
        int port = instance.getPort();
        System.out.println("IP 端口：" + host + ":" +port);

        url = "http://"+host+":"+port+"/nacos-provider/goods/findOne/"+id;
        // 3. 调用方法
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
        String url = "http://nacos-provider/nacos-provider/goods/findOne/" + id;
        // 2. 调用方法
        Goods goods = restTemplate.getForObject(url, Goods.class);
        order.setGoods(goods);
        return order;
    }

    /**
     * 三、通过Feign声明式rest客户端进行调用
     *      1、引入依赖
     *      2、主启动类添加@EnableFeignClients注解
     *      3、编写FeignClient接口
     *      4、使用FeignClient中定义的方法代替RestTemplate
     *      5、Feign底层依赖Ribbon实现负载均衡和远程调用，使用feign，Ribbon的一切功能都能使用
     */
    //注意事项：热点参数限流对默认的SpringMVC资源无效，需要利用@SentinelResource注解标记资源
    @SentinelResource("hot")
    @GetMapping("/feign/{id}")
    public Order findOrderById3(@PathVariable("id") int id) {
        System.out.println("findOrderById3..." + id);
        Order order = new Order();
        order.setOrderNo("10003L");
        order.setOrderName("小奋的订单");
        Goods goods = orderService.findGoodsById(id);
        order.setGoods(goods);
        return order;
    }

    /*流控模式有哪些 ?
        •直接：对当前资源限流
        •关联：高优先级资源触发阈值，对低优先级资源限流。
        •链路：阈值统计时，只统计从指定资源进入当前资源的请求，是对请求来源的限流*/

    /*流控效果?
        是指请求达到流控阈值时应该采取的措施，包括三种：
        - 快速失败：达到阈值后，新的请求会被立即拒绝并抛出FlowException异常。是默认的处理方式。
        - warm up：预热模式，对超出阈值的请求同样是拒绝并抛出异常。但这种模式阈值会动态变化，从一个较小值逐渐增加到最大阈值。
                  是应对服务冷启动的一种方案。请求阈值初始值是 maxThreshold / coldFactor，持续指定时长后，逐渐提高到maxThreshold值。而coldFactor的默认值是3.
        - 排队等待：让所有的请求按照先后次序排队执行，两个请求的间隔不能小于指定时长*/

    /**
     * 查询
     */
    @GetMapping("/query")
    public String query() {
        //查询订单同时需要查询商品
        orderService.queryGoods();
        return "查询订单成功";
    }

    /**
     * 新增
     * 备注：用于测试sentinel流控链路模式（从/query进入queryGoods()方法限制QPS为2  而从/save进入的不做限制，对goods进行限流）
     */
    @GetMapping("/save")
    public String save() {
        //新增订单同时需要查询商品
        orderService.queryGoods();
        System.err.println("新增订单");
        return "新增订单成功";
    }

    /**
     * 更新
     * 备注：用于测试sentinel流控关联模式（当/update QPS达到5的时候，对/query作出限流）
     */
    @GetMapping("/update")
    public String update() {
        return "更新订单成功";
    }
}
