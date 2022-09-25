package com.adolesce.cloud.es.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2022/6/15 23:17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EsQueryResult {
    /**
     * 文档总数
     */
    private Long total;

    /**
     * 文档结果集
     */
    private List<?> resultList;
}
