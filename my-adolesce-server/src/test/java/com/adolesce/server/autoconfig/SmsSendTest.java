package com.adolesce.server.autoconfig;

import com.adolesce.autoconfig.template.SmsTemplate;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SmsSendTest {
    @Autowired
    private SmsTemplate smsTemplate;

    /**
     * 测试发送短信验证码
     */
    @Test
    public void testSendCode() {
        smsTemplate.sendCode("18301327332");
    }

    /**
     * 测试通过亿美发送短信
     */
    @Test
    public void testSendByYiMei() {
        smsTemplate.sendSmsByYimei("18301327332", "有商家希望和您的童趣手工制作小店进行合作，有意向请联系010-82903982。");
    }

    /**
     * 测试通过互亿无线发送短信
     */
    @Test
    public void testSendSmsByHuyi() {
        smsTemplate.sendSmsByHuyi("18301327332", "有商家希望和您的童趣手工制作小店进行合作，有意向请联系010-82903982。");
    }

    /**
     * 互亿无线短信发送
     */
    private static String Url = "http://106.ihuyi.com/webservice/sms.php?method=Submit";

    public static void main(String[] args) {
        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(Url);

        client.getParams().setContentCharset("GBK");
        method.setRequestHeader("ContentType", "application/x-www-form-urlencoded;charset=GBK");

        int mobile_code = (int) ((Math.random() * 9 + 1) * 100000);
        String content = new String("您的验证码是：" + mobile_code + "。请不要把验证码泄露给其他人。");
        NameValuePair[] data = {//提交短信
                new NameValuePair("account", "C10663788"), //查看用户名 登录用户中心->验证码通知短信>产品总览->API接口信息->APIID
                new NameValuePair("password", "6e5e0d90579f77ef10562ad331788c75"), //查看密码 登录用户中心->验证码通知短信>产品总览->API接口信息->APIKEY
                //new NameValuePair("password", util.StringUtil.MD5Encode("密码")),
                new NameValuePair("mobile", "18301327332"),
                new NameValuePair("content", content),
        };
        method.setRequestBody(data);

        try {
            client.executeMethod(method);
            String SubmitResult = method.getResponseBodyAsString();
            //System.out.println(SubmitResult);
            Document doc = DocumentHelper.parseText(SubmitResult);
            Element root = doc.getRootElement();

            String code = root.elementText("code");
            String msg = root.elementText("msg");
            String smsid = root.elementText("smsid");

            System.out.println(code);
            System.out.println(msg);
            System.out.println(smsid);

            if ("2".equals(code)) {
                System.out.println("短信提交成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
