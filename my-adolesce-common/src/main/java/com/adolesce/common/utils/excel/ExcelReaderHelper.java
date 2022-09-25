package com.adolesce.common.utils.excel;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;


/**
 * 类名称：ExcelReaderHelper
 * 类描述 ：excel通用解析类
 *
 * @author : 刘威东
 * 创建时间：2015年6月26日 下午5:07:01
 */
@Slf4j
public class ExcelReaderHelper {
    private Workbook workBook;

    private ExcelReaderHelper() {
    }

    public ExcelReaderHelper(InputStream in, String fileSuffix) throws IOException {
        //this.workBook = WorkbookFactory.create(in);
        this.workBook = StringUtils.equals("xlsx", fileSuffix) ? new XSSFWorkbook(in) : new HSSFWorkbook(in);
    }

    public ExcelReaderHelper(MultipartFile file) throws IOException {
        this(file.getInputStream(), FilenameUtils.getExtension(file.getOriginalFilename()));
    }

    /**
     * 获取文件中sheet的数量
     *
     * @return
     */
    public int getSheetCount() {
        if (Objects.isNull(workBook)) {
            return 0;
        }
        return workBook.getNumberOfSheets();
    }

    /**
     * 获取指定sheet中的行数量
     *
     * @param sheetIndex
     * @return
     */
    public int getRowsOfSheet(int sheetIndex) {
        Sheet sheet = workBook.getSheetAt(sheetIndex);
        return sheet.getLastRowNum();
    }


    /**
     * getExcelRow
     * 功能描述：取得excel的某一行记录
     * 逻辑描述：
     *
     * @param
     * @return XSSFRow
     * @throws Exception
     * @since Ver 1.00
     */
    public Row getExcelRow(int sheetIndex, int rowIndex) {
        Sheet sheet = workBook.getSheetAt(sheetIndex);
        int rowCount = sheet.getLastRowNum();
        if (rowIndex > rowCount) {
            return null;
        }
        return sheet.getRow(rowIndex);
    }

    /**
     * 校验该行是否为空
     *
     * @param excelRow            行对象
     * @param needReadColumnIndex 需要读取的列坐标
     * @return true：是 false：否
     */
    public static boolean validRowIsNull(Row excelRow, List<Integer> needReadColumnIndex) {
        if (Objects.isNull(excelRow) || StringUtils.isBlank(excelRow.toString())) {
            return true;
        }
        for (Integer index : needReadColumnIndex) {
            //有任意一列不为空返回此行不为null
            Cell cell = excelRow.getCell(index);
            Object cellValue = getCellValue(cell);
            if (Objects.isNull(cellValue)) {
                continue;
            }
            return false;
        }
        return true;
    }

    /**
     * 获取该单元格值
     *
     * @param cell
     * @return 单元格值
     */
    public static Object getCellValue(Cell cell) {
        if (Objects.isNull(cell)) {
            return null;
        }
        Object cellValue;
        switch (cell.getCellTypeEnum()) {
            case STRING:
                cellValue = StringUtils.trimToEmpty(cell.getStringCellValue());
                cellValue = StringUtils.isBlank(cell.getStringCellValue()) ? null : cellValue;
                break;
            case BLANK:
                cellValue = null;
                break;
            case NUMERIC:
            case FORMULA:
                if (DateUtil.isCellDateFormatted(cell)) {
                    cellValue = DateUtil.getJavaDate(cell.getNumericCellValue());
                } else {
                    cellValue = cell.getNumericCellValue();
                }
                break;
            case BOOLEAN:
                cellValue = cell.getBooleanCellValue();
                break;
            case ERROR:
                cellValue = cell.getErrorCellValue();
                break;
            default:
                cellValue = null;
                break;
        }
        return cellValue;
    }
}
