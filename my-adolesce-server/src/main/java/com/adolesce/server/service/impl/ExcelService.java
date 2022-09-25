/*******************************************************************************
 * @(#)InvoiceService.java 2019年11月19日 14:22
 * Copyright 2019 明医众禾科技（北京）有限责任公司. All rights reserved.
 *******************************************************************************/
package com.adolesce.server.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.StyleSet;
import com.adolesce.cloud.dubbo.api.db.HeiMaStudentMsgApi;
import com.adolesce.cloud.dubbo.domain.db.CaiWuMsg;
import com.adolesce.cloud.dubbo.domain.db.HeiMaStudentMsg;
import com.adolesce.cloud.dubbo.domain.mongo.Address;
import com.adolesce.cloud.dubbo.domain.mongo.Location;
import com.adolesce.cloud.dubbo.dto.HeiMaStudentMsgDto;
import com.adolesce.common.utils.excel.EasyExcelHelper;
import com.adolesce.common.utils.excel.export.ExcelExportHelper;
import com.adolesce.common.utils.excel.export.ExcelExportParams;
import com.adolesce.common.utils.excel.imports.ExcelImportBaseBo;
import com.adolesce.common.utils.excel.imports.ExcelImportHelper;
import com.adolesce.common.vo.EasyExcelSheetParams;
import com.adolesce.common.vo.Response;
import com.adolesce.server.handler.CaiWuSheetHandler;
import com.adolesce.server.listener.CaiwuMsgEasyExcelListener;
import com.adolesce.server.listener.StudentMsgEasyExcelListener;
import com.adolesce.server.vo.excel.CaiWuMsgImportVo;
import com.adolesce.server.vo.excel.HeiMaStudentMsgEasyVo;
import com.adolesce.server.vo.excel.HeiMaStudentMsgImportVo;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.enums.CellExtraTypeEnum;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 * Application name： ExcelService.java
 * Application describing：Excel服务层
 */
@Slf4j
@Service
public class ExcelService {
    @DubboReference
    private HeiMaStudentMsgApi heiMaStudentMsgApi;

    /**
     * 导出方式一（代码指定sheet header）
     *
     * @param response
     */
    public void exportStyle1(HttpServletResponse response) throws Exception {
        List<HeiMaStudentMsg> records = getHeiMaStudentMsgRecords();
        if (CollUtil.isEmpty(records)) {
            return;
        }
        String[] headerName1 = {"姓名", "年龄", "手机号", "学历", "毕业学校", "毕业时间", "城市", "地址描述"};
        String[] headerKey1 = {"name", "age", "phone", "education", "school", "graduateTime", "address.city", "address.location.address"};

        String[] headerName2 = {"姓名", "薪资", "工作信息", "入职日期", "备注", "创建时间"};
        String[] headerKey2 = {"name", "salary", "workMsg", "ruzhiTime", "remark", "createTime"};

        ExcelExportParams exportParams = ExcelExportParams.create(response)
                .addSheet("sheet1", headerName1, headerKey1, records)
                .addSheet("sheet2", headerName2, headerKey2, records, ExcelExportHelper.YMD_HMS);
        exportParams.setFileName("学生信息表");

        ExcelExportHelper.export(exportParams);
    }

