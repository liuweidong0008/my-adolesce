package com.adolesce.cloud.dubbo.domain.db;

import com.adolesce.cloud.dubbo.domain.mongo.Address;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2022/2/28 13:49
 */
@Data
@TableName("heima_student_msg ")
public class HeiMaStudentMsg implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type= IdType.AUTO)
    private Long id;
    /**
     * 姓名
     */
    @TableField("name")
    private String name;
    /**
     * 性别
     */
    @TableField("sex")
    private String sex ;
    /**
     * 年龄
     */
    @TableField("age")
    private Integer age;
    /**
     * 手机号
     */
    @TableField("phone")
    private String phone;
    /**
     * 学历
     */
    @TableField("education")
    private String education;
    /**
     * 毕业学校
     */
    @TableField("school")
    private String school;
    /**
     * 毕业时间
     */
    @TableField("graduate_time")
    private Date graduateTime;
    /**
     * 专业
     */
    @TableField("specialty")
    private String specialty;
    /**
     * 所属班级组
     */
    @TableField("`group`")
    private String group;
    /**
     * 所属java期数(班级)
     */
    @TableField("java_batch")
    private Integer javaBatch;
    /**
     * 平时学习表现(1:优秀,2:良好,3:一般,4:差,5:非常差)
     */
    @TableField("peacetime_level")
    private Integer peacetimeLevel;
    /**
     * 就业标识(0:普通，1:冲锋队，2:末位)
     */
    @TableField("find_work_flag")
    private Integer findWorkFlag;
    /**
     * 就业关注级别(1:重点关注 2:中等关注 3:一般关注)
     */
    @TableField("follow_level")
    private Integer followLevel;
    /**
     * 期望薪资
     */
    @TableField("hope_salary")
    private String hopeSalary;
    /**
     * 期望城市
     */
    @TableField("hope_city")
    private String hopeCity;
    /**
     * 模拟面试情况备注
     */
    @TableField("simulation_meet_remark")
    private String simulationMeetRemark;
    /**
     * 是否有offer(1:是,0:否)
     */
    @TableField("offer_flag")
    private Integer offerFlag;
    /**
     * 工作薪资
     */
    @TableField(value="salary",updateStrategy = FieldStrategy.IGNORED)
    private Integer salary;
    /**
     * 工作信息(工作地点、福利待遇)
     */
    @TableField(value="work_msg",updateStrategy = FieldStrategy.IGNORED)
    private String workMsg;
    /**
     * 入职时间
     */
    @TableField(value="ruzhi_time",updateStrategy = FieldStrategy.IGNORED)
    private Date ruzhiTime;
    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * 修改时间HeiMaStudentMsg
     */
    @TableField("update_time")
    private Date updateTime;

    /**
     * 毕业时间（字符串格式）
     */
    @TableField(exist = false)
    private String graduateTimeStr;
    /**
     * 入职时间（字符串格式）
     */
    @TableField(exist = false)
    private String ruzhiTimeStr;
    @TableField(exist = false)
    private Address address;

}
