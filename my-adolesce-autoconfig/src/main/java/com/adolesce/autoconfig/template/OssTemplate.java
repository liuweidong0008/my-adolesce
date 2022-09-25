package com.adolesce.autoconfig.template;

import com.adolesce.autoconfig.config.OssProperties;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class OssTemplate {
    private OssProperties properties;
    @Autowired(required = false)
    private OSS ossClient;

    public OssTemplate(OssProperties properties) {
        this.properties = properties;
    }

    /**
     * 创建桶
     *
     * @return 是否创建成功
     */
    public boolean createBucket() {
        OSS ossClient = this.createOssClient();
        boolean result = false;
        try {
            String bucketName = properties.getBucketName();
            // 判断容器是否存在,不存在就创建
            if (!ossClient.doesBucketExist(bucketName)) {
                //1、创建
                ossClient.createBucket(bucketName);
                //2、为桶赋权限（公共读）
                CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
                createBucketRequest.setCannedACL(CannedAccessControlList.PublicRead);
                Bucket bucket = ossClient.createBucket(createBucketRequest);
                bucket.getName();
                log.info("创建阿里云OSS存储空间成功，桶名称：{}" , bucketName);
            }
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("创建阿里云OSS存储空间异常：{}" , e);
        } finally {
            ossClient.shutdown();
        }
        return result;
    }

    /**
     * 删除桶
     *
     * @return 是否删除成功
     */
    public boolean deleteBucket() {
        OSS ossClient = this.createOssClient();
        boolean result = false;
        try {
            String bucketName = properties.getBucketName();
            // 判断容器是否存在,存在就删除
            if (ossClient.doesBucketExist(bucketName)) {
                ossClient.deleteBucket(bucketName);
                log.info("删除阿里云OSS存储空间成功，桶名称：{}" , bucketName);
            }
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("删除阿里云OSS存储空间异常：{}" , e);
        } finally {
            ossClient.shutdown();
        }
        return result;
    }

    /**
     * 文件上传
     *
     * @param filePath 文件名称
     * @param is       输入流
     * @return 文件网络URL
     */
    public String uploadFile(String filePath, InputStream is) {
        this.createBucket();
        String fileUrl = null;
        OSS ossClient = this.createOssClient();
        try {
            //1、拼写文件路径
            String fileName = UUID.randomUUID().toString() + filePath.substring(filePath.lastIndexOf("."));
            filePath = new SimpleDateFormat("yyyy/MM/dd").format(new Date())
                    + "/" + fileName;

            //2、上传文件至阿里云
            //PutObjectRequest putObjectRequest = new PutObjectRequest(properties.getBucketName(), filePath, is);
            PutObjectResult putObjectResult = ossClient.putObject(properties.getBucketName(), filePath, is, getExtracted(is, fileName));
            //3、手动方式拼接文件访问URL
            fileUrl = properties.getUrl() + "/" + filePath;
            //https://liuweidong-bucket.oss-cn-beijing.aliyuncs.com/2022/06/07/f4aea1c2-f23c-440c-abef-19f2f3e37b6f.jpg

            //API方式获取文件访问URL
            Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 100);
            URL url = ossClient.generatePresignedUrl(properties.getBucketName(), filePath, expiration);
            //fileUrl = url.toString();
            //http://liuweidong-bucket.oss-cn-beijing.aliyuncs.com/2022/06/07/f4aea1c2-f23c-440c-abef-19f2f3e37b6f.jpg?Expires=4808168252&OSSAccessKeyId=LTAI5tA2k96KpofAhLVCeNj9&Signature=YGne5lf3GKoHsnAS0eijbKKwLjg%3D
            log.info("文件成功上传至阿里云OSS，URL：{}" , fileUrl);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("文件上传至阿里云OSS异常：{}" , e);
        } finally {
            ossClient.shutdown();
        }
        return fileUrl;
    }

    /**
     * 文件删除
     *
     * @param fileUrl 文件网络URL
     * @return 是否删除成功
     */
    public boolean deleteFile(String fileUrl) {
        OSS ossClient = this.createOssClient();
        boolean result = false;
        try {
            //1、对文件url做处理，得到文件在OSS中的路径（去除域名）
            String filename = fileUrl.replace(properties.getUrl() + "/", "");
            ossClient.deleteObject(properties.getBucketName(), filename);
            result = true;
            log.info("从阿里云OSS删除文件成功，URL：{}" , fileUrl);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("从阿里云OSS删除文件异常：{}" , e);
        } finally {
            ossClient.shutdown();
        }
        return result;
    }

    /**
     * 批量删除文件或目录
     *
     * @param fileUrls
     */
    public boolean deleteFiles(List<String> fileUrls) {
        OSS ossClient = this.createOssClient();
        boolean result = false;
        try {
            fileUrls = fileUrls.stream().map(fileUrl -> fileUrl.replace(properties.getUrl() + "/", "")).collect(Collectors.toList());
            ossClient.deleteObjects(new DeleteObjectsRequest(properties.getBucketName()).withKeys(fileUrls));
            result = true;
            log.info("从阿里云OSS批量删除文件成功，URL：{}" , fileUrls);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("从阿里云OSS批量删除文件异常：{}" , e);
        } finally {
            ossClient.shutdown();
        }
        return result;
    }

    /**
     * 创建文件夹
     * <p>
     * param folder 文件夹名如"a/b/c/"
     * return 文件夹名
     */
    public boolean createFolder(String folder) {
        OSS ossClient = this.createOssClient();
        boolean result = false;
        String bucketName = properties.getBucketName();
        try {
            // 判断文件夹是否存在，不存在则创建
            if (!isFileExist(folder)) {
                // 创建文件夹
                ossClient.putObject(bucketName, folder, new ByteArrayInputStream(new byte[0]));
                // 得到文件夹名
                /*OSSObject object = ossClient.getObject(bucketName, folder);
                String fileDir = object.getKey();*/
                log.info("从阿里云OSS创建文件夹成功，文件夹名称：{}" , folder);
            }
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("从阿里云OSS创建文件夹异常：{}" , e);
        } finally {
            ossClient.shutdown();
        }
        return result;
    }

    /**
     * 通过文件名下载文件
     *
     * @param objectName    要下载的文件名
     * @param localFileName 本地要创建的文件名(不要后缀)
     */
    public void downloadFile(String objectName, String localFileName) {
        OSS ossClient = this.createOssClient();
        try {
            String suffix = objectName.substring(objectName.lastIndexOf("."));
            if (suffix != null && !"".equals(suffix)) {
                localFileName = localFileName.concat(suffix);
            }
            // 下载OSS文件到本地文件。如果指定的本地文件存在会覆盖，不存在则新建。
            ossClient.getObject(new GetObjectRequest(properties.getBucketName(), objectName), new File(localFileName));
            log.info("从阿里云OSS下载文件成功，文件名称：{}" , objectName);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("从阿里云OSS下载文件异常：{}" , e);
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 列举 指定路径下所有的文件的文件名
     * 如果要列出根路径下的所有文件，path= ""
     *
     * @param path
     * @return
     */
    public List<String> listFileName(String path) {
        OSS ossClient = this.createOssClient();
        List<String> res = new ArrayList<>();
        try {
            // 构造ListObjectsRequest请求。
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest(properties.getBucketName());
            // 设置prefix参数来获取fun目录下的所有文件。
            listObjectsRequest.setPrefix(path);
            // 列出文件。
            ObjectListing listing = ossClient.listObjects(listObjectsRequest);
            // 遍历添加文件名称
            listing.getObjectSummaries().forEach(obj -> res.add(obj.getKey()));
            log.info("从阿里云OSS获取{}文件夹下所有文件名称，文件名称：{}", path,res);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("从阿里云OSS获取{}文件夹下所有文件名称异常：{}", e);
        } finally {
            ossClient.shutdown();
        }
        return res;
    }

    /**
     * 列举文件下所有的文件url信息
     */
    public List<String> listFileUrl(String path) {
        OSS ossClient = this.createOssClient();
        List<String> res = new ArrayList<>();
        try {
            // 构造ListObjectsRequest请求
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest(properties.getBucketName());
            // 设置prefix参数来获取fun目录下的所有文件。
            listObjectsRequest.setPrefix(path);
            // 列出文件。
            ObjectListing listing = ossClient.listObjects(listObjectsRequest);
            // 遍历所有文件。
            for (OSSObjectSummary objectSummary : listing.getObjectSummaries()) {
                //文件访问路径(设置URL过期时间为100年)
                Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 100);
                URL url = ossClient.generatePresignedUrl(properties.getBucketName(), objectSummary.getKey(), expiration);
                res.add(url.toString());
            }
            log.info("从阿里云OSS获取{}文件夹下所有文件URL，URL：{}", path,res);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("从阿里云OSS获取{}文件夹下所有文件URL异常：{}", e);
        } finally {
            ossClient.shutdown();
        }
        return res;
    }

    /**
     * 判断文件是否存在
     *
     * @param objectName
     * @return
     */
    public boolean isFileExist(String objectName) {
        OSS ossClient = this.createOssClient();
        return ossClient.doesObjectExist(properties.getBucketName(), objectName);
    }

    private OSS createOssClient() {
        return new OSSClientBuilder().build(properties.getEndpoint(), properties.getAccessKey(), properties.getSecret());
    }

    private ObjectMetadata getExtracted(InputStream is, String fileName) throws IOException {
        // 创建上传Object的Metadata
        ObjectMetadata metadata = new ObjectMetadata();
        // 上传的文件的长度
        metadata.setContentLength(is.available());
        // 指定该Object被下载时的网页的缓存行为
        metadata.setCacheControl("no-cache");
        // 指定该Object下设置Header
        metadata.setHeader("Pragma", "no-cache");
        // 指定该Object被下载时的内容编码格式
        metadata.setContentEncoding("utf-8");
        // 文件的MIME，定义文件的类型及网页编码，决定浏览器将以什么形式、什么编码读取文件。如果用户没有指定则根据Key或文件名的扩展名生成，
        // 如果没有扩展名则填默认值application/octet-stream
        metadata.setContentType(getContentType(fileName));
        return metadata;
    }

    /**
     * 通过文件名判断并获取OSS服务文件上传时文件的contentType
     *
     * @param fileName 文件名
     * @return 文件的contentType
     */
    private String getContentType(String fileName) {
        // 文件的后缀名
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        if (".bmp".equalsIgnoreCase(fileExtension)) {
            return "image/bmp";
        }
        if (".gif".equalsIgnoreCase(fileExtension)) {
            return "image/gif";
        }
        if (".jpeg".equalsIgnoreCase(fileExtension) || ".jpg".equalsIgnoreCase(fileExtension)
                || ".png".equalsIgnoreCase(fileExtension)) {
            return "image/jpeg";
        }
        if (".png".equalsIgnoreCase(fileExtension)) {
            return "image/png";
        }
        if (".html".equalsIgnoreCase(fileExtension)) {
            return "text/html";
        }
        if (".txt".equalsIgnoreCase(fileExtension)) {
            return "text/plain";
        }
        if (".vsd".equalsIgnoreCase(fileExtension)) {
            return "application/vnd.visio";
        }
        if (".ppt".equalsIgnoreCase(fileExtension) || "pptx".equalsIgnoreCase(fileExtension)) {
            return "application/vnd.ms-powerpoint";
        }
        if (".doc".equalsIgnoreCase(fileExtension) || "docx".equalsIgnoreCase(fileExtension)) {
            return "application/msword";
        }
        if (".xml".equalsIgnoreCase(fileExtension)) {
            return "text/xml";
        }
        // 默认返回类型
        return "";
    }
}
