package com.adolesce.server.controller;

import com.adolesce.cloud.dubbo.domain.db.MpUser;
import com.adolesce.common.annotation.Cache;
import com.adolesce.common.entity.User;
import com.adolesce.common.vo.Response;
import com.adolesce.server.service.impl.GlobalCachDemoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 通用缓存解决方案演示类
 * 1、所有请求都添加缓存吗？否，只针对GET请求
 * 2、所有GET请求都添加缓存吗？否，只针对添加了@Cach的请求方法
 * 3、缓存的时间长短如何控制？通过在@Cach注解上指定缓存时间来控制
 * 4、怎么控制缓存开关？通过配置文件配置缓存开关
 * 5、缓存请求拦截屏障和返回体增强屏障解决方:
 * 1)、拦截器 + 响应体增强（GlobalCacheInterceptor + WebConfig + GlobalCacheResponseBodyAdvice）
 * 2)、AOP 环绕通知（GlobalCacheAopAround）
 * 3)、AOP 前置通知 + 返回后通知 + 全局异常（GlobalCacheAop + GlobalExceptionHandler + BusinessException）
 */
@Slf4j
@RestController

/*所有的@Cacheable（）里面都有一个value＝“xxx”的属性，如果方法多了，写起来也是挺累的，如果可以一次性声明完 那就省事了，
    所以，有了@CacheConfig这个配置，一个类中可能会有多个缓存操作，而这些缓存操作可能是重复的。这个时候可以使用@CacheConfig。*/
//@CacheConfig(cacheNames = "cache-demo")
public class GlobalCachDemoController {
    @Autowired
    private GlobalCachDemoService globalCachDemoService;

    @GetMapping("action1")
    public Response action1() {
        Map<String, Object> result = this.globalCachDemoService.action1();
        return Response.success(result);
    }

    @Cache(time = "30",group = "myUser",key = "#user.getUserName(),#sex,#user.getAge()," +
            "T(com.adolesce.common.entity.User).getMsg(),#user.getAge() lt 10,#user.getUserName() eq 'lwd'")
    @GetMapping("action2")
    public Response action2(User user, String sex) {
        Map<String, Object> result = this.globalCachDemoService.action2();
        return Response.success(result);
    }

    @Cache
    @RequestMapping("action3")
    public Response action3() {
        Map<String, Object> result = this.globalCachDemoService.action3();
        return Response.success(result);
    }


    /**
     * @Cacheable 先查缓存，缓存中查到不进入controller，直接返回缓存结果，缓存中查不到则执行业务逻辑并将return结果放入缓存
     *    1、value:缓存的名称，在 spring 配置文件中定义，必须指定至少一个
     *        例如:@Cacheable(value=”mycache”)、@Cacheable(value={”cache1”,”cache2”}
     *    2、key：缓存的 key，可以为空，如果指定要按照 SpEL 表达式编写，如果不指定，则缺省按照方法的所有参数进行组合
     *        例如：@Cacheable(value=”testcache”,key=”#userName”)
     *    3、condition：缓存的条件，可以为空，使用 SpEL 编写，返回 true 或者 false，返回 true 才进行缓存
     *        例如：@Cacheable(value=”testcache”,condition=”#userName.length()>2”)
     *    4、unless：缓存的条件，可以为空，使用 SpEL 编写，返回 true 或者 false，返回true 不进行缓存
     *        例如：@Cacheable(value=”testcache”,unless=”#userName.length()>2”)
     */
    @RequestMapping("action4")
    @Cacheable(value="action4",key = "#p0")
    public Response action4(String userName) {
        // 方法内部实现不考虑缓存逻辑，直接实现业务
        System.out.println(userName);
        Map<String, Object> result = this.globalCachDemoService.action1();
        return Response.success(result);
    }

    /**
     * @CachePut 不查缓存，直接执行业务逻辑，并将return结果放入缓存
     */
    @RequestMapping("action5")
    @CachePut(value="action5",key = "#user.getUserName()")
    public Response action5(MpUser user) {
        // 方法内部实现不考虑缓存逻辑，直接实现业务
        System.out.println(user);
        Map<String, Object> result = this.globalCachDemoService.action1();
        return Response.success(result);
    }

