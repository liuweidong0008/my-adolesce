package com.adolesce.server;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import io.jsonwebtoken.*;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Json Web Token：规范了Token生成规则,本质就是一个经过了加密处理和校验处理的字符串，格式为**A.B.C**（Header.PayLoad.Verify Signature）
 * <p>
 * Header ： json串（指定加密算法HS256和token类型JWT）经过**base64加密**生成的**字符串**
 * <p>
 * PayLoad：用户指定的关键数据**json串**（如用户名、用户ID） 经过**base64加密**生成的**字符串**
 * <p>
 * Verify Signature：Header 的base64加密串**  +  **PayLoad 的base64加密串** 整体进行基于**秘钥**的HS256**加密**
 * <p>
 * ​		好处：在验证方面安全性比较高、服务器无状态（不需要存储token数据）、提供了token有效期的机制，非常方便
 */
public class TestJWT {
    String secret = "itcast";

    @Test
    public void testCreateToken() {
        //Header ： json串（指定加密算法HS256和token类型JWT）经过base64加密生成的字符串
        Map<String, Object> header = new HashMap<String, Object>();
        header.put(JwsHeader.TYPE, JwsHeader.JWT_TYPE);  //json type 为 JWT
        header.put(JwsHeader.ALGORITHM, "HS256"); //第三部分加密算法为 HS256

        //PayLoad：用户指定的关键数据json串（如用户名、用户ID） 经过base64加密生成的字符串
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("mobile", "1333333333");
        claims.put("id", "2");

        // 生成token
        String jwt = Jwts.builder()
                //.setHeader(header)  //header，可省略
                .setClaims(claims) //payload，存放数据的位置，不能放置敏感数据，如：密码等
                .signWith(SignatureAlgorithm.HS256, secret) //设置加密方法和加密盐
                //.setExpiration(new Date(System.currentTimeMillis() + 3000)) //设置过期时间，3秒后过期
                .setExpiration(new DateTime().offset(DateField.HOUR, 12)) //设置过期时间，12小时后过期
                .compact();

        System.out.println(jwt);
    }

    @Test
    public void testDecodeToken() {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJtb2JpbGUiOiIxMzMzMzMzMzMzIiwiaWQiOiIyIiwiZXhwIjoxNjUyNTc2OTEwfQ.np3TqMAs-ikoACgpuH1V6Ikd2iUVGxFbtvElwigHo00";
        try {
            // 通过token解析数据
            Claims body = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();

//            System.out.println(body.getExpiration().getTime()-System.currentTimeMillis());
//            System.out.println( body.getExpiration().getTime()-System.currentTimeMillis()> 300*1000);

            System.out.println(body); //{mobile=1333333333, id=2, exp=1605513392}
            //long timeoutSecond = (body.getExpiration().getTime() - System.currentTimeMillis())/1000;
            long timeoutSecond = (Long.valueOf(body.get("exp").toString()) * 1000 - System.currentTimeMillis())/1000;
            double timoutMinute = BigDecimal.valueOf(timeoutSecond).divide(BigDecimal.valueOf(60),2).doubleValue();
            double timoutHour = BigDecimal.valueOf(timoutMinute).divide(BigDecimal.valueOf(60),2).doubleValue();
            System.out.println("有效期还剩："+timeoutSecond+"秒");
            System.out.println("有效期还剩："+timeoutSecond+"秒");
            System.out.println("有效期还剩："+timoutMinute+"分");
            System.out.println("有效期还剩："+timoutHour+"小时");
        } catch (ExpiredJwtException e) {
            System.out.println("token已经过期！");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("token不合法！");
        }
    }
}