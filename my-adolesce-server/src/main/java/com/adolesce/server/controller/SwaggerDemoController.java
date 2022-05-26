package com.adolesce.server.controller;

import com.adolesce.common.swagger.bean.SwaggerUserDto;
import com.adolesce.common.swagger.bean.SwaggerUserDto2;
import com.adolesce.common.swagger.bean.SwaggerUserVo;
import com.adolesce.common.vo.Response;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

/**
 * https://www.cnblogs.com/smallfa/p/15098992.html
 * @author Administrator
 * @version 1.0
 * @description: Swagger演示控制类
 *
 * 原生swagger访问地址：http://localhost:8081/my_adolesce/swagger-ui.html
 *
 * knife4j访问地址：http://localhost:8081/my_adolesce/doc.html
 * 整合knife4j swagger访问地址：http://localhost:8081/my_adolesce/swagger-ui/index.html
 */
@RestController
@RequestMapping("/swagger/demo")
@Api(value = "我是Swagger演示请求类", tags = {"我是Swagger演示请求类~~"})
public class SwaggerDemoController {
    @ApiOperation(value = "Swagger测试1", notes = "通过用户名、手机号、年龄获取用户信息")
    @GetMapping("/test1")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "String",paramType="query"),
            @ApiImplicitParam(name = "phone", value = "手机号", required = true, dataType = "String",paramType="query"),
            @ApiImplicitParam(name = "age", value = "年龄", required = true, dataType = "Integer",paramType="query")
    })
    public Response test1(@ApiParam(name = "username",required = true,value = "姓名") String username, String phone, Integer age) {
        return Response.success();
    }

    @ApiOperation(value = "Swagger测试2", notes = "通过用户名、手机号、年龄获取用户信息")
    @GetMapping("/test2/{userName}/{phone}/{age}")
    public SwaggerUserVo test2(
            @ApiParam(name = "username", value = "用户名", required = true) @PathVariable("userName") String userName,
            @ApiParam(name = "phone", value = "手机号", required = true) @PathVariable("phone") String phone,
            @ApiParam(name = "age", value = "年龄", required = true) @PathVariable("age") String age) {
        return new SwaggerUserVo();
    }

    @ApiOperation(value = "Swagger测试3", notes = "通过条件获取用户信息")
    @PostMapping("/test3")
    public SwaggerUserVo test3(SwaggerUserDto user) {
        return new SwaggerUserVo();
    }

    @ApiOperation(value = "Swagger测试4", notes = "通过条件获取用户信息")
    @PostMapping("/test4")
    public SwaggerUserVo test4(SwaggerUserDto2 user) {
        return new SwaggerUserVo();
    }

    @ApiOperation(value = "Swagger测试5", notes = "通过条件获取用户信息")
    @PostMapping("/test5")
    public SwaggerUserVo test5(@RequestBody SwaggerUserDto2 user) {
        return new SwaggerUserVo();
    }
}
