package com.adolesce.cloud.dubbo.controller;


import com.adolesce.cloud.dubbo.domain.UserDemo;
import com.adolesce.cloud.dubbo.api.UserDemoApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserDemoController {
    @DubboReference(version = "1.0.0",retries = 3,loadbalance = "random",timeout = 2000)
    private UserDemoApi userService;

    /**
     * 根据id查询用户
     */
    @GetMapping("/{id}")
    public UserDemo queryById(@PathVariable("id") Long id) {
        UserDemo user  = userService.queryById(id);
        return user;
    }
}
