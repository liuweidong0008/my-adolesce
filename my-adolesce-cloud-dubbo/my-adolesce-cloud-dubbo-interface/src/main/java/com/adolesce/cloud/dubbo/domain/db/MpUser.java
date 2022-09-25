package com.adolesce.cloud.dubbo.domain.db;

import com.adolesce.cloud.dubbo.enums.SexEnum;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author lwd
 * @since 2021-05-18
 */
@Data
@TableName("my_mp_user")
public class MpUser extends BasePojo {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    //@TableField(select = false)
    private String password;

    /**
     * 姓名
     */
    //@Version
    private String name;

    /**
     * 年龄
     */
    //@TableField(updateStrategy = FieldStrategy.IGNORED)
    private Integer age;

    /**
     * 性别
     */
    private SexEnum sex;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 生日
     */
    private LocalDate birthday;

    @TableField(exist = false)
    private List<MpAddress> addresses;

    //不希望该值存入数据库
    @TableField(exist = false)
    private String totalAge;

    //不希望该值存入数据库
    @TableField(exist = false)
    private String count;

    //不希望该值存入数据库
    @TableField(exist = false)
    private String maxBirthday;

    //不希望该值存入数据库
    @TableField(exist = false)
    private String cardNo;

}
