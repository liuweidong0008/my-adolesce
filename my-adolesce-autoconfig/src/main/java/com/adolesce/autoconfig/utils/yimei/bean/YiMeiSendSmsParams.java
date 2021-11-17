package com.adolesce.autoconfig.utils.yimei.bean;

import lombok.Data;

import java.util.List;

@Data
public class YiMeiSendSmsParams {
    //短信URL
    private String url;
    //短信签名
    private String signName = "测试";
    private String appId;
    private String secretKey;
    //手机号
    private String mobile;
    //手机号集合
    private List<String> mobiles;
    //发送内容
    private String content;
    //是否压缩
    private boolean isGzip = false;

}
