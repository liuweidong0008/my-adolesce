Eureka客户端（服务消费方）

1、整合Eureka步骤，作为客户端（服务提供方）
    1)、引入Eureka客户端依赖
        <!-- eureka-client -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>

    2)、主启动类上标注Eureka客户端注解
        @EnableEurekaClient  //eureka 客户端,该注解在新版本可省略，但是不建议省略

    3）、配置文件增加配置 application.yml
       server:
         port: 8002  #自定义监听端口,默认8080
         servlet:
           context-path: /eureka-consumer #配置项目名

       spring:
         application:
           name: eureka-consumer # 设置当前应用的名称。将来会在eureka中Application显示。将来需要使用该名称来获取路径

       eureka:
         instance:
           hostname: localhost # 主机名
           prefer-ip-address: true # 是否将当前实例的ip注册到eureka server中,默认为false。注册主机名，如果设置为true，默认IP为非回环地址的第一个，可用ip-address属性指定自己的IP
           ip-address: 127.0.0.1 # 设置当前实例的ip
           instance-id: ${eureka.instance.ip-address}:${spring.application.name}:${server.port} # 设置web控制台显示的 实例id，不能重复
           lease-renewal-interval-in-seconds: 30 # 默认每隔30秒 | eureka client向eureka server发送一次心跳包（又称续约时间）
           lease-expiration-duration-in-seconds: 90 # 默认如果90秒内eureka client没有向eureka server发心跳包，服务器呀，你把我干掉吧~（又称服务剔除时间）
         client:
           service-url:
             defaultZone: http://localhost:8761/eureka-server/eureka # eureka服务端地址，将来客户端使用该地址和eureka进行通信（默认就是该地址，多个可用，分隔）
             #defaultZone: http://eureka-server:8761/eureka-server/eureka,http://eureka-server2:8762/eureka-server2/eureka

    4)、启动

    5）、Ribbon集成
        1、在RestTemplate上添加@LoadBalance注解
        2、将服务的IP端口替换成服务名称

    5）、Ribbon负载均衡策略设置（一般用默认的负载均衡规则，不做修改）
        1、原理
            - LoadBalancerInterceptor拦截我们的RestTemplate请求http://userservice/user/1
            - RibbonLoadBalancerClient会从请求url中获取服务名称，也就是user-service
            - DynamicServerListLoadBalancer根据user-service到eureka拉取服务列表
            - eureka返回列表，localhost:8081、localhost:8082
            - IRule利用内置负载均衡规则，从列表中选择一个，例如localhost:8081
            - RibbonLoadBalancerClient修改请求地址，用localhost:8081替代userservice，得到http://localhost:8081/user/1，发起真实请求
        2、设置方式
            1）、代码方式：
                1、通过@Bean的方式定义一个新的IRule：RibbonRuleConfig
                    @Bean
                    public IRule rule() {
                        return new RandomRule();
                    }
                2、如果要对全局服务生效，在该类上添加@Configuration注解即可
                3、如果是对局部服务生效，在启动类上添加如下注解，指定对某个服务进行配置：@RibbonClient(name = "EUREKA-PROVIDER", configuration = RibbonRuleConfig.class)
            2）、配置方式：
                userservice: # 给某个微服务配置负载均衡规则，这里是userservice服务
                  ribbon:
                    NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule # 负载均衡规则

    6）、Feign集成
        1、添加依赖
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-starter-openfeign</artifactId>
            </dependency>
        2、启动类上添加注解
            @EnableFeignClients
        3、新建Feign客户端（本质上是一个接口）
            @FeignClient("userservice")
            public interface UserClient {
                @GetMapping("/user/{id}")
                User findById(@PathVariable("id") Long id);
            }
            这个客户端主要是基于SpringMVC的注解来声明远程调用的信息，比如：
            - 服务名称：userservice
            - 请求方式：GET
            - 请求路径：/user/{id}
            - 请求参数：Long id
            - 返回值类型：User
            这样，Feign就可以帮助我们发送http请求，无需自己使用RestTemplate来发送了。

    6）、Feign调用日志级别设置
        1、设置项目日志级别
            # 设置当前的日志级别 debug，feign只支持记录debug级别的日志
            logging:
              level:
                com.adolesce: debug
        2、设置方式
            1）、代码方式：
                 1、通过@Bean的方式定义一个新的Level：FeignLogConfig
                    @Bean
                    public Logger.Level feignLogLevel(){
                        return Logger.Level.BASIC; // 日志级别为BASIC
                    }
                 2、如果要对全局服务生效，在主启动类上添加 @EnableFeignClients(defaultConfiguration = FeignLogConfig.class)
                 3、如果是对局部服务生效，则把它放到对应的@FeignClient注解中：@FeignClient(value = "EUREKA-PROVIDER", configuration = FeignLogConfig.class)
            2）、配置方式
                    feign:
                      client:
                        config:
                          default: # 这里用default就是全局配置，如果是写服务名称，则是针对某个微服务的局部配置
                            loggerLevel: FULL #  日志级别


    7）、Ribbon超时和重试（默认1s，重试1次）
        ribbon: #全局ribbon的参数设置
          ConnectTimeout: 1000 # 连接超时时间 默认1s
          ReadTimeout: 3000 # 逻辑处理的超时时间,即通信超时时间 默认1s

    8）、饥饿加载
        Ribbon默认是采用懒加载，即第一次访问时才会去创建LoadBalanceClient，请求时间会很长。
        而饥饿加载则会在项目启动时创建，降低第一次访问的耗时，通过下面配置开启饥饿加载：
            ribbon:
              eager-load:
                enabled: true
                clients: userservice