package com.adolesce.server.controller;

import com.adolesce.common.entity.User;
import com.adolesce.common.vo.Response;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/**
 * 请求方式演示类
 */
@Slf4j
@RestController
@RequestMapping("requestStyle")
public class RequestStyleController {
    /**
     * 接参方式1：将参数放在URL路径上或者表单方式进行传参
     * 调用示例：http://localhost:8081/my_adolesce/requestStyle/test1?userName=刘威东&age=4
     *
     * @param userName 姓名
     * @param age      年龄
     *
     * 备注：参数可不传
     */
    @RequestMapping("test1")
    public String requestTest1(String userName, Integer age) {
        //userName = new String(userName.getBytes("ISO-8859-1"),"utf-8");
        System.out.println("userName:" + userName + ",age:" + age);
        return userName;
    }

    /**
     * 接参方式2：将参数放在URL路径上或者表单方式进行传参，通过@RequestParam进行参数名、是否必传设置
     * 调用示例：http://localhost:8081/my_adolesce/requestStyle/test2?userName=刘威东
     *
     * @param userName 姓名
     * @param age      年龄
     *
     * 备注：@RequestParam用于给参数起别名、设置默认值，并且可设置参数是否是必传，默认为必传，否则请求报400
     *       @RequestParam(value = "userName", required = false, defaultValue = "World")
     */
    @RequestMapping("test2")
        public String requestTest2(@RequestParam String userName,
                               @RequestParam(value = "age", required = false) Integer age1,
                               String age) {
        System.out.println("userName:" + userName + ",age1:" + age1 + "age" + age);
        return userName;
    }

    /**
     * 接参方式3：将参数放在URL路径上或者表单方式进行传参，使用Map进行接参(不可用，接收不到参数)
     * 调用示例：http://localhost:8081/my_adolesce/requestStyle/test3?userName=刘威东&age=8&address=湖南省长沙市东方红路
     *
     * @param paramMap 接参map
     *
     * 备注：通过Map接参必须标注@RequestParam注解，否则接收不到参数
     */
    @RequestMapping("test3")
    public String requestTest3(Map<String, Object> paramMap) {
        System.out.println(JSON.toJSONString(paramMap));
        return JSON.toJSONString(paramMap);
    }

    /**
     * 接参方式4：将参数放在URL路径上或者表单方式进行传参，使用Map进行接参并标注@RequestParam注解
     * 调用示例：http://localhost:8081/my_adolesce/requestStyle/test3?userName=刘威东&age=8&address=湖南省长沙市东方红路
     *
     * @param paramMap 接参map
     *
     * 备注：参数可不传
     */
    @RequestMapping("test4")
    public String requestTest4(@RequestParam Map<String, Object> paramMap) {
        System.out.println(JSON.toJSONString(paramMap));
        return JSON.toJSONString(paramMap);
    }

    /**
     * 接参方式5：将参数放在URL路径上或者表单方式进行传参，使用对象进行接参
     * 调用示例：http://localhost:8081/my_adolesce/requestStyle/test5?userName=刘威东&age=8&address.city=湖南省长沙市&address.street=东方红路
     *
     * @param user 接参对象
     *
     * 备注：参数可不传
     */
    @RequestMapping("test5")
    public String requestTest5(User user) {
        System.out.println(JSON.toJSONString(user));
        return JSON.toJSONString(user);
    }

    /**
     * 接参方式6：将参数放在URL路径上或者表单方式进行传参，使用对象进行接参并标注@RequestParam注解(不可用，接收不到参数)
     * 调用示例：http://localhost:8081/my_adolesce/requestStyle/test5?userName=刘威东&age=8&address.city=湖南省长沙市&address.street=东方红路
     *
     * @param user 接参对象
     *
     * 备注：通过对象接参不能标注@RequestParam注解，否则接收不到参数
     */
    @RequestMapping("test6")
    public String requestTest6(@RequestParam(required = false, value = "user") User user) {
        System.out.println(JSON.toJSONString(user));
        return JSON.toJSONString(user);
    }

