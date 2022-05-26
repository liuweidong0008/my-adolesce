package com.adolesce.common.utils.excel.imports;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 类名称：ExcelImportBaseBo
 * 类描述 ：excel导入，输出vo的基类
 *
 * @author : 刘威东
 * 创建时间：2015年6月26日 下午5:07:01
 * 版本： V1.0
 */
@Data
public class ExcelImportBaseBo {
    /**
     * 所处物理行数
     */
    private Integer dataLine;

    /**
     * 错误信息Map（字段key>错误信息）
     */
    protected Map<String, String> errors;

    /**
     * 所有错误信息
     */
    private String allErrors = "";

    /**
     * 是否验证通过
     *
     * @return true:有错误  false:无错误
     */
    public boolean hasErrors() {
        if (CollUtil.isEmpty(errors)) {
            return false;
        }
        return true;
    }

    /**
     * 设置验证信息
     *
     * @param fieldName 字段名称
     * @param errorMsg  错误信息
     */
    public void addFieldError(String fieldName, String errorMsg) {
        if (CollUtil.isEmpty(errors)) {
            errors = new HashMap<>();
        }
        if (errors.containsKey(fieldName)) {
            String msg = errors.get(fieldName);
            errors.put(fieldName, new StringBuilder(msg).append(",").append(errorMsg).toString());
        } else {
            errors.put(fieldName, errorMsg);
        }
        allErrors = allErrors + errorMsg + "；";
    }

    /**
     * 获取某字段的错误信息
     *
     * @param fieldName 自动名称
     * @return 字段错误信息
     */
    public String getFieldError(String fieldName) {
        if (CollUtil.isEmpty(errors)) {
            return null;
        }
        if (errors.containsKey(fieldName)) {
            return errors.get(fieldName);
        }
        return null;
    }

}
