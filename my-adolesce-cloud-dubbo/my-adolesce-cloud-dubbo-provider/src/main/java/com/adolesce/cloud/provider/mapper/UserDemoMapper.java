package com.adolesce.cloud.provider.mapper;


import com.adolesce.cloud.dubbo.domain.UserDemo;
import org.springframework.stereotype.Repository;

@Repository
public class UserDemoMapper {
    public UserDemo findById(Long id){
        return new UserDemo(id,"lisi","湖南省岳阳市~~");
    }
}