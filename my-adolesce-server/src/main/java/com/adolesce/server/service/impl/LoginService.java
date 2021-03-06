package com.adolesce.server.service.impl;

import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.generator.MathGenerator;
import cn.hutool.captcha.generator.RandomGenerator;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ObjectUtil;
import com.adolesce.autoconfig.template.SmsTemplate;
import com.adolesce.cloud.dubbo.api.db.MpUserApi;
import com.adolesce.cloud.dubbo.domain.db.MpUser;
import com.adolesce.common.enums.CaptchaType;
import com.adolesce.common.exception.BusinessException;
import com.adolesce.common.utils.JwtUtils;
import com.adolesce.common.vo.ErrorResult;
import com.adolesce.common.vo.Response;
import com.google.code.kaptcha.Producer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.FastByteArrayOutputStream;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class LoginService {
    @Autowired
    private SmsTemplate smsTemplate;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @DubboReference
    private MpUserApi mpUserApi;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Resource(name = "captchaProducer")
    private Producer captchaProducer;
    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;
    @Value("${adolesce.captcha.type}")
    private String captchaType;
    @Value("${adolesce.jwt.secret}")
    private String secret;

    /**
     * ???????????????????????????
     *
     * @param phone ?????????
     */
    public void sendCode(String phone) {
        String redisKey = "CHECK_CODE_" + phone;
        //?????????????????????????????????????????????????????????
        if (this.redisTemplate.hasKey(redisKey)) {
            throw new BusinessException(ErrorResult.codeNoFailure());
        }
        //?????????????????????
        //String code = this.smsTemplate.sendCode(phone);
        String code = "123456";
        if (StringUtils.isEmpty(code)) {
            throw new BusinessException(ErrorResult.sendCodeFail());
        }
        //??????????????????????????????????????????redis??????????????????5??????
        this.redisTemplate.opsForValue().set(redisKey, code, Duration.ofMinutes(5));

    }

    /**
     * ????????????
     *
     * @param phone ?????????
     * @param code  ???????????????
     * @return
     */
    public Map login(String phone, String code) {
        String redisKey = "CHECK_CODE_" + phone;
        boolean isNew = false;
        //???????????????
        String redisData = this.redisTemplate.opsForValue().get(redisKey);
        if (StringUtils.isEmpty(code) || !StringUtils.equals(code, redisData)) {
            throw new BusinessException(ErrorResult.checkCodeError());
        }
        //??????????????????????????????????????????
        this.redisTemplate.delete(redisKey);
        MpUser user = mpUserApi.queryUserByPhone(phone);

        /*QueryWrapper<MpUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        user = mpUserApi.getOne(queryWrapper);*/

        if (ObjectUtil.isEmpty(user)) {
            //?????????????????????
            user = new MpUser();
            user.setPhone(phone);
            user.setPassword(DigestUtils.md5Hex("123456"));
            this.mpUserApi.save(user);
            isNew = true;
        }

        //PayLoad??????????????????????????????json???????????????????????????ID??? ??????base64????????????????????????
        Map<String, Object> claims = new HashMap<String, Object>();
        claims.put("id", user.getId());
        claims.put("phone", phone);
        String token = JwtUtils.getToken(claims, secret);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("token", token);
        resultMap.put("isNew", isNew);
        return resultMap;
    }

    public Response sendMqMsg() {
        Map<String, String> map = new HashMap<>();
        String msg = "Hello RocketMQ!";
        map.put("mymsg", msg);
        this.rocketMQTemplate.convertAndSend("adolesce-topic", map);
        return Response.success();
    }

    public void outputImageCaptcha(HttpServletResponse response, String type) throws IOException {
        String uuid = UUID.randomUUID().toString(true);
        String verifyKey = "IMAGE_CAPTCHA_" + uuid;
        String capStr, code = null;
        BufferedImage image = null;
        AbstractCaptcha captcha = null;
        if(StringUtils.isBlank(type)){
            type = captchaType;
        }
        switch (CaptchaType.getByTypeName(type)){
            //???????????????code?????? ?????????????????????????????????
            case PENGGLE_MATH: {
                String capText = captchaProducerMath.createText();
                capStr = capText.substring(0, capText.lastIndexOf("@"));
                code = capText.substring(capText.lastIndexOf("@") + 1);
                image = captchaProducerMath.createImage(capStr);
                break;
            }
            case PENGGLE_CHAR:{
                capStr = code = captchaProducer.createText();
                image = captchaProducer.createImage(capStr);
                break;
            }
            case HUTOOL_LINE_CHAR:{
                captcha = CaptchaUtil.createLineCaptcha(299, 97);
                break;
            }
            case HUTOOL_CIRCLE_CHAR: {
                captcha = CaptchaUtil.createCircleCaptcha(200, 100, 4, 20);
                break;
            }
            case HUTOOL_SHEAR_CHAR:{
                captcha = CaptchaUtil.createShearCaptcha(200, 100, 4, 4);
                break;
            }
            case HUTOOL_RANDOM_NUMBER:{
                RandomGenerator randomGenerator = new RandomGenerator("0123456789", 4);
                captcha = CaptchaUtil.createLineCaptcha(200, 100);
                captcha.setGenerator(randomGenerator);
                captcha.createCode();
                break;
            }
            //???????????????code?????? ?????????????????????????????????????????????MathGenerator????????????????????????
            case HUTOOL_MATH:{
                captcha = CaptchaUtil.createShearCaptcha(200, 45, 4, 4);
                captcha.setGenerator(new MathGenerator());
                captcha.createCode();
                break;
            }
            default:
                break;
        }
        if(ObjectUtil.isNotEmpty(captcha)){
            code = captcha.getCode();
            captcha.write(response.getOutputStream());
            //captcha.getImageBytes();
        }else{
            // ?????????????????????
            FastByteArrayOutputStream os = new FastByteArrayOutputStream();
            ImageIO.write(image, "jpg", os);
            response.getOutputStream().write(os.toByteArray());
            //Base64.encode(os.toByteArray());
        }
        redisTemplate.opsForValue().set(verifyKey, code, 5, TimeUnit.MINUTES);
       /* Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("uuid",uuid);
        resultMap.put("imageBytes",);
        return Response.success(resultMap);*/
    }

    public static void main(String[] args) {
        MathGenerator mathGenerator = new MathGenerator();
        boolean verify = mathGenerator.verify("44-70=", "-26");
        System.out.println( verify == true ? "??????":"??????");
    }
}
