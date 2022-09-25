package com.adolesce.cloud.db.service;

import com.adolesce.cloud.db.controller.SpringTransactionDemoService;
import com.adolesce.cloud.db.mapper.MpUserMapper;
import com.adolesce.cloud.dubbo.domain.db.MpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2022/4/26 13:21
 */

@Service
public class SpringTransactionDemoService2 {
    @Autowired
    private MpUserMapper mpUserMapper;

    @Transactional
    public void addMpUserToDB10(){
        MpUser user = SpringTransactionDemoService.getMpUser(10);
        mpUserMapper.insert(user);
        int i = 1/0;
    }
}
