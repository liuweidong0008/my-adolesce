公共通用工程

1、advice
    1)、GlobalCacheResponseBodyAdvice 全局缓存响应体增强类，与全局缓存拦截器配合

2、annotation
    1)、Cache 全局缓存注解

3、aop
    1)、GlobalCacheAop   全局缓存AOP(前置通知+返回后通知)
    2)、GlobalCacheAopAround  全局缓存AOP(环绕通知)

4、cachepack
    缓存组件包（利用多态）

5、config
    1)、RocketConfig     RocketMQ配置类
    2)、WebConfig        WebMvc 拦截器添加配置类

6、entity
    1）、Address
    2）、Goods
    3）、User

7、enums
    1)、ResultCode
    2)、WeekType
    3)、WeekTypeNum

8、exception
    1)、BusinessException  自定义业务异常，用于全局缓存测试

9、handler
    1)、GlobalExceptionHandler   全局异常处理类
    2)、JacksonObjectMapper     对象映射器:基于jackson将Java对象转为json，或者将json转为Java对象

10、init
    1）、MyApplicationContextAware 项目初始化执行代码

11、intercepter
    1)、GlobalCacheInterceptor   全局缓存拦截器，与全局缓存响应体增强类配合

12、swagger bean

13、utils
    enums

14、vo
    1)、AccountChangeEvent   账户改变事件类，用于RocketMQ分布式事务测试
    2)、Response             项目MVC通用返回信息实体类


bo
    1)、Address   MongoDB测试实体类
    2)、BasePojo  Mybatis-Plus 数据库实体公共类
    3)、BatisUser Mybatis 原生使用方式测试实体类
    4)、MpUser    Mybatis-plus使用方式测试实体类
    5)、MyUser    MongoDB测试实体类

mapper
    1)、AccountInfoMapper    账户信息Mapper类，用于RocketMQ分布式事务测试
    2)、BatisUserAnnoMapper  Mybatis 注解 Mapper类，用于Mybatis原生注解方式测试
    3)、BatisUserXmlMapper   Mybatis xml  Mapper类，用于Mybatis原生xml方式测试
    4)、MpUserMapper         Mybatis-Plus Mapper类，用于Mybatis-Plus方式测试