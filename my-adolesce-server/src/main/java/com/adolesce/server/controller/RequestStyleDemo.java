package com.adolesce.server.controller;

import com.adolesce.common.bo.MyUser;
import com.adolesce.common.vo.Response;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
public class RequestStyleDemo {
    /**
     * 接参方式1
     *
     * @param userName 姓名
     * @param age      年龄
     * @return 调用：http://localhost:8081/my_adolesce/requestStyle/test1?userName=刘威东&age=4
     * 备注：参数可不传
     */
    @RequestMapping("test1")
    public String requestTest1(String userName, Integer age,
                               @RequestHeader(value = "myHeader",required = false) String myHeader,
                               @RequestHeader(value = "Authorization",required = false) String token,
                               @CookieValue("mycookie1")String mycookie1,
                               @CookieValue("mycookie2")String mycookie2) {
        //userName = new String(userName.getBytes("ISO-8859-1"),"utf-8");
        System.out.println("userName:" + userName + ",age:" + age);
        return userName;
    }

    /**
     * 接参方式2
     *
     * @param userName 姓名
     * @param age      年龄
     * @return 调用：http://localhost:8081/my_adolesce/requestStyle/test2?userName=刘威东
     * @RequestParam(value = "userName", required = false, defaultValue = "World")
     * 备注：@RequestParam用于给参数起别名、设置默认值，并且可设置参数是否是必传，默认为必传，否则请求报400
     */
    @RequestMapping("test2")
    public String requestTest2(@RequestParam String userName,
                               @RequestParam(value = "age", required = false) Integer age1, String age) {
        //userName = new String(userName.getBytes("ISO-8859-1"),"utf-8");
        System.out.println("userName:" + userName + ",age1:" + age1 + "age" + age);
        return userName;
    }

    /**
     * 接参方式3(不可用，接收不到参数)
     *
     * @param paramMap 接参map
     * @return 调用：http://localhost:8081/my_adolesce/requestStyle/test3?userName=刘威东&age=8&address=湖南省长沙市东方红路
     */
    @RequestMapping("test3")
    public String requestTest3(Map<String, Object> paramMap) {
        System.out.println(JSON.toJSONString(paramMap));
        return JSON.toJSONString(paramMap);
    }

    /**
     * 接参方式4
     *
     * @param paramMap 接参map
     * @return 调用：http://localhost:8081/my_adolesce/requestStyle/test3?userName=刘威东&age=8&address=湖南省长沙市东方红路
     * 备注：参数可不传
     */
    @RequestMapping("test4")
    public String requestTest4(@RequestParam Map<String, Object> paramMap) {
        System.out.println(JSON.toJSONString(paramMap));
        return JSON.toJSONString(paramMap);
    }

    /**
     * 接参方式5
     *
     * @param myUser
     * @return 调用：http://localhost:8081/my_adolesce/requestStyle/test5?userName=刘威东&age=8&address.city=湖南省长沙市&address.street=东方红路
     * 备注：参数可不传
     */
    @RequestMapping("test5")
    public String requestTest5(MyUser myUser) {
        System.out.println(JSON.toJSONString(myUser));
        return JSON.toJSONString(myUser);
    }

    /**
     * 接参方式6(不可用，接收不到参数)
     *
     * @param myUser
     * @return 调用：http://localhost:8081/my_adolesce/requestStyle/test6?userName=lwd&age=12&address=123123
     */
    @RequestMapping("test6")
    public String requestTest6(@RequestParam(required = false,value = "myUser") MyUser myUser) {
        System.out.println(JSON.toJSONString(myUser));
        return JSON.toJSONString(myUser);
    }


    /**
     * post请求RequestBody方式接参（Body raw 最右边选JSON）
     *
     * @param paramMap
     * @return 调用：http://localhost:8081/my_adolesce/requestStyle/test7
     * @RequestBody用于post请求，不能用于get请求
     * @RequestBody注解可以接收json格式的数据，并将其转换成对应的数据类型。
     */
    @RequestMapping("test7")
    public Object requestTest7(@RequestBody Map<String, Object> paramMap) {
        System.out.println(JSON.toJSONString(paramMap));
        return paramMap;
    }

    /**
     * post请求RequestBody方式接参（Body raw 最右边选JSON）
     *
     * @param myUser
     * @return 调用：http://localhost:8081/my_adolesce/requestStyle/test8
     * 备注：其中属性至少传一个
     * <p>
     * { "userName":"张三","age":12}
     */
    @PostMapping("test8")
    public Object requestTest8(@RequestBody MyUser myUser) {
        System.out.println(JSON.toJSONString(myUser));
        return myUser;
    }

    /**
     * post请求RequestBody方式接参（Body raw 最右边选JSON）
     *
     * @param myUsers
     * @return 调用：http://localhost:8081/my_adolesce/requestStyle/test9
     * <p>
     * [
     *  { "userName":"张三","age":12},
     *  { "userName":"李四","age":18}
     * ]
     */
    @PostMapping("test9")
    public Object requestTest9(@RequestBody List<MyUser> myUsers) {
        System.out.println(JSON.toJSONString(myUsers));
        return myUsers;
    }