    /**
     * 导出方式二（xml配置sheet header）
     *
     * @param response
     */
    public void exportStyle2(HttpServletResponse response) throws Exception {
        List<HeiMaStudentMsg> records = getHeiMaStudentMsgRecords();
        if (CollUtil.isEmpty(records)) {
            return;
        }
        try {
            ExcelExportParams exportParams = ExcelExportParams.create(response, "studentMsgRecord");
            exportParams.addDataList("sheet1", records);
            exportParams.addDataList("sheet2", records, ExcelExportHelper.YMD_HMS);
            exportParams.setFileName("学生信息表");
            ExcelExportHelper.export(exportParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出方式三（配置Excel导出模板方式）
     *
     * @param response
     */
    public void exportStyle3(HttpServletResponse response) throws Exception {
        List<HeiMaStudentMsg> records = getHeiMaStudentMsgRecords();
        if (CollUtil.isEmpty(records)) {
            return;
        }
        //方式一：sheet名称由模板决定
        /*List dataLists = new ArrayList<>();
        dataLists.add(records);
        dataLists.add(records);
        ExcelExportHelper.export(dataLists, "studentMsgRecordTemp.xlsx", "学生信息记录表1.xlsx", response);*/

        //方式二：sheet名称可代码指定
        //模板sheet名称
        String[] tempSheetNames = {"Sheet1", "Sheet2"};
        //自定义sheet名称
        String[] sheetNames = {"页面一", "页面二"};
        //导出数据，添加数据与sheet顺序对应
        List dataLists = new ArrayList<>();
        dataLists.add(records);
        dataLists.add(records);
        ExcelExportHelper.export(tempSheetNames, sheetNames, dataLists, "studentMsgRecordTemp.xlsx",
                "学生信息记录表2.xlsx", response);
    }

    /**
     * 导出方式四（Hutool）
     *
     * @param response
     */
    public void exportStyle4(HttpServletResponse response) throws Exception {
        List<HeiMaStudentMsg> records = getHeiMaStudentMsgRecords();
        if (CollUtil.isEmpty(records)) {
            return;
        }
        String[] headerName1 = {"姓名", "年龄", "手机号", "学历", "毕业学校", "毕业时间"};
        String[] headerKey1 = {"name", "age", "phone", "education", "school", "graduateTime"};

        String[] headerName2 = {"姓名", "薪资", "工作信息", "入职日期", "备注", "创建时间"};
        String[] headerKey2 = {"name", "salary", "workMsg", "ruzhiTime", "remark", "createTime"};
        try {
            // 通过工具类创建writer
            //xls
            //ExcelWriter writer = ExcelUtil.getWriter();
            //xlsx
            ExcelWriter writer = ExcelUtil.getBigWriter();
            // 默认未添加alias的属性也会写出，如果想只写出加了别名的字段，可以调用此方法排除之
            writer.setOnlyAlias(true);

            //创建sheet1
            writer.setSheet("sheet1");
            for (int i = 0; i < headerKey1.length; i++) {
                writer.addHeaderAlias(headerKey1[i], headerName1[i]);
                writer.setColumnWidth(i, 24);
            }
            this.setStyle(writer);
            //合并单元格后的标题行，使用默认标题样式
            writer.merge(headerName1.length - 1, "学生信息记录1");
            // 一次性写出内容，使用默认样式，强制输出标题
            writer.write(records, true);

            //关键：清空别名Map，防止两个sheet之间数据干扰
            writer.clearHeaderAlias();

            //创建sheet2
            writer.setSheet("sheet2");
            for (int i = 0; i < headerKey2.length; i++) {
                writer.addHeaderAlias(headerKey2[i], headerName2[i]);
                writer.setColumnWidth(i, 24);
            }
            this.setStyle(writer);
            //合并单元格后的标题行，使用默认标题样式
            writer.merge(headerName1.length - 1, "学生信息记录2");
            // 一次性写出内容，使用默认样式，强制输出标题
            writer.write(records, true);

            //输出excel至浏览器
            ExcelExportHelper.outputFile(response, "学生信息记录表.xlsx", writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出方式五（easyexcel）
     *
     * @param response
     * @param writeType
     */
    public void exportStyle5(HttpServletResponse response, Integer writeType) throws Exception {
        com.alibaba.excel.ExcelWriter excelWriter = null;
        try {
            List<HeiMaStudentMsg> records = getHeiMaStudentMsgRecords();
            if (CollUtil.isEmpty(records)) {
                return;
            }
            List<HeiMaStudentMsgEasyVo> exportVos = new ArrayList<>();
            records.stream().forEach(r -> {
                HeiMaStudentMsgEasyVo exportVo = new HeiMaStudentMsgEasyVo();
                BeanUtils.copyProperties(r, exportVo);
                exportVos.add(exportVo);
            });

            // 设置要导出列的属性名
            // 必须要跟类型的属性名保持一致
            Set<String> columnNames  = new HashSet<>();
            columnNames.add("name");

            switch (writeType){
                case 1:
                    //原生方式导出单个sheet页
                    String fileName = "测试导出单个sheet";
                    response.setContentType("application/vnd.ms-excel;charset=utf-8");
                    response.setCharacterEncoding("utf-8");
                    response.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes("gb2312"), "ISO8859-1") + ".xlsx");

                    //2.1、利用原生方式导出单个sheet的excel文件
                    EasyExcel.write(response.getOutputStream(),HeiMaStudentMsgEasyVo.class)
                            //设置 要导出列的属性名
                            //.includeColumnFiledNames(columnNames)
                            //.excludeColumnFieldNames(columnNames)
                            // 自适应宽度，但是这个不是特别精确
                            .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                            .sheet("测试sheet")
                            .doWrite(exportVos);
                    break;
                case 2:
                    //原生方式导出多个sheet页
                    fileName = "测试导出多个sheet";
                    response.setContentType("application/vnd.ms-excel;charset=utf-8");
                    response.setCharacterEncoding("utf-8");
                    response.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes("gb2312"), "ISO8859-1") + ".xlsx");

                    //2.2、利用原生方式导出多个sheet的excel文件
                    excelWriter = EasyExcel.write(response.getOutputStream(), HeiMaStudentMsgEasyVo.class)
                            // 自适应宽度，但是这个不是特别精确
                            .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                            .build();
                    WriteSheet writeSheet;
                    writeSheet = EasyExcel.writerSheet(0, "sheet1").head(HeiMaStudentMsgEasyVo.class).build();
                    excelWriter.write(exportVos, writeSheet);
                    writeSheet = EasyExcel.writerSheet(1, "sheet2").head(HeiMaStudentMsgEasyVo.class).build();
                    excelWriter.write(exportVos, writeSheet);
                    break;
                case 3:
                    //原生方式多页mysql数据分多次写入同一sheet页
                    fileName = "测试导出单个sheet";
                    response.setContentType("application/vnd.ms-excel;charset=utf-8");
                    response.setCharacterEncoding("utf-8");
                    response.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes("gb2312"), "ISO8859-1") + ".xlsx");
                    excelWriter = EasyExcel.write(response.getOutputStream(), HeiMaStudentMsgEasyVo.class).build();
                    // 这里注意 如果同一个sheet只要创建一次
                    writeSheet = EasyExcel.writerSheet("测试sheet").build();
                    // 去调用写入,这里我调用了五次，实际使用时根据数据库分页的总的页数来
                    for (int i = 0; i < 5; i++) {
                        // 分页去数据库查询数据 这里可以去数据库查询每一页的数据
                        List<HeiMaStudentMsgEasyVo> data = exportVos;
                        excelWriter.write(data, writeSheet);
                    }
                    break;
                case 4:
                    //原生方式多页mysql数据分多次写到不同的sheet 同一个对象
                    excelWriter = EasyExcel.write(response.getOutputStream(), HeiMaStudentMsgEasyVo.class).build();
                    // 去调用写入,这里我调用了五次，实际使用时根据数据库分页的总的页数来。这里最终会写到5个sheet里面
                    for (int i = 0; i < 5; i++) {
                        // 每次都要创建writeSheet 这里注意必须指定sheetNo 而且sheetName必须不一样
                        writeSheet = EasyExcel.writerSheet(i, "模板" + i).build();
                        // 分页去数据库查询数据 这里可以去数据库查询每一页的数据
                        List<HeiMaStudentMsgEasyVo> data = exportVos;
                        excelWriter.write(data, writeSheet);
                    }
                    break;
                case 5:
                    //利用自定义工具类导出单个sheet
                    EasyExcelHelper.export(response,"我是测试文件","测试sheet1",exportVos,HeiMaStudentMsgEasyVo.class);
                    break;
                case 6:
                    //利用自定义工具类导出多个sheet的excel文件
                    List<EasyExcelSheetParams> sheetParams = new ArrayList<>();
                    EasyExcelSheetParams sheetParam1 = new EasyExcelSheetParams(exportVos,HeiMaStudentMsgEasyVo.class);
                    sheetParam1.setSheetNo(0);
                    sheetParam1.setSheetName("sheet1");

                    EasyExcelSheetParams sheetParam2 = new EasyExcelSheetParams(exportVos,HeiMaStudentMsgEasyVo.class);
                    sheetParam2.setSheetNo(1);
                    sheetParam2.setSheetName("sheet2");

                    sheetParams.add(sheetParam1);
                    sheetParams.add(sheetParam2);
                    EasyExcelHelper.exportMutiSheets(response,"我是测试文件",sheetParams);
                    break;
                default:
                    break;
            }
        } finally {
            //其实不用，因为easyExcel的文件流会自动关闭
            if (!Objects.isNull(excelWriter)) {
                excelWriter.finish();
            }
        }
    }

