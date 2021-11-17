package com.adolesce.cloud.dubbo.domain.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2021/10/3 21:46
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="my_jpa_address")
public class JpaAddress{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String province;

    private String city;

    private String area;

    /**
     * CascadeType.PERSIST 级联新增，保存父对象时会新建其中包含的子对象
     * CascadeType.MERGE 级联修改，保存父对象时会更新其中所包含的子对象数据
     * CascadeType.REMOVE 级联删除，当删除关联关系时会将子对象的数据删除
     * CascadeType.REFRESH 级联刷新，保存关联关系时会更新子对象和数据库中一致(意思是你在父对象中添加一个只包含ID的子对象，也可以保存进去)
     * CascadeType.ALL 包含上述所有操作
     *
     * 此处暂不设置级联操作(操作JpaAddress的时候不操作JpaUser)
     */
    @ManyToOne(cascade = CascadeType.MERGE)
    //用于维护一个外键，address表的user_id对应user表的id，默认对应user表的主键，所以，此处的referencedColumnName = "id"可省略
    @JoinColumn(name = "userid",referencedColumnName = "id")
    private JpaUser user;

    private Date createTime;
    private Date updateTime;

    public JpaAddress(String province, String city, String area) {
        this.province = province;
        this.city = city;
        this.area = area;
    }

    @Override
    public String toString() {
        return "JpaAddress{" +
                "id=" + id +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", area='" + area + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
