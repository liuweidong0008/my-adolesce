package com.adolesce.server.utils.threadlocal;

import com.adolesce.common.entity.course.Teacher;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2022/7/8 16:44
 */
public class CurrentTeacherUtil {
    private static ThreadLocal<Teacher> CURRENT_TEACHER_THREAD_LOCAL = new InheritableThreadLocal<>();

    /**
     * 设置当前老师
     */
    public static void setCurrentTeacher(Teacher teacher) {
        CURRENT_TEACHER_THREAD_LOCAL.set(teacher);
    }

    /**
     * 获取当前老师
     */
    public static Teacher getCurrentTeacher() {
        return CURRENT_TEACHER_THREAD_LOCAL.get();
    }

    /**
     * 清理
     */
    public static void clear() {
        CURRENT_TEACHER_THREAD_LOCAL.remove();
    }

}
