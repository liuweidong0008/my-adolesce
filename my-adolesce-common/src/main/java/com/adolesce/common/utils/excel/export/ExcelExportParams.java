package com.adolesce.common.utils.excel.export;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.adolesce.common.utils.XmlReadHelper;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <b>Application name：</b> ExcelSheet.java <br>
 * <b>Application describing：Excel导出参数实体类 </b> <br>
 * <b>@Date：</b> 2019年12月21日 14:15 <br>
 * <b>@author：</b> <a href="mailto:lwd@miyzh.com"> liuWeidong </a> <br>
 * <b>@version：</b>V2.0.0 <br>
 */
@Data
public class ExcelExportParams implements Serializable {
    /**
     * 文件名称
     */
    private String fileName = "数据列表";
    /**
     * 文件后缀
     */
    private String fileSuffix = ".xlsx";
    /**
     * response
     */
    private HttpServletResponse httpServletResponse;
    /**
     * sheets
     */
    private List<ExcelSheet> sheets;


    private ExcelExportParams() {
    }

    /**
     * 初始化创建导出参数
     *
     * @param response
     * @return
     */
    public static ExcelExportParams create(HttpServletResponse response) {
        ExcelExportParams exportParams = new ExcelExportParams();
        exportParams.setHttpServletResponse(response);
        return exportParams;
    }

    /**
     * 添加sheet
     *
     * @param sheet
     * @return
     */
    public ExcelExportParams addSheet(ExcelSheet sheet) {
        if (CollUtil.isEmpty(sheets)) {
            this.sheets = new ArrayList<>();
        }
        Map<String, ExcelSheet> sheetMap = sheets.stream().collect(Collectors.toMap(ExcelSheet::getSheetName, Function.identity()));
        if (Objects.nonNull(sheetMap.get(sheet.getSheetName()))) {
            ExcelSheet temp = sheetMap.get(sheet.getSheetName());
            BeanUtil.copyProperties(sheet, temp);
        } else {
            this.sheets.add(sheet);
        }
        return this;
    }

    /**
     * 添加sheet
     *
     * @param dataList   数据集合
     * @param headerName 头名称
     * @param headerKey  头key
     * @param sheetName  sheet名称
     * @return
     * @throws Exception
     */
    public ExcelExportParams addSheet(String sheetName, String[] headerName, String[] headerKey, List<?> dataList) throws Exception {
        return this.addSheet(sheetName, headerName, headerKey, dataList, null);
    }

    /**
     * 添加sheet
     *
     * @param dataList    数据集合
     * @param headerName  头名称
     * @param headerKey   头key
     * @param sheetName   sheet名称
     * @param dateFormart 日期格式化
     * @return
     * @throws Exception
     */
    public ExcelExportParams addSheet(String sheetName, String[] headerName, String[] headerKey, List<?> dataList, String dateFormart) throws Exception {
        ExcelSheet sheet = new ExcelSheet(sheetName, headerName, headerKey, dataList);
        if (StringUtils.isNotEmpty(dateFormart)) {
            sheet.setDateFormart(dateFormart);
        }
        return this.addSheet(sheet);
    }

    /**
     * 初始化创建导出参数(使用配置文件初始化sheet信息场景)
     *
     * @param response
     * @param exportConfigName Excel导出配置名称
     * @return
     */
    public static ExcelExportParams create(HttpServletResponse response, String exportConfigName) {
        ExcelExportParams exportParams = create(response);
        List<ExcelSheet> sheets = XmlReadHelper.getExcelExportConfigByName(exportConfigName);
        exportParams.setSheets(sheets);
        return exportParams;
    }

    /**
     * 添加数据集合(使用配置文件初始化sheet信息场景使用)
     *
     * @param sheetName sheet名称
     * @param dataList  数据集合
     * @return
     */
    public ExcelExportParams addDataList(String sheetName, List<?> dataList) {
        return this.addDataList(sheetName, dataList, null);
    }

    /**
     * 添加数据集合(使用配置文件初始化sheet信息场景使用)
     *
     * @param sheetName   sheet名称
     * @param dataList    数据集合
     * @param dateFormart 日期格式
     * @return
     */
    public ExcelExportParams addDataList(String sheetName, List<?> dataList, String dateFormart) {
        if (CollUtil.isNotEmpty(sheets)) {
            Map<String, ExcelSheet> sheetMap = sheets.stream().collect(Collectors.toMap(ExcelSheet::getSheetName, Function.identity()));
            if (Objects.nonNull(sheetMap.get(sheetName))) {
                sheetMap.get(sheetName).setDataList(dataList);
                if (StringUtils.isNotEmpty(dateFormart)) {
                    sheetMap.get(sheetName).setDateFormart(dateFormart);
                }
            }
        }
        return this;
    }
}
