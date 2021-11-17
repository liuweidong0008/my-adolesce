package com.adolesce.common.swagger.bean;

import lombok.Data;

import java.util.Date;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2021/10/12 11:56
 */
@Data
public class SwaggerUserDto {
    private String userName;
    private String password;
    private String name;
    private Integer age;
    private Integer sex;
    private String phone;
    private Date birthday;
}
