package com.adolesce.server.autoGit;

import java.util.Arrays;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2021/8/25 23:48
 */
public class MoveMdFile {
    public static void main(String[] args) {
        String[] images = {
                "image-20211027194244117.png",
                "image-20211027194322769.png",
                "image-20211027202727463.png",
                "image-20211027203028715.png",
                "image-20211027194720796.png",
                "image-20211027194900612.png",
                "image-20211027195500042.png",
                "image-20211027195926224.png",
                "image-20211027200403564.png",
                "image-20211027200603438.png"
        };
        String sourcePath = "C:\\Users\\Administrator\\AppData\\Roaming\\Typora\\typora-user-images\\";
        String destPath = "D:\\b探花\\探花交友5.0-new\\探花项目课程建议及疑问\\assets";

        Arrays.stream(images).map(s -> "move " + sourcePath + s + " "+ destPath)
                .forEach(System.out::println);

     /*   String sourcePath = "D:\\a前置\\SpringCloud\\SpringCloud\\day02\\讲义\\img\\";
        String destPath = "D:\\b探花\\5.0双元\\day9双元\\assets";

        Arrays.stream(images).map(s -> "copy " + sourcePath + s + " "+ destPath)
                .forEach(System.out::println);*/

    }
}

