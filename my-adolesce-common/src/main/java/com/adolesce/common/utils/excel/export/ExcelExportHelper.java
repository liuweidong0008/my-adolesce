package com.adolesce.common.utils.excel.export;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.ExcelWriter;
import lombok.extern.slf4j.Slf4j;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Application name： ExcelExportHelper.java
 * Application describing：excel导出通用帮助类
 * Copyright： Copyright &copy; 2019 明医众禾科技（北京）有限责任公司 版权所有。
 * Company： 明医众禾科技（北京）有限责任公司
 * Date： 2019/12/25
 *
 * @author： <area href="mailto:liuwd@miyzh.com"> liuweidong </area>
 * @version：V3.0
 */
@Slf4j
public class ExcelExportHelper {
    public static final String XLSX = ".xlsx";
    public static final String XLS = ".xls";

    public static final String YMD_HMS = "yyyy-MM-dd HH:mm:ss";
    public static final String YMD = "yyyy-MM-dd";

    /**
     * 导出Excel
     *
     * @param params 导出参数
     * @return
     */
    public static void export(ExcelExportParams params) {
        Workbook workbook = createExcel(params.getSheets(), params.getFileSuffix());
        String fileName = new StringBuilder(params.getFileName()).append(params.getFileSuffix()).toString();
        outputFile(params.getHttpServletResponse(), fileName, workbook);
    }

    /**
     * 创建Excel
     *
     * @param exportSheets sheet集合
     * @param fileSuffix   文件后缀
     * @return
     */
    public static Workbook createExcel(List<ExcelSheet> exportSheets, String fileSuffix) {
        Workbook wb = null;
        try {
            if (CollUtil.isEmpty(exportSheets)) {
                throw new Exception("缺少sheet相关参数配置");
            }
            wb = StringUtils.equals(XLSX, fileSuffix) ? new SXSSFWorkbook() : new HSSFWorkbook();
            for (ExcelSheet sheet : exportSheets) {
                createSheet(wb, sheet);
            }
        } catch (Exception e) {
            log.error("Excel创建异常:{}", e.getMessage());
            e.printStackTrace();
        }
        return wb;
    }

    /**
     * 创建Sheet
     *
     * @param wb
     * @param exportSheet
     */
    private static void createSheet(Workbook wb, ExcelSheet exportSheet) throws Exception {
        String sheetName = exportSheet.getSheetName();
        List<?> dataList = exportSheet.getDataList();
        List<ExcelSheet.ExcelHeader> excelHeaders = exportSheet.getHeaders();
        String dateFormart = exportSheet.getDateFormart();
        if (CollUtil.isEmpty(excelHeaders)) {
            throw new Exception("缺少header相关参数配置");
        }
        for (ExcelSheet.ExcelHeader header : excelHeaders) {
            if (StringUtils.isEmpty(header.getHeaderKey())) {
                throw new Exception("缺少headerKey相关参数配置");
            } else if (StringUtils.isEmpty(header.getHeaderName())) {
                header.setHeaderName(header.getHeaderKey());
            }
        }
        int headerSize = excelHeaders.size();
        Sheet sheet = wb.createSheet(sheetName);
        CellStyle headCellStyle = getCellStyle(wb, true);
        CellStyle dataCellStyle = getCellStyle(wb, false);
        int rownum = 0;
        Row row = sheet.createRow(rownum);
        row.setHeight((short) 658);
        Cell cell;

        for (int i = 0; i < headerSize; i++) {
            cell = row.createCell(i);
            cell.setCellStyle(headCellStyle);
            cell.setCellValue(excelHeaders.get(i).getHeaderName());
        }
        //class name > (field name > field)
        Map<String, Map<String, Field>> classMap = new HashMap<>();
        if (CollUtil.isNotEmpty(dataList)) {
            for (Object obj : dataList) {
                row = sheet.createRow(++rownum);
                row.setHeight((short) 458);
                Object fieldValue;
                for (int j = 0; j < headerSize; j++) {
                    fieldValue = getFieldValue(obj, excelHeaders.get(j).getHeaderKey(), classMap);
                    cell = row.createCell(j);
                    cell.setCellType(CellType.STRING);
                    cell.setCellStyle(dataCellStyle);
                    if (Objects.isNull(fieldValue)) {
                        cell.setCellValue("");
                        continue;
                    }
                    if (fieldValue instanceof Date) {
                        Date dateTemp = (Date) fieldValue;
                        cell.setCellValue(DateUtil.format(dateTemp, dateFormart));
                        continue;
                    }
                    cell.setCellValue(fieldValue.toString());
                }
            }
        }
        for (int i = 0; i < headerSize; i++) {
            sheet.setColumnWidth(i, 6251);
        }
    }

