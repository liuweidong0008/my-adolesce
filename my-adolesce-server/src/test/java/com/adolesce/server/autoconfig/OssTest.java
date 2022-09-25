package com.adolesce.server.autoconfig;

import com.adolesce.autoconfig.template.OssTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@RunWith(SpringRunner.class)
@SpringBootTest
public class OssTest {
    @Autowired
    private OssTemplate ossTemplate;

    //测试创建桶
    @Test
    public void testCreateBucket() {
        boolean result = ossTemplate.createBucket();
        System.err.println("桶创建结果：" + (result ? "成功" : "失败"));
    }

    //测试删除桶
    @Test
    public void testDeleteBucket() {
        //1、先清除该桶下的所有文件
        /*List<String> res = ossTemplate.listFileName("2022/06/07/");
        ossTemplate.deleteFiles(res);*/

        //2、删除桶
        boolean result = ossTemplate.deleteBucket();
        System.err.println("桶删除结果：" + (result ? "成功" : "失败"));
    }

    //测试上传文件
    @Test
    public void testUpload() throws FileNotFoundException {
        String path = "D://some-test-file/pic/hourse2.jpg";
        FileInputStream inputStream = new FileInputStream(new File(path));
        String fileUrl = ossTemplate.uploadFile(path, inputStream);
        System.err.println("文件上传完成，OSS路径：" + fileUrl);
    }

    //测试下载文件
    @Test
    public void testDown() {
        String objectName = "2022/06/07/074d1565-c48b-465f-a6c5-8f53165cc1a9.jpg";
        String localFileName = "D://222";
        ossTemplate.downloadFile(objectName,localFileName);
    }

    //测试删除文件
    @Test
    public void testDelete() {
        String filePath = "https://lwd-bucket.oss-cn-beijing.aliyuncs.com/images/2021/07/11/16259748266146510.jpg";
        boolean result = ossTemplate.deleteFile(filePath);
        System.err.println("文件删除结果：" + (result ? "成功" : "失败"));
    }

    //测试删除文件
    @Test
    public void testDeletes() {
        List<String> filePaths = new ArrayList<>();
        filePaths.add("https://liuweidong-bucket.oss-cn-beijing.aliyuncs.com/2022/06/05/b2e67e3e-6bef-42f5-92c8-6bcc0a982174.jpg");
        filePaths.add("https://liuweidong-bucket.oss-cn-beijing.aliyuncs.com/2022/06/05/74f34d20-abcb-443a-947e-ec0a4164afa9.jpg");
        boolean result = ossTemplate.deleteFiles(filePaths);
        System.err.println("文件批量删除结果：" + (result ? "成功" : "失败"));
    }

    //测试创建文件夹
    @Test
    public void testCreateFolder() {
        String folder = "a/b/c/";
        boolean result = ossTemplate.createFolder(folder);
        System.err.println("文件夹创建结果：" + (result ? "成功" : "失败"));
    }

    /**
     * 测试列出路径下的所有文件名称
     */
    @Test
    public void listFileName() {
        List<String> res = ossTemplate.listFileName("2022/06/05/");
        res.forEach(System.err::println);

        /*2022/06/05/16909a9d-48eb-47da-9fbd-d07b256955c5.jpg
        2022/06/05/74f34d20-abcb-443a-947e-ec0a4164afa9.jpg
        2022/06/05/b2e67e3e-6bef-42f5-92c8-6bcc0a982174.jpg*/
    }

    /**
     * 测试列出路径下的所有文件URL
     */
    @Test
    public void listFileUrl() {
        List<String> res = ossTemplate.listFileUrl("2022/06/05/");
        res.stream().map(url -> {
            if(!url.startsWith("https")){
                return url.replace("http","https");
            }
            return url;
        }).collect(Collectors.toList()).forEach(System.err::println);

        /*https://liuweidong-bucket.oss-cn-beijing.aliyuncs.com/2022/06/05/16909a9d-48eb-47da-9fbd-d07b256955c5.jpg?Expires=4808173509&OSSAccessKeyId=LTAI5tA2k96KpofAhLVCeNj9&Signature=h9vaLbiI9vzb1j0HM0h%2FgKRXk2g%3D
        https://liuweidong-bucket.oss-cn-beijing.aliyuncs.com/2022/06/05/74f34d20-abcb-443a-947e-ec0a4164afa9.jpg?Expires=4808173509&OSSAccessKeyId=LTAI5tA2k96KpofAhLVCeNj9&Signature=gM%2BPCA5KONH2t4mRl2kP4MOGf%2BI%3D
        https://liuweidong-bucket.oss-cn-beijing.aliyuncs.com/2022/06/05/b2e67e3e-6bef-42f5-92c8-6bcc0a982174.jpg?Expires=4808173509&OSSAccessKeyId=LTAI5tA2k96KpofAhLVCeNj9&Signature=59grLJAC84eWjE0A76qnTS9G8kk%3D*/
    }

    /**
     * 测试文件是否存在
     */
    @Test
    public void isFileExist() {
        boolean result = ossTemplate.isFileExist("2022/06/05/b2e67e3e-6bef-42f5-92c8-6bcc0a982174.jpg");
        System.err.println("文件是否存在：" + (result ? "存在" : "不存在"));
    }
}
