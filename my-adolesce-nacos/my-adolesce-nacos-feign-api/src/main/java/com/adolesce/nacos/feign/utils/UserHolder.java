package com.adolesce.nacos.feign.utils;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2022/6/20 0:27
 */
public class UserHolder {
    private static final ThreadLocal<Long> tl = new ThreadLocal<>();

    public static void setUser(Long userId) {
        tl.set(userId);
    }
    public static Long getUser() {
        return tl.get();
    }
    public static void removeUser(){
        tl.remove();
    }

}
