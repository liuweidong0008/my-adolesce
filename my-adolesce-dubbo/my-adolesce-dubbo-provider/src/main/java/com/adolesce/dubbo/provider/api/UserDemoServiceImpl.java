package com.adolesce.dubbo.provider.api;

import com.adolesce.dubbo.domain.UserDemo;
import com.adolesce.dubbo.provider.mapper.UserMapper;
import com.adolesce.dubbo.api.UserDemoApi;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

//暴露dubbo服务
@DubboService(version = "1.0.0",weight = 50)
public class UserDemoServiceImpl implements UserDemoApi {
    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDemo queryById(Long id) {
        System.out.println("Dubbo提供方：根据ID查询User信息  V1...");
        UserDemo user = userMapper.findById(id);
        user.setUsername(user.getUsername()+"v1.0");
        return user;
    }
}