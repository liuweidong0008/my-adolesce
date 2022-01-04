package com.adolesce.server.service.impl;

import com.adolesce.autoconfig.template.SmsTemplate;
import com.adolesce.cloud.dubbo.api.db.MpUserApi;
import com.adolesce.cloud.dubbo.domain.db.MpUser;
import com.adolesce.common.vo.Response;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserService {
    @Autowired
    private SmsTemplate smsTemplate;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    //@DubboReference
    private MpUserApi mpUserApi;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Value("${jwt.secret}")
    private String secret;

    /**
     * 发送登录短信验证码
     *
     * @param phone 手机号
     */
    public Response sendCode(String phone) {
        String redisKey = "CHECK_CODE_" + phone;
        //先判断该手机号发送的验证码是否还未失效
        if (this.redisTemplate.hasKey(redisKey)) {
            return Response.failure("上一次发送的验证码还未失效！");
        }
        //发送短信验证码
        //String code = this.smsService.sendCode(phone);
        String code = "123456";
        if (StringUtils.isEmpty(code)) {
            return Response.failure("发送短信验证码失败！");
        }

        //短信发送成功，将验证码保存到redis中，有效期为5分钟
        this.redisTemplate.opsForValue().set(redisKey, code, Duration.ofMinutes(5));
        return Response.success();
    }

    /**
     * 用户登录
     *
     * @param phone 手机号
     * @param code  手机验证码
     * @return
     */
    public Response login(String phone, String code) {
        String redisKey = "CHECK_CODE_" + phone;
        boolean isNew = false;

        //校验验证码
        String redisData = this.redisTemplate.opsForValue().get(redisKey);
        if (!StringUtils.equals(code, redisData)) {
            return Response.failure("验证码错误！");
        }

        //验证码在校验完成后，需要废弃
        this.redisTemplate.delete(redisKey);

        QueryWrapper<MpUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);

        MpUser user = this.mpUserApi.getOne(queryWrapper);

        if (null == user) {
            //需要注册该用户
            user = new MpUser();
            user.setPhone(phone);
            user.setPassword(DigestUtils.md5Hex("123456"));
            //注册新用户
            this.mpUserApi.save(user);
            isNew = true;
        }

        //Header ： json串（指定加密算法HS256和token类型JWT）经过base64加密生成的字符串
        Map<String, Object> header = new HashMap<String, Object>();
        header.put(JwsHeader.TYPE, JwsHeader.JWT_TYPE);  //json type 为 JWT
        header.put(JwsHeader.ALGORITHM, "HS256"); //第三部分加密算法为 HS256

        //PayLoad：用户指定的关键数据json串（如用户名、用户ID） 经过base64加密生成的字符串
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("id", user.getId());

        // 生成token
        String token = Jwts.builder()
                .setHeader(header)  //header，可省略
                .setClaims(claims) //payload，存放数据的位置，不能放置敏感数据，如：密码等
                .signWith(SignatureAlgorithm.HS256, secret) //设置加密方法和加密盐
                //.signWith(SignatureAlgorithm.RS256, secret)
                .setExpiration(new DateTime().plusHours(12).toDate()) //设置过期时间，12小时后过期
                //.setExpiration(new cn.hutool.core.date.DateTime().offset(DateField.HOUR, 12)) //设置过期时间，12小时后过期
                .compact();

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("token", token);
        resultMap.put("isNew", isNew);
        return Response.success(resultMap);
    }

    public Response queryUserByToken(String token) {
        Response response;
        try {
            // 通过token解析数据
            Map<String, Object> body = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();

            MpUser user = new MpUser();
            user.setId(Long.valueOf(body.get("id").toString()));

            //需要返回user对象中的mobile，需要查询数据库获取到mobile数据
            //如果每次都查询数据库，必然会导致性能问题，需要对用户的手机号进行缓存操作
            //数据缓存时，需要设置过期时间，过期时间要与token的时间一致
            //如果用户修改了手机号，需要同步修改redis中的数据

            String redisKey = "USER_PHONE_" + user.getId();
            if (this.redisTemplate.hasKey(redisKey)) {
                String phone = this.redisTemplate.opsForValue().get(redisKey);
                user.setPhone(phone);
            } else {
                //查询数据库
                MpUser u = this.mpUserApi.getById(user.getId());
                user.setPhone(u.getPhone());

                //将手机号写入到redis中
                //在jwt中的过期时间的单位为：秒
                long timeout = Long.valueOf(body.get("exp").toString()) * 1000 - System.currentTimeMillis();
                this.redisTemplate.opsForValue().set(redisKey, u.getPhone(), timeout, TimeUnit.MILLISECONDS);
            }
            response = Response.success(user);
        } catch (ExpiredJwtException e) {
            response = Response.failure("token已经过期！");
            log.info("token已经过期！ token = " + token);
        } catch (Exception e) {
            response = Response.failure("token不合法！");
            log.error("token不合法！ token = " + token, e);
        }
        return response;
    }

    public Response sendMqMsg() {
        Map<String, String> map = new HashMap<>();
        String msg = "Hello RocketMQ!";
        map.put("mymsg", msg);
        this.rocketMQTemplate.convertAndSend("adolesce-topic", map);
        return Response.success();
    }
}
