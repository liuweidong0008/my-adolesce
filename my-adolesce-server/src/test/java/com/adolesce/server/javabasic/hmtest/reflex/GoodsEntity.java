package com.adolesce.server.javabasic.hmtest.reflex;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2021/12/8 13:17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsEntity {
    private String name;
    private Double price;
    private Integer code;
}
