package com.adolesce.server.controller;


import cn.hutool.core.collection.CollectionUtil;
import com.adolesce.common.entity.Goods;
import com.adolesce.common.entity.Order;
import com.adolesce.server.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
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

    @GetMapping("/{id}")
    public Order findOrderById(@PathVariable("id") int id) {
        System.out.println("findOrderById..." + id);
        String centerName = "consul";

        Order order = new Order();
        order.setOrderNo("10001L");
        order.setOrderName("小明的订单");

        //远程调用Goods服务中的findOne接口
        String url = this.getStaticUrl(centerName);
        //Goods goods = restTemplate.getForObject(url, Goods.class);

        //演示discoveryClient 使用（启动类上标注@EnableDiscoveryClient注解）
        List<ServiceInstance> instances = this.getInstances(centerName);

        //判断集合是否有数据
        if (CollectionUtil.isEmpty(instances)) {
            //集合没有数据
            return null;
        }

        ServiceInstance instance = instances.get(0);
        String host = instance.getHost();
        int port = instance.getPort();
        System.out.println("IP 端口：" + host + ":" + port);

        url = getDynamicUrl(centerName,id, host, port);
        Goods goods = restTemplate.getForObject(url, Goods.class);
        order.setGoods(goods);
        return order;
    }

    /**
     * NetFix Ribbon：基于客户端的负载均衡器
     * 一、作用：
     * 1、简化服务调用
     * 2、实现负载均衡
     * <p>
     * 二、使用 Ribbon简化RestTemplate调用
     * 1、在声明RestTemplate Bean的时候，添加一个注解：@LoadBalance
     * 2、在使用RestTemplate发起请求时，需要定义url时，host:port 可以替换成服务提供方的应用名称
     * 3、开启@LoadBalance后，就不能以discoveryClient.getInstances去获取实例了，restTemplate调用时会报找不到实例
     * <p>
     * 三、Ribbon负载均衡策略一共七种：
     * 1、随机：RandomRule                   随机调用节点
     * 2、轮询：RoundRobinRule               依次调用每个节点（默认）
     * 3、最小并发：BestAvailableRule        选取并发量最小，最闲的节点
     * 4、过滤：AvailabilityFilteringRule    过滤掉故障节点、并发高的节点
     * 5、响应时间：WeightedResponseTimeRule 选取对发送的小数据包响应最快的节点
     * 6、轮询重试：RetryRule                轮询默认为十次，超过十次进行再次的重试轮询
     * 7、性能可用性：ZoneAvoidanceRule      同地域机房节点轮询
     * <p>
     * 四、负载均衡策略设置有以下两种方式：
     * 1、通过编码方式：IRule配置类 + 主启动类上@RibbonClient注解(不标注该注解则将配置类作用于全局)
     * 2、通过配置文件的方式
     *
     * @param id
     * @return
     */
    @GetMapping("/ribbon/{id}")
    public Order findOrderById2(@PathVariable("id") int id) {
        System.out.println("findOrderById2..." + id);
        Order order = new Order();
        order.setOrderNo("10002L");
        order.setOrderName("小本的订单");

        //1、定义url
        String url = this.getFeignUrl("consul", id);
        // 2. 调用方法
        Goods goods = restTemplate.getForObject(url, Goods.class);
        order.setGoods(goods);
        return order;
    }

    /**
     * 通过Feign声明式rest客户端进行调用
     * 1、Feign底层依赖Ribbon实现负载均衡和远程调用
     * 2、使用feign，Ribbon的一切功能都能使用
     *
     * @param id
     * @return
     */
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

    private String getStaticUrl(String centerName) {
        String url = "";
        switch (centerName) {
            case "consul":
                url = "http://localhost:4000/goods/findOne/1";
                break;
            case "eureka":
                url = "http://localhost:6000/eureka-provider/goods/findOne/1";
                break;
            case "nacos":
                url = "http://localhost:7000/nacos-provider/goods/findOne/1";
                break;
            default:
                break;
        }
        return url;
    }

    private String getDynamicUrl(String centerName, int id, String host, int port) {
        String url = "";
        switch (centerName) {
            case "consul":
                url = "http://" + host + ":" + port + "/goods/findOne/" + id;
                break;
            case "eureka":
                url = "http://" + host + ":" + port + "/eureka-provider/goods/findOne/" + id;
                break;
            case "nacos":
                url = "http://" + host + ":" + port + "/nacos-provider/goods/findOne/" + id;
                break;
            default:
                break;
        }
        return url;
    }

    private String getFeignUrl(String centerName, int id) {
        String url = "";
        switch (centerName) {
            case "consul":
                url = "http://CONSUL-PROVIDER/goods/findOne/"+id;        //consul 整合rabbion进行调用时加上自定义的项目路径会报错,目前只能用根路径（待解决）
                break;
            case "eureka":
                url = "http://EUREKA-PROVIDER/eureka-provider/goods/findOne/"+id;
                break;
            case "nacos":
                url = "http://nacos-provider/nacos-provider/goods/findOne/" + id;
                break;
            default:
                break;
        }
        return url;
    }

    private List<ServiceInstance> getInstances(String centerName) {
        List<ServiceInstance> instances = null;
        String url = "";
        switch (centerName) {
            case "consul":
                instances = discoveryClient.getInstances("CONSUL-PROVIDER"); //Application name
                break;
            case "eureka":
                instances = discoveryClient.getInstances("EUREKA-PROVIDER"); //Application name
                break;
            case "nacos":
                instances = discoveryClient.getInstances("nacos-provider"); //Application name
                break;
            default:
                break;
        }
        return instances;
    }
}
