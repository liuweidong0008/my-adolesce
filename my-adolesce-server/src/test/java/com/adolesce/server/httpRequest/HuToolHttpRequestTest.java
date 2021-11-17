package com.adolesce.server.httpRequest;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.adolesce.common.entity.User;
import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2021/7/3 20:37
 */
public class HuToolHttpRequestTest {
    private final static String SERVER_UTL = "http://127.0.0.1:8081/my_adolesce/requestStyle/";

    @Test
    public void test1() {
        //链式构建请求
        HttpResponse httpResponse = HttpRequest.get(SERVER_UTL + "test1" + "?userName=刘威东&age=4") //构建GET请求，请求参数放在URL上
                .timeout(20000)//超时，毫秒                      //设置响应超时时间
                .execute();                     //执行请求
        System.out.println(httpResponse);
    }

    @Test
    public void test2() {
        //链式构建请求
        HttpResponse httpResponse = HttpRequest.get(SERVER_UTL + "test1")
                .header("myHeader", "222")       //设置请求头
                .header("Authorization", "333")  //设置请求头

                .form("userName", "刘威东")       //设置请求体（key-value形式）
                .form("age", 30)                 //设置请求体（key-value形式）
                .cookie("mycookie1=123;mycookie2=456")       //设置cookie
                .timeout(20000)//超时，毫秒
                .execute();
        System.out.println(httpResponse);
    }

    @Test
    public void test3() {
        File file = new File("D:/pictrues/hourse2.jpg");
        //链式构建请求
        HttpResponse httpResponse = HttpRequest.post(SERVER_UTL + "upload")
                .header("myHead", "222")         //设置请求头
                .header("Authorization", "333")  //设置请求头
                .form("userName", "张三")        //设置请求体（key-value形式）
                .form("files", file)                   //设置请求体（文件上传）
                .timeout(20000)//超时，毫秒
                .execute();
        System.out.println(httpResponse);
    }

    @Test
    public void test4() {
        User user = new User();
        user.setUserName("王五");
        user.setAge(88);

        List<User> myUserList = new ArrayList<>();
        myUserList.add(user);

        Map<String, User> myUserMap = new HashMap<>();
        myUserMap.put("1", user);
        //链式构建请求
        HttpResponse httpResponse = HttpRequest.post(SERVER_UTL + "test10")  //test7 test8 test9 test10
                .body(JSON.toJSONString(myUserMap))        //设置请求体（JSON格式）
                .timeout(20000)//超时，毫秒
                .execute();
        System.out.println(httpResponse);

    }
}
