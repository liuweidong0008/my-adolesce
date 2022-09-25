package com.adolesce.cloud.dubbo.api.es;

import com.adolesce.cloud.dubbo.domain.es.EsPageResult;
import com.adolesce.cloud.dubbo.domain.es.EsRequestParams;

import java.util.List;
import java.util.Map;

public interface EsSearchHotelApi{
    /**
     * 条件查询
     * @param params 查询参数
     * @return
     */
    EsPageResult search(EsRequestParams params);

    /**
     * 聚合查询
     * @param params 查询条件
     * @return
     */
    Map<String, List<String>> getFilters(EsRequestParams params);

    /**
     * 自动补全查询
     * @param key 补全前缀
     * @return
     */
    List<String> getSuggestion(String key);

}
