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
         port: 6000  #自定义监听端口,默认8080
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
             defaultZone: http://localhost:6761/eureka-server/eureka # eureka服务端地址，将来客户端使用该地址和eureka进行通信（默认就是该地址，多个可用，分隔）
             #defaultZone: http://eureka-server:6761/eureka-server/eureka,http://eureka-server2:6762/eureka-server2/eureka

    4)、启动

    5)、服务端Hystrix服务降级
        1、引入依赖
            <!-- hystrix -->
             <dependency>
                 <groupId>org.springframework.cloud</groupId>
                 <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
             </dependency>
        2、启动类上打上注解开启Hystrix功能：@EnableCircuitBreaker
        3、服务端定义降级方法
        4、在服务端提供的服务上使用 @HystrixCommand 注解来配置降级方法
        5、服务端什么时候出现降级?
             1）. 服务端出现异常
             2）. 服务端处理超时（Hystrix的超时时间，默认超时1s）
             3) . 触发熔断

    6)、Hystrix-熔断（无需任何配置，默认就已打开）
     * 服务熔断：
        1、Hystrix 熔断机制，用于监控微服务调用情况，当失败的情况达到预定的阈值（5秒内失败20次或者5秒内失败几率一半以上了），会打开断路器，拒绝所有请求，直到服务恢复正常为止。
        2、断路器三种状态：打开、半开、关闭
        3、每隔5秒会将断路器变为半开状态，去调用一次服务，如果成功则关闭熔断器，如果还是失败则重新打开熔断器
        4、修改服务提供方的方法（GoodsController），配置熔断参数
          • circuitBreaker.sleepWindowInMilliseconds：监控时间
          • circuitBreaker.requestVolumeThreshold：失败次数
          • circuitBreaker.errorThresholdPercentage：失败率

     7）、服务端超时重试测试（关闭客户端hystrix降级情况下测试）
         服务端休眠	服务端Hystrix降级超时时间    消费端Ribbon-ReadTimeout时间    服务端现象				  	    消费端现象			浏览器现象
         1s			1s					        1s							   调用2次					  	抛2次超时异常		错误页面
         1s			2s					        1s							   调用2次					  	抛2次超时异常		错误页面
         2s			2s					        2s							   调用2次					  	抛2次超时异常		错误页面
         2s			2s					        1s							   调用2次					  	抛2次超时异常		错误页面
         2s 		3s					        1s							   调用2次					  	抛2次超时异常		错误页面
         2s			1s					        1s							   调用2次、抛2次sleep打断错误	抛2次超时异常		错误页面

         2s			1s					        2s							   调用1次、抛1次sleep打断错误	正常				降级页面
         3s			1s					        2s							   调用1次、抛1次sleep打断错误	正常				降级页面

         1s			1s					        2s							   调用1次					  	正常				降级页面
         1s			2s					        2s							   调用1次						正常				正常页面

         2s			1s					        3s							   调用1次、抛1次sleep打断错误	正常				降级页面
         2s			2s					        3s							   调用1次					  	正常				降级页面|正常页面
         2s			3s					        3s							   调用1次					  	正常				正常页面

         Ribbon-ReadTimeout <= 服务端休眠时间
            服务端降级超时时间  >= Ribbon-ReadTimeout 优先触发重试，不会服务降级
            服务端降级超时时间 < Ribbon-ReadTimeout 优先服务降级，不会触发重试

         Ribbon-ReadTimeout > 服务端休眠时间 ：不会触发重试
            服务端降级超时时间 <= 服务端休眠时间  返回服务降级结果
            服务端降级超时时间 > 服务端休眠时间  返回正常结果