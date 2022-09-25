package com.adolesce.cloud.db.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.adolesce.cloud.db.mapper.MpUserMapper;
import com.adolesce.cloud.db.service.SpringTransactionDemoService2;
import com.adolesce.cloud.dubbo.domain.db.MpUser;
import com.adolesce.cloud.dubbo.enums.SexEnum;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;

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
     *
     * 加入事务、不调用其他方法（事务生效）
     */
    @Transactional
    public void addMpUserToDB1(){
        MpUser user = getMpUser(1);
        mpUserMapper.insert(user);
    }

    /**
     *
     * 不加入事务、最后抛出【运行时异常】（出现异常不会回滚）
     */
    public void addMpUserToDB2(){
        MpUser user = getMpUser(2);
        this.mpUserMapper.insert(user);
        int i = 1 / 0;
    }

    /**
     *
     * 加入事务、最后抛出【运行时异常】（事务生效：出现异常会回滚）
     */
    @Transactional
    public void addMpUserToDB3(){
        MpUser user = getMpUser(3);
        this.mpUserMapper.insert(user);
        int i = 1 / 0;
    }

    /**
     *
     * 【事务失效场景1 ： 异常被try catch吞掉】
     * 加入事务、最后抛出【运行时异常】、自行try catch（事务失效1：异常会被try catch吞掉，不会进行回滚）
     */
    @Transactional
    public void addMpUserToDB4(){
        try {
            MpUser user = getMpUser(4);
            this.mpUserMapper.insert(user);
            int i = 1 / 0;
        }catch (Exception e){
            log.error("保存用户信息出错：{}",e);
        }
    }

    /**
     *
     * 【事务失效场景2：抛出的是检查型异常】
     * 加入事务、最后抛出【检查型异常】（事务失效2：出现异常不会回滚）注意：手动指定rollback = ParseException.class后会进行回滚
     */
    @Transactional //(rollbackFor = {ParseException.class})
    public void addMpUserToDB5() throws ParseException {
        MpUser user = getMpUser(5);
        mpUserMapper.insert(user);
        new SimpleDateFormat("yyyy-MM-dd").parse("123123");
    }

    /**
     *
     * 加入事务、最后抛出【检查型异常】, 手动指定rollback = ParseException.class后会进行回滚
     */
    @Transactional(rollbackFor = {ParseException.class})
    public void addMpUserToDB6() throws ParseException {
        MpUser user = getMpUser(6);
        mpUserMapper.insert(user);
        new SimpleDateFormat("yyyy-MM-dd").parse("123123");
    }

    /**
     *
     * 加入事务、最后抛出【检查型异常】、自行try catch（事务失效：出现异常不会回滚）
     */
    @Transactional(rollbackFor = {ParseException.class})
    public void addMpUserToDB7(){
        try {
            MpUser user = getMpUser(7);
            mpUserMapper.insert(user);
            new SimpleDateFormat("yyyy-MM-dd").parse("123123");
        }catch (Exception e){
            log.error("保存用户信息出错：{}",e);
        }
    }

    /**
     *
     * 【事务失效场景3：本类无事务方法调用本类有事务方法】
     * 当前方法无事务，使用this调用当前类的事务方法（事务失效3：事务方法中出现异常不会回滚）
     * 原理：发生自调用，类里面使用this调用本类的方法，此时这个this不是代理对象，所以事务失效
     */
    public void addMpUserToDB8(){
        addMpUserToDB3();
    }

    /**
     *
     * 当前方法无事务，使用spring容器中对象引用调用当前类的事务方法（事务生效：事务方法中出现异常会回滚）
     * 原理：发生自调用，类里面使用容器中的代理类调用的事务方法，所以事务生效
     */
    public void addMpUserToDB9(){
        springTransactionDemoService.addMpUserToDB3();
    }

    public void addMpUserToDB9_1(){
        ((SpringTransactionDemoService)AopContext.currentProxy()).addMpUserToDB3();
    }

    /**
     *
     * 当前方法无事务，调用其他类的事务方法（事务生效：事务方法中出现异常会回滚）
     * 原理：发生自调用，类里面使用容器中其他类的代理对象调用的事务方法，所以事务生效
     */
    public void addMpUserToDB10(){
        springTransactionDemoService2.addMpUserToDB10();
    }

    /**
     *
     * 当前类有事务方法调用当前类的事务方法（事务生效，下游的事务方法中出现异常会进行全面回滚）
     */
    @Transactional
    public void addMpUserToDB11(){
        MpUser user = getMpUser(11);
        this.mpUserMapper.insert(user);
        addMpUserToDB3();
    }

    /**
     *
     * 【事务失效场景4：事务方法被非public修饰】
     */
    @Transactional
    void addMpUserToDB12(){
        MpUser user = getMpUser(12);
        this.mpUserMapper.insert(user);
        int i = 1 / 0;
    }

    /**
     *
     * 【事务失效场景5：事务方法被final修饰】
     *  被final修饰的方法，代理类中是获取不到成员属性的
     */
    @Transactional
    public final void addMpUserToDB13(){
        MpUser user = getMpUser(13);
        this.mpUserMapper.insert(user);
    }

    /**
     *
     * 【事务失效场景6：设置了错误的传播行为】
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void addMpUserToDB14(){
        MpUser user = getMpUser(14);
        this.mpUserMapper.insert(user);
        int i = 1 / 0;
    }

    @Transactional
    public synchronized String add88MpUserToDB() throws InterruptedException {
        List<MpUser> users = this.mpUserMapper.selectList(Wrappers.<MpUser>lambdaQuery().eq(MpUser::getUserName, "测试用户88"));
        if(CollectionUtil.isEmpty(users)){
            MpUser user = getMpUser(88);
            user.setUserName("测试用户"+88);
            this.mpUserMapper.insert(user);
            return "添加用户88成功";
        }
        return "数据库已存在88用户，无需添加";
    }

    /*public  String add88MpUserToDB_() throws InterruptedException {
        //begin

        synchronized (this){
            List<MpUser> users = this.mpUserMapper.selectList(Wrappers.<MpUser>lambdaQuery().eq(MpUser::getUserName, "测试用户88"));
            if(CollectionUtil.isEmpty(users)){
                MpUser user = getMpUser(88);
                user.setUserName("测试用户"+88);
                this.mpUserMapper.insert(user);
                return "添加用户88成功";
            }
            return "数据库已存在88用户，无需添加";
        }

        //commit | rollback
    }*/

    @Transactional
    public void deleteMpUser(String userName){
        this.mpUserMapper.delete(Wrappers.<MpUser>lambdaQuery().eq(MpUser::getUserName, userName));
    }
    public List<MpUser> selectMpUser(String userName){
        return this.mpUserMapper.selectList(Wrappers.<MpUser>lambdaQuery().eq(MpUser::getUserName, userName));
    }

    public synchronized String add99MpUserToDB(){
        return this.springTransactionDemoService.add99MpUserToDB_();
    }

    @Transactional
    public String add99MpUserToDB_(){
        List<MpUser> users = this.mpUserMapper.selectList(Wrappers.<MpUser>lambdaQuery().eq(MpUser::getUserName, "测试用户99"));
        if(CollectionUtil.isEmpty(users)){
            MpUser user = getMpUser(99);
            user.setUserName("测试用户"+99);
            this.mpUserMapper.insert(user);
            return "添加用户99成功";
        }
        return "数据库已存在99用户，无需添加";
    }

    public static MpUser getMpUser(Integer num) {
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