    /**
     * @CachEvict 对缓存进行清空
     *    1、allEntries：是否清空所有缓存内容，缺省为 false，如果指定为 true，则方法调用后将立即清空所有缓存
     *         例如：@CachEvict(value=”testcache”,allEntries=true)
     *    2、beforeInvocation：是否在方法执行前就清空，缺省为 false，如果指定为 true，则在方法还没有执行的时候就清空缓存，缺省情况下，如果方法执行抛出异常，则不会清空缓存
     *         例如：@CachEvict(value=”testcache”，beforeInvocation=true)
     */
    @RequestMapping("action6")
    //@CacheEvict(value="action6",allEntries=true)// 清空action7 组下所有缓存
    @CacheEvict(value="action6",key = "#user.getUserName()") // 清空action7组下某个用户的缓存
    public Response action6(MpUser user) {
        System.out.println(user);
        Map<String, Object> result = this.globalCachDemoService.action1();
        return Response.success(result);
    }

    /**
     * //@Cacheable将在执行方法之前（此时通过condition 的#result还拿不到返回值）判断condition，如果返回true，则查缓存；
     *      @Cacheable(value = "user", key = "#id", condition = "#id lt 10")
     *      public User conditionFindById(final Lon
     * //@CachePut将在执行完方法后（#result就能拿到返回值了）判断condition，如果返回true，则放入缓存；
     *      @CachePut(value = "user", key = "#id", condition = "#result.username ne 'zhang'")
     *      public User conditionSave(final User
     * //@CachePut将在执行完方法后（#result就能拿到返回值了）判断unless，如果返回false，则放入缓存；（即跟condition相反）
     *      @CachePut(value = "user", key = "#user.id", unless = "#result.username eq 'zhang'")
     *      public User conditionSave2(final User
     * //@CacheEvict 在方法执行之后调用（#result能拿到返回值了）；且判断condition，如果返回true，则移除缓存；
     *      @CacheEvict(value = "user", key = "#user.id", beforeInvocation = false, condition = "#result.username ne 'zhang'")
     *      public User conditionDelete(final User user)
     *
     *      @CacheEvict(value = "user", key = "#user.id", condition = "#root.target.canCache() and #root.caches[0].get(#user.id).get().username ne #user.username", beforeInvocation = true)
     *      public void conditionUpdate(User user)
     *
     * //@Caching 有时候我们可能组合多个Cache注解使用；比如用户新增成功后，我们要添加id–>user；username—>user；email—>user的缓存；此时就需要@Caching组合多个注解标签了。
     *      @Caching(put = {
     *              @CachePut(value = "user", key = "#user.id"),
     *              @CachePut(value = "user", key = "#user.username"),
     *              @CachePut(value = "user", key = "#user.email")
     *      })
     *      public User save(User user)
     *
     *
     * Spring Cache提供了一些供我们使用的SpEL上下文数据，下表直接摘自Spring官方文档：
     *    名称	          位置	        描述	                                                                                示例
     *    methodName      root对象	    当前被调用的方法名	                                                                 #root.methodName
     *    method	      root对象	    当前被调用的方法	                                                                     #root.method.name
     *    target	      root对象	    当前被调用的目标对象	                                                                 #root.target
     *    targetClass	  root对象	    当前被调用的目标对象类	                                                             #root.targetClass
     *    args	          root对象	    当前被调用的方法的参数列表	                                                             #root.args[0]
     *    caches	      root对象	    当前方法调用使用的缓存列表（如@Cacheable(value={“cache1”, “cache2”})），则有两个cache	 #root.caches[0].name
     *    argument name   执行上下文     当前被调用的方法的参数，如findById(User user)，我们可以通过#id拿到参数	                 #user.id
     *    result	      执行上下文	    方法执行后的返回值（仅当方法执行之后的判断有效                                            #result
     *
     */

}
