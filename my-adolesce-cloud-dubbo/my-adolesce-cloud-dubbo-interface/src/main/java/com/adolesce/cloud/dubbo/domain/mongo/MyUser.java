package com.adolesce.cloud.dubbo.domain.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * 用户表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "my_user"/* + "_{num}"*/)
public class MyUser implements java.io.Serializable {
    private static final long serialVersionUID = -291788258125767614L;
    @Id
    private ObjectId id;
    //用户ID
    private ObjectId userId;
    @Field("name")
    //用戶名
    private String userName;
    //年龄
    private Integer age;
    //地址
    private Address address;
    //性别
    private Integer sex;
    //是否老用户
    private Boolean isOld = false;
    //创建时间
    private Long created;
    //粉丝数
    private Integer fenCount;
    //创建时间
    private Date createDate;
}
