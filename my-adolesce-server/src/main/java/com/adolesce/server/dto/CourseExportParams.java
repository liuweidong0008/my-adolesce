package com.adolesce.server.dto;

import lombok.Data;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2022/8/20 17:51
 */
@Data
public class CourseExportParams {
    /**
     * java开班类型(基础 就业)
     */
    private String courseType;
    /**
     * java 期数
     */
    private String javaBatch;
    /**
     * 教室
     */
    private String classRoom;
    /**
     * 开课日期
     */
    private String startDate;
    /**
     * 授课老师
     */
    private String teacher;

}
