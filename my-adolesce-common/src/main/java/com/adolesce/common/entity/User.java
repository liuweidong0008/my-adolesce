package com.adolesce.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2021/8/28 21:20
 */

/**
 * 用户表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    private Long id;
    private String seriNo;
    private String userName; //用戶名
    private Integer age; //年龄
    private Address address; //地址
    private Integer sex; //性别
    private Boolean isOld = false; //是否老用户
    private Long created; //创建时间

    public static String getMsg(){
        return "msg";
    }
}