    /**
     * 根据属性名获取属性值
     *
     * @param obj       对象
     * @param headerKey 属性名（xx 或 xx.yy.zz....）
     * @param classMap  class name > (field name > field)
     * @return
     */
    private static Object getFieldValue(Object obj, String headerKey, Map<String, Map<String, Field>> classMap) {
        Object fieldValue = null;
        Field field;
        String className;
        Map<String, Field> fieldMap;
        List<String> fieldNames = Arrays.asList(headerKey.split("\\."));

        for (String fiedName : fieldNames) {
            className = obj.getClass().getName();
            fieldMap = classMap.get(className);
            if (CollUtil.isEmpty(fieldMap)) {
                fieldMap = getFieldMap4Object(obj);
                classMap.put(className, fieldMap);
            }
            field = fieldMap.get(fiedName);
            if (Objects.isNull(field)) {
                return fieldValue;
            }
            try {
                field.setAccessible(true);
                fieldValue = field.get(obj);
                if (Objects.isNull(fieldValue)) {
                    return fieldValue;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            obj = fieldValue;
        }
        return fieldValue;
    }

    /**
     * 获取类属性字段Map（字段名称：字段）
     *
     * @param obj
     * @return Map
     */
    private static Map<String, Field> getFieldMap4Object(Object obj) {
        List<Field> fields = new ArrayList<>();
        Class tempClass = obj.getClass();
        //当父类为null的时候说明到达了最上层的父类(Object类).
        while (tempClass != null) {
            fields.addAll(Arrays.asList(tempClass.getDeclaredFields()));
            //得到父类,然后赋给自己
            tempClass = tempClass.getSuperclass();
        }
        Map<String, Field> fieldMap = fields.stream().collect(Collectors.toMap(Field::getName, Function.identity()));
        return fieldMap;
    }

    /**
     * 单元格样式
     *
     * @param wb
     * @return
     */
    public static CellStyle getCellStyle(Workbook wb, boolean isHead) {
        CellStyle cellStyle = wb.createCellStyle();
        //设置单元格垂直水平居中
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        //设置字体
        Font dataCellFont = wb.createFont();
        dataCellFont.setFontName("宋体");
        dataCellFont.setFontHeightInPoints((short) (isHead ? 14 : 11));
        if (isHead) {
            dataCellFont.setBold(true);
        }
        cellStyle.setFont(dataCellFont);
        return cellStyle;
    }

    /**
     * 输出文件
     *
     * @param response
     * @param fileName 文件名
     * @param object
     * @throws IOException
     */
    public static void outputFile(HttpServletResponse response, String fileName, Object object) {
        OutputStream outputStream = null;
        try {
            if (Objects.isNull(object) || Objects.isNull(response)) {
                throw new Exception("缺少相关参数");
            }
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            /*response.setHeader("Content-disposition",
                    "attachment;filename=" + new String(fileName.getBytes("gb2312"), "ISO-8859-1"));*/
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            outputStream = response.getOutputStream();

            Workbook wb;
            ExcelWriter writer;
            if (object instanceof Workbook) {
                wb = (Workbook) object;
                if (CollUtil.isEmpty(wb.sheetIterator())) {
                    throw new Exception("缺少header相关参数配置");
                }
                outputStream.flush();
                wb.write(outputStream);
            } else if (object instanceof ExcelWriter) {
                writer = (ExcelWriter) object;
                writer.flush(outputStream, true);
                writer.close();
            }
        } catch (Exception e) {
            log.error("Excel输出异常:{}", e.getMessage());
            e.printStackTrace();
        } finally {
            IoUtil.close(outputStream);
        }
    }

    /**
     * 配置模板导出
     *
     * @param dataLists   数据
     * @param templateName excel模板名称
     * @param fileName     文件名
     * @param response
     */
    public static void export(List<List<?>> dataLists, String templateName,
                              String fileName, HttpServletResponse response) {
        InputStream inputStream = getExcelTempInputStream(templateName);
        try {
            // 模板加载
            int i = 1;
            Map<String,List<?>>beanParams = new HashMap<>();
            for(List<?> dataList:dataLists){
                beanParams.put("dataList" + i++,dataList);
            }
            Workbook workbook = new XLSTransformer().transformXLS(inputStream, beanParams);
            outputFile(response, fileName, workbook);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IoUtil.close(inputStream);
        }
    }

    /**
     * 配置模板导出
     *
     * @param sheetNames   sheet名称数组（和dataLists顺序对应）
     * @param dataLists    数据集合
     * @param templateName excel模板名称
     * @param fileName     文件名
     * @param response
     */
    public static void export(String[] tempSheetNames, String[] sheetNames, List<List<?>> dataLists, String templateName,
                              String fileName, HttpServletResponse response) {
        InputStream inputStream = getExcelTempInputStream(templateName);
        List<String> tempSheetNameList = Arrays.asList(tempSheetNames);
        List<String> sheetNameList = Arrays.asList(sheetNames);
        try {
            // 模板加载
            List beanParamsList = new ArrayList();
            for (int i = 0; i < dataLists.size(); i++) {
                Map<String, List<?>> map = new HashMap<>();
                map.put("dataList" + (i + 1), dataLists.get(i));
                beanParamsList.add(map);
            }
            Workbook workbook = new XLSTransformer().transformXLS(inputStream, tempSheetNameList, sheetNameList, beanParamsList);
            outputFile(response, fileName, workbook);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IoUtil.close(inputStream);
        }
    }

    /**
     * 获取resources下文件流
     *
     * @param templateName
     * @return
     */
    public static InputStream getExcelTempInputStream(String templateName) {
        //inputStream =  Thread.currentThread().getContextClassLoader().getResourceAsStream("templates/excel/"+templateName);
        //inputStream = new FileInputStream(ResourceUtils.getFile("classpath:templates/excel/"+templateName));
        //inputStream = new ExcelHelper().getClass().getResourceAsStream("/templates/excel/"+templateName);
        //inputStream = new ClassPathResource("templates/excel/"+templateName).getInputStream();
        //inputStream = ExcelHelper.class.getResourceAsStream("/templates/excel/"+templateName);

        //File file = new File(this.getClass().getResource("/").getFile().replace("%20", " ") + "/config/" + configFileName);
        InputStream inputStream = ExcelExportHelper.class.getClassLoader().getResourceAsStream("templates/excel/" + templateName);
        return inputStream;
    }
}