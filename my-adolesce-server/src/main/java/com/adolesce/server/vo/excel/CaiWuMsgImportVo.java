package com.adolesce.server.vo.excel;

import com.adolesce.common.utils.excel.imports.ExcelImportBaseBo;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * Application describing：导入Vo
 * @author： <area href="mailto:liuwd@miyzh.com"> LiuWeidong </area>
 * @version：V2.0.0
 */
@Data
public class CaiWuMsgImportVo extends ExcelImportBaseBo {
    /**
     * 主键
     */
    @ExcelIgnore
    private Long id;
    /**
     * 序号
     */
    @ExcelProperty(value = {"序号",""}, index = 0)
    private Integer seriNo;
    /**
     * 单位名称
     */
    @ExcelProperty(value = {"单位名称",""}, index = 1)
    private String companyName ;
    /**
     * 单位编码
     */
    @ExcelProperty(value = {"单位编码",""}, index = 2)
    private String companyNo;
    /**
     * 系统名称
     */
    @ExcelProperty(value = {"系统名称",""}, index = 3)
    private String systemName;
    /**
     * 系统编码
     */
    @ExcelProperty(value = {"系统编码",""}, index = 4)
    private String systemNo;
    /**
     * 数据库名称
     */
    @ExcelProperty(value = {"数据库名称",""}, index = 5)
    private String dbName;
    /**
     * 表英文名
     */
    @ExcelProperty(value = {"表英文名",""}, index = 6)
    private String tableEnName;
    /**
     *
     */
    @ExcelProperty(value = {"",""}, index = 7)
    private String msg;
    /**
     * 表字段英文名
     */
    @ExcelProperty(value = {"表字段英文名",""}, index = 8)
    private String tableFieldEnName;
    /**
     * 字段英文名
     */
    @ExcelProperty(value = {"字段英文名",""}, index = 9)
    private String fieldEnName;
    /**
     * 字段中文名
     */
    @ExcelProperty(value = {"字段中文名",""}, index = 10)
    private String fieldChName;
    /**
     * 字段业务描述
     */
    @ExcelProperty(value = {"字段业务描述",""}, index = 11)
    private String fieldBusinessDes;
    /**
     * 字段类型
     */
    @ExcelProperty(value = {"字段类型",""}, index = 12)
    private String fieldType;
    /**
     * 是否主键
     */
    @ExcelProperty(value = {"是否主键",""}, index = 13)
    private String isPk;
    /**
     * 是否必填
     */
    @ExcelProperty(value = {"是否必填",""}, index = 14)
    private String isRequired;
    /**
     * 是否敏感字段
     */
    @ExcelProperty(value = {"是否敏感字段",""}, index = 15)
    private String isSensitiveField;
    /**
     * 是否参考数据
     */
    @ExcelProperty(value = {"是否参考数据",""}, index = 16)
    private String isReferData;
    /**
     * 参考数据映射关系
     */
    @ExcelProperty(value = {"参考数据映射关系",""}, index = 17)
    private String dataMappingRelation;

}
