package com.adolesce.cloud.provider.service;

import com.adolesce.cloud.dubbo.domain.UserDemo;
import com.adolesce.cloud.dubbo.api.UserDemoApi;
import com.adolesce.cloud.provider.mapper.UserDemoMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

//暴露dubbo服务，
@DubboService(version = "1.0.0")
public class UserDemoServiceImpl implements UserDemoApi {
    @Autowired
    private UserDemoMapper userMapper;

    @Override
    public UserDemo queryById(Long id) {
        UserDemo user = userMapper.findById(id);
        user.setUsername(user.getUsername()+"v1.0");
        return user;
    }
}