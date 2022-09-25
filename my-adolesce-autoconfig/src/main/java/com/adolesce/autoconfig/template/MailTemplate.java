package com.adolesce.autoconfig.template;

import cn.hutool.core.util.ObjectUtil;
import com.adolesce.autoconfig.bean.mail.MailSendParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class MailTemplate {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    //MailSenderAutoConfiguration  MailSenderPropertiesConfiguration  JavaMailSender
    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    /**
     * @param mailSendParams 邮件发送参数
     */
    public void sendSimpleMail(MailSendParams mailSendParams) {
        checkMail(mailSendParams);
        SimpleMailMessage message = new SimpleMailMessage();
        BeanUtils.copyProperties(mailSendParams,message);
        message.setFrom(this.from);
        try {
            this.mailSender.send(message);
            this.logger.info("简单邮件已经发送：{}->{}",from,mailSendParams.getTo());
        } catch (Exception e) {
            this.logger.error("简单邮件发送异常：{}->{}，错误信息：{}",from,mailSendParams.getTo(),e);
            //群发邮件，如果接收方存在失效邮箱，会导致群发失败，可以尝试剔除失效邮箱进行重发
            retrySendEmail(mailSendParams, e);
        }
    }

    /**
     * @param mailSendParams 邮件发送参数
     */
    public void sendMail(MailSendParams mailSendParams) {
        checkMail(mailSendParams);
        MimeMessage message = this.mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(this.from);
            helper.setTo(mailSendParams.getTo());
            helper.setSubject(mailSendParams.getSubject());
            helper.setText(mailSendParams.getText(), true);
            if(ObjectUtil.isNotEmpty(mailSendParams.getCc())){
                helper.setCc(mailSendParams.getCc());
            }
            if(ObjectUtil.isNotEmpty(mailSendParams.getBcc())){
                helper.setBcc(mailSendParams.getBcc());
            }
            if(ObjectUtil.isNotEmpty(mailSendParams.getReplyTo())){
                helper.setReplyTo(mailSendParams.getReplyTo());
            }

            FileSystemResource res = null;
            File file = null;

            //添加附件
            if (ObjectUtil.isNotEmpty(mailSendParams.getAttachmentPaths())) {
                for (String filePath : mailSendParams.getAttachmentPaths()) {
                    file = new File(filePath);
                    res = new FileSystemResource(new File(filePath));
                    helper.addAttachment(file.getName(), res);
                    //helper.addAttachment(file.getName(), file);
                }
            }
            //静态资源
            if (ObjectUtil.isNotEmpty(mailSendParams.getInlinePaths())) {
                for (Map.Entry<String, String> entry : mailSendParams.getInlinePaths().entrySet()) {
                    file = new File(entry.getValue());
                    res = new FileSystemResource(file);
                    helper.addInline(entry.getKey(), res);
                    //helper.addInline(entry.getKey(), file);
                }
            }
            this.mailSender.send(message);
            this.logger.info("邮件已经发送：{}->{}",from,mailSendParams.getTo());
        } catch (MessagingException e) {
            this.logger.error("邮件发生异常：{}->{}，错误信息：{}",from,mailSendParams.getTo(),e);
            //群发邮件，如果接收方存在失效邮箱，会导致群发失败，可以尝试剔除失效邮箱进行重发
            retrySendEmail(mailSendParams, e);
        }
    }

    /**
     * 尝试重发邮件（如果接收方存在失效邮箱，会导致群发失败，可以尝试剔除失效邮箱进行重发）
     * @param mailSendParams
     * @param e
     */
    private void retrySendEmail(MailSendParams mailSendParams, Exception e) {
        String[] invalid = getInvalidAddresses(e);
        if (invalid != null) {
            mailSendParams.setTo(filterByArray(mailSendParams.getTo(), invalid));
            mailSendParams.setCc(filterByArray(mailSendParams.getCc(), invalid));
            sendSimpleMail(mailSendParams);
        }
    }

    /**
     * 从异常获取无效地址
     * @param e
     * @return
     */
    private static String[] getInvalidAddresses(Throwable e) {
        if (e == null) {
            return null;
        }
        if (e instanceof MailSendException) {
            System.out.println("e instanceof SendFailedException");
            Exception[] exceptions = ((MailSendException) e).getMessageExceptions();
            for (Exception exception : exceptions) {
                if (exception instanceof SendFailedException) {
                    return getStringAddress(((SendFailedException) exception).getInvalidAddresses());
                }
            }
        }
        if (e instanceof SendFailedException) {
            return getStringAddress(((SendFailedException) e).getInvalidAddresses());
        }
        return null;
    }

    /**
     * 将Address[]转成String[]
     * @param address
     * @return
     */
    private static String[] getStringAddress(Address[] address) {
        List<String> invalid = new ArrayList<>();
        for (Address a : address) {
            String aa = ((InternetAddress) a).getAddress();
            if (!StringUtils.isEmpty(aa)) {
                invalid.add(aa);
            }
        }
        return invalid.stream().distinct().toArray(String[]::new);
    }

    /**
     * 过滤数组source，规则为数组元素包含了数组filter中的元素则去除
     *
     * @param source
     * @param filter
     * @return
     */
    private static String[] filterByArray(String[] source, String[] filter) {
        List<String> result = new ArrayList<>();
        for (String s : source) {
            boolean contains = false;
            for (String f : filter) {
                if (s.contains(f)) {
                    contains = true;
                    break;
                }
            }
            if (!contains) {
                result.add(s);
            }
        }
        return result.stream().toArray(String[]::new);
    }

    public void checkMail(MailSendParams sendParams) {
        Assert.notNull(sendParams,"邮件请求不能为空");
        Assert.notNull(sendParams.getTo(), "邮件收件人不能为空");
        Assert.notNull(sendParams.getSubject(), "邮件主题不能为空");
        Assert.notNull(sendParams.getText(), "邮件内容不能为空");
    }
}