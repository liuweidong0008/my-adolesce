package com.adolesce.common.utils;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@Slf4j
public class JwtUtils {
    // TOKEN的有效期一周（S）
    private static final Integer TOKEN_TIME_OUT = 7 * 24 * 3600;

    /**
     * 生成Token
     */
    public static String getToken(Map claims, String secret) {
        //Header：json串（指定加密算法HS256和token类型JWT）经过base64加密生成的字符串
        Map<String, Object> header = new HashMap<String, Object>();
        header.put(JwsHeader.TYPE, JwsHeader.JWT_TYPE);  //json type 为 JWT
        header.put(JwsHeader.ALGORITHM, "HS256"); //第三部分加密算法为 HS256

        return Jwts.builder()
                .setHeader(header)  //header，可省略
                .setClaims(claims) //payload，存放数据的位置，不能放置敏感数据，如：密码等
                .signWith(SignatureAlgorithm.HS512, secret) //加密方式
                //.signWith(SignatureAlgorithm.RS256, secret)
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_TIME_OUT * 1000)) //过期时间戳
                //.setExpiration(new org.joda.time.DateTime().plusHours(12).toDate()) //设置过期时间，12小时后过期
                //.setExpiration(new cn.hutool.core.date.DateTime().offset(DateField.HOUR, 12)) //设置过期时间，12小时后过期
                .compact();
    }

    /**
     * 获取Token中的claims信息
     */
    public static Claims getClaims(String token, String secret) {
        return Jwts.parser()
                        .setSigningKey(secret)
                        .parseClaimsJws(token).getBody();
    }

    /**
     * 是否有效 true-有效，false-失效
     */
    public static boolean verifyToken(String token, String secret) {
        if (StringUtils.isEmpty(token)) {
            return false;
        }
        try {
            Claims body = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
            //在jwt中的过期时间的单位为：秒
            //long timeoutSecond = (body.getExpiration().getTime() - System.currentTimeMillis())/1000;
            long timeoutSecond = (Long.valueOf(body.get("exp").toString()) * 1000 - System.currentTimeMillis())/1000;
            double timoutMinute = BigDecimal.valueOf(timeoutSecond).divide(BigDecimal.valueOf(60),2).doubleValue();
            double timoutHour = BigDecimal.valueOf(timoutMinute).divide(BigDecimal.valueOf(60),2).doubleValue();

            log.info("token剩余有效时间：{}秒", timeoutSecond);
            log.info("token剩余有效时间：{}分", timoutMinute);
            log.info("token剩余有效时间：{}小时", timoutHour);
        }catch (ExpiredJwtException e) {
            log.error("token已经过期！ token = " + token, e);
            return false;
        } catch (Exception e) {
            log.error("token不合法！ token = " + token, e);
            return false;
        }
        return true;
    }
}