package com.adolesce.cloud.dubbo.domain.db;

import java.util.Date;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2021/10/13 13:53
 */
public interface JpaUserInfo {
    public Long getId();

    public String getUserName();

    public String getName();
    public String getPassword();

    public String getCardNo();

    public Integer getAge();

    public Integer getSex();

    public String getPhone();

    public Date getBirthday();

    public Date getStartTime();

    public Date getEndTime();
}
