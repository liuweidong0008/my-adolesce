package com.adolesce.common.utils.excel;

import com.adolesce.common.vo.EasyExcelSheetParams;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

public class EasyExcelHelper {
    private final static String DEFAULT_FILE_NAME = "数据文件";
    private final static String DEFAULT_SHEET_NAME = "sheet0";

    /**
     * 写出一个 excel 文件到本地
     * <br />
     * 将类型所有加了 @ExcelProperty 注解的属性全部写出
     *
     * @param fileName  文件路径 不要后缀
     * @param sheetName sheet名
     * @param data      写出的数据
     * @param clazz     要写出数据类的Class类型对象
     * @param <T>       写出的数据类型
     */
    public static <T> void writeExcel(String fileName, String sheetName, List<T> data, Class<T> clazz) {
        writeExcel(null, fileName, sheetName, data, clazz);
    }

    /**
     * 按照指定的属性名进行写出一个excel文件到本地
     *
     * @param attrName  指定的属性名 必须与数据类型的属性名一致
     * @param fileName  文件路径 不要后缀
     * @param sheetName sheet名
     * @param data      要写出的数据
     * @param clazz     要写出数据类的Class类型对象
     * @param <T>       要写出的数据类型
     */
    public static <T> void writeExcel(Set<String> attrName, String fileName, String sheetName, List<T> data, Class<T> clazz) {
        fileName = StringUtils.isBlank(fileName) ? DEFAULT_FILE_NAME : fileName;
        sheetName = StringUtils.isBlank(sheetName) ? DEFAULT_SHEET_NAME : sheetName;
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            write(fos, attrName, sheetName, data, clazz);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * 读取指定格式的excel文件
     *
     * @param fileName 文件名
     * @param clazz    数据类型的class对象
     * @param <T>      数据类型
     * @return
     */
    public static <T> List<T> readExcel(String fileName, Class<T> clazz) {
        return readExcel(fileName, clazz, null);
    }

    /**
     * 读取指定格式的 excel文件
     * 注意一旦传入自定义监听器，则返回的list为空，数据需要在自定义监听器里面获取
     *
     * @param fileName     文件名
     * @param clazz        数据类型的class对象
     * @param readListener 自定义监听器
     * @param <T>          数据类型
     * @return
     */
    public static <T> List<T> readExcel(String fileName, Class<T> clazz, ReadListener<T> readListener) {
        try (FileInputStream fis = new FileInputStream(fileName)) {
            return read(fis, clazz, readListener);
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    /**
     * 导出一个 excel 到浏览器（单个sheet）
     *
     * @param response
     * @param fileName  件名 最好为英文，不要后缀名
     * @param sheetName sheet名
     * @param data      要写出的数据
     * @param clazz     要写出数据类的Class类型对象
     * @param <T>       要写出的数据类型
     */
    public static <T> void export(HttpServletResponse response, String fileName, String sheetName, List<T> data, Class<T> clazz) {
        export(response, null, fileName, sheetName, data, clazz);
    }

    /**
     * 按照指定的属性名进行写出一个 excel 到浏览器（单个sheet）
     *
     * @param response
     * @param attrName  指定的属性名 必须与数据类型的属性名一致
     * @param fileName  文件名 最好为英文，不要后缀名
     * @param sheetName sheet名
     * @param data      要写出的数据
     * @param clazz     要写出数据类的Class类型对象
     * @param <T>       要写出的数据类型
     */
    public static <T> void export(HttpServletResponse response, Set<String> attrName, String fileName, String sheetName,
                                  List<T> data, Class<T> clazz) {
        try {
            OutputStream os = response.getOutputStream();
            fileName = StringUtils.isBlank(fileName) ? DEFAULT_FILE_NAME : fileName;
            sheetName = StringUtils.isBlank(sheetName) ? DEFAULT_SHEET_NAME : sheetName;
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setCharacterEncoding("utf-8");
            response.addHeader("Content-disposition", "attachment;filename=" +
                    new String(fileName.getBytes("gb2312"), "ISO8859-1")
                    + ExcelTypeEnum.XLSX.getValue());
            write(os, attrName, sheetName, data, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出一个 excel 到浏览器（多个sheet）
     *
     * @param response
     * @param fileName  件名 最好为英文，不要后缀名
     * @param sheetParams  多个sheet封装参数
     * @param <T>       要写出的数据类型
     */
    public static <T> void exportMutiSheets(HttpServletResponse response, String fileName, List<EasyExcelSheetParams> sheetParams) {
        ExcelWriter excelWriter = null;
        try {
            OutputStream os = response.getOutputStream();
            fileName = StringUtils.isBlank(fileName) ? DEFAULT_FILE_NAME : fileName;
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setCharacterEncoding("utf-8");
            response.addHeader("Content-disposition", "attachment;filename=" +
                    new String(fileName.getBytes("gb2312"), "ISO8859-1")
                    + ExcelTypeEnum.XLSX.getValue());

            excelWriter  = EasyExcel.write(response.getOutputStream())
                    // 自适应宽度，但是这个不是特别精确
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .build();
            int sheetNo = 0;
            for (EasyExcelSheetParams sheetParam: sheetParams){
                WriteSheet writeSheet = EasyExcel.writerSheet(
                        Objects.isNull(sheetParam.getSheetNo())?sheetNo++:sheetParam.getSheetNo(),
                        Objects.isNull(sheetParam.getSheetName())?"sheet"+ sheetNo:sheetParam.getSheetName()
                        ).head(sheetParam.getClazz()).build();
                excelWriter.write(sheetParam.getData(), writeSheet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(!Objects.isNull(excelWriter)){
                excelWriter.finish();
            }
        }
    }

    /**
     * 接收一个excel文件，并且进行解析
     * 注意一旦传入自定义监听器，则返回的list为空，数据需要在自定义监听器里面获取
     *
     * @param multipartFile excel文件
     * @param clazz         数据类型的class对象
     * @param readListener  监听器
     * @param <T>
     * @return
     */
    public static <T> List<T> importExcel(MultipartFile multipartFile, Class<T> clazz, ReadListener<T> readListener) {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            return read(inputStream, clazz, readListener);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static <T> void write(OutputStream os, Set<String> attrName, String sheetName, List<T> data, Class<T> clazz) {
        ExcelWriterBuilder write = EasyExcel.write(os, clazz);
        // 如果没有指定要写出那些属性数据，则写出全部
        if (!CollectionUtils.isEmpty(attrName)) {
            write.includeColumnFiledNames(attrName);
        }
        write.registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).sheet(sheetName).doWrite(data);
    }

    private static <T> List<T> read(InputStream in, Class<T> clazz, ReadListener<T> readListener) {
        List<T> list = new ArrayList<>();
        Optional<ReadListener> optional = Optional.ofNullable(readListener);
        EasyExcel.read(in, clazz, optional.orElse(new AnalysisEventListener<T>() {
            @Override
            public void invoke(T data, AnalysisContext context) {
                list.add(data);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                System.out.println("解析完成");
            }
        })).sheet().doRead();
        return list;
    }

}
