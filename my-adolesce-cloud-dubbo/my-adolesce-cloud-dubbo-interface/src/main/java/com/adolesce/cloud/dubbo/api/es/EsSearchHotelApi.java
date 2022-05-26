package com.adolesce.cloud.dubbo.api.es;

import com.adolesce.cloud.dubbo.domain.es.EsPageResult;
import com.adolesce.cloud.dubbo.domain.es.EsRequestParams;

public interface EsSearchHotelApi{
    EsPageResult search(EsRequestParams params) throws Exception;
}
