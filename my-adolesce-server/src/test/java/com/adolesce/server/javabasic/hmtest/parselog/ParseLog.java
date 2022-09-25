package com.adolesce.server.javabasic.hmtest.parselog;

import cn.hutool.core.collection.CollectionUtil;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2022/5/21 23:31
 */
public class ParseLog {
    private final Pattern p = Pattern.compile("(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2},\\d{3}) \\[(.*)\\]\\[(.*)\\] ([^ ]*) - (.*)$");
    private final String UTF8_BOM = "\uFEFF";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Test
    public void parseLog1(){
        String logmsg = "2015-01-22 01:52:54,237 [http-bio-80-exec-5] FATAL TestLog4jServlet - Show FATAL message";
        String regex = "(\\d{4}-\\d{2}-\\d{2}) (\\d{2}:\\d{2}:\\d{2},\\d{3}) \\[(.*)\\] ([^ ]*) ([^ ]*) - (.*)$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(logmsg);
        System.out.println(m.matches());

        if (m.matches() && m.groupCount() == 6) {
            String date = m.group(1);
            String time = m.group(2);
            String threadId = m.group(3);
            String priority = m.group(4);
            String category = m.group(5);
            String message = m.group(6);
            System.out.println("date: " + date);
            System.out.println("time: " + time);
            System.out.println("threadId: " + threadId);
            System.out.println("priority: " + priority);
            System.out.println("category: " + category);
            System.out.println("message: " + message);
        }
    }

    @Test
    public void parseLog2(){
        String logmsg = "2015-05-28 16:13:45,873 [main][INFO ] sender.EtermSessionConnectPool - 准备初始化 EtermSessionConnectPool";
        Matcher m = p.matcher(logmsg);
        System.out.println(m.matches());
        if (m.matches() && m.groupCount() == 5) {
            String date = m.group(1);
            String threadId = m.group(2);
            String logLevel = m.group(3);
            String className = m.group(4);
            String message = m.group(5);
            System.out.println("date: " + date);
            System.out.println("threadId: " + threadId);
            System.out.println("logLevel: " + logLevel);
            System.out.println("className: " + className);
            System.out.println("message: " + message);
        }
    }

    @Test
    public void parseLog3() throws IOException {
        BufferedReader in = null;
        try {
            File logFile = new File("D:/some-test-file/log/test-q.log");
            in = new BufferedReader(new FileReader(logFile));
            String line;
            StringBuffer stringBuffer = new StringBuffer();
            boolean flag = false;
            List<AppLog> appLogs = new ArrayList<>();
            while ((line = in.readLine()) != null) {
                if (line.startsWith(UTF8_BOM)) {
                    line = line.substring(1);
                }
                if(p.matcher(line).matches()){
                    if(flag){
                        AppLog appLog = parseLogMsg(stringBuffer.toString());
                        appLogs.add(appLog);
                        stringBuffer = new StringBuffer();
                    }
                    stringBuffer.append(line);
                    flag = true;
                    continue;
                }else{
                    stringBuffer.append(line);
                }
            }
            if(CollectionUtil.isNotEmpty(appLogs)){
                appLogs.forEach(System.err::println);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        } finally {
            in.close();
        }
    }

    private AppLog parseLogMsg(String log) throws ParseException {
        Matcher m = p.matcher(log);
        AppLog appLog = new AppLog();
        if (m.matches() && m.groupCount() == 5) {
            String date = m.group(1);
            date = date.substring(0,date.indexOf(","));
            String threadId = m.group(2);
            String logLevel = m.group(3);
            String className = m.group(4);
            String message = m.group(5);

            appLog.setLogTime(dateFormat.parse(date));
            appLog.setThreadName(threadId);
            appLog.setLogLeval(logLevel);
            appLog.setClassName(className);
            appLog.setLogInfo(message);
        }
        return appLog;
    }
}
