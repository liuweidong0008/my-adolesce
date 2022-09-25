package com.adolesce.common.entity.course;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2022/7/5 19:56
 */
@Data
@NoArgsConstructor
public class TimeTable {
    /**
     * 日期
     */
    private String date = "";
    /**
     * 课程编号
     */
    private String lessonNo = "";
    /**
     * 课程名称
     */
    private String lessonName = "";
    /**
     * 是否大纲
     */
    private String isOutline = "";
    /**
     * 上课模式
     */
    private String lessonModel = "";
    /**
     * 是否阶段考试
     */
    private String isTest = "";
    /**
     * 上课教室
     */
    private String classRoom = "";
    /**
     * 老师姓名
     */
    private String teacherName = "";
    /**
     * 老师工号
     */
    private String teacherWorkNo = "";
    /**
     * 代课费
     */
    private String haveTipsayCost = "";
    /**
     * 晚自习辅导老师姓名
     */
    private String nightTeacherName = "";
    /**
     * 晚自习辅导老师工号
     */
    private String nightTeacherWorkNo = "";
    /**
     * 有无晚自习补贴
     */
    private String haveNightCost = "";
    /**
     * 备注
     */
    private String remark = "";

    /**
     * 是否法定节假日
     */
    private Boolean isHoliday;
    private Boolean isHolidayFirst;
}
