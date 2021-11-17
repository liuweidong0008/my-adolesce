package com.adolesce.server.autoGit;

import org.apache.commons.lang.StringUtils;

import java.io.*;

public class GitAutoOperation {

    public static void main(String[] args) {
        BufferedReader in = null;
        BufferedWriter out = null;
        try {
            File fileIn = new File("D:/heima/学生项目代码/57期git仓库地址.txt");
            File fileOut = new File("D:/heima/学生项目代码/autogit.bat");
            in = new BufferedReader(new FileReader(fileIn));
            out = new BufferedWriter(new FileWriter(fileOut));
            String line = null;
            String[] arr = null;
            String name = null;
            String gitAddress = null;

            writeContent(out, "@echo off\nd:\ncd \\heima\\学生项目代码\n");
            boolean isFirst = true;

            while (!StringUtils.equalsIgnoreCase(line = in.readLine(), "END")) {
                System.out.println(line);
                //组与组之间的空行
                if (StringUtils.isEmpty(line)) {
                    writeContent(out, "cd ..\ncd ..");
                    //组信息
                } else if (StringUtils.contains(line,"(")) {
                    isFirst = true;
                    writeContent(out, "mkdir " + line);
                    writeContent(out, "cd " + line);
                    //组员及git地址信息
                } else if(StringUtils.contains(line, "|")){
                    arr = line.split("\\|");
                    name = arr[0];
                    gitAddress = StringUtils.trim(arr[1]);
                    if (!isFirst) {
                        writeContent(out, "cd ..");
                    }
                    writeContent(out, "mkdir " + name);
                    writeContent(out, "cd " + name);
                    writeContent(out, "git clone " + gitAddress + "\n");
                    isFirst = false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void writeContent(BufferedWriter out, String s) throws IOException {
        out.write(s);
        out.newLine();
        out.flush();
    }
}