    private void setStyle(ExcelWriter writer) {
        writer.setDefaultRowHeight(32);
        Font headerCellFont = writer.createFont();
        headerCellFont.setFontName("宋体");
        headerCellFont.setFontHeightInPoints((short) 14);
        headerCellFont.setBold(true);

        Font dataCellFont = writer.createFont();
        headerCellFont.setFontName("宋体");
        headerCellFont.setFontHeightInPoints((short) 11);

        StyleSet style = writer.getStyleSet();
        style.getHeadCellStyle().setFont(headerCellFont);
        style.getCellStyle().setFont(dataCellFont);
        // 第二个参数表示是否也设置头部单元格背景
        style.setBackgroundColor(IndexedColors.WHITE1, false);
    }

    private List<HeiMaStudentMsg> getHeiMaStudentMsgRecords() throws ParseException {
        int n = 1;
        List<HeiMaStudentMsg> records = new ArrayList<>();
        HeiMaStudentMsgDto heiMaStudentMsgDto = HeiMaStudentMsgDto.builder().currentPage(1).pageSize(5000).build();
        Response response = heiMaStudentMsgApi.queryByParams(heiMaStudentMsgDto);
        if (Objects.isNull(response) || CollUtil.isEmpty((List) ((Map) response.getData()).get("data"))) {
            return records;
        }
        records = (List<HeiMaStudentMsg>) ((Map) response.getData()).get("data");

        if (CollUtil.isNotEmpty(records)) {
            for (HeiMaStudentMsg record : records) {
                Address address = new Address();
                address.setCity("北京");
                Location location = new Location();
                location.setAddress("东城区南锣鼓巷子" + n++ + "号");
                address.setLocation(location);
                record.setAddress(address);
            }
        }
        return records;
    }

