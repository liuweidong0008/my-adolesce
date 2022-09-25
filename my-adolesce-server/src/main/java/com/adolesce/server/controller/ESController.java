package com.adolesce.server.controller;

import com.adolesce.cloud.dubbo.api.es.EsSearchHotelApi;
import com.adolesce.cloud.dubbo.domain.es.EsPageResult;
import com.adolesce.cloud.dubbo.domain.es.EsRequestParams;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2021/11/29 16:53
 */
@RestController
@RequestMapping("es")
public class ESController {
    @DubboReference
    private EsSearchHotelApi esHotelApi;

    @PostMapping("hotel/list")
    public EsPageResult search(@RequestBody EsRequestParams params){
        EsPageResult pageResult = esHotelApi.search(params);
        return pageResult;
    }

    @PostMapping("hotel/filters")
    public Map<String, List<String>> getFilters(@RequestBody EsRequestParams params) {
        return esHotelApi.getFilters(params);
    }

    @GetMapping("hotel/suggestion")
    public List<String> getSuggestion(@RequestParam("key") String key) {
        return esHotelApi.getSuggestion(key);
    }
}
