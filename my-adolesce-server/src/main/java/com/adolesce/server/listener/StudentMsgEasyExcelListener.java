package com.adolesce.server.listener;

import cn.hutool.core.collection.CollectionUtil;
import com.adolesce.server.vo.excel.HeiMaStudentMsgEasyVo;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.metadata.CellExtra;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.api.Assert;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

//注意：AnalysisEventListener不能交由spring管理，每次读取excel都需要new，至于里面用到spring管理的bean,可以通过构造方法传递进去
@Slf4j
public class StudentMsgEasyExcelListener extends AnalysisEventListener<HeiMaStudentMsgEasyVo> {

    /**
     * 每隔50条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 50;
    /**
     * 缓存的数据
     */
    private List<HeiMaStudentMsgEasyVo> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
    /**
     * 假设这个是一个DAO，当然有业务逻辑这个也可以是一个service。当然如果不用存储这个对象没用。
     */
    //private DemoDAO demoDAO;

    public StudentMsgEasyExcelListener() {
    }

    /**
     * 如果使用了spring管理的类,请使用这个构造方法。每次创建Listener的时候需要把spring管理的类传进来
     *
     * @param demoDAO
     */
    //public StudentMsgEasyExcelListener(DemoDAO demoDAO) {
        //this.demoDAO = demoDAO;
    //}


    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data    one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     */
    @Override
    public void invoke(HeiMaStudentMsgEasyVo data, AnalysisContext context) {
        log.info("逐条解析数据:{}", JSON.toJSONString(data));
        cachedDataList.add(data);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            // 存储完成清理 list
            //cachedDataList.clear();
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    /**
     * 所有数据解析完成了 会进行调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
        log.info("所有数据解析完成！");
    }

    /**
     * 加上存储数据库
     */
    private void saveData() {
        if(CollectionUtil.isNotEmpty(cachedDataList)){
            log.info("================================================================================================================");
            log.info("{}条数据，开始存储数据库！", cachedDataList.size());
            //System.out.println(cachedDataList);
            //demoDAO.save(cachedDataList);
            log.info("存储数据库成功！");
            log.info("================================================================================================================");
        }
    }

    /**
     * 读取表头：这里会一行行的返回头（最先开始调用）
     *
     * @param headMap
     * @param context
     */
    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        log.info("解析到一条头数据:{}", JSON.toJSONString(headMap));
        // 如果想转成成 Map<Integer,String>
        // 方案1： 不要implements ReadListener 而是 extends AnalysisEventListener
        // 方案2： 调用 ConverterUtils.convertToStringMap(headMap, context) 自动会转换
    }

    /**
     * 读取额外信息（批注、超链接、合并单元格信息读取）：在doAfterAllAnalysed之前调用
     *      EasyExcel.read 后面需要进行API指定
     *        // 需要读取批注 默认不读取
     *        .extraRead(CellExtraTypeEnum.COMMENT)
     *        // 需要读取超链接 默认不读取
     *        .extraRead(CellExtraTypeEnum.HYPERLINK)
     *        // 需要读取合并单元格信息 默认不读取
     *        .extraRead(CellExtraTypeEnum.MERGE).sheet().doRead();
     *
     * @param extra
     * @param context
     */
    @Override
    public void extra(CellExtra extra, AnalysisContext context) {
        log.info("读取到了一条额外信息:{}", JSON.toJSONString(extra));
        switch (extra.getType()) {
            case COMMENT:
                log.info("额外信息是批注,在rowIndex:{},columnIndex;{},内容是:{}", extra.getRowIndex(), extra.getColumnIndex(),
                        extra.getText());
                break;
            case HYPERLINK:
                if ("Sheet1!A1".equals(extra.getText())) {
                    log.info("额外信息是超链接,在rowIndex:{},columnIndex;{},内容是:{}", extra.getRowIndex(),
                            extra.getColumnIndex(), extra.getText());
                } else if ("Sheet2!A1".equals(extra.getText())) {
                    log.info(
                            "额外信息是超链接,而且覆盖了一个区间,在firstRowIndex:{},firstColumnIndex;{},lastRowIndex:{},lastColumnIndex:{},"
                                    + "内容是:{}",
                            extra.getFirstRowIndex(), extra.getFirstColumnIndex(), extra.getLastRowIndex(),
                            extra.getLastColumnIndex(), extra.getText());
                } else {
                    Assert.fail("Unknown hyperlink!");
                }
                break;
            case MERGE:
                log.info(
                        "额外信息是合并单元格,而且覆盖了一个区间,在firstRowIndex:{},firstColumnIndex;{},lastRowIndex:{},lastColumnIndex:{}",
                        extra.getFirstRowIndex(), extra.getFirstColumnIndex(), extra.getLastRowIndex(),
                        extra.getLastColumnIndex());
                break;
            default:
        }
    }

    /**
     * 在转换异常 获取其他异常下会调用本接口。抛出异常则停止读取。如果这里不抛出异常则 继续读取下一行。
     *
     * @param exception
     * @param context
     * @throws Exception
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) {
        log.error("解析失败，但是继续解析下一行:{}", exception.getMessage());
        // 如果是某一个单元格的转换异常 能获取到具体行号
        // 如果要获取头的信息 配合invokeHeadMap使用
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException)exception;
            log.error("第{}行，第{}列解析异常，数据为:{}", excelDataConvertException.getRowIndex(),
                    excelDataConvertException.getColumnIndex(), excelDataConvertException.getCellData());
        }
    }

}