    /*
     * 导入方式一
     *
     * @param fileItem
     * @param redirectAttributes
     */
    public Response importStyle1(MultipartFile fileItem) throws Exception {
        //读取sheet1信息,并保存在List中
        List<ExcelImportBaseBo> resultList = ExcelImportHelper.importExcel("studentMsgImportConfiger", fileItem, 0);
        //将导入数据保存到数据库表
        return this.saveImportData(resultList);
    }

    private Response saveImportData(List<ExcelImportBaseBo> resultList) {
        HeiMaStudentMsgImportVo importVo;
        Map<String, String> dataMap = new HashMap();
        //文件数据校验
        if (CollUtil.isNotEmpty(resultList)) {
            for (ExcelImportBaseBo bo : resultList) {
                //基本校验导入数据的格式
                if (bo.hasErrors()) {
                    return Response.failure("第" + bo.getDataLine() + "行：" + bo.getAllErrors());
                }
                //业务校验有无相同手机号
                importVo = (HeiMaStudentMsgImportVo) bo;
                if (Objects.nonNull(importVo.getPhone())) {
                    String data = dataMap.get(importVo.getPhone());
                    if (Objects.nonNull(data)) {
                        return Response.failure("第【" + importVo.getDataLine() + "】行手机号与第【" + data + "】行手机号重复,请确认！\n手机号：【" + importVo.getPhone() + "】");
                    }
                    dataMap.put(importVo.getPhone(), importVo.getDataLine().toString());
                }
            }
        } else {
            return Response.failure("excel中填写的信息为空,请填写相关数据!");
        }
        //数据入库
        List<HeiMaStudentMsg> studentMsgs = new ArrayList<>();
        for (int i = 0; i < resultList.size(); i++) {
            //获取表格数据，保存到对象中
            importVo = (HeiMaStudentMsgImportVo) resultList.get(i);
            HeiMaStudentMsg studentMsg = new HeiMaStudentMsg();
            BeanUtils.copyProperties(importVo, studentMsg);
            studentMsgs.add(studentMsg);
        }
        studentMsgs.forEach(System.out::println);
        return Response.success(studentMsgs, "数据导入成功");
    }