    /**
     * 接参方式7：将参数放在请求体以JSON格式进行传参，使用Map进行接参并标注@RequestBody注解
     * 调用示例：http://localhost:8081/my_adolesce/requestStyle/test7
     *         {"userName":"张三","age":12}
     *
     * @param paramMap
     *
     * 备注：
     *     1、发起post请求，后端RequestBody方式接参（postman 在Body raw 最右边选JSON）
     *     2、@RequestBody用于post请求请求体json格式接参，并将其转换成对应的数据类型。不能用于get请求
     */
    @RequestMapping("test7")
    public Object requestTest7(@RequestBody Map<String, Object> paramMap) {
        System.out.println(JSON.toJSONString(paramMap));
        return paramMap;
    }

    /**
     * 接参方式8：将参数放在请求体以JSON格式进行传参，使用对象进行接参并标注@RequestBody注解
     * 调用示例：http://localhost:8081/my_adolesce/requestStyle/test8
     *         {"userName":"张三","age":12}
     *
     * @param user 接参对象
     */
    @PostMapping("test8")
    public Object requestTest8(@RequestBody User user) {
        System.out.println(JSON.toJSONString(user));
        return user;
    }

    /**
     * 接参方式9：将参数放在请求体以JSON格式进行传参，使用List<对象>进行接参并标注@RequestBody注解
     * 调用示例：http://localhost:8081/my_adolesce/requestStyle/test9
     *      [
     *         { "userName":"张三","age":12},
     *         { "userName":"李四","age":18}
     *      ]
     *
     * @param  users 接参对象集合
     */
    @PostMapping("test9")
    public Object requestTest9(@RequestBody List<User> users) {
        System.out.println(JSON.toJSONString(users));
        return users;
    }

    /**
     * 接参方式10：将参数放在请求体以JSON格式进行传参，使用Map<key,对象>进行接参并标注@RequestBody注解
     * 调用方式：http://localhost:8081/my_adolesce/requestStyle/test10
     *       {
     *          "1":{ "userName":"张三","age":12},
     *          "2":{ "userName":"李四","age":18}
     *       }
     *
     * @param userMap 接参对象Map
     */
    @PostMapping("test10")
    public Object requestTest10(@RequestBody Map<String, User> userMap) {
        System.out.println(JSON.toJSONString(userMap));
        return userMap;
    }

    /**
     * 接参方式11：将参数以逗号分隔字符串进行传递，后端用List进行接收
     * 调用示例：http://localhost:8081/my_adolesce/requestStyle/test11?ids=1,2,3,4
     *
     * @param ids id字符串
     */
    @RequestMapping("test11")
    public String requestTest11(@RequestParam List<String> ids) {
        log.debug("ids:{}", JSON.toJSON(ids));
        return JSON.toJSONString(ids);
    }

    /**
     * 接参方式12：参数以路径变量的形式放在URL路径上，后端用PathVariable方式进行接参
     * 调用示例：http://localhost:8081/my_adolesce/requestStyle/test12/刘威东/12
     *
     * @param userName
     * @param age
     * @return 调用：
     */
    @RequestMapping("test12/{userName}/{age}")
    public String requestTest12(@PathVariable("userName") String userName, @PathVariable Integer age) {
        return userName;
    }

    /**
     * 接参方式13：过HttpServletRequest进行参数接收获取
     * 调用示例：http://localhost:8081/my_adolesce/requestStyle/test13?userName=lwd
     *
     * @param request
     */
    @RequestMapping("test13")
    public String test13(HttpServletRequest request) {
        String userName = request.getParameter("userName");
        String age = request.getParameter("age");
        System.out.println("userName:" + userName);
        System.out.println("age:" + age);
        return userName;
    }

