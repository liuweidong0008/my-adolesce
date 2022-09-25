package com.adolesce.server.utils.threadlocal;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2022/7/8 16:44
 */
public class CurrentLessonCountUtil {
    private static ThreadLocal<Integer> CURRENT_LESSON_COUNT_THREAD_LOCAL = new InheritableThreadLocal<>();

    /**
     * 设置天数
     */
    public static void setLessonCount(Integer count) {
        CURRENT_LESSON_COUNT_THREAD_LOCAL.set(count);
    }

    /**
     * 获取当前天数
     */
    public static Integer getLessonCount() {
        return CURRENT_LESSON_COUNT_THREAD_LOCAL.get();
    }

    /**
     * 清理天数
     */
    public static void clear() {
        CURRENT_LESSON_COUNT_THREAD_LOCAL.remove();
    }

    /**
     * 天数递增
     */
    public static Integer lessonCountIncrement() {
        Integer daysCount = getLessonCount() + 1;
        setLessonCount(daysCount);
        return daysCount;
    }

    /**
     * 天数递减
     */
    public static Integer lessonCountDecrement() {
        Integer daysCount = getLessonCount() - 1;
        setLessonCount(daysCount);
        return daysCount;
    }
}
