package com.adolesce.server.vo.excel;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * Application describing：EsayExcel导出Vo
 * @author： <area href="mailto:liuwd@miyzh.com"> LiuWeidong </area>
 * @version：V2.0.0
 */
@Data
@HeadRowHeight(value = 35) // 表头行高
@ContentRowHeight(value = 25) // 内容行高
//@HeadFontStyle      表头样式
//@ContentFontStyle   内容样式(字体名称、字体高度、是否斜体、是否设置删除水平线、字体颜色、下划线、是否加粗、编码格式、偏移量)
//@ColumnWidth(value = 50) 列宽（最大可设置到255）
//@ContentStyle
//@HeadStyle
public class HeiMaStudentMsgEasyVo {
    /**
     * 主键
     */
    @ExcelIgnore
    private Long id;
    /**
     * 姓名
     */
    @ExcelProperty(value = {"基本信息","姓名"}, index = 0,order = 1)
    private String name;
    /**
     * 性别
     */
    @ExcelProperty(value = {"基本信息","性别"}, index = 1,order = 3)
    private String sex ;
    /**
     * 年龄
     */
    @ExcelProperty(value = {"基本信息","年龄"}, index = 2,order = 2)
    private Integer age;
    /**
     * 手机号
     */
    @ExcelProperty(value = "手机号", index = 3,order = 4)
    private String phone;
    /**
     * 学历
     */
    @ExcelProperty(value = {"学历信息","学历"}, index = 4,order = 5)
    private String education;
    /**
     * 工作薪资
     */
    @ExcelProperty(value = {"工作信息","工作薪资"}, index = 5,order = 9)
    private Integer salary;
    /**
     * 毕业学校
     */
    @ExcelProperty(value = {"学历信息","毕业学校"}, index = 6,order = 6)
    private String school;
    /**
     * 毕业时间
     */
    @ExcelProperty(value = {"学历信息","毕业时间"}, index = 7,order = 7)
    @JSONField(format="yyyy-MM-dd")
    @DateTimeFormat("yyyy-MM-dd")
    private Date graduateTime;
    /**
     * 专业
     */
    @ExcelProperty(value = "专业", index = 8,order = 8)
    private String specialty;

    /**
     * 工作信息(工作地点、福利待遇)
     */
    @ExcelProperty(value = {"工作信息","详细信息"}, index = 9,order = 10)
    private String workMsg;
    /**
     * 入职时间
     */
    @ExcelIgnore
    private Date ruzhiTime;
    /**
     * 备注
     */
    @ExcelProperty(value = "备注", index = 10,order = 11)
    private String remark;
    /**
     * 创建时间
     */
    @ExcelIgnore
    private Date createTime;
    /**
     * 修改时间HeiMaStudentMsg
     */
    @ExcelIgnore
    private Date updateTime;
}
