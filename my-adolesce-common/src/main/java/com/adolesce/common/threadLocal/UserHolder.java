package com.adolesce.common.threadLocal;

import com.adolesce.common.entity.User;

/**
 * 工具类：实现向threadlocal存储数据的方法
 */
public class UserHolder {
    private static ThreadLocal<User> TL_USER = new ThreadLocal<>();

    //将用户对象，存入Threadlocal
    public static void set(User user) {
        TL_USER.set(user);
    }

    //从当前线程，获取用户对象
    public static User get() {
        return TL_USER.get();
    }

    //从当前线程，获取用户对象的id
    public static Long getUserId() {
        return TL_USER.get().getId();
    }

    //从当前线程，获取用户对象的手机号码
    public static String getMobile() {
        return TL_USER.get().getMobile();
    }

    public static void remove() {
        TL_USER.remove();
    }
}
