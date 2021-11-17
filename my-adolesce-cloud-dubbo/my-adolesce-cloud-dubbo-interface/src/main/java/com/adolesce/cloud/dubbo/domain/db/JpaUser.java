package com.adolesce.cloud.dubbo.domain.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name="my_jpa_user")
public class JpaUser{
    private static final long serialVersionUID = 1L;

    /**
     * PS:@GeneratedValue注解的strategy属性提供四种值:
     *	-AUTO主键由程序控制, 是默认选项 ,不设置就是这个
     *	-IDENTITY 主键由数据库生成, 采用数据库自增长, Oracle不支持这种方式
     *	-SEQUENCE 通过数据库的序列产生主键, MYSQL  不支持
     *	-Table 提供特定的数据库产生主键, 该方式更有利于数据库的移植
     * SpringBoot的@GeneratedValue 是不需要加参数的,但是如果数据库控制主键自增(auto_increment), 不加参数就会报错.(血的教训, 看了@GeneratedValue源代码才知道)*/

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     *  用户名
     *
     *  在不进行字段设置时没有@Column注解也行，支持实体类驼峰与数据库下划线自动映射
     *      updatable = false  不会更新此字段，也不会报错,可以插入。 默认为true
     *      insertable = false 不会插入此字段，也不会报错，可更新。默认为true，字段不存在会新增
     */
    //@Column(name = "user_name")
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
     * 性别
     */
    private Integer sex;

    /**
     * 手机号
     */
    private String phone;

    /**
     * CascadeType.PERSIST 级联新增，保存父对象时会新建其中包含的子对象
     * CascadeType.MERGE 级联修改，保存父对象时会更新其中所包含的子对象数据
     * CascadeType.REMOVE 级联删除，当删除关联关系时会将子对象的数据删除
     * CascadeType.REFRESH 级联刷新，即在保存前先更新别人的修改：如Order、Item被用户A、B同时读出做修改且B的先保存了，在A保存时会先更新Order、Item的信息再保存。
     * CascadeType.DETACH：级联脱离，如果你要删除一个实体，但是它有外键无法删除，你就需要这个级联权限了。它会撤销所有相关的外键关联。
     * CascadeType.ALL 包含上述所有操作
     *
     * FetchType.LAZY 懒加载 （默认） 在包含级联查询的场景下，如果使用懒加载，查询级联数据时，session已经关闭，所以，要设置为急加载
     * FetchType.EAGER 急加载
     */
    @OneToOne(mappedBy = "user",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private JpaIdentity identity;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<JpaAddress> addresses;

    /**
     * 生日
     */
    @Temporal(TemporalType.DATE)
    private Date birthday;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    private Date updateTime;

    @Transient //不参与映射
    private String startTime;
    @Transient
    private String endTime;

    //为防止嵌套打印死循环导致内存溢出不应打印级联对象
    @Override
    public String toString() {
        return "JpaUser{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", sex=" + sex +
                ", phone='" + phone + '\'' +
                ", birthday=" + birthday +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
