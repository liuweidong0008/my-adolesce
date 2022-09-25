package com.adolesce.autoconfig.bean.mail;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class MailSendParams {
    private String subject;//邮件主题
    private String text;//邮件内容
    private List<String> attachmentPaths;//附件地址
    private List<String> accessoryPath;//附件地址
    private Map<String, String> inlinePaths; //静态资源地址
    private String[] to;  //接收方邮箱
    private String[] cc;  //抄送方邮箱
    private String[] bcc;  //隐秘抄送方邮箱
    private String replyTo; //快速回复邮箱
    private Date sentDate; //发送日期
}
