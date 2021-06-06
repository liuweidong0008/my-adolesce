package com.adolesce.common.mapper;


import com.adolesce.common.bo.BatisUser;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface BatisUserAnnoMapper {
    @Select({"SELECT * FROM my_batis_user order by birthday"})
    @Results({
            // @Result(property = "userSex", column = "user_sex", javaType = UserSexEnum.class),
            @Result(property = "userName", column = "user_name"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time")})
    List<BatisUser> getAll();

    @Select({"SELECT * FROM my_batis_user WHERE id = #{id}"})
    @Results({
            @Result(property = "userName", column = "user_name"),
            @Result(property = "createTime", column = "create_time"),
            @Result(property = "updateTime", column = "update_time")})
    BatisUser getById(Long paramLong);

    @Insert({"INSERT INTO my_batis_user(id, user_name, password, name, age, email, birthday, create_time, update_time)" +
            " VALUES(#{id}, #{userName}, #{password}, #{name}, #{age},#{email},#{birthday},#{createTime},#{updateTime})"})
    void insert(BatisUser paramUsers);

    @Update({"UPDATE my_batis_user SET user_name=#{userName},password=#{password},age=#{age},birthday=#{birthday} WHERE id =#{id}"})
    void update(BatisUser paramUsers);

    @Delete({"DELETE FROM my_batis_user WHERE id =#{id}"})
    void deleteById(Long paramLong);
}