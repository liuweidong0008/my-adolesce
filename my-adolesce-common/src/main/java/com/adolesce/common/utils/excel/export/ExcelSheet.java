/*******************************************************************************
 * @(#)ExcelSheet.java 2019年12月21日 14:15
 * Copyright 2019 明医众禾科技（北京）有限责任公司. All rights reserved.
 *******************************************************************************/
package com.adolesce.common.utils.excel.export;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>Application name：</b> ExcelSheet.java <br>
 * <b>Application describing：ExcelSheet实体类 </b> <br>
 * <b>Copyright：</b> Copyright &copy; 2019 明医众禾科技（北京）有限责任公司 版权所有。<br>
 * <b>Company：</b> 明医众禾科技（北京）有限责任公司 <br>
 * <b>@Date：</b> 2019年12月21日 14:15 <br>
 * <b>@author：</b> <a href="mailto:lwd@miyzh.com"> liuWeidong </a> <br>
 * <b>@version：</b>V2.0.0 <br>
 */
@Data
public class ExcelSheet {
    public ExcelSheet() {
    }

    public ExcelSheet(String sheetName, String[] headerName, String[] headerKey, List<?> dataList) throws Exception {
        if (headerKey == null || headerKey.length == 0) {
            throw new Exception("缺少如下参数：headerKey");
        }
        if (headerName == null || headerName.length == 0) {
            headerName = headerKey;
        }
        if (headerName.length != headerKey.length) {
            throw new Exception("headerName长度应与headerKey一致");
        }
        this.sheetName = sheetName;
        this.dataList = dataList;
        for (int i = 0; i < headerKey.length; i++) {
            this.addHeader(headerName[i], headerKey[i]);
        }
    }

    /**
     * sheet头
     */
    private List<ExcelHeader> headers;
    /**
     * sheet名称
     */
    private String sheetName = "sheet";
    /**
     * 日期格式
     */
    private String dateFormart = "yyyy-MM-dd";
    /**
     * 导出数据
     */
    private List<?> dataList;

    /**
     * 添加头信息
     *
     * @param headerName 头名称
     * @param headerKey  头属性key
     * @return
     */
    public ExcelSheet addHeader(String headerName, String headerKey) {
        if (CollUtil.isEmpty(this.headers)) {
            this.headers = new ArrayList<>();
        }
        ExcelHeader excelHeader = new ExcelHeader(headerName, headerKey);
        this.headers.add(excelHeader);
        return this;
    }

    /**
     * Excel头
     */
    @Data
    public static class ExcelHeader {
        private String headerName;
        private String headerKey;

        ExcelHeader(String headerName, String headerKey) {
            this.headerName = headerName;
            this.headerKey = headerKey;
        }
    }

}