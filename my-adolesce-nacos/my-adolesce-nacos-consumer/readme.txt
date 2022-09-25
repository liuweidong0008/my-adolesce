Nacos客户端（服务消费方）

1、整合Nacos步骤，作为客户端（服务消费方）
    1)、引入Nacos客户端依赖
        <!--nacos-->
        <dependency>
            <groupId>com.alibaba.cloud</groupId>
            <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

    2）、配置文件增加配置 application.yml
       server:
         port: 7001
         servlet:
           context-path: /nacos-consumer #配置项目名
       spring:
         application:
           name: my-adolesce
         cloud:
           nacos:
              server-addr:  127.0.0.1:8848 # 配置nacos 服务端地址

    3)、启动

    4)、整合sentinel
        1、引入依赖
             <!--引入Sentinel依赖-->
            <dependency>
                <groupId>com.alibaba.cloud</groupId>
                <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
            </dependency>
        2、增加控制台监控配置
            spring:
              cloud:
                sentinel:
                  transport:
                    dashboard: localhost:8080 # sentinel控制台地址
        3、注意事项
           1）、sentinel默认只标记controller中的方法为资源，如果要标记其他方法，需要利用@SentinelResource注解
           2）、sentinel默认会将controller的方法做context整合，导致链路模式的流控失效，需要修改application.yml配置，添加：
               spring:
                 cloud:
                   sentinel:
                     web-context-unify: false # 关闭context整合


    5)、Sentinel限流（通过sentinel控制台进行流控限流设置、通过Jmeter测试）
         1、流控模式
            1、直接模式：对当前资源限流
            2、关联模式：高优先级资源触发阈值，对低优先级资源限流。
            3、链路模式：阈值统计时，只统计从指定资源进入当前资源的请求，是对请求来源的限流
         2、流控效果
            1、快速失败：达到阈值后，新的请求会被立即拒绝并抛出FlowException异常。是默认的处理方式。
            2、warm up 预热：预热模式，对超出阈值的请求同样是拒绝并抛出异常。但这种模式阈值会动态变化，从一个较小值逐渐增加到最大阈值。
                            是应对服务冷启动的一种方案。请求阈值初始值是 maxThreshold / coldFactor，持续指定时长后，逐渐提高到maxThreshold值。而coldFactor的默认值是3.
            3、排队等待：让所有的请求按照先后次序排队执行，两个请求的间隔不能小于指定时长
         3、特殊：热点参数限流（对热点链接、热点参数设置限流规则）

    5)、Feign整合Sentinel实现隔离和熔断降级
        1、修改配置文件，开启Feign的Sentinel功能：
            feign:
              sentinel:
                enabled: true # 开启feign对sentinel的支持

        2、编写失败降级逻辑
          业务失败后，不能直接报错，而应该返回用户一个友好提示或者默认结果，这个就是失败降级逻辑。
          给FeignClient编写失败后的降级逻辑
          ① 方式一：FallbackClass，无法对远程调用的异常做处理
          ② 方式二：FallbackFactory，可以对远程调用的异常做处理，可以优先选择这种

        3、熔断降级
            1）、断路器三种状态
                熔断降级是解决雪崩问题的重要手段。其思路是由断路器统计服务调用的异常比例、慢请求比例，如果超出阈值则会熔断该服务。即拦截访问该服务的一切请求；而当服务恢复时，断路器会放行访问该服务的请求。
                断路器控制熔断和放行是通过状态机来完成的：
                1、closed：关闭状态，断路器放行所有请求，并开始统计异常比例、慢请求比例。超过阈值则切换到open状态
                2、open：打开状态，服务调用被熔断，访问被熔断服务的请求会被拒绝，快速失败，直接走降级逻辑。Open状态5秒后会进入half-open状态
                3、half-open：半开状态，放行一次请求，根据执行结果来判断接下来的操作。
                    - 请求成功：则切换到closed状态
                    - 请求失败：则切换到open状态
            2）、规则设置（通过sentinel控制台进行降级设置）
                1、慢调用：业务的响应时长（RT）大于指定时长的请求认定为慢调用请求。在指定时间内，如果请求数量超过设定的最小数量，慢调用比例大于设定的阈值，则触发熔断。
                2、异常比例/异常数：统计指定时间内的调用，如果调用次数超过指定请求数，并且出现异常的比例达到设定的比例阈值（或超过指定异常数），则触发熔断。

    6)、sentinel授权规则
        1、sentinel授权规则可以对调用方的来源做控制，有白名单和黑名单两种方式。
            - 白名单：来源（origin）在白名单内的调用者允许访问
            - 黑名单：来源（origin）在黑名单内的调用者不允许访问

        2、sentinel控制台的授权规则
            - 资源名：就是受保护的资源，例如/order/{orderId}
            - 流控应用：是来源者的名单，
              - 如果是勾选白名单，则名单中的来源被许可访问。
              - 如果是勾选黑名单，则名单中的来源被禁止访问。
           如：允许请求从gateway到order-service，不允许浏览器访问order-service，那么白名单中就要填写网关的来源名称（origin）

        3、Sentinel是通过RequestOriginParser这个接口的parseOrigin来获取请求的来源的。
            public interface RequestOriginParser {
                /**
                 * 从请求request对象中获取origin，获取方式自定义
                 */
                String parseOrigin(HttpServletRequest request);
            }
            这个方法的作用就是从request对象中，获取请求者的origin值并返回。
            默认情况下，sentinel不管请求者从哪里来，返回值永远是default，也就是说一切请求的来源都被认为是一样的值default。
            因此，我们需要自定义这个接口的实现，让不同的请求，返回不同的origin。

        4、实践流程
            1）、给网关添加请求头处理局部过滤器，请求经过网关时，为请求加上请求头
                - AddRequestHeader=origin,gateway
            2）、在nacos-consumer消费端增加RequestOriginParser接口，重写parseOrigin方法，返回值用来标识请求来源
                @Component
                public class HeaderOriginParser implements RequestOriginParser {
                    @Override
                    public String parseOrigin(HttpServletRequest request) {
                        // 1.获取请求头
                        String origin = request.getHeader("origin");
                        // 2.非空判断
                        if (StringUtils.isEmpty(origin)) {
                            origin = "blank";
                        }
                        return origin;
                    }
                }
            3）、sentinel控制台为资源添加授权规则
                资源名：/order/goods/feign/{id}
                流控应用：gateway （当来源为gateway时才放行，否则拦截禁止访问）
                授权类型：白名单
            4）、测试
                访问：http://localhost:10010/nc/nacos-consumer/order/goods/feign/3?authorization=admin 不受限制
                访问：http://localhost:9001/nacos-consumer/order/goods/feign/3 报无权限访问

    7)、sentinel自定义异常结果
            1）、默认情况下，发生限流、降级、授权拦截时，都会抛出异常到调用方。异常结果都是flow limmiting（限流）。
                这样不够友好，无法得知是限流还是降级还是授权拦截。
            2）、如果要自定义异常时的返回结果，需要实现BlockExceptionHandler接口：
                public interface BlockExceptionHandler {
                    /**
                     * 处理请求被限流、降级、授权拦截时抛出的异常：BlockException
                     */
                    void handle(HttpServletRequest request, HttpServletResponse response, BlockException e) throws Exception;
                }
            3）、这里的BlockException包含多个不同的子类：
                | 异常                 | 说明           |
                | -------------------- | ------------------ |
                | FlowException        | 限流异常           |
                | ParamFlowException   | 热点参数限流的异常 |
                | DegradeException     | 降级异常           |
                | AuthorityException   | 授权规则异常       |
                | SystemBlockException | 系统规则异常       |

        6、sentinel规则管理的三种模式
            - 原始模式：Sentinel的默认模式，将规则保存在内存，重启服务会丢失。
            - pull模式：保存在本地文件或数据库，定时去读取
            - push模式：保存在nacos，监听变更实时更新

    8)、sentinel push模式持久化规则
        1、在nacos-consumer中引入sentinel监听nacos的依赖：
            <dependency>
                <groupId>com.alibaba.csp</groupId>
                <artifactId>sentinel-datasource-nacos</artifactId>
            </dependency>

        2、在nacos-consumer中的application.yml文件配置nacos地址及监听的配置信息：（相当于将sentinel的各种规则以配置文件的方式持久化到nacos）
           spring:
             cloud:
               sentinel:
                 datasource:
                   flow:     #持久化限流规则到nacos
                     nacos:
                       server-addr: localhost:8848 # nacos地址
                       dataId: nacos-provider-flow-rules #唯一配置文件的标识（不能随便写，格式是固定的：applicationName-flow-rules）
                       groupId: SENTINEL_GROUP
                       rule-type: flow #（流控） 还可以是：degrade（降级）、authority（授权）、param-flow（热点）
                   degrade:  #持久化降级规则到nacos
                     nacos:
                       server-addr: localhost:8848
                       dataId: nacos-provider-degrade-rules
                       groupId: SENTINEL_GROUP
                       rule-type: degrade