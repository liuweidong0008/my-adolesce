package com.adolesce.server.autoGit;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.io.*;

public class GitAutoOperation {

    @Test
    public void createGitBat() {
        String gitAddressFilePath = "D:/heima/学生项目代码/69期瑞吉外卖项目日志git地址.txt";
        String gitCloneBatFilePath = "D:/heima/学生项目代码/1.bat";
        String gitPullBatFilePath = "D:/heima/学生项目代码/2.bat";

        beginCreateGitCloneBat(gitAddressFilePath, gitCloneBatFilePath);
        beginCreateGitPullBat(gitAddressFilePath, gitPullBatFilePath);
        deleteFile();
    }

    private void beginCreateGitCloneBat(String gitAddressFilePath, String gitCloneBatFilePath) {
        BufferedReader in = null;
        BufferedWriter out = null;
        try {
            File fileIn = new File(gitAddressFilePath);
            File fileOut = new File(gitCloneBatFilePath);
            in = new BufferedReader(new FileReader(fileIn));
            out = new BufferedWriter(new FileWriter(fileOut));
            String line;
            String[] arr;
            String name;
            String gitAddress;

            writeContent(out, "@echo off\nd:\ncd \\heima\\学生项目代码\n");
            boolean isFirst = true;

            while (!StringUtils.equalsIgnoreCase(line = in.readLine(), "END")) {
                System.out.println(line);
                //组与组之间的空行
                if (StringUtils.isEmpty(line)) {
                    writeContent(out, "cd ..\ncd ..");
                    //组信息
                } else if (StringUtils.contains(line, "(")) {
                    isFirst = true;
                    writeContent(out, "mkdir " + line);
                    writeContent(out, "cd " + line);
                    //组员及git地址信息
                } else if (StringUtils.contains(line, "|")) {
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
            cloneFile("D:/heima/学生项目代码/1.bat", "D:/heima/学生项目代码/gitClone.bat");
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

    private void beginCreateGitPullBat(String gitAddressFilePath, String gitPullBatFilePath) {
        BufferedReader in = null;
        BufferedWriter out = null;
        try {
            File fileIn = new File(gitAddressFilePath);
            File fileOut = new File(gitPullBatFilePath);
            in = new BufferedReader(new FileReader(fileIn));
            out = new BufferedWriter(new FileWriter(fileOut));
            String line;
            String[] arr;
            String name;
            String gitAddress;

            writeContent(out, "@echo off\nd:\ncd \\heima\\学生项目代码\n");
            boolean isFirst = true;

            while (!StringUtils.equalsIgnoreCase(line = in.readLine(), "END")) {
                System.out.println(line);
                //组与组之间的空行
                if (StringUtils.isEmpty(line)) {
                    writeContent(out, "cd ..\ncd ..\ncd ..");
                    //组信息
                } else if (StringUtils.contains(line, "(")) {
                    isFirst = true;
                    writeContent(out, "cd " + line);
                    //组员及git地址信息
                } else if (StringUtils.contains(line, "|")) {
                    arr = line.split("\\|");
                    name = arr[0];
                    gitAddress = StringUtils.trim(arr[1]);
                    if (!isFirst) {
                        writeContent(out, "cd ..\ncd ..");
                    }
                    writeContent(out, "cd " + name);
                    String projectName = gitAddress.substring(gitAddress.lastIndexOf("/") + 1);
                    if (projectName.contains(".git")) {
                        projectName = projectName.substring(0, projectName.lastIndexOf("."));
                    }
                    writeContent(out, "cd " + projectName);
                    writeContent(out, "git pull " + "\n");
                    isFirst = false;
                }
            }
            cloneFile("D:/heima/学生项目代码/2.bat", "D:/heima/学生项目代码/gitPull.bat");
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

    public void cloneFile(String origin, String dest) throws IOException {
        File fileIn = new File(origin);
        File fileOut = new File(dest);

        if (!fileOut.getParentFile().exists()) {
            fileOut.getParentFile().mkdirs();
        }

        BufferedReader in = new BufferedReader(new FileReader(fileIn));
        BufferedWriter out = new BufferedWriter(new FileWriter(fileOut));
        String line = null;
        while ((line = in.readLine()) != null) {
            out.write(line);
            out.newLine();
            out.flush();
        }
        in.close();
        out.close();
        fileIn.delete();
    }

    private void writeContent(BufferedWriter out, String s) throws IOException {
        out.write(s);
        out.newLine();
        out.flush();
    }

    public void deleteFile(){
        File fileIn = new File("D:/heima/学生项目代码/1.bat");
        fileIn.delete();
        fileIn = new File("D:/heima/学生项目代码/2.bat");
        fileIn.delete();
    }
}
