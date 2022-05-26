package com.adolesce.common.utils.excel.imports;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 类名称：ExcelImportConfigItem
 * 类描述 ：excel配置模板中的公共配置信息，如 类型，对应vo，开始行
 *
 * @author :刘威东
 * 创建时间：2015年6月26日 下午5:07:01
 */
@Data
public class ExcelImportConfig {
    /**
     * 配置名称
     */
    private String configName;

    /**
     * 封装数据的Class对象
     */
    private Class boClazz;

    /**
     * 读取起始行，物理行数（1开始）
     */
    private Integer startLine;

    /**
     * excel配置字段定义列表
     */
    private List<Field> fieldList;

    public void addField(Field field) {
        if (CollUtil.isEmpty(fieldList)) {
            fieldList = new ArrayList<>();
        }
        fieldList.add(field);
    }

    @Data
    public static class Field {
        /**
         * excel中所在列
         */
        private int columnIndex;
        /**
         * 字段名称
         */
        private String fieldName;
        /**
         * 字段描述
         */
        private String fieldDesc;
        /**
         * 是否允许为空
         */
        private boolean alowBlank;
        /**
         * 正则表单式
         */
        private String regexStr;
        /**
         * 正则表达式验证未通过时的错误信息
         */
        private String errorMsg;
    }

}
