package com.adolesce.cloud.dubbo.api;


import com.adolesce.cloud.dubbo.domain.UserDemo;

public interface UserDemoApi {

    UserDemo queryById(Long id);
}
