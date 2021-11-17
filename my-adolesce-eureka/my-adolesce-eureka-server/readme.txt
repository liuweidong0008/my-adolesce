Eureka服务端(注册中心)

1、整合Eureka注册中心搭建步骤
    1)、工程根pom引入SpringCloud依赖
        <!--引入Spring Cloud 依赖-->
           <dependencyManagement>
               <dependencies>
                   <dependency>
                       <groupId>org.springframework.cloud</groupId>
                       <artifactId>spring-cloud-dependencies</artifactId>
                       <version>Greenwich.RELEASE</version>
                       <type>pom</type>
                       <scope>import</scope>
                   </dependency>
               </dependencies>
           </dependencyManagement>

    2）、pom文件引入eureka-server依赖
        <!-- eureka-server -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
        </dependency>

    3）、启动类上加上注解
        @EnableEurekaServer
        作用：启用EurekaServer（服务治理注册中心：服务注册与发现）

    4）、配置文件增加配置 application.yml
        server:
          port: 8761
          servlet:
            context-path: /eureka-server #配置项目名
        spring:
          application:
            name: eureka-server-ha

        # eureka 配置
        # eureka 一共有4部分 配置
        # 1. dashboard:eureka的web控制台配置
        # 2. server:eureka的服务端配置
        # 3. client:eureka的客户端配置
        # 4. instance:eureka的实例配置

        eureka:
          instance:
            hostname: localhost # 主机名
          client:
            service-url:
              defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka-server/eureka # eureka服务端地址，将来客户端使用该地址和eureka进行通信
            register-with-eureka: false # 是否将自己的路径 注册到eureka上。eureka server 不需要的，eureka provider client 需要
            fetch-registry: false # 是否需要从eureka中抓取路径。eureka server 不需要的，eureka consumer client 需要

    5）、启动

    6）、eureka控制台默认路径:项目根路径
        如：localhost:9876

        控制台显示信息介绍：
            System status:系统状态信息
            DS Replicas:集群信息
            tance currently registered with Eureka: 实例注册信息
            General Info ：通用信息
            Instance Info ：实例信息