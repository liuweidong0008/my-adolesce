package com.adolesce.dubbo.provider.mapper;

import com.adolesce.dubbo.domain.UserDemo;
import org.springframework.stereotype.Repository;

@Repository
public class UserMapper {
    public UserDemo findById(Long id){
        return new UserDemo(id,"张三","湖南省岳阳市");
    }
}