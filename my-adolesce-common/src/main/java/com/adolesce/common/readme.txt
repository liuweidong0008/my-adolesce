1、advice
    1)、GlobalCacheResponseBodyAdvice 全局缓存响应体增强类，与全局缓存拦截器配合

2、annotation
    1)、Cache 全局缓存注解

3、aop
    1)、GlobalCacheAop   全局缓存AOP(前置通知+返回后通知)
    2)、GlobalCacheAopAround  全局缓存AOP(环绕通知)

4、bo
    1)、Address   MongoDB测试实体类
    2)、BasePojo  Mybatis-Plus 数据库实体公共类
    3)、BatisUser Mybatis 原生使用方式测试实体类
    4)、MpUser    Mybatis-plus使用方式测试实体类
    5)、MyUser    MongoDB测试实体类

5、config
    1)、HuyiSMSConfig    互亿无线短信发送配置类
    2)、MybatisPlusPageConfig  Mybatis-Plus分页配置类
    3)、RocketConfig     RocketMQ配置类
    4)、WebConfig        WebMvc 拦截器添加配置类
    5)、YiMeiSMSConfig   亿美短信发送配置类

6、enums
    1)、SexEnum  用户性别枚举类（Mybatis-Plus枚举）

7、exception
    1)、BusinessException  自定义业务异常，用于全局缓存测试

8、handler
    1)、GlobalExceptionHandler   全局异常处理类
    2)、MyMetaObjectHandler      Mybatis-Plus属性自动填充处理类

9、intercepter
    1)、GlobalCacheInterceptor   全局缓存拦截器，与全局缓存响应体增强类配合

10、mapper
    1)、AccountInfoMapper    账户信息Mapper类，用于RocketMQ分布式事务测试
    2)、BatisUserAnnoMapper  Mybatis 注解 Mapper类，用于Mybatis原生注解方式测试
    3)、BatisUserXmlMapper   Mybatis xml  Mapper类，用于Mybatis原生xml方式测试
    4)、MpUserMapper         Mybatis-Plus Mapper类，用于Mybatis-Plus方式测试

11、utils
    1)、yimei 亿美短信发送工具包

12、vo
    1)、AccountChangeEvent   账户改变事件类，用于RocketMQ分布式事务测试
    2)、Response             项目MVC通用返回信息实体类