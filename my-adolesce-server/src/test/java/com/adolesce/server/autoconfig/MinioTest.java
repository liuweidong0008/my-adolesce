package com.adolesce.server.autoconfig;

import cn.hutool.core.io.FileUtil;
import com.adolesce.autoconfig.template.MinioTemplate;
import io.minio.ObjectWriteResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2022/9/1 11:52
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MinioTest {
    @Autowired
    private MinioTemplate minioTemplate;

    /* @Before
    public void before() {
        if(ObjectUtil.isEmpty(minioTemplate)){
            MinioClient minioClient = MinioClient
                    .builder()
                    .credentials("minioadmin", "minioadmin")
                    .endpoint("http://localhost:9000")
                    .build();
            minioTemplate = new MinioTemplate(minioClient);
        }
    }*/

    //测试创建桶
    @Test
    public void testCreateBucket() {
        String bucketName = "mytest-bucket";
        boolean result = minioTemplate.createBucket(bucketName);
        System.out.println(result ? "创建桶成功！" : "创建桶失败");
    }

    //测试查询所有桶名称
    @Test
    public void testListBucketNames() {
        List<String> buckets = minioTemplate.listBucketNames();
        buckets.forEach(System.out::println);
    }

    //测试删除所有桶名称
    @Test
    public void testRemoveBucket() {
        String bucketName = "ly-bucket";
        boolean result = minioTemplate.removeBucket(bucketName);
        System.out.println(result ? "删除桶成功！" : "删除桶失败");
    }

    //测试在桶中创建文件夹
    @Test
    public void testMkDir() {
        String bucketName = "lwd-bucket";
        String dir = "school/class/";
        minioTemplate.createFile(bucketName, dir);
    }

    //测试查询桶中所有对象
    @Test
    public void testQueryObjects() {
        String bucketName = "lwd-bucket";
        List<String> objectNames = minioTemplate.listObjectNames(bucketName);
        objectNames.forEach(System.out::println);
    }

    //测试上传文件
    @Test
    public void testUploadFile() throws FileNotFoundException {
        //1、桶
        String bucketName = "mytest-bucket";
        //2、上传上去的路径文件名
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String todayStr = sdf.format(new Date());
        String objectName = todayStr + "/myhourse.jpg";
        //3、文件流
        String filePath = "D://some-test-file/pic/hourse2.jpg";
        FileInputStream in = new FileInputStream(filePath);
        //4、上传
        ObjectWriteResponse objectWriteResponse = minioTemplate.putObject(bucketName, objectName, in, "image/jpg");
        //5、打印文件路径
        String path = "http://localhost:9000/" + bucketName + "/" + objectName;
        System.out.println(path);
    }

    //测试下载文件
    @Test
    public void testDownFile() {
        //1、桶
        String bucketName = "mytest-bucket";
        //2、上传上去的路径文件名
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String todayStr = sdf.format(new Date());
        String objectName = todayStr + "/myhourse.jpg";
        //3、下载
        String filePath = "D://1.jpg";
        InputStream in = minioTemplate.getObject(bucketName,objectName);
        FileUtil.writeFromStream(in,new File(filePath));
    }
}
