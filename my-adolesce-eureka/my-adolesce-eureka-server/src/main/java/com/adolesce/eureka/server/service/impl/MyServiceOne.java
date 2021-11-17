package com.adolesce.eureka.server.service.impl;

import com.adolesce.eureka.server.controller.MyController;
import com.adolesce.eureka.server.service.IMyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2021/7/19 18:56
 */
@Service
public class MyServiceOne implements IMyService {
    @Autowired
    private MyController myController;

    @Override
    public String getName() {
        return "my name is MyServiceOne";
    }
}
