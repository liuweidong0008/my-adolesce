package com.adolesce.dubbo.api;


import com.adolesce.dubbo.domain.UserDemo;

public interface UserDemoApi {

    UserDemo queryById(Long id);
}
