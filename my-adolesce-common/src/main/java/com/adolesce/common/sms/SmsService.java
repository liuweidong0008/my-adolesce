package com.adolesce.common.sms;

import com.adolesce.common.config.HuyiSMSConfig;
import com.adolesce.common.config.YiMeiSMSConfig;
import com.adolesce.common.utils.yimei.YMSmsSenderUtil;
import com.adolesce.common.utils.yimei.bean.YiMeiSendSmsParams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Slf4j
@Service
public class SmsService {
    @Autowired
    private HuyiSMSConfig huyiSMSConfig;
    @Autowired
    private YiMeiSMSConfig yiMeiSMSConfig;

    public String sendCode(String mobile){
        String mobileCode = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        //String content = new String("您的验证码是：" + mobileCode + "。请不要把验证码泄露给其他人。");
        String content = "有商家希望和您的童趣手工制作小店进行合作，有意向请联系010-82903982";
        Boolean isSuccess = this.sendSmsByYimei(mobile,content);
        if(isSuccess){
            return mobileCode;
        }
        return null;
    }

    private Boolean sendSmsByHuyi(String mobile,String content){
        Boolean isSuccess = false;
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(this.huyiSMSConfig.getUrl());

        client.getParams().setContentCharset("GBK");
        method.setRequestHeader("ContentType", "application/x-www-form-urlencoded;charset=GBK");

        NameValuePair[] data = {//提交短信
                new NameValuePair("account", this.huyiSMSConfig.getAccount()), //查看用户名 登录用户中心->验证码通知短信>产品总览->API接口信息->APIID
                new NameValuePair("password", this.huyiSMSConfig.getPassword()), //查看密码 登录用户中心->验证码通知短信>产品总览->API接口信息->APIKEY
                //new NameValuePair("password", util.StringUtil.MD5Encode("密码")),
                new NameValuePair("mobile", mobile),
                new NameValuePair("content", content),
        };
        method.setRequestBody(data);
        try {
            client.executeMethod(method);
            String SubmitResult = method.getResponseBodyAsString();

            Document doc = DocumentHelper.parseText(SubmitResult);
            Element root = doc.getRootElement();

            String code = root.elementText("code");
            String msg = root.elementText("msg");
            String smsid = root.elementText("smsid");

            System.out.println(code);
            System.out.println(msg);
            System.out.println(smsid);
            isSuccess = "2".equals(code);
        } catch (Exception e) {
            log.error("【互亿无线】短信发送失败：",e);
            e.printStackTrace();
        }
        return isSuccess;
    }

    private Boolean sendSmsByYimei(String mobile,String content){
        YiMeiSendSmsParams params = new YiMeiSendSmsParams();
        params.setContent(content);
        params.setSignName(this.yiMeiSMSConfig.getSignName());
        params.setAppId(this.yiMeiSMSConfig.getAppId());
        params.setSecretKey(this.yiMeiSMSConfig.getSecretKey());
        //批量发送
        String[] mobiles = mobile.split(",");
        if(mobiles.length > 1){
            params.setUrl(this.yiMeiSMSConfig.getBatchUrl());
            params.setMobiles(Arrays.asList(mobiles));
            return YMSmsSenderUtil.textBatchSend(params);
        //单个发送
        }else{
            params.setUrl(this.yiMeiSMSConfig.getSingleUrl());
            params.setMobile(mobile);
            return YMSmsSenderUtil.textSingleSend(params);
        }
    }
}
