package com.adolesce.common.swagger.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2021/10/12 11:56
 */
@Data
@ApiModel(value="用户对象",description="用于封装用户信息")
public class SwaggerUserDto2 extends SwaggerBase{
    @JsonProperty("username") //修改json格式数据里面的属性名称
    //name="username" 不会生效，多余
    @ApiModelProperty(value="用户名",name="username",dataType = "String",required = true, example="liuwedong0008")
    private String userName;
    @ApiModelProperty(value="密码",name="password",example="123456")
    private String password;
    @ApiModelProperty(value="姓名",name="name",example="刘威东")
    private String name;
    @ApiModelProperty(value="年龄",name="age",example="12")
    private Integer age;
    @ApiModelProperty(value="性别",name="sex",example="1")
    private Integer sex;
    private String phone;
}
