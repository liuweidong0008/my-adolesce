package com.adolesce.cloud.dubbo.domain.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2021/10/3 21:46
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MpAddress implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String province;

    private String city;

    private String area;

    private MpUser user;
}
