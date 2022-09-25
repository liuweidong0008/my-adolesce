package com.adolesce.server.encrypt;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.MD5;
import org.junit.Test;
import org.springframework.util.DigestUtils;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2022/2/6 11:02
 */
public class Md5Test {
    @Test
    public void test1() {
        String password = "admin";
        String salt = "123abc";

        //1、spring MD5加密
        String password1 = DigestUtils.md5DigestAsHex((salt+password).getBytes());
        System.out.println(password1);

        //2、Hutool 加密
        String password2 = MD5.create().digestHex(salt + password);
        String password3 = MD5.create().setSalt(salt.getBytes()).digestHex(password);
        String password4 = SecureUtil.md5().digestHex(salt + password);
        String password5 = SecureUtil.md5(salt + password);


        System.out.println(password2);
        System.out.println(password3);
        System.out.println(password4);
        System.out.println(password5);
    }
}
