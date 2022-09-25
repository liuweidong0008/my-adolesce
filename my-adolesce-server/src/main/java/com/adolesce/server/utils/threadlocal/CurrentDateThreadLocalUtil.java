package com.adolesce.server.utils.threadlocal;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

import java.util.Date;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2022/7/8 16:44
 */
public class CurrentDateThreadLocalUtil {
    private static ThreadLocal<Date> CURRENT_DATE_THREAD_LOCAL = new InheritableThreadLocal<>();

    /**
     * 添加时间
     */
    public static void setCurrentDate(Date date) {
        CURRENT_DATE_THREAD_LOCAL.set(date);
    }

    /**
     * 获取当前时间
     */
    public static Date getCurrentDate() {
        return CURRENT_DATE_THREAD_LOCAL.get();
    }

    /**
     * 获取当前时间字符串
     */
    public static String getCurrentDateStr() {
        return DateUtil.formatDate(getCurrentDate());
    }

    /**
     * 清理时间
     */
    public static void clear() {
        CURRENT_DATE_THREAD_LOCAL.remove();
    }

    /**
     * 时间递增
     */
    public static String currentDateIncrement() {
        DateTime dateTime = DateUtil.offsetDay(getCurrentDate(), 1);
        setCurrentDate(dateTime);
        return dateTime.toString("yyyy-MM-dd");
    }

    /**
     * 时间递减
     */
    public static String currentDateDecrement() {
        DateTime dateTime = DateUtil.offsetDay(getCurrentDate(), -1);
        setCurrentDate(dateTime);
        return DateUtil.formatDate(dateTime);
    }
}
