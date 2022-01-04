package com.adolesce.server.controller;

import com.adolesce.cloud.dubbo.api.es.EsSearchHotelApi;
import com.adolesce.cloud.dubbo.domain.es.EsPageResult;
import com.adolesce.cloud.dubbo.domain.es.EsRequestParams;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2021/11/29 16:53
 */
@RestController
@RequestMapping("es")
public class EsController {
    //@DubboReference
    private EsSearchHotelApi esHotelApi;

    @PostMapping("hotel/list")
    public EsPageResult search(@RequestBody EsRequestParams params) {
        EsPageResult pageResult = esHotelApi.search(params);
        return pageResult;
    }
}