    /**
     * post请求RequestBody方式接参（Body raw 最右边选JSON）
     *
     * @param myUsers
     * @return 调用：http://localhost:8081/my_adolesce/requestStyle/test10
     * {
     * "1":{ "userName":"张三","age":12},
     * "2":{ "userName":"李四","age":18}
     * }
     */
    @PostMapping("test10")
    public Object requestTest10(@RequestBody Map<String, MyUser> myUsers) {
        System.out.println(JSON.toJSONString(myUsers));
        return myUsers;
    }

    /**
     * 测试逗号分隔字符串后端List接收
     *
     * @param ids
     * @return 调用：http://localhost:8081/my_adolesce/requestStyle/test11?ids=1,2,3,4
     */
    @RequestMapping("test11")
    public String requestTest11(@RequestParam List<String> ids) {
        log.debug("ids:{}", JSON.toJSON(ids));
        return JSON.toJSONString(ids);
    }

    /**
     * 测试PathVariable方式接参
     *
     * @param userName
     * @return 调用：http://localhost:8081/my_adolesce/requestStyle/test12/刘威东
     */
    @RequestMapping("test12/{userName}/{age}")
    public String requestTest12(@PathVariable("userName") String userName, @PathVariable Integer age) {
        return userName;
    }

    /**
     * 测试通过HttpServletRequest接收
     *
     * @param request
     * @return
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
     * 测试获取请求头
     * @RequestHeader 默认必填
     *
     * @param token
     * @return
     */
    @RequestMapping("test14")
    public String test14(@RequestHeader("Authorization")String token,HttpServletRequest request) {
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
     * 测试获取cookie
     *
     * @param mycookie
     * @return
     */
    @RequestMapping("test15")
    public String test15(@CookieValue("mycookie")String mycookie,HttpServletRequest request) {
        System.out.println("mycookie:" + mycookie);

        System.out.println(request.getCookies());
        return mycookie;
    }

    /**
     * Spring也支持URL中的矩阵变量，矩阵变量可以出现在任何路径片段中，每一个矩阵变量都用分号（;）隔开
     * @MatrixVariable 中 parhVar的名称与占位符的名称保持一致,value的名称与前端的Key值名称保持一致
     *
     * @PathVariable 能接收到矩阵变量中多个;分割的第一个参数
     *
     * 如：/test16/id=123;name=lwd/pets/status=l;age=5
     */
    @GetMapping(value = "test16/{msg1}/pets/{msg2}")
    public void test16(@PathVariable String msg1,
                   @PathVariable String msg2,
                   @MatrixVariable(pathVar="msg1",value="name") String name,
                   @MatrixVariable(pathVar="msg2",value="status") String status) {
        System.out.println("name:"+name+":"+",status:"+status);
        System.out.println("msg1:"+msg1+":"+"msg2:"+msg2);
    }

    /**
     * /test17/color=red;year=2012
     * /test17/color=red,green,blue
     * /test17/color=red;color=green;color=blue
     */
    @RequestMapping("test17/{param}")
    public String test17(@MatrixVariable(pathVar="param",value="color")String[] yanse){
        Arrays.stream(yanse).forEach(System.out::println);
        return yanse.toString();
    }

    /**
     * test18/param1/1;a=1/param2/2;b=2/param3/3;c=3
     */
    @GetMapping(value ="test18/s1/{param1}/s2/{param2}/s3/{param3}")
    public void test18(@PathVariable String param1,
                     @PathVariable String param2,
                     @PathVariable String param3,
                     @MatrixVariable(pathVar="param1",value="a") String a,
                     @MatrixVariable(pathVar="param2",value="b",required =false) String b,
                     @MatrixVariable(pathVar="param3",value="c",required =false) String c){
        System.out.println("param1=====》"+param1);
        System.out.println("param2=====》"+param2);
        System.out.println("param3=====》"+param3);
        System.out.println("a======》"+a);
        System.out.println("b======》"+b);
        System.out.println("c======》"+c);
    }

    /**
     * 上传文件
     *
     * @param multipartFiles
     * @return
     */
    @PostMapping("upload")
    public Response upload(@RequestParam(value = "files",required = false) List<MultipartFile> multipartFiles,
                           @RequestHeader(value = "Authorization",required = false)String token, String userName,
                           @RequestHeader(value = "myHead")String myHead,HttpServletRequest request) {
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

    @RequestMapping("register")
    public void register(MyUser myUser,HttpServletResponse response) throws UnsupportedEncodingException {
        String url = "redirect:/loginPage?username=" + URLEncoder.encode(myUser.getUserName(),"UTF-8") + "&age=" +
                URLEncoder.encode(myUser.getAge().toString(),"UTF-8");
        response.setHeader("Location",url);
    }
}
