package com.adolesce.cloud.db.api;


import com.adolesce.cloud.db.mapper.MyGoodsMapper;
import com.adolesce.cloud.dubbo.api.db.MyGoodsApi;
import com.adolesce.cloud.dubbo.domain.db.MyGoods;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lwd
 * @since 2021-05-18
 */
@DubboService
public class MyGoodsServiceImpl implements MyGoodsApi {
    @Autowired
    private MyGoodsMapper myGoodsMapper;

    @Override
    public List<MyGoods> selectList(QueryWrapper queryWrapper) {
        return myGoodsMapper.selectList(queryWrapper);
    }
}
