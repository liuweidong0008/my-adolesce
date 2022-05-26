package com.adolesce.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2022/4/2 22:19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EasyExcelSheetParams {
    private String sheetName;
    private Integer sheetNo;
    private List data;
    private Class clazz;

    public EasyExcelSheetParams(List data, Class clazz) {
        this.data = data;
        this.clazz = clazz;
    }
}
