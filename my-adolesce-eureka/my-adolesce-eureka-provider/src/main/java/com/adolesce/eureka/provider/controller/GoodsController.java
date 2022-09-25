package com.adolesce.eureka.provider.controller;

import com.adolesce.eureka.provider.domain.Goods;
import com.adolesce.eureka.provider.service.GoodsService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Goods Controller 服务提供方
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    private GoodsService goodsService;

    @Value("${server.port}")
    private int port;

    /**
     * 1、什么时候出现降级?
     *    1. 出现异常
     *    2. 服务调用超时（Hystrix的超时时间，默认1s超时）
     *
     * 2、服务提供方降级引入步骤？
     *      1、在服务提供方，引入 hystrix 依赖
     *      2、定义降级方法
     *      3、使用 @HystrixCommand 注解配置指定降级方法
     *      4、在启动类上开启Hystrix功能：@EnableCircuitBreaker
     *      5、服务上使用fallbackMethod属性指定降级后调用的方法名称  @HystrixCommand(fallbackMethod = "findOne_fallback")
     *      6、客户端和服务端降级优先级：
     *          1）、超时情况下，当服务端和客户端同时有降级处理时 会 优先走客户端降级（客户端对于时间临界值更加接近，当然不会触发服务端的了）
     *          2）、异常情况下，当服务端和客户端同时有降级处理时 会 优先走服务端降级（服务端已经出现异常进行降级处理了，当然不会触发客户端的了）
     *
     * 3、服务熔断：
     *      1、Hystrix 熔断机制，用于监控微服务调用情况，当失败的情况达到预定的阈值（5秒内失败20次或者5秒内失败几率一半以上了），会打开断路器，拒绝所有请求，直到服务恢复正常为止。
     *      2、断路器三种状态：打开、半开、关闭
     *      3、每隔5秒会将断路器变为半开状态，去调用一次服务，如果成功则关闭熔断器，如果还是失败则重新打开熔断器
     */
    @GetMapping("/findOne/{id}")
    @HystrixCommand(fallbackMethod = "findOneFallback", commandProperties = {
            //设置Hystrix的超时时间，超过该时间会触发服务降级，默认是1s (其他相关配置项可到 HystrixCommandProperties 类进行查找)
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000"),
            //监控时间 默认5000 毫秒
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value = "5000"),
            //失败次数。默认20次
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "20"),
            //失败率 默认50%
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50")
    })
    public Goods findOne(@PathVariable("id") int id) throws InterruptedException {
        System.err.println("调用服务方....");
        //1、超时（休眠2秒）
        if(id == 1){
            Thread.sleep(3000);
        }
        //2、造异常测试熔断（当id == 2 则出现异常）
        if(id  == 2){
            int i = 3/0;
        }
        Goods goods = goodsService.findOne(id);
        goods.setTitle(goods.getTitle() + ":" + port); //将端口号设置到了商品标题上
        return goods;
    }

    /**
     * 定义降级方法：
     * 1. 方法的返回值需要和原方法一样
     * 2. 方法的参数需要和原方法一样
     */
    public Goods findOneFallback(int id) {
        Goods goods = new Goods();
        goods.setTitle("通过服务端降级了~~~");
        return goods;
    }
}
