/*******************************************************************************
 * @(#)ExcelExport.java 2019年12月18日 17:22
 * Copyright 2019 明医众禾科技（北京）有限责任公司. All rights reserved.
 *******************************************************************************/
package com.adolesce.server.controller;

import com.adolesce.common.vo.Response;
import com.adolesce.server.service.impl.ExcelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * <b>Application name：</b> ExcelExport.java <br>
 * <b>Application describing： </b> <br>
 * <b>Copyright：</b> Copyright &copy; 2019 明医众禾科技（北京）有限责任公司 版权所有。<br>
 * <b>Company：</b> 明医众禾科技（北京）有限责任公司 <br>
 * <b>@Date：</b> 2019年12月18日 17:22 <br>
 * <b>@author：</b> <a href="mailto:lwd@miyzh.com"> liuWeidong </a> <br>
 * <b>@version：</b>V2.0.0 <br>
 */

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
    public void exportStyle5(HttpServletResponse response) throws Exception {
        this.excelService.exportStyle5(response);
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
    public Response importStyle2(@RequestParam("file") MultipartFile file) throws Exception {
        return this.excelService.importStyle2(file);
    }

}