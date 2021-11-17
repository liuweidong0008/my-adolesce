package com.adolesce.eureka.server.service.impl;

import com.adolesce.eureka.server.service.IMyService;
import org.springframework.stereotype.Service;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2021/7/19 18:56
 */
@Service
public class MyServiceTwo implements IMyService {
    @Override
    public String getName() {
        return "my name is MyServiceTwo";
    }
}
