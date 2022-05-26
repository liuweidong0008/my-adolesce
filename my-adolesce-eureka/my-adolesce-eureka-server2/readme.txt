Eureka服务端集群(注册中心)

1、Eureka注册中心集群搭建步骤
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
          port: 6762  #自定义监听端口,默认8080
          servlet:
            context-path: /eureka-server2 #配置项目名

        spring:
          application:
            name: eureka-server-ha

        eureka:
          instance:
            hostname: eureka-server2
          client:
            service-url:
              defaultZone: http://eureka-server:6761/eureka-server/eureka #集群模式 相互注册,配置其他注册中心节点地址
            register-with-eureka: false # 是否将自己的路径 注册到eureka上，默认为true。eureka server 不需要的，eureka provider client 需要
            fetch-registry: false # 是否需要从eureka中抓取路径，默认为true。eureka server 不需要的，eureka consumer client 需要

    5）、启动

    6）、改造另外的注册中心结点
         改造 defaultZone 属性的指向（互相指向）
         defaultZone: http://eureka-server2:6762/eureka-server2/eureka

    7）、改造eureka客户端（服务提供方和消费方）
         改造 defaultZone 属性的指向（指向所有的注册中心地址，逗号分割）
         defaultZone: http://eureka-server:6761/eureka-server/eureka,http://eureka-server2:6762/eureka-server2/eureka