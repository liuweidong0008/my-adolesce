package com.adolesce.autoconfig.template;

import com.adolesce.autoconfig.config.AliyunVisionProperties;
import com.alibaba.fastjson.JSON;
import com.aliyun.imageaudit20191230.Client;
import com.aliyun.imageaudit20191230.models.*;
import com.aliyun.teaopenapi.models.Config;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2021/9/14 20:52
 */
@Slf4j
public class AliyunVisionTemplate {
    private Client client;

    private AliyunVisionProperties aliyunVisionProperties;

    public AliyunVisionTemplate(AliyunVisionProperties aliyunVisionProperties) {
        this.aliyunVisionProperties = aliyunVisionProperties;
        try {
            Config config = new Config()
                    // 您的AccessKey ID
                    .setAccessKeyId(aliyunVisionProperties.getAccessKeyId())
                    // 您的AccessKey Secret
                    .setAccessKeySecret(aliyunVisionProperties.getAccessKeySecret());
            // 访问的域名
            config.endpoint = aliyunVisionProperties.getEndpoint();
            client = new Client(config);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Vision配置缺失，请补充!");
        }
    }

    /**
     * 阿里云文本内容检查
     *
     * @param content
     * @return map  key - suggestion内容
     * pass：文本正常，可以直接放行，
     * review：文本需要进一步人工审核，
     * block：文本违规，可以直接删除或者限制公开
     * value -   通过，或 出错原因
     * @throws Exception
     */
    public Map<String, String> visionTextScan(String content) throws Exception {
        //指定待检测的文本
        ScanTextRequest.ScanTextRequestTasks task = new ScanTextRequest.ScanTextRequestTasks().setContent(content);
        List<ScanTextRequest.ScanTextRequestTasks> taskList = new ArrayList<>();
        taskList.add(task);

        //指定文本检测的应用场景
        List<ScanTextRequest.ScanTextRequestLabels> labelsList = new ArrayList<>();
        Arrays.stream(this.aliyunVisionProperties.getTxtLabels().split(",")).forEach(
                label -> {
                    ScanTextRequest.ScanTextRequestLabels scanTextRequestLabels = new ScanTextRequest.ScanTextRequestLabels();
                    scanTextRequestLabels.setLabel(label);
                    labelsList.add(scanTextRequestLabels);
                }
        );

        //开始文本扫描
        ScanTextRequest scanTextRequest = new ScanTextRequest()
                .setTasks(taskList)
                .setLabels(labelsList);
        log.info("文本检测任务：{}", JSON.toJSONString(scanTextRequest, true));
        ScanTextResponse response = client.scanText(scanTextRequest);
        log.info("文本检测结果：{}", JSON.toJSONString(response, true));
        //服务端接收到请求，并完成处理返回的结果
        Map<String, String> resultMap = new HashMap<>();
        try {
            if (response != null) {
                //根据label和suggestion做相关处理
                String suggestion = "";
                String label = "";
                String reason = "";
                //文本要检测的场景的处理结果
                List<ScanTextResponseBody.ScanTextResponseBodyDataElements> elements = response.getBody().getData().getElements();
                for (ScanTextResponseBody.ScanTextResponseBodyDataElements element : elements) {
                    ScanTextResponseBody.ScanTextResponseBodyDataElementsResults results = element.getResults().get(0);
                    //根据label和suggestion做相关处理
                    label = results.getLabel();
                    suggestion = results.getSuggestion();
                    resultMap.put("suggestion", suggestion);
                    if (suggestion.equals("review")) {
                        resultMap.put("reason", "文章内容中有不确定词汇");
                        log.info("返回结果，resultMap={}", resultMap);
                        return resultMap;
                    } else if (suggestion.equals("block")) {
                        reason = "文章内容中有敏感词汇";
                        if ("spam".equals(label)) {
                            reason = "文字包含垃圾信息";
                        } else if ("ad".equals(label)) {
                            reason = "文字包含广告信息";
                        } else if ("politics".equals(label)) {
                            reason = "文字包含涉政内容";
                        } else if ("terrorism".equals(label)) {
                            reason = "文字包含暴恐内容";
                        } else if ("abuse".equals(label)) {
                            reason = "文字包含辱骂内容";
                        } else if ("porn".equals(label)) {
                            reason = "文字包含色情内容";
                        } else if ("flood".equals(label)) {
                            reason = "文字包含灌水内容";
                        } else if ("contraband".equals(label)) {
                            reason = "文字包含违禁内容";
                        } else if ("meaningless".equals(label)) {
                            reason = "文章内容无意义";
                        }
                        resultMap.put("reason", reason);
                        log.info("返回结果，resultMap={}", resultMap);
                        return resultMap;
                    }
                }
                resultMap.put("reason", "检测通过");
            }
        } catch (Exception e) {
            log.error("阿里云文本内容检查出错！");
            e.printStackTrace();
            new RuntimeException("阿里云文本内容检查出错！");
        }
        log.info("返回结果，resultMap={}", resultMap);
        return resultMap;
    }

    /**
     * 图片内容安全审核
     */
    public Map<String, String> visionImageScan(List<String> imageList) throws Exception {
        //设置待检测图片， 一张图片一个task
        List<ScanImageRequest.ScanImageRequestTask> taskList = new ArrayList();
        imageList.forEach(
                imgUrl -> {
                    ScanImageRequest.ScanImageRequestTask task = new ScanImageRequest.ScanImageRequestTask();
                    task.setImageURL(imgUrl);
                    taskList.add(task);
                }
        );
        //设置图片扫描
        ScanImageRequest scanImageRequest = new ScanImageRequest()
                .setScene(Arrays.asList(aliyunVisionProperties.getImgScenes().split(","))) //设置要检测的场景
                .setTask(taskList);
        log.info("图片检测任务：{}", JSON.toJSONString(scanImageRequest, true));
        ScanImageResponse response = client.scanImage(scanImageRequest);
        log.info("图片检测结果：{}", JSON.toJSONString(response, true));
        //服务端接收到请求，并完成处理返回的结果
        Map<String, String> resultMap = new HashMap<>();
        if (response != null) {
            //图片要检测的场景的处理结果, 如果是多个场景，则会有每个场景的结果
            List<ScanImageResponseBody.ScanImageResponseBodyDataResults> results = response.getBody().getData().getResults();
            for (ScanImageResponseBody.ScanImageResponseBodyDataResults result : results) {
                List<ScanImageResponseBody.ScanImageResponseBodyDataResultsSubResults> subResults = result.getSubResults();
                for(ScanImageResponseBody.ScanImageResponseBodyDataResultsSubResults subResult:subResults){
                    //根据scene和suggestion做相关处理
                    String suggestion = subResult.getSuggestion();
                    String label = subResult.getLabel();
                    resultMap.put("suggestion", suggestion);
                    if (!suggestion.equals("pass")) {
                        resultMap.put("label", label);
                        return resultMap;
                    }
                }
            }
        }
        return resultMap;
    }
}
