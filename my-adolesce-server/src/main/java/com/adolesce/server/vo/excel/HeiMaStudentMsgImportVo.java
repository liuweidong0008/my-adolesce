package com.adolesce.server.vo.excel;

import com.adolesce.common.utils.excel.imports.ExcelImportBaseBo;
import lombok.Data;

import java.util.Date;

/**
 * Application describing：导入Vo
 * @author： <area href="mailto:liuwd@miyzh.com"> LiuWeidong </area>
 * @version：V2.0.0
 */
@Data
public class HeiMaStudentMsgImportVo extends ExcelImportBaseBo {
    /**
     * 主键
     */
    private Long id;
    /**
     * 姓名
     */
    private String name;
    /**
     * 性别
     */
    private String sex ;
    /**
     * 年龄
     */
    private Integer age;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 学历
     */
    private String education;
    /**
     * 毕业学校
     */
    private String school;
    /**
     * 毕业时间
     */
    private Date graduateTime;
    /**
     * 专业
     */
    private String specialty;
    /**
     * 所属班级组
     */
    private String group;
    /**
     * 所属java期数(班级)
     */
    private Integer javaBatch;
    /**
     * 平时学习表现(1:优秀,2:良好,3:一般,4:差,5:非常差)
     */
    private Integer peacetimeLevel;
    /**
     * 就业标识(0:普通，1:冲锋队，2:末位)
     */
    private Integer findWorkFlag;
    /**
     * 就业关注级别(1:重点关注 2:中等关注 3:一般关注)
     */
    private Integer followLevel;
    /**
     * 期望薪资
     */
    private String hopeSalary;
    /**
     * 期望城市
     */
    private String hopeCity;
    /**
     * 模拟面试情况备注
     */
    private String simulationMeetRemark;
    /**
     * 是否有offer(1:是,0:否)
     */
    private Integer offerFlag;
    /**
     * 工作薪资
     */
    private Integer salary;
    /**
     * 工作信息(工作地点、福利待遇)
     */
    private String workMsg;
    /**
     * 入职时间
     */
    private Date ruzhiTime;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间HeiMaStudentMsg
     */
    private Date updateTime;

}
