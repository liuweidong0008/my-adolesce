package com.adolesce.server.autoGit;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2021/7/5 21:08
 */
public class FeedBack {

    @Test
    public void showNoCommit(){
        BufferedReader in = null;
        try {
            File fileInAll = new File("D:/heima/学生项目代码/all.txt");
            in = new BufferedReader(new FileReader(fileInAll));
            String line = null;
            String name = null;

            Set<String> allStudentName = new HashSet<>();
            Set<String> commitStudentName = new HashSet<>();
            Set<String> noCommitStudentName = new HashSet<>();
            while (!StringUtils.isEmpty(line = in.readLine())) {
                name = StringUtils.trimToEmpty(line);
                allStudentName.add(line);
            }

            File fileInCommit = new File("D:/heima/学生项目代码/commit.txt");
            in = new BufferedReader(new FileReader(fileInCommit));

            /*注：\n 回车(\u000a)
            \t 水平制表符(\u0009)
            \s 空格(\u0008)
            \r 换行(\u000d)*/
            while (!StringUtils.isEmpty(line = in.readLine())) {
                name = StringUtils.trimToEmpty(line);
                Pattern p = Pattern.compile("\\s*|\t|\r|\n");
                Matcher m = p.matcher(name);
                name = m.replaceAll("");
                commitStudentName.add(name);
            }
            //差集
            //方式一
            //allStudentName.removeAll(commitStudentName);
            //方式二
            noCommitStudentName = allStudentName.stream().filter(t-> !commitStudentName.contains(t)).collect(Collectors.toSet());
            noCommitStudentName.stream().forEach(System.out::println);
            //System.out.println(noCommitStudentName);

            //交集
            //方式一
            //allStudentName.retainAll(commitStudentName);
            //方式二
            //allStudentName = allStudentName.stream().filter(t -> commitStudentName.contains(t)).collect(Collectors.toSet());
            //System.out.println(allStudentName);


            //并集
            //方式一
            //allStudentName.removeAll(commitStudentName)
            //allStudentName.addAll(commitStudentName);
            //System.out.println(allStudentName);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void  toolsTest(){
    }
}
