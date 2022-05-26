package com.adolesce.autoconfig.utils.yimei;


import cn.hutool.crypto.digest.MD5;
import com.adolesce.autoconfig.utils.AES;
import com.adolesce.autoconfig.utils.GZIPUtils;
import com.adolesce.autoconfig.utils.yimei.bean.ResultModel;
import com.adolesce.autoconfig.utils.yimei.bean.YiMeiSendSmsParams;
import com.adolesce.autoconfig.utils.yimei.http.*;
import com.adolesce.autoconfig.utils.yimei.request.SmsBatchOnlyRequest;
import com.adolesce.autoconfig.utils.yimei.request.SmsSingleRequest;
import com.adolesce.autoconfig.utils.yimei.response.ResponseData;
import com.adolesce.autoconfig.utils.yimei.response.SmsResponse;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class YMSmsSenderUtil {
    // 加密算法
    private static String algorithm = "AES/ECB/PKCS5Padding";
    // 编码
    private static String encode = "UTF-8";

    public static void main(String[] args) {
        YiMeiSendSmsParams params = new YiMeiSendSmsParams();
        params.setUrl("http://shmtn.b2m.cn/inter/sendSingleSMS");

        //602423
        //params.setAppId("EUCP-EMY-SMS1-1IOG0");
        //params.setSecretKey("366FC3E3AC0F03CE");

        //649142
        params.setAppId("EUCP-EMY-SMS1-391UA");
        params.setSecretKey("759B0349B84719D4");
        params.setSignName("【饿了么】");
        params.setMobile("18573985801"); //18390952800  18569412474
        params.setContent("有商家希望和您的童趣手工制作小店进行合作，有意向请联系010-82903982。");

        // 发送单条短信
        textSingleSend(params);

        // 发送批量短信
       /* params.setUrl("http://shmtn.b2m.cn/inter/sendBatchOnlySMS");
        List<String> mobiles = CollectionUtil.newArrayList("16674226292", "18301327332");
        params.setMobiles(mobiles);
        textBatchSend(params);*/
    }

    /**
     * 发送单条短信
     *
     * @params 短信发送参数
     */
    public static boolean textSingleSend(YiMeiSendSmsParams params) {
        boolean isSuccess = false;
        log.info("yimei sms single send start");
        SmsSingleRequest requestParams = new SmsSingleRequest();
        requestParams.setContent(params.getSignName() + params.getContent() + "回复TD退订。");
        requestParams.setMobile(params.getMobile());
        requestParams.setRequestTime(System.currentTimeMillis());
        requestParams.setRequestValidPeriod(60);

        ResultModel result = requestText(params.getAppId(), params.getSecretKey(),
                algorithm, requestParams, params.getUrl(), params.isGzip(), encode);
        log.info("yimei sms single send result code:" + result.getCode());
        System.err.println("yimei sms single send result code:" + result.getCode());

        if ("SUCCESS".equals(result.getCode())) {
            isSuccess = true;
            SmsResponse response = JSON.parseObject(result.getResult(), SmsResponse.class);
            //JsonHelper.fromJson(SmsResponse.class, result.getResult());
            if (response != null) {
                System.err.println("data : " + response.getMobile() + "," + response.getSmsId() + "," + response.getCustomSmsId());
            }
        }
        log.info("yimei sms single send end");
        return isSuccess;
    }

    /**
     * 发送批次短信
     *
     * @param params 短信参数
     */
    public static boolean textBatchSend(YiMeiSendSmsParams params) {
        boolean isSuccess = false;
        log.info("yimei sms batch send start");
        SmsBatchOnlyRequest requestParams = new SmsBatchOnlyRequest();
        requestParams.setContent(params.getSignName() + params.getContent() + "回复TD退订。");
        requestParams.setMobiles(params.getMobiles().toArray(new String[params.getMobiles().size()]));
        requestParams.setRequestTime(System.currentTimeMillis());
        requestParams.setRequestValidPeriod(60);
        ResultModel result = requestText(params.getAppId(), params.getSecretKey(),
                algorithm, requestParams, params.getUrl(), params.isGzip(), encode);
        System.err.println("yimei sms batch send result code:" + result.getCode());
        log.info("yimei sms batch send result code:" + result.getCode());
        if ("SUCCESS".equals(result.getCode())) {
            isSuccess = true;
            SmsResponse[] response = JSON.parseObject(result.getResult(), SmsResponse[].class);
            //JsonHelper.fromJson(SmsResponse[].class, result.getResult());
            if (response != null) {
                for (SmsResponse d : response) {
                    System.err.println("data:" + d.getMobile() + "," + d.getSmsId() + "," + d.getCustomSmsId());
                }
            }
        }
        log.info("yimei sms batch send end");
        return isSuccess;
    }

    /**
     * 文本短信公共请求方法
     */
    public static ResultModel requestText(String appId, String secretKey, String algorithm,
                                          Object content, String url, final boolean isGzip, String encode) {
        Map<String, String> headers = new HashMap<String, String>();
        EmayHttpRequestBytes request = null;
        try {
            headers.put("appId", appId);
            headers.put("encode", encode);
            String requestJson = JSON.toJSONString(content);
            //String requestJson = JsonHelper.toJsonString(content);
            byte[] bytes = requestJson.getBytes(encode);
            if (isGzip) {
                headers.put("gzip", "on");
                bytes = GZIPUtils.compress(bytes);
            }
            byte[] parambytes = AES.encrypt(bytes, secretKey.getBytes(), algorithm);
            request = new EmayHttpRequestBytes(url, encode, "POST", headers, null, parambytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        EmayHttpClient client = new EmayHttpClient();
        String code = null;
        String result = null;
        try {
            EmayHttpResponseBytes res = client.service(request, new EmayHttpResponseBytesPraser());
            if (res == null) {
                return new ResultModel(code, result);
            }
            if (res.getResultCode().equals(EmayHttpResultCode.SUCCESS)) {
                if (res.getHttpCode() == 200) {
                    code = res.getHeaders().get("result");
                    if (code.equals("SUCCESS")) {
                        byte[] data = res.getResultBytes();
                        data = AES.decrypt(data, secretKey.getBytes(), algorithm);
                        if (isGzip) {
                            data = GZIPUtils.decompress(data);
                        }
                        result = new String(data, encode);
                    }
                } else {
                    //System.out.println("请求接口异常,请求码:" + res.getHttpCode());
                }
            } else {
                //System.out.println("请求接口网络异常:" + res.getResultCode().getCode());
            }
        } catch (Exception e) {
            //System.out.println("解析失败");
            e.printStackTrace();
        }
        ResultModel re = new ResultModel(code, result);
        return re;
    }


    /**
     * 发送语音短信
     *
     * @param appId     用户名
     * @param secretKey 密码
     * @param url       接口url
     * @param content   发送内容
     * @param mobile    发送手机号
     */
    public static void voiceSend(String appId, String secretKey, String url, String content, String mobile) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = format.format(new Date());// 时间戳
        //String sign = Md5.encrypt((appId + secretKey + timestamp));// 签名
        String sign = MD5.create().digestHex(appId + secretKey + timestamp);// 签名
        log.info("yimei sms voice send start");
        Map<String, String> params = new HashMap<String, String>();
        try {
            params.put("appId", appId);
            params.put("sign", sign);
            params.put("timestamp", timestamp);
            params.put("mobiles", mobile);
            params.put("content", URLEncoder.encode(content, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String json = requestVoice(params, url);
        if (json != null) {
            ResponseData<SmsResponse> data = JSON.parseObject(json, ResponseData.class);
            //JsonHelper.fromJson(new TypeToken<ResponseData<SmsResponse>>() {}, json);
            String code = data.getCode();
            System.err.println("yimei sms voice send result code:" + code);
            log.info("yimei sms voice send result code:" + code);
            if ("SUCCESS".equals(code)) {
                System.err.println("data:" + data.getData().getMobile() + "," + data.getData().getSmsId() + "," + data.getData().getCustomSmsId());
            }
        }
        log.info("yimei sms voice send end");
    }

    /**
     * 语音短信公共请求方法
     */
    public static String requestVoice(Map<String, String> params, String url) {
        EmayHttpRequestKV request = new EmayHttpRequestKV(url, "UTF-8", "POST", null, null, params);
        EmayHttpClient client = new EmayHttpClient();
        String json = null;
        try {
            String mapst = "";
            for (String key : params.keySet()) {
                String value = params.get(key);
                mapst += key + "=" + value + "&";
            }
            mapst = mapst.substring(0, mapst.length() - 1);
            //String res = HttpClientUtils.simplePostInvoke(url,params);
            EmayHttpResponseString res = client.service(request, new EmayHttpResponseStringPraser());
            if (res == null) {
                //System.err.println("请求接口异常");
                return null;
            }
            if (res.getResultCode().equals(EmayHttpResultCode.SUCCESS)) {
                if (res.getHttpCode() == 200) {
                    json = res.getResultString();
                } else {
                    System.err.println("请求接口异常,请求码:" + res.getHttpCode());
                }
            } else {
                //System.out.println("请求接口网络异常:" + res.getResultCode().getCode());
            }
        } catch (Exception e) {
            //System.err.println("解析失败");
            e.printStackTrace();
        }
        return json;
    }
}

