Nacos 注册中心服务提供方


1、整合Nacos步骤，作为客户端（服务提供方）
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
         port: 8002
         servlet:
           context-path: /nacos-provider #配置项目名

       spring:
         cloud:
           nacos:
             discovery:
               server-addr:  127.0.0.1:8848 # 配置nacos 服务端地址
         application:
           name: nacos-provider # 服务名称

    4)、启动


Nacos（Dynamic Naming and Configuration Service） 是阿里巴巴2018年7月开源的项目。
• 它专注于服务发现和配置管理领域 致力于帮助您发现、配置和管理微服务。Nacos 支持几乎所有主流类型的“服
务”的发现、配置和管理。
• 一句话概括就是**Nacos = Spring Cloud注册中心 + Spring Cloud配置中心。**
• 官网：https://nacos.io/
• 下载地址： https://github.com/alibaba/nacos/releases
• 使用：下载、解压、启动（点击 startup.cmd  高版本启动命令：startup.cmd -m standalone）
• 控制台访问地址：http://localhost:8848/nacos(默认用户名|密码均为 nacos)