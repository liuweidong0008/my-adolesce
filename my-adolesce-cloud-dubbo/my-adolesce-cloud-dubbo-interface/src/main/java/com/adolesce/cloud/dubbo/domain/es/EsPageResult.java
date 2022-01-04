package com.adolesce.cloud.dubbo.domain.es;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2021/11/29 17:18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EsPageResult implements Serializable {
    private Long total;
    private List<HotelDoc> hotels;
}
