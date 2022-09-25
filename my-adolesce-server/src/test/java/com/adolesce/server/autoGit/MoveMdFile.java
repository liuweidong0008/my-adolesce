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
                "image-20220811010037696",
                "image-20220811010041681",
                "image-20220811010046506",
                "image-20220811005843495",
                "image-20220811005848580",
                "image-20220811005814934",
                "image-20220811005633014",
                "image-20220811005626710",
                "image-20220811005620097",
                "image-20220811005549903",
                "image-20220811005532239",
                "image-20220811005521604",
                "image-20220811005512791",
                "image-20220811005504703",
                "image-20220811005456261",
                "image-20220811005447166",
                "image-20220811005440519",
                "image-20220811005413892",
                "image-20220811005332242",
                "image-20220811005310007",
                "image-20220811005249460",
                "image-20220811005229184",
                "image-20220811005211577",
                "image-20220811005143256",
                "image-20220811005138663",
                "image-20220811005149489",
                "image-20220811005131483",
                "image-20220811005111366",
                "image-20220811005116293",
                "image-20220811005120612",
                "image-20220811005037747",
                "image-20220811005030552",
                "image-20220811005025758",
                "image-20220811005017932",
                "image-20220811004957139",
                "image-20220811004950691"
        };
        String sourcePath = "C:\\Users\\Administrator\\AppData\\Roaming\\Typora\\typora-user-images\\";
        String destPath = "C:\\Users\\Administrator\\Desktop\\git\\images";

        Arrays.stream(images).map(s -> "move " + sourcePath + s + ".png "+ destPath).forEach(System.out::println);

     /*   String sourcePath = "D:\\a前置\\SpringCloud\\SpringCloud\\day02\\讲义\\img\\";
        String destPath = "D:\\b探花\\5.0双元\\day9双元\\assets";

        Arrays.stream(images).map(s -> "copy " + sourcePath + s + " "+ destPath)
                .forEach(System.out::println);*/

    }
}

