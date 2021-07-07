package com.adolesce.common.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

/**
 * 用户表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "my_user")
public class MyUser implements java.io.Serializable {
    private static final long serialVersionUID = -291788258125767614L;

    //@Id
    private ObjectId id;
    private ObjectId userId; //用户ID
    private String userName; //用戶名
    private Integer age; //年龄
    private Address address; //地址
    private Integer sex; //性别
    private Boolean isOld = false; //是否老用户
    private Long created; //创建时间

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof MyUser)) {
            return false;
        }
        MyUser user = (MyUser) o;
        return age.equals(user.age) &&
                Objects.equals(userName, user.userName) &&
                Objects.equals(sex, user.sex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, age, sex);
    }
}