    /**
     * 接参方式14：测试获取请求头
     * @param token
     */
    @RequestMapping("test14")
    public String test14(@RequestHeader("Authorization") String token, HttpServletRequest request) {
        System.out.println("token:" + token);
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            //获取每个请求头名称
            String headerName = headerNames.nextElement();
            //跟距请求头获取请求值
            String value = request.getHeader(headerName);
            System.out.println(headerName + " : " + value);
        }
        return token;
    }

     /**
     * 接参方式15： 测试获取cookie（Head的Cookie key=value形式，逗号分割）
     * @param mycookie
     */
    @RequestMapping("test15")
    public String test15(@CookieValue(value = "mycookie",required = false) String mycookie, HttpServletRequest request) {
        System.out.println("mycookie:" + mycookie);
        System.out.println(request.getCookies());
        return mycookie;
    }

    /**
     * 接参方式16：Spring也支持URL中的矩阵变量，矩阵变量可以出现在任何路径片段中，每一个矩阵变量都用分号（;）隔开
     * 调用示例：http://localhost:8081/my_adolesce/requestStyle/test16-1/id=123;name=lwd/pets/status=l;age=5
     *
     * @PathVariable 能接收到矩阵变量中多个;分割的第一个参数
     * @MatrixVariable 中 parhVar的名称与占位符的名称保持一致,value的名称与前端的Key值名称保持一致
     */
    @RequestMapping(value = "test16-1/{msg1}/pets/{msg2}")
    public void test16_1(@PathVariable String msg1,
                       @PathVariable String msg2,
                       @MatrixVariable(pathVar = "msg1", value = "name") String name,
                       @MatrixVariable(pathVar = "msg2", value = "status") String status) {
        System.out.println("name:" + name + ":" + ",status:" + status);
        System.out.println("msg1:" + msg1 + ":" + "msg2:" + msg2);
    }

    /**
     * http://localhost:8081/my_adolesce/requestStyle/test16-2/color=red;year=2012
     * http://localhost:8081/my_adolesce/requestStyle/test16-2/color=red,green,blue
     * http://localhost:8081/my_adolesce/requestStyle/test16-2/color=red;color=green;color=blue
     */
    @RequestMapping("test16-2/{param}")
    public String test16_2(@MatrixVariable(pathVar = "param", value = "color") String[] yanse) {
        Arrays.stream(yanse).forEach(System.out::println);
        return yanse.toString();
    }

    /**
     * http://localhost:8081/my_adolesce/requestStyle/test16-3/s1/1;a=1/s2/2;b=2/s3/3;c=3
     */
    @RequestMapping(value = "test16-3/s1/{param1}/s2/{param2}/s3/{param3}")
    public void test16_3(@PathVariable String param1,
                       @PathVariable String param2,
                       @PathVariable String param3,
                       @MatrixVariable(pathVar = "param1", value = "a") String a,
                       @MatrixVariable(pathVar = "param2", value = "b", required = false) String b,
                       @MatrixVariable(pathVar = "param3", value = "c", required = false) String c) {
        System.out.println("param1=====》" + param1);
        System.out.println("param2=====》" + param2);
        System.out.println("param3=====》" + param3);
        System.out.println("a======》" + a);
        System.out.println("b======》" + b);
        System.out.println("c======》" + c);
    }

    /**
     * 上传文件
     *
     * @param multipartFiles
     * @return
     */
    @PostMapping("upload")
    public Response upload(@RequestParam(value = "files", required = false) List<MultipartFile> multipartFiles,
                           @RequestHeader(value = "Authorization", required = false) String token,
                           String userName,
                           @RequestHeader(value = "myHead") String myHead,
                           HttpServletRequest request) {
        multipartFiles.stream().forEach(file -> System.out.println(file.getOriginalFilename()));

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            //获取每个请求头名称
            String headerName = headerNames.nextElement();
            //跟距请求头获取请求值
            String value = request.getHeader(headerName);
            System.out.println(headerName + " : " + value);
        }
        return Response.success();
    }
}
