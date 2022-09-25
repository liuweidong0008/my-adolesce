package com.adolesce.cloud.dubbo.domain.db;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2022/2/28 13:49
 */
@Data
@TableName("caiwu")
public class CaiWuMsg implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type= IdType.AUTO)
    private Long id;
    /**
     * 序号
     */
    @TableField("seri_no")
    private Integer seriNo;
    /**
     * 单位名称
     */
    @TableField("company_name")
    private String companyName ;
    /**
     * 单位编码
     */
    @TableField("company_no")
    private String companyNo;
    /**
     * 系统名称
     */
    @TableField("system_name")
    private String systemName;
    /**
     * 系统编码
     */
    @TableField("system_no")
    private String systemNo;
    /**
     * 数据库名称
     */
    @TableField("db_name")
    private String dbName;
    /**
     * 表英文名
     */
    @TableField("table_en_name")
    private String tableEnName;
    /**
     * 表字段英文名
     */
    @TableField("table_field_en_name")
    private String tableFieldEnName;
    /**
     * 字段英文名
     */
    @TableField("field_en_name")
    private String fieldEnName;
    /**
     * 字段中文名
     */
    @TableField("field_ch_name")
    private String fieldChName;
    /**
     * 字段业务描述
     */
    @TableField("field_business_des")
    private String fieldBusinessDes;
    /**
     * 字段类型
     */
    @TableField("field_type")
    private String fieldType;
    /**
     * 是否主键
     */
    @TableField("is_pk")
    private String isPk;
    /**
     * 是否必填
     */
    @TableField("is_required")
    private String isRequired;
    /**
     * 是否敏感字段
     */
    @TableField("is_sensitive_field")
    private String isSensitiveField;
    /**
     * 是否参考数据
     */
    @TableField("is_refer_data")
    private String isReferData;
    /**
     * 参考数据映射关系
     */
    @TableField("data_mapping_relation")
    private String dataMappingRelation;

}
