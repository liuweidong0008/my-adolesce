package com.adolesce.server.encrypt;

import org.junit.Test;
import org.springframework.util.DigestUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2022/8/5 18:20
 */
public class SignTest {

    /**
     * 生成接口sign验签参数
     */
    @Test
    public void interFaceSignTest() {
        //接口参数
        Map<String, Object> paramsMap = new HashMap();
        paramsMap.put("nonce", "aDwQZGXgI");
        paramsMap.put("openid", "eaeda62010f011ea99dbf522bdfd49b2");
        paramsMap.put("timestamp", 1588062436);
        paramsMap.put("ext", "{\"refreshToken\": \"5d54d216b90df6611778e8ff\", \"accessToken\":\"144115205364725641\"}");
        paramsMap.put("version", "1.0");
        paramsMap.put("appid", "60040");

        //对参数进行自然排序，得到验签串str
        String result = this.getSortedContentStyle1(paramsMap);

        //拼接私钥加密串
        String secrity = "sign_test_demo";
        result += secrity;

        //对验签串进行MD5加密
        String sign = DigestUtils.md5DigestAsHex(result.getBytes());
        System.out.println(sign);

        //将sign存入参数map中
        paramsMap.put("sign",sign);

        //最后拿着这个paramsMap发起第三方的http请求
    }

    private String getSortedContentStyle1(Map<String, Object> paramsMap) {
        List<String> keyList = paramsMap.keySet().stream().collect(Collectors.toList());
        Collections.sort(keyList);

        /*Object[] array = paramsMap.keySet().toArray();
        Arrays.sort(array);*/

        StringBuffer sb = new StringBuffer();
        for (String key : keyList) {
            //sb.append("&");
            sb.append(key);
            //sb.append("=");
            sb.append(paramsMap.get(key));
        }
        String content = sb.toString().replaceFirst("&", "");
        return content;
    }

    private String getSortedContentStyle2(Map<String, Object> paramsMap) {
        Map<String, Object> sortMap = new TreeMap<String, Object>();
        paramsMap.forEach((k, v) -> {
            sortMap.put(k, v);
        });

        StringBuffer sb = new StringBuffer();
        sortMap.forEach((key, value) -> {
            //sb.append("&");
            sb.append(key);
            //sb.append("=");
            sb.append(paramsMap.get(key));
        });
        String content = sb.toString().replaceFirst("&", "");
        return content;
    }

}
