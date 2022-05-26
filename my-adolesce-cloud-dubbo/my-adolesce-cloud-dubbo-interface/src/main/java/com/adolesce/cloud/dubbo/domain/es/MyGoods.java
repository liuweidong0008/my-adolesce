package com.adolesce.cloud.dubbo.domain.es;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2022/1/16 20:33
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyGoods {
    private String id;
    private String title;
    private String desc;
    private String categoryName;
}
