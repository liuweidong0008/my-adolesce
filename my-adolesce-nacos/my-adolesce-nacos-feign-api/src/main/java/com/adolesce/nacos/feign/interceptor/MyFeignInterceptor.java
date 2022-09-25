package com.adolesce.nacos.feign.interceptor;

import com.adolesce.nacos.feign.utils.UserHolder;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class MyFeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        if(Objects.isNull(UserHolder.getUser())){
            return;
        }
        template.header("authorization", UserHolder.getUser().toString());
    }
}