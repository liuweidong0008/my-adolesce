package com.adolesce.cloud.db.service;

import com.adolesce.cloud.db.mapper.MpUserMapper;
import com.adolesce.cloud.dubbo.domain.db.MpUser;
import com.adolesce.cloud.dubbo.enums.SexEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

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
    public void addMpUserToDB8(){
        MpUser user = getMpUser(8);
        mpUserMapper.insert(user);
        int i = 1/0;
    }


    private MpUser getMpUser(Integer num) {
        MpUser user = new MpUser();
        user.setUserName("itcast" + num);
        user.setPassword("itheima" + num );
        user.setName("事务测试用户" + num);
        user.setAge(88);
        user.setSex(SexEnum.MAN);
        user.setPhone("18301327332");
        user.setBirthday(LocalDate.now());
        return user;
    }
}
