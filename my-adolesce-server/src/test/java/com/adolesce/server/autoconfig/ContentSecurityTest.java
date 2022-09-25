package com.adolesce.server.autoconfig;

import com.adolesce.autoconfig.template.AliyunGreenTemplate;
import com.adolesce.autoconfig.template.AliyunVisionTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ContentSecurityTest {
    @Autowired
    private AliyunGreenTemplate aliyunGreenTemplate;

    @Autowired
    private AliyunVisionTemplate aliyunVisionTemplate;

    /**
     * 阿里云云盾审核测试
     * @throws Exception
     */
    @Test
    public void greenTemplateTest() throws Exception {
        //文本反垃圾检测
        //String textContent = "本校小额贷款，安全、快捷、方便、无抵押，随机随贷，当天放款，上门服务";
        String textContent = "我是一个好人";
        Map<String, String> textScanMap = aliyunGreenTemplate.greenTextScan(textContent);
        textScanMap.forEach((k,v)-> System.err.println(k+" : "+v));

        System.err.println("---------------------------------------我是分割线-----------------------------------------");

        //图片检测
        List<String> list = new ArrayList<>();
        list.add("http://images.china.cn/site1000/2018-03/17/dfd4002e-f965-4e7c-9e04-6b72c601d952.jpg");
        list.add("https://gd-hbimg.huaban.com/81ce67c79ab7f9a163674c489aff0bab4d8a9a239aef1-kOa6S9_fw1200");
        Map<String,String> imageScanMap = aliyunGreenTemplate.greenImageScan(list);
        imageScanMap.forEach((k,v)-> System.err.println(k+" : "+v));
    }

    /**
     * 阿里云视觉智能开放平台检测
     * @throws Exception
     */
    @Test
    public void visionTemplate() throws Exception {
        //文本反垃圾检测
        String textContent = "你个龟儿子";
        //String textContent = "要讲文明，不许骂人";
        Map<String, String> textScanMap = aliyunVisionTemplate.visionTextScan("你个龟儿子");
        textScanMap.forEach((k,v)-> System.err.println(k+" : "+v));

        System.err.println("---------------------------------------我是分割线-----------------------------------------");

        //图片检测
        List<String> list = new ArrayList<>();
        list.add("http://images.china.cn/site1000/2018-03/17/dfd4002e-f965-4e7c-9e04-6b72c601d952.jpg");
        list.add("https://gd-hbimg.huaban.com/81ce67c79ab7f9a163674c489aff0bab4d8a9a239aef1-kOa6S9_fw1200");
        Map<String,String> imageScanMap = aliyunVisionTemplate.visionImageScan(list);
        imageScanMap.forEach((k,v)-> System.err.println(k+" : "+v));
    }
}