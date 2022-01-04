项目主工程
    角色：
    1）、作为Http请求接收端
    2）、作为Rocketmq消息生产端
    3）、作为Eureka服务消费方
    4）、作为Consul服务消费方
    5）、作为Nacos 服务消费方
    6）、其他技术演示、测试端

1、整合Eureka步骤，作为客户端（服务消费方）
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
          port: 8081  #自定义监听端口,默认8080
          servlet:
            context-path: /my_adolesce #配置项目名

        spring:
          application:
            name: my-adolesce # 设置当前应用的名称。将来会在eureka中Application显示。

        #eureka相关配置
        eureka:
          instance:
            hostname: localhost # 主机名
          client:
            service-url:
              defaultZone: http://localhost:8761/eureka-server/eureka # eureka服务端地址，将来客户端使用该地址和eureka进行通信
              #defaultZone: http://eureka-server:8761/eureka-server/eureka,http://eureka-server2:8762/eureka-server2/eureka

    4)、启动


2、整合Consul步骤，作为客户端（服务消费方）
    1)、引入Consul客户端依赖
        <!--consul 客户端-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-consul-discovery</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

    2）、配置文件增加配置 application.yml
       server:
         port: 8081  #自定义监听端口,默认8080
         servlet:
           context-path: /my_adolesce #配置项目名
       spring:
         cloud:
           #consul注册中心配置
           consul:
             host: localhost # consul 服务端的 ip
             port: 8500 # consul 服务端的端口 默认8500
             discovery:
               service-name: ${spring.application.name} # 当前应用注册到consul的名称
               prefer-ip-address: true # 是否以ip形式注册
               #ip-address: 127.0.0.1 # 设置当前实例的ip
         application:
             name: my-adolesce

    4)、启动

Nacos 注册中心服务提供方
3、整合Nacos步骤，作为客户端（服务消费方）
    1)、引入Consul客户端依赖
        <!--nacos-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
            <version>0.2.2.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

    2）、配置文件增加配置 application.yml
       server:
         port: 8081  #自定义监听端口,默认8080
         servlet:
           context-path: /my_adolesce #配置项目名
       spring:
         cloud:
           nacos:
             discovery:
               server-addr:  127.0.0.1:8848 # 配置nacos 服务端地址
         application:
           name: my-adolesce

    4)、启动

总结(心得)：
    1、以上三种注册中心皆可用discoveryClient.getInstances方式去获取提供者实例，然后成功发起服务调用；
    2、以上三种注册中心皆可用Ribbon方式去成功发起服务调用,但是有细微区别，如consul方式服务提供方不能加context-path属性，
       再如nacos实例名不能用大写，必须小写（nacos-provider 大小写敏感），其他两种注册中心则大小写都可以；
    3、当开启使用Ribbon方式去发起服务调用时（RestTemplate+LoadBalance），就不能使用discoveryClient.getInstances方式了，会报找不到实例错误。

