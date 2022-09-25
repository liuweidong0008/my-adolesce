package com.adolesce.server.excel;

import com.adolesce.common.utils.excel.imports.ExcelImportHelper;
import com.adolesce.server.handler.CaiWuSheetHandler;
import com.adolesce.server.vo.excel.CaiWuMsgImportVo;
import org.junit.Test;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2022/8/3 20:16
 */
public class ExcelImportTest {
    /**
     * excel海量数据导入测试
     * @throws Exception
     */
    @Test
    public void importMaxDataTest1() throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        //1、指定需要导入的excel文档路径
        String path = "D:\\pictrues\\财务管控（20210607修正）(2)(1).xlsx";
        //2、指定读取excel的哪个sheet页
        int readSheetNo = 4;
        //3、读取出来的数据存入该list
        List<CaiWuMsgImportVo> dataList = new ArrayList<>();
        //4、开始读取
        ExcelImportHelper.importMaxExcel(path,readSheetNo,new CaiWuSheetHandler(dataList));
    }
}
