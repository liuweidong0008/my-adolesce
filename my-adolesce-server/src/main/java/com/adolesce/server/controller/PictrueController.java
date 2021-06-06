package com.adolesce.server.controller;

import com.adolesce.common.vo.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Controller
@RequestMapping("pic")
public class PictrueController {
    /**
     * 上传文件
     *
     * @param multipartFile
     * @return
     */
    @ResponseBody
    @PostMapping("upload")
    public Response upload(@RequestParam(value = "file",required = false)MultipartFile multipartFile) {
        System.out.println(multipartFile.getOriginalFilename());
        return Response.success();
    }
}
