package com.adolesce.server.listener;

import cn.hutool.core.collection.CollectionUtil;
import com.adolesce.server.vo.excel.CaiWuMsgImportVo;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

// 注意：AnalysisEventListener不能交由spring管理，每次读取excel都需要new，至于里面用到spring管理的bean,可以通过构造方法传递进去
@Slf4j
public class CaiwuMsgEasyExcelListener extends AnalysisEventListener<CaiWuMsgImportVo> {

    /**
     * 每隔50条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 50;
    /**
     * 缓存的数据
     */
    private List<CaiWuMsgImportVo> cachedDataList = new ArrayList<>(BATCH_COUNT);
    /**
     * 假设这个是一个DAO，当然有业务逻辑这个也可以是一个service。当然如果不用存储这个对象没用。
     */
    //private DemoDAO demoDAO;

    public CaiwuMsgEasyExcelListener() {
    }

    /**
     * 如果使用了spring,请使用这个构造方法。每次创建Listener的时候需要把spring管理的类传进来
     *
     * @param demoDAO
     */
    //public DemoDataListener(DemoDAO demoDAO) {
        //this.demoDAO = demoDAO;
    //}


    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data    one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     */
    @Override
    public void invoke(CaiWuMsgImportVo data, AnalysisContext context) {
        log.info("逐条解析数据:{}", JSON.toJSONString(data));
        cachedDataList.add(data);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            // 存储完成清理 list
            cachedDataList = new ArrayList(BATCH_COUNT);
        }
    }

    /**
     * 所有数据解析完成了 都会来调用
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
            System.out.println(cachedDataList);
            //demoDAO.save(cachedDataList);
            log.info("存储数据库成功！");
            log.info("================================================================================================================");
        }
    }
}