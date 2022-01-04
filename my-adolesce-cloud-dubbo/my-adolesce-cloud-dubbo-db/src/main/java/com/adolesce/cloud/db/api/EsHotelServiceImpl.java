package com.adolesce.cloud.db.api;

import com.adolesce.cloud.db.mapper.EsHotelMapper;
import com.adolesce.cloud.dubbo.api.db.EsHotelApi;
import com.adolesce.cloud.dubbo.domain.db.EsHotel;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class EsHotelServiceImpl extends ServiceImpl<EsHotelMapper, EsHotel> implements EsHotelApi {
}
