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
@Entity(name="my_jpa_identity")
public class JpaIdentity {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cardNo;
    private Date startTime;
    private Date endTime;
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "userid",referencedColumnName = "id")
    private JpaUser user;

}
