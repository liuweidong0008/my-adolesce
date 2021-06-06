package com.adolesce.server.controller;

import com.adolesce.common.annotation.Cache;
import com.adolesce.common.vo.Response;
import com.adolesce.server.service.impl.GlobalCachDemoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 通用缓存解决方案
 * 1、所有请求都添加缓存吗？否，只针对GET请求
 * 2、所有GET请求都添加缓存吗？否，只针对添加了@Cach的请求方法
 * 3、缓存的时间长短如何控制？通过在@Cach注解上指定缓存时间来控制
 * 4、怎么控制缓存开关？通过配置文件配置缓存开关
 * 5、缓存请求拦截屏障和返回体增强屏障解决方:
 *      1)、拦截器 + 响应体增强（GlobalCacheInterceptor + WebConfig + GlobalCacheResponseBodyAdvice）
 *      2)、AOP 环绕通知（GlobalCacheAopAround）
 *      3)、AOP 前置通知 + 返回后通知 + 全局异常（GlobalCacheAop + GlobalExceptionHandler + BusinessException）
 */
@Slf4j
@RestController
public class GlobalCachDemoController {
    @Autowired
    private GlobalCachDemoService globalCachDemoService;

    @ResponseBody
    @GetMapping("action1")
    public Response action1() {
        Map<String,Object> result = this.globalCachDemoService.action1();
        return Response.success(result);
    }

    @ResponseBody
    @Cache
    @GetMapping("action2")
    public Response action2() {
        Map<String,Object> result = this.globalCachDemoService.action2();
        return Response.success(result);
    }

    @ResponseBody
    //@Cache
    @RequestMapping("action3")
    public Response action3() {
        Map<String,Object> result = this.globalCachDemoService.action3();
        return Response.success(result);
    }

    @ResponseBody
    @RequestMapping("action4")
    public Response action4() {
        Map<String,Object> result = this.globalCachDemoService.action4();
        return Response.success(result);
    }
}