    public Response importStyle2(MultipartFile file, Integer readType) throws IOException {
        /*
         * EasyExcel是一个基于Java的、快速、简洁、解决大文件内存溢出的Excel处理工具。他能让你在不用考虑性能、内存的等因素的情况下，快速完成Excel的读、写等功能
         * 快速：快速的读取excel中的数据。   简洁：映射excel和实体类，让代码变的更加简洁。   大文件：在读写大文件的时候使用磁盘做缓存，更加的节约内存（64M内存20秒读取75M）
         *
         * EasyExcel 读取 是基于SAX方式 因此在解析时需要传入监听器
         */
        switch (readType) {
            case 1:
                //easyExcel原生方式导入
                EasyExcel.read(file.getInputStream(), HeiMaStudentMsgEasyVo.class, new StudentMsgEasyExcelListener())
                        // 需要读取批注 默认不读取
                        .extraRead(CellExtraTypeEnum.COMMENT)
                        // 需要读取超链接 默认不读取
                        .extraRead(CellExtraTypeEnum.HYPERLINK)
                        // 需要读取合并单元格信息 默认不读取
                        .extraRead(CellExtraTypeEnum.MERGE).sheet(0).doRead();
                break;
            case 2:
                //easyExcel工具类方式导入
                EasyExcelHelper.importExcel(file, HeiMaStudentMsgEasyVo.class, new StudentMsgEasyExcelListener(), 1);
                break;
            case 3:
                //内置PageReadListener方式导入
                EasyExcel.read(file.getInputStream(), HeiMaStudentMsgEasyVo.class, new PageReadListener<HeiMaStudentMsgEasyVo>(dataList -> {
                    for (HeiMaStudentMsgEasyVo demoData : dataList) {
                        log.info("读取到一条数据{}", JSON.toJSONString(demoData));
                    }
                }))
                        //.registerConverter(new GenderConverter())//注册全局转换器,所有java为string,excel为string的都会用这个转换器
                        //.headRowNumber(1)  //设置头占的行数，如果多行头，可以设置其他值。不传入也可以，因为默认会根据class来解析。不传class，默认为1，传了最终以这个为准
                        .sheet(0).doRead();
                break;
            case 4:
                // 一个文件一个reader方式导入（构建一个excelReader可以读取多个sheet页）
                try (ExcelReader excelReader = EasyExcel.read(file.getInputStream(), HeiMaStudentMsgEasyVo.class, new StudentMsgEasyExcelListener()).build()) {
                    // 构建一个sheet 这里可以指定名字或者no
                    ReadSheet readSheet = EasyExcel.readSheet(1).build();
                    // 读取一个sheet
                    excelReader.read(readSheet);
                }
                break;
            case 5:
                // 读取全部sheet方式导入
                // 这里需要注意 DemoDataListener的doAfterAllAnalysed 会在每个sheet读取完毕后调用一次。然后所有sheet都会往同一个DemoDataListener里面写
                EasyExcel.read(file.getInputStream(), HeiMaStudentMsgEasyVo.class, new StudentMsgEasyExcelListener()).doReadAll();
                break;
            case 6:
                // 读取部分sheet方式导入
                try (ExcelReader excelReader = EasyExcel.read(file.getInputStream()).build()) {
                    // 这里为了简单 所以注册了 同样的head 和Listener 自己使用功能必须不同的Listener
                    ReadSheet readSheet1 =
                            EasyExcel.readSheet(0).head(HeiMaStudentMsgEasyVo.class).registerReadListener(new StudentMsgEasyExcelListener()).build();
                    ReadSheet readSheet2 =
                            EasyExcel.readSheet(1).head(HeiMaStudentMsgEasyVo.class).registerReadListener(new StudentMsgEasyExcelListener()).build();
                    // 这里注意 一定要把sheet1 sheet2 一起传进去，不然有个问题就是03版的excel 会读取多次，浪费性能
                    excelReader.read(readSheet1, readSheet2);
                }
                break;
            case 7: {
                // 同步方式导入：不推荐使用，如果数据量大会把数据放到内存里面
                // 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish
                List<HeiMaStudentMsgEasyVo> list = EasyExcel.read(file.getInputStream()).head(HeiMaStudentMsgEasyVo.class).sheet().doReadSync();
                for (HeiMaStudentMsgEasyVo data : list) {
                    log.info("读取到数据:{}", JSON.toJSONString(data));
                }
                // 这里 也可以不指定class，返回一个list，然后读取第一个sheet 同步读取会自动finish
                /*List<Map<Integer, String>> listMap = EasyExcel.read(file.getInputStream()).sheet().doReadSync();
                for (Map<Integer, String> data : listMap) {
                    // 返回每条数据的键值对 表示所在的列 和所在列的值
                    log.info("读取到数据:{}", JSON.toJSONString(data));
                }*/
                break;
            }
            default:
                break;
        }
        return Response.success();
    }


    /*
     * 导入海量数据方式一
     *
     * @param fileItem
     * @param redirectAttributes
     */
    public Response importMaxStyle1(MultipartFile fileItem) throws Exception {
        //创建一个Excel导入帮助类实例
        ExcelImportHelper importHelper = new ExcelImportHelper();
        //读取sheet1信息,并保存在List中
        List<ExcelImportBaseBo> resultList = importHelper.importExcel("caiwuMsgImportConfiger", fileItem, 3);
        //将导入数据保存到数据库表
        return this.saveImportMaxData(resultList);
    }

    private Response saveImportMaxData(List<ExcelImportBaseBo> resultList) {
        CaiWuMsgImportVo importVo;
        Map<String, String> dataMap = new HashMap();
        //文件数据校验
        if (CollUtil.isNotEmpty(resultList)) {
            for (ExcelImportBaseBo bo : resultList) {
                //基本校验导入数据的格式
                if (bo.hasErrors()) {
                    return Response.failure("第" + bo.getDataLine() + "行：" + bo.getAllErrors());
                }
            }
        } else {
            return Response.failure("excel中填写的信息为空,请填写相关数据!");
        }
        //数据入库
        List<CaiWuMsg> studentMsgs = new ArrayList<>();
        CaiWuMsg caiWuMsg;
        for (int i = 0; i < resultList.size(); i++) {
            //获取表格数据，保存到对象中
            importVo = (CaiWuMsgImportVo) resultList.get(i);
            caiWuMsg = new CaiWuMsg();
            BeanUtils.copyProperties(importVo, caiWuMsg);
            studentMsgs.add(caiWuMsg);
        }
        studentMsgs.forEach(System.out::println);
        return Response.success(studentMsgs, "数据导入成功");
    }

    /**
     * 导入海量数据方式二
     *
     * @param file
     * @return
     * @throws Exception
     */
    public Response importMaxStyle2(MultipartFile file) throws Exception {
        //1、指定读取excel的哪个sheet页
        int readSheetNo = 4;
        //2、读取出来的数据存入该list
        List<CaiWuMsgImportVo> dataList = new ArrayList<>();
        //3、开始读取
        ExcelImportHelper.importMaxExcel(file.getInputStream(), readSheetNo, new CaiWuSheetHandler(dataList));
        return Response.success("数据导入成功");
    }

    /**
     * 导入海量数据方式三
     *
     * @param file
     * @return
     */
    public Response importMaxStyle3(MultipartFile file) {
        /*
         * EasyExcel 读取 是基于SAX方式
         * 因此在解析时需要传入监听器
         */
        // 第一个参数 为 excel文件路径
        // 读取时的数据类型
        // 监听器
        //EasyExcel.read(file.getInputStream(), HeiMaStudentMsgEasyVo.class,new StudentMsgListener()).sheet().doRead();
        EasyExcelHelper.importExcel(file, CaiWuMsgImportVo.class, new CaiwuMsgEasyExcelListener(), 3);
        return Response.success();
    }

}
