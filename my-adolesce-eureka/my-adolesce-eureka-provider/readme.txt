Eureka客户端(服务提供方)

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
         port: 8003  #自定义监听端口,默认8080
         servlet:
           context-path: /eureka-provider #配置项目名

       spring:
         application:
           name: eureka-provider # 设置当前应用的名称。将来会在eureka中Application显示。将来需要使用该名称来获取路径

       eureka:
         instance:
           hostname: localhost # 主机名
           prefer-ip-address: true # 是否将当前实例的ip注册到eureka server中,默认为false。注册主机名，如果设置为true，默认IP为非回环地址的第一个，可用ip-address属性指定自己的IP
           ip-address: 127.0.0.1 # 设置当前实例的ip
           instance-id: ${eureka.instance.ip-address}:${spring.application.name}:${server.port} # 设置web控制台显示的 实例id，不能重复
           lease-renewal-interval-in-seconds: 120 # 默认每隔30秒 | eureka client向eureka server发送一次心跳包（又称续约时间）
           lease-expiration-duration-in-seconds: 90 # 默认如果90秒内eureka client没有向eureka server发心跳包，服务器呀，你把我干掉吧~（又称服务剔除时间）
         client:
           service-url:
             defaultZone: http://localhost:8761/eureka-server/eureka # eureka服务端地址，将来客户端使用该地址和eureka进行通信（默认就是该地址，多个可用，分隔）
             #defaultZone: http://eureka-server:8761/eureka-server/eureka,http://eureka-server2:8762/eureka-server2/eureka

    4)、启动