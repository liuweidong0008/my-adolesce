package com.adolesce.cloud.db.controller;

import com.adolesce.cloud.db.mapper.MpUserMapper;
import com.adolesce.cloud.db.service.SpringTransactionDemoService2;
import com.adolesce.cloud.dubbo.domain.db.MpUser;
import com.adolesce.cloud.dubbo.enums.SexEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2022/4/26 13:21
 */

@Service
@Slf4j
public class SpringTransactionDemoService {
    @Autowired
    private MpUserMapper mpUserMapper;
    @Autowired
    private SpringTransactionDemoService springTransactionDemoService;
    @Autowired
    private SpringTransactionDemoService2 springTransactionDemoService2;

    /**
     * 加入事务、不调用其他方法（事务生效）
     */
    public void addMpUserToDB1(){
        MpUser user = getMpUser(1);
        mpUserMapper.insert(user);
    }

    /**
     * 加入事务、最后抛出【运行时异常】（事务生效：出现异常会回滚）
     *
     */
    @Transactional
    public void addMpUserToDB2(){
        MpUser user = getMpUser(2);
        this.mpUserMapper.insert(user);
        int i = 1 / 0;
    }

    /**
     * 加入事务、最后抛出【运行时异常】、自行try catch（事务失效1：异常会被try catch吞掉，不会进行回滚）
     * 事务失效场景1 ： 异常会被try catch吞掉
     */
    public void addMpUserToDB3(){
        try {
            MpUser user = getMpUser(3);
            this.mpUserMapper.insert(user);
            int i = 1 / 0;
        }catch (Exception e){
            log.error("保存用户信息出错：{}",e);
        }
    }

    /**
     * 加入事务、最后抛出【检查型异常】（事务失效2：出现异常不会回滚）注意：手动指定rollback = ParseException.class后会进行回滚
     * 事务失效场景2：抛出的是检查型异常
     * @throws ParseException
     */
    @Transactional //(rollbackFor = {ParseException.class})
    public void addMpUserToDB4() throws ParseException {
        MpUser user = getMpUser(4);
        mpUserMapper.insert(user);
        new SimpleDateFormat("yyyy-MM-dd").parse("123123");
    }

    /**
     * 加入事务、最后抛出【检查型异常】、自行try catch（事务失效：出现异常不会回滚）
     * @throws ParseException
     */
    @Transactional
    public void addMpUserToDB5(){
        try {
            MpUser user = getMpUser(5);
            mpUserMapper.insert(user);
            new SimpleDateFormat("yyyy-MM-dd").parse("123123");
        }catch (Exception e){
            log.error("保存用户信息出错：{}",e);
        }
    }

    /**
     * 当前方法无事务，使用this调用当前类的事务方法（事务失效3：事务方法中出现异常不会回滚）
     * 事务失效场景3：本类无事务方法调用本类有事务方法
     * 原理：发生自调用，类里面使用this调用本类的方法，此时这个this不是代理对象，所以事务失效
     */
    public void addMpUserToDB6(){
        addMpUserToDB2();
    }

    /**
     * 当前方法无事务，使用spring容器中对象引用调用当前类的事务方法（事务生效：事务方法中出现异常会回滚）
     * 原理：发生自调用，类里面使用容器中的代理类调用的事务方法，所以事务生效
     */
    public void addMpUserToDB7(){
        springTransactionDemoService.addMpUserToDB2();
    }

    public void addMpUserToDB7_1(){
        ((SpringTransactionDemoService)AopContext.currentProxy()).addMpUserToDB2();
    }

    /**
     * 当前方法无事务，调用其他类的事务方法（事务生效：事务方法中出现异常会回滚）
     * 原理：发生自调用，类里面使用容器中其他类的代理对象调用的事务方法，所以事务生效
     */
    public void addMpUserToDB8(){
        springTransactionDemoService2.addMpUserToDB8();
    }

    /**
     * 当前类有事务方法调用当前类的事务方法（事务生效，下游的事务方法中出现异常会进行全面回滚）
     */
    @Transactional
    public void addMpUserToDB9(){
        MpUser user = getMpUser(9);
        this.mpUserMapper.insert(user);
        addMpUserToDB2();
    }

    /**
     * 事务失效场景4：事务方法被非public修饰
     */
    @Transactional
    void addMpUserToDB10(){
        MpUser user = getMpUser(10);
        this.mpUserMapper.insert(user);
        int i = 1 / 0;
    }

    /**
     * 事务失效场景5：事务方法被final修饰
     */
    @Transactional
    public final void addMpUserToDB11(){
        MpUser user = getMpUser(11);
        this.mpUserMapper.insert(user);
    }

    /**
     * 事务失效场景6：设置了错误的传播行为
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void addMpUserToDB12(){
        MpUser user = getMpUser(12);
        this.mpUserMapper.insert(user);
        int i = 1 / 0;
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
