package com.adolesce.server.autoconfig;

import com.adolesce.autoconfig.bean.mail.MailSendParams;
import com.adolesce.autoconfig.template.MailTemplate;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.FileWriter;
import java.io.StringWriter;
import java.util.*;

/**
 * 1、发送准备工作
 *      1）、找到发送方邮箱的POP3/SMTP设置并打开、拿到授权码，SMTP服务器是用来发送邮件的，POP3是用来接收邮件的
 * 2、问题：
 *      1）、存在失效邮箱地址导致JavaMailSender群发失败
 *          进行邮件群发，使用的是SpringBoot的org.springframework.mail.javamail.JavaMailSender
 *          如果待发送的邮件地址列表中存在一个无效的地址【该地址是一个合法的邮件地址，但是是无效地址，如：BingDwenDwen@163.com，它是一个合法的邮件地址，但却是无效的地址】，则会导致所有邮件发送失败。
 *      2）、日志没有报任何异常返回发送成功，收件方却没有实际收到邮件
 *          可以登录的你的发送账号，查看邮件系统退信原因。试着检查你公司的企业邮箱服务器的反垃圾邮件规则是否拦截了该邮件。
 *      3）、
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MailTest {
    @Autowired
    private MailTemplate mailTemplate;

    @Autowired
    private Configuration configuration;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void sendSimpleMail() {
        String[] to = {"liuweinan0008@163.com"};
        MailSendParams sendParams = MailSendParams.builder().subject("开会").text("下午1点大会议室召开部门会议").to(to).build();
        this.mailTemplate.sendSimpleMail(sendParams);
    }

    @Test
    public void sendHtmlMail() {
        String text = "<html>\n<body>\n    <h3>hello world ! 这是一封Html邮件!</h3>\n</body>\n</html>";
        String[] to = {"liuweinan0008@163.com"};
        MailSendParams sendParams = MailSendParams.builder().subject("测试").text(text).to(to).build();
        this.mailTemplate.sendMail(sendParams);
    }

    @Test
    public void sendAttachmentsMail() {
        List<String> attachmentPaths = new ArrayList<String>();
        attachmentPaths.add("D://some-test-file/pic/1.zip");
        attachmentPaths.add("D://some-test-file/pic/hourse1.jpg");
        String[] to = {"liuweinan0008@163.com"};

        MailSendParams sendParams = MailSendParams.builder().subject("test attachment mail").text("有附件，请查收！")
                .to(to).attachmentPaths(attachmentPaths).build();
        this.mailTemplate.sendMail(sendParams);
    }

    @Test
    public void sendInlineResourceMail() {
        String rscId1 = "001";
        String rscId2 = "002";

        StringBuffer content = new StringBuffer();
        content.append("<html><body>这是有图片的邮件：").append("<img src='cid:").append(rscId1).append("' >")
                .append("<img src='cid:").append(rscId2).append("' >").append("</body></html>");

        Map<String, String> inLineMap = new HashMap<String, String>();
        inLineMap.put(rscId1, "D://some-test-file/pic/hourse1.jpg");
        inLineMap.put(rscId2, "D://some-test-file/pic/hourse2.jpg");

        String[] to = {"liuweinan0008@163.com"};

        MailSendParams sendParams = MailSendParams.builder().subject("test inline mail").text(content.toString())
                .to(to).inlinePaths(inLineMap).build();

        this.mailTemplate.sendMail(sendParams);
    }

    @Test
    public void sendMailForFreeMarker1() throws Exception {
        String[] to = {"liuweinan0008@163.com"};
        String[] bcc = {"363426466@qq.com"};
        String templateName = "holiday.html";

        //构建 Freemarker 的基本配置
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_0);
        // 配置模板位置
        ClassLoader loader = this.getClass().getClassLoader();
        //模板的位置  在templates包下
        configuration.setClassLoaderForTemplateLoading(loader, "templates/html");
        //加载模板(入参具体模板名称)
        Template template = configuration.getTemplate(templateName);

        //模板参数
        Map<String,Object> map = new HashMap();//可以自定义自己的对象
        map.put("date","2022-02-01");
        map.put("festival","春节");

        StringWriter out = new StringWriter();
        //模板渲染，渲染的结果将被保存到 out 中 ，将out 中的 html 字符串发送即可
        template.process(map, out);

        //静态资源
        Map<String, String> inLineMap = new HashMap<String, String>();
        inLineMap.put("ewm", "D://some-test-file/pic/hourse1.jpg");

        //附件
        List<String> attachmentPaths = new ArrayList<String>();
        attachmentPaths.add("D://some-test-file/pic/1.zip");
        attachmentPaths.add("D://some-test-file/pic/hourse2.jpg");

        MailSendParams sendParams = MailSendParams.builder().subject("FreemarkerMail 邮件模板 邮件主题")
                .text(out.toString()).to(to).cc(bcc)
                .bcc(bcc).replyTo(bcc[0]).inlinePaths(inLineMap)
                .attachmentPaths(attachmentPaths)
                .sentDate(new Date())
                .build();

        this.mailTemplate.sendMail(sendParams);
    }

    @Test
    public void sendMailForFreeMarker2() throws Exception {
        String[] to = {"liuweinan0008@163.com"};
        String[] bcc = {"363426466@qq.com"};
        String templateName = "sp_wage_bill_mail.html";

        Template template = configuration.getTemplate(templateName);

        //模板参数
        Map<String,String>map = new HashMap<String,String>();
        map.put("memberName","刘威东");
        map.put("sendYear","2017年");
        map.put("sendMonth","2月份");
        map.put("shortUrl","http://www.baidu.com");
        map.put("omRealName","摆渡科技公司");
        map.put("companyName","阿狸科技公司");

        StringWriter out = new StringWriter();
        //模板渲染，渲染的结果将被保存到 out 中 ，将out 中的 html 字符串发送即可
        template.process(map, out);
        template.process(map,new FileWriter("d:/list.html"));

        //静态资源
        Map<String, String> inLineMap = new HashMap<String, String>();
        inLineMap.put("ewm", "D://some-test-file/pic/hourse1.jpg");

        //附件
        List<String> attachmentPaths = new ArrayList<String>();
        attachmentPaths.add("D://some-test-file/pic/1.zip");
        attachmentPaths.add("D://some-test-file/pic/hourse2.jpg");

        MailSendParams sendParams = MailSendParams.builder().subject("FreemarkerMail 邮件模板 邮件主题")
                .text(out.toString()).to(to).cc(bcc)
                .bcc(bcc).replyTo(bcc[0]).inlinePaths(inLineMap)
                .attachmentPaths(attachmentPaths)
                .sentDate(new Date())
                .build();

        this.mailTemplate.sendMail(sendParams);
    }

}