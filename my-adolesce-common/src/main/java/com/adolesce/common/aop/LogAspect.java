package com.adolesce.common.aop;

import com.adolesce.common.vo.ErrorResult;
import com.adolesce.common.vo.Response;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * 操作日志记录
 */
@Data
@Order(2)
@Aspect
@Component
@Slf4j
public class LogAspect {
    private ThreadLocal<Long> startTime = new ThreadLocal<>();

    /**
     * 定义请求日志切入点，其切入点表达式有多种匹配方式,这里是指定路径
     */
    //切入点表达式语法：execution(方法修饰符 返回类型 方法所属的包.类名.方法名称(方法参数)
    //1、execution(public * com.hs.demo.controller.*.*(..))     com.hs.demo.controller包中所有类的public方法都应用切面里的通知
    //2、execution(* com.hs.demo.service..*.*(..))     com.hs.demo.service包及其子包下所有类中的所有方法都应用切面里的通知
    //3、execution(* com.hs.demo.service.EmployeeService.*(..))     com.hs.demo.service.EmployeeService类中的所有方法都应用切面里的通知
    @Pointcut("execution(public * com.adolesce.*.controller.*.*(..))")
    public void logAspect() {
    }

    @Around("logAspect()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        String methodName = pjp.getSignature().getName();
        Object ret = null;
        try {
            // 接收到请求，记录请求内容
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();
            startTime.set(System.currentTimeMillis());
            //@Before
            System.err.println("**************【日志环绕前置通知】【" + methodName + "方法开始】**************");
            log.info("{}请求 - 请求Url:{}", request.getMethod(), URLDecoder.decode(request.getRequestURI().toString(), "UTF-8"));
            String header = request.getHeader("Content-Type");
            //后续若有表单请求在此处理
            if (StringUtils.isNotBlank(header) && header.contains("json")) {
                log.info("请求参数:{}", JSON.toJSONString(pjp.getArgs()));
            } else {
                log.info("请求参数:{}", JSON.toJSONString(request.getParameterMap()));
            }
            log.info("请求开始时间:{}", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            // 处理完请求，返回内容 (这句相当于method.invoke(obj,args)，通过反射来执行接口中的方法)
            ret = pjp.proceed();
            //@AfterReturning
            System.err.println("**************【日志环绕返回后通知】【" + methodName + "方法返回】**************");
            log.info("请求返回:{}", JSON.toJSONString(ret));
            log.info("请求耗时:{}毫秒", (System.currentTimeMillis() - startTime.get()));
            return ret;
        } catch (Exception e) {
            //@AfterThrowing
            System.err.println("**************【日志环绕异常通知】【" + methodName + "方法异常，异常信息：" + e + "】**************");
            e.printStackTrace();
            return Response.failure(ErrorResult.error().getErrMessage());
        } finally {
            //@After
            System.err.println("**************【日志环绕后置通知】【" + methodName + "方法结束】**************");
            return ret;
        }
    }

    @Before("logAspect()")
    public void before(JoinPoint point) {
        System.err.println("**************【日志前置通知】【" + point.getSignature().getName() + "方法开始】**************");
    }

    @After("logAspect()")
    public void after(JoinPoint point) {
        System.err.println("**************【日志后置通知】【" + point.getSignature().getName() + "方法结束】**************");
    }

    @AfterReturning(pointcut = "logAspect()", returning = "result")
    public void afterReturning(JoinPoint point, Object result) {
        System.err.println("**************【日志返回后通知】【" + point.getSignature().getName() + "方法返回】**************");
        log.info("请求返回:{} - AfterReturning", JSON.toJSONString(result));
    }

    @AfterThrowing(pointcut = "logAspect()", throwing = "e")
    public void afterThrowing(JoinPoint point, Throwable e) {
        System.err.println("**************【日志异常通知】【" + point.getSignature().getName() + "方法异常，异常信息：" + e + "】**************");
    }


    /*AOP五大通知
        @Before
        前置通知：目标方法之前执行
        @After
        后置通知：目标方法之后执行（始终执行）
        @AfterReturning
        返回通知：执行方法结束前执行（异常不执行）
        @AfterThrowing
        异常通知：出现异常的时候执行
        @Around
        环绕通知：环绕目标方法执行
    */

    //Spring-Boot-2.X 对应Spring5
    //正常情况访问
    // Around-Before ->> Before ->> 目标方法 ->> AfterReturning ->> After ->> Around-AfterReturning ->> Around-After

    //异常情况访问
    // Around-Before ->> Before ->> 目标方法 ->> AfterThrowing  ->> After ->> Around-AfterThrowing  ->> Around-After


    //Spring-Boot-1.X 对应Spring4
    //正常情况访问
    // Around-Before ->> Before ->> 目标方法 ->> Around-AfterReturning ->> After ->> AfterReturning

    //异常情况访问
    // Around-Before ->> Before ->> 目标方法 ->> Around-AfterThrowing  ->> After ->> AfterThrowing
}
