package com.adolesce.server.controller;

import com.adolesce.common.vo.Response;
import com.adolesce.server.service.impl.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("login")
public class LoginController {
    @Autowired
    private LoginService loginService;

    /**
     * 发送短信验证码接口
     *
     * @param param
     * @return
     */
    @PostMapping("sendCode")
    public Response sendCheckCode(@RequestBody Map<String, String> param) {
        String phone = param.get("phone");
        this.loginService.sendCode(phone);
        return Response.success();
    }

    /**
     * 用户登录
     *
     * @param param
     * @return
     */
    @PostMapping("login")
    public Response login(@RequestBody Map<String, String> param) {
        String phone = param.get("phone");
        String code = param.get("code");
        Map resultMap = this.loginService.login(phone, code);
        return Response.success(resultMap);
    }

    /**
     * 生成图形验证码
     */
    @GetMapping("/imageCaptcha")
    public void imageCaptcha(HttpServletResponse response,String type) throws IOException {
        loginService.outputImageCaptcha(response,type);
    }

    /**
     * 发送rabbitmq消息
     *
     * @return
     */
    @GetMapping("sendMqMsg")
    public Response sendMqMsg() {
        return this.loginService.sendMqMsg();
    }
}
