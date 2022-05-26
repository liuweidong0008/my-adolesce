package com.adolesce.autoconfig.template;

import com.adolesce.autoconfig.config.HuyiSMSProperties;
import com.adolesce.autoconfig.config.YiMeiSMSProperties;
import com.adolesce.autoconfig.utils.yimei.YMSmsSenderUtil;
import com.adolesce.autoconfig.utils.yimei.bean.YiMeiSendSmsParams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang3.RandomStringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.Arrays;

@Slf4j
public class SmsTemplate {
    private YiMeiSMSProperties yiMeiSMSProperties;
    private HuyiSMSProperties huyiSMSProperties;

    public SmsTemplate(YiMeiSMSProperties yiMeiSMSProperties, HuyiSMSProperties huyiSMSProperties) {
        this.yiMeiSMSProperties = yiMeiSMSProperties;
        this.huyiSMSProperties = huyiSMSProperties;
    }

    /**
     * 通过亿美短信平台发送短信
     * @param mobile 手机号(多个用逗号分割)
     * @param content 发送内容
     * @return
     */
    public Boolean sendSmsByYimei(String mobile, String content) {
        YiMeiSendSmsParams params = new YiMeiSendSmsParams();
        params.setContent(content);
        params.setSignName(this.yiMeiSMSProperties.getSignName());
        params.setAppId(this.yiMeiSMSProperties.getAppId());
        params.setSecretKey(this.yiMeiSMSProperties.getSecretKey());
        //批量发送
        String[] mobiles = mobile.split(",");
        if (mobiles.length > 1) {
            params.setUrl(this.yiMeiSMSProperties.getBatchUrl());
            params.setMobiles(Arrays.asList(mobiles));
            return YMSmsSenderUtil.textBatchSend(params);
            //单个发送
        } else {
            params.setUrl(this.yiMeiSMSProperties.getSingleUrl());
            params.setMobile(mobile);
            return YMSmsSenderUtil.textSingleSend(params);
        }
    }

    /**
     * 发送验证码短信
     * @param mobile
     * @return
     */
    public String sendCode(String mobile) {
        //String mobileCode = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        String mobileCode = RandomStringUtils.randomNumeric(6);
        String content = new String("您的验证码是：" + mobileCode + "。请不要把验证码泄露给其他人。");
        Boolean isSuccess = this.sendSmsByYimei(mobile, content);
        if (isSuccess) {
            return mobileCode;
        }
        return null;
    }

    /**
     * 通过互亿短信平台发送短信
     * @param mobile 手机号
     * @param content 发送内容
     * @return
     */
    public Boolean sendSmsByHuyi(String mobile, String content) {
        Boolean isSuccess = false;
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(this.huyiSMSProperties.getUrl());

        client.getParams().setContentCharset("GBK");
        method.setRequestHeader("ContentType", "application/x-www-form-urlencoded;charset=GBK");

        NameValuePair[] data = {//提交短信
                new NameValuePair("account", this.huyiSMSProperties.getAccount()), //查看用户名 登录用户中心->验证码通知短信>产品总览->API接口信息->APIID
                new NameValuePair("password", this.huyiSMSProperties.getPassword()), //查看密码 登录用户中心->验证码通知短信>产品总览->API接口信息->APIKEY
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
            log.error("【互亿无线】短信发送失败：", e);
            e.printStackTrace();
        }
        return isSuccess;
    }
}
