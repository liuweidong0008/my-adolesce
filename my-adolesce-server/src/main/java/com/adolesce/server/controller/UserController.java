package com.adolesce.server.controller;

import com.adolesce.common.vo.Response;
import com.adolesce.server.service.impl.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@Controller
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 发送短信验证码接口
     *
     * @param param
     * @return
     */
    @PostMapping("sendCode")
    public Response sendCheckCode(@RequestBody Map<String, String> param) {
        String phone = param.get("phone");
        Response response;
        try {
            response = this.userService.sendCode(phone);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("发送短信验证码失败~ phone = " + phone, e);
            response = Response.failure("短信验证码发送失败！");
        }
        return response;
    }

    /**
     * 用户登录
     *
     * @param param
     * @return
     */
    @PostMapping("login")
    public Response login(@RequestBody Map<String,String> param){
        String phone = param.get("phone");
        String code = param.get("code");
        Response response;
        try {
            response = this.userService.login(phone, code);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("用户登录失败~ phone = " + phone, e);
            response = Response.failure("用户登录失败！");
        }
        return response;
    }

    /**
     * 校验token，根据token查询用户数据
     *
     * @param token
     * @return
     */
    @GetMapping("{token}")
    public Response queryUserByToken(@PathVariable("token") String token) {
        return this.userService.queryUserByToken(token);
    }
}
