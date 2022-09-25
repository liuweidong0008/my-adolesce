package com.adolesce.server.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ReadConverterContext;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.data.WriteCellData;

/**
 * @ClassName GenderConverter
 * @Description 为 EasyExcel工具提供的数据转换器，性别转换   男=0，女=1
 * @Author admin
 * @Date 9:55 2022/9/1
 * @Version 2.0
 **/
public class GenderConverter implements Converter<String> {

    private static final String MAN = "男";
    private static final String WOMAN = "女";

    /**
     * 实体类中对象属性类型
     */
    @Override
    public Class<?> supportJavaTypeKey() {
        return String.class;
    }

    /**
     * Excel中对应的CellData属性类型
     */
    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    /**
     * 这里读的时候会调用
     *
     */
    public String convertToJavaData(ReadConverterContext<?> context) {
        // 从Cell中读取数据
        String gender = context.getReadCellData().getStringValue();
        // 判断Excel中的值，将其转换为预期的数值
        if (MAN.equals(gender)) {
            return "男";
        } else if (WOMAN.equals(gender)) {
            return "女";
        }
        return null;
    }

    /**
     * 这里写的时候会调用
     */
    public WriteCellData<?> convertToExcelData(WriteConverterContext<String> context) {
        String gender = context.getValue();
        if (MAN.equals(gender)) {
            return new WriteCellData<>("先生");
        } else if (WOMAN.equals(gender)) {
            return new WriteCellData<>("女士");
        }
        return new WriteCellData<>("");
    }
}


























