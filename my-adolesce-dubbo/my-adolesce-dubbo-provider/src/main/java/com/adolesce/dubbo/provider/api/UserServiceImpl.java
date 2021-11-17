package com.adolesce.dubbo.provider.api;

import com.adolesce.dubbo.domain.UserDemo;
import com.adolesce.dubbo.provider.mapper.UserMapper;
import com.adolesce.dubbo.api.UserDemoApi;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

//暴露dubbo服务，
@DubboService(version = "1.0.0")
public class UserServiceImpl implements UserDemoApi {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDemo queryById(Long id) {
        UserDemo user = userMapper.findById(id);
        user.setUsername(user.getUsername()+"v1.0");
        return user;
    }
}