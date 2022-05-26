Dubbo进行RPC远程服务调用【nacos注册中心】

1、整合步骤
    1)、服务提供方和消费方均导入依赖
        <!--dubbo的起步依赖-->
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo-spring-boot-starter</artifactId>
            <version>2.7.8</version>
        </dependency>
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo-registry-nacos</artifactId>
            <version>2.7.8</version>
        </dependency>

    2）、服务提供方 配置文件增加配置 application.yml
       server:
         port: 5000
       spring:
         application:
           name: dubbo-provider
       logging:
         level:
           cn.itcast: debug
         pattern:
           dateformat: HH:mm:ss:SSS
       #配置dubbo提供者
       #指定dubbo协议和访问端口（Tomcat中会起一个Netty服务，端口为20881）
       dubbo:
         protocol:
           name: dubbo
           port: 20881
       #注册中心的地址
         registry:
           address: nacos://127.0.0.1:8848
       #dubbo注解的包扫描
         scan:
           base-packages: com.adolesce.dubbo.provider.api

    3)、服务消费方 配置文件增加配置 application.yml
        server:
          port: 8083
        spring:
          application:
            name: dubbo-consumer
        logging:
          level:
            cn.itcast: debug
          pattern:
            dateformat: HH:mm:ss:SSS
        #配置dubbo服务消费者
        dubbo:
          registry:
            address: nacos://127.0.0.1:8848
          consumer:
            check: false  #关闭了启动检查
            retries: 0

    4)、在interface模块定义接口和公用实体类
    5）、服务方编写服务类、客户端进行调用
        服务提供方编写服务类，并用@DubboService标注
        服务消费方引入服务接口，并用@DubboReference标注

    6)、启动

2、关于配置项
    1)、启动检查
           1）、通过在消费端注解上添加属性指定：@DubboReference(check=false)，对当前消费者生效

           2）、也可通过在consumer 消费者 模块中添加如下配置信息，对当前项目下所有消费者生效：
                dubbo:
                  registry:
                    address: nacos://127.0.0.1:8848
                  consumer:
                    check: false

    2）、多版本支持:指定服务版本
            1）、Dubbo服务提供方指定新老服务版本：  @DubboService(version = "2.0.0")
            2）、Dubbo服务消费方指定消费服务的版本：@DubboReference(version = "2.0.0")

    3）、超时与重试
            超时:当Dubbo服务提供方响应时间超过 超时时间则会抛出timeout的异常，可进行配置
            重试:当Dubbo服务方抛出异常后，客户端还可以进行重试，重试次数可进行配置
            1）、消费方注解
                @DubboReference(timeout = 30000,retries = 4)
            2）、消费方配置文件
                      dubbo:
                        registry:
                          address: nacos://127.0.0.1:8848
                        consumer:
                          timeout: 3000
                          retries: 0
            3）、服务端注解
                @DubboService(timeout = 20000,retries = 4)
            4）、服务提供方配置文件
                      dubbo:
                        protocol:
                          name: dubbo
                          port: 20881
                        #注册中心的地址
                        registry:
                          address: nacos://127.0.0.1:8848
                        #dubbo注解的包扫描
                        scan:
                          base-packages: com.adolesce.dubbo.provider.api
                        provider:
                          timeout: 2000
                          retries: 4

            5）、以上配置优先级：     消费方注解 > 服务方注解 > 消费方文件配置 > 服务方文件配置

    3）、负载均衡
            Dubbo提供了4种负载均衡策略，帮助消费者找到**最优**提供者并调用
            * random ：按权重随机，默认值。按权重设置随机概率。
            * roundrobin ：按权重进行轮询调用。
            * leastactive：最少活跃调用数，Dubbo认为活跃度最小的性能会更高，而相同活跃数进行随机调用。
            * consistenthash：一致性 Hash，相同参数的请求总是发到同一提供者。

            配置方式：
                1)、可通过在消费者注解上指定：
                       @DubboReference(loadbalance = "roundrobin")

                2）、也可以在消费者配置文件中进行指定：
                    dubbo:
                      registry:
                        address: nacos://127.0.0.1:8848
                      consumer:
                        loadbalance: random

                3）、可通过在提供者注解上指定
                       @DubboService(loadbalance = "roundrobin")

                4）、可以在提供者配置文件中进行指定
                    dubbo:
                      protocol:
                        name: dubbo
                        port: 20881
                    #注册中心的地址
                      registry:
                        address: nacos://127.0.0.1:8848
                    #dubbo注解的包扫描
                      scan:
                        base-packages: cn.itcast.user.service
                      provider:
                        loadbalance: random

                5）、以上配置优先级：消费方注解 > 服务方注解 > 消费方文件配置 > 服务方文件配置