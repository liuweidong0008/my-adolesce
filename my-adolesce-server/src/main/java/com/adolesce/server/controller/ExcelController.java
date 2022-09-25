/*******************************************************************************
 * @(#)ExcelExport.java 2019年12月18日 17:22
 * Copyright 2019 明医众禾科技（北京）有限责任公司. All rights reserved.
 *******************************************************************************/
package com.adolesce.server.controller;

import com.adolesce.common.vo.Response;
import com.adolesce.server.service.impl.ExcelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping("/excel")
public class ExcelController {
    @Resource
    private ExcelService excelService;

    /**
     * 导出方式一
     *
     * @param response
     */
    @GetMapping(value = "/export/style1")
    public void exportStyle1(HttpServletResponse response) throws Exception {
        this.excelService.exportStyle1(response);
    }

    /**
     * 导出方式二
     *
     * @param response
     */
    @GetMapping(value = "/export/style2")
    public void exportStyle2(HttpServletResponse response) throws Exception {
        this.excelService.exportStyle2(response);
    }

    /**
     * 导出方式三
     *
     * @param response
     */
    @GetMapping(value = "/export/style3")
    public void exportStyle3(HttpServletResponse response) throws Exception{
        this.excelService.exportStyle3(response);
    }

    /**
     * 导出方式四
     *
     * @param response
     */
    @GetMapping(value = "/export/style4")
    public void exportStyle4(HttpServletResponse response) throws Exception {
        this.excelService.exportStyle4(response);
    }

    /**
     * 导出方式五
     *
     * @param response
     */
    @GetMapping(value = "/export/style5")
    public void exportStyle5(HttpServletResponse response,Integer writeType) throws Exception {
        this.excelService.exportStyle5(response,writeType);
    }


    /**
     * 导入页面跳转
     */
    @GetMapping(value = "/import/jumpPage")
    public String jumpPage(){
        return "excel/import/uploadPage";
    }


    /**
     * 导入方式一
     *
     * @param file
     */
    @PostMapping(value = "/import/style1")
    public Response importStyle1(@RequestParam("file") MultipartFile file) throws Exception {
        return this.excelService.importStyle1(file);
    }

    /**
     * 导入方式二
     *
     * @param file
     */
    @PostMapping(value = "/import/style2")
    public Response importStyle2(@RequestParam("file") MultipartFile file,Integer readType) throws Exception {
        return this.excelService.importStyle2(file,readType);
    }


    /**
     * 大量数据导入方式一（用户模式读取，只适合导入15W以内的excel）
     *
     * @param file
     */
    @PostMapping(value = "/import/max/style1")
    public Response importMaxStyle1(@RequestParam("file") MultipartFile file) throws Exception {
        long startTime = System.currentTimeMillis();
        Response response = this.excelService.importMaxStyle1(file);
        long endTime = System.currentTimeMillis();
        System.out.println("耗时：" + (endTime - startTime) / 1000 + "秒");
        return response;
    }

    /**
     * 大量数据导入方式二(sax 事件驱动模式读取)
     *
     * @param file
     */
    @PostMapping(value = "/import/max/style2")
    public Response importMaxStyle2(@RequestParam("file") MultipartFile file) throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Response response = this.excelService.importMaxStyle2(file);

        stopWatch.stop();
        System.out.println("耗时：" + stopWatch.getTotalTimeSeconds() + "秒");
        return response;
    }

    /**
     * 大量数据导入方式三（easyExcel方式读取）
     *
     * @param file
     */
    @PostMapping(value = "/import/max/style3")
    public Response importMaxStyle3(@RequestParam("file") MultipartFile file){
        long startTime = System.currentTimeMillis();
        Response response = this.excelService.importMaxStyle3(file);
        long endTime = System.currentTimeMillis();
        System.out.println("耗时：" + (endTime - startTime) / 1000 + "秒");
        return response;
    }


}