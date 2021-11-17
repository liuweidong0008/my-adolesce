Consul 注册中心服务提供方


1、整合Consul步骤，作为客户端（服务提供方）
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
         port: 8000
         #consul 服务提供方设置context-path属性后，消费方整合rabbion进行调用会报错,所以目前提供方整合ribbon不能设置这个属性(待解决)
         #servlet:
           #context-path: /consul-provider #配置项目名

       spring:
         cloud:
           consul:
             host: localhost # consul 服务端的 ip
             port: 8500 # consul 服务端的端口 默认8500
             discovery:
               service-name: ${spring.application.name} # 当前应用注册到consul的名称
               prefer-ip-address: true # 是否以ip形式注册
               #ip-address: 127.0.0.1 # 设置当前实例的ip
         application:
           name: consul-provider # 应用名称

    4)、启动


Consul 是由 HashiCorp 基于 Go 语言开发的，支持多数据中心，分布式高可用的服务发布和注册服务软件。
• 用于实现分布式系统的服务发现与配置。
• 使用起来也较 为简单。具有天然可移植性(支持Linux、windows和Mac OS X)；安装包仅包含一个可执行文件，
    方便部署 。
• 官网地址： https://www.consul.io
• 使用：下载、解压、启动（consul agent -dev）  dev模式：不会持久化数据
• 控制台访问地址：http://localhost:8500