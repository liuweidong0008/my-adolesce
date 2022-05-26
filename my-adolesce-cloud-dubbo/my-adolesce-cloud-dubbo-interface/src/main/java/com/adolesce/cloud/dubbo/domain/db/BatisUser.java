package com.adolesce.cloud.dubbo.domain.db;

import cn.hutool.core.util.ObjectUtil;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
public class BatisUser extends BasePojo {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
    private Long id;

    /**
     * 用户名
     */
    @Column
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 姓名
     */
    private String name;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 生日
     */
    private LocalDate birthday;

    private List<BatisAddress> addresses;

    @Transient
    private String birthdayStr;

    public String getBirthdayStr() {
        if(ObjectUtil.isEmpty(birthday)){
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return this.birthday.format(formatter);
    }

    @Transient
    private String startTime;
    @Transient
    private String endTime;
    @Transient
    private String cardNo;
    @Transient
    private String ids;
}
