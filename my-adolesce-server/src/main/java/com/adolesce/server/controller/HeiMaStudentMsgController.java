package com.adolesce.server.controller;

import com.adolesce.cloud.dubbo.dto.HeiMaStudentMsgDto;
import com.adolesce.common.vo.Response;
import com.adolesce.server.dto.CourseExportParams;
import com.adolesce.server.service.impl.HeiMaStudentMsgService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping
public class HeiMaStudentMsgController {
    @Autowired
    private HeiMaStudentMsgService heiMaStudentMsgService;

    /**
     * 分页查询
     * @throws Exception
     */
    @RequestMapping("/heiMaStudentMsg/queryByParams")
    public String queryByParams(HeiMaStudentMsgDto studentMsgDto){
        Response response;
        try {
            response = heiMaStudentMsgService.queryByParams(studentMsgDto);
        }catch (Exception e){
            response = Response.failure();
            log.error("查询学生信息出错：{}",e);
        }
        String jsonData = JSON.toJSONString(response);
        String retStr = studentMsgDto.getCallback() + "(" + jsonData + ")";
        return retStr;
    }

    /**
     * 保存学生信息
     * @throws Exception
     */
    @RequestMapping("/heiMaStudentMsg/save")
    public String save(HeiMaStudentMsgDto studentMsgDto){
        Response response;
        try {
            heiMaStudentMsgService.saveStudentMsg(studentMsgDto);
            response = Response.success();
        }catch (Exception e){
            response = Response.failure();
            log.error("保存学生信息出错：{}",e);
        }
        String jsonData = JSON.toJSONString(response);
        String retStr = studentMsgDto.getCallback() + "(" + jsonData + ")";
        return retStr;
    }

    /**
     * 根据ID删除
     * @throws Exception
     */
    @RequestMapping("/heiMaStudentMsg/delete")
    public String delete(HeiMaStudentMsgDto studentMsgDto){
        Response response;
        try {
            heiMaStudentMsgService.deleteById(studentMsgDto.getId());
            response = Response.success();
        }catch (Exception e){
            response = Response.failure();
            log.error("删除学生信息出错：{}",e);
        }
        String jsonData = JSON.toJSONString(response);
        String retStr = studentMsgDto.getCallback() + "(" + jsonData + ")";
        return retStr;
    }

    /**
     * 根据用户ID查询详情
     * @throws Exception
     */
    @RequestMapping("/heiMaStudentMsg/queryById")
    public String queryById(HeiMaStudentMsgDto studentMsgDto){
        Response response;
        try {
            response = heiMaStudentMsgService.queryById(studentMsgDto);
        }catch (Exception e){
            response = Response.failure();
            log.error("根据ID查询学生信息出错：{}",e);
        }
        String jsonData = JSON.toJSONString(response);
        String retStr = studentMsgDto.getCallback() + "(" + jsonData + ")";
        return retStr;
    }

    /**
     * 文件上传
     * @param offerFile
     * @return
     */
    @PostMapping("/heiMaStudentMsg/upload")
    public void upload(MultipartFile offerFile){
        //file是一个临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除
        log.info(offerFile.toString());

        String basePath = "D:/img/";
        //原始文件名
        String originalFilename = offerFile.getOriginalFilename();//abc.jpg
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //使用UUID重新生成文件名，防止文件名称重复造成文件覆盖
        String fileName = UUID.randomUUID().toString() + suffix;//dfsdfdfd.jpg

        //创建一个目录对象
        File dir = new File(basePath);
        //判断当前目录是否存在
        if(!dir.exists()){
            //目录不存在，需要创建
            dir.mkdirs();
        }
        try {
            //将临时文件转存到指定位置
            offerFile.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * excel导入
     * @return
     */
    @RequestMapping("/heiMaStudentMsg/importExcel")
    public Response importExcel(MultipartFile excelFile) throws Exception {
        return this.heiMaStudentMsgService.importStyle(excelFile);
    }

    /**
     * 导出老师课表
     */
    @ResponseBody
    @GetMapping(value = "/teacher/course/export")
    public void courseExport(HttpServletResponse response, CourseExportParams params) throws Exception{
        this.heiMaStudentMsgService.courseExport(response,params);
    }
}
