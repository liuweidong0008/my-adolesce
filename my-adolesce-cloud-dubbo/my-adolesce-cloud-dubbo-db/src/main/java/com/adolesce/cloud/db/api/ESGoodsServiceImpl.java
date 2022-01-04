package com.adolesce.cloud.db.api;


import com.adolesce.cloud.db.mapper.ESGoodsMapper;
import com.adolesce.cloud.dubbo.api.db.ESGoodsApi;
import com.adolesce.cloud.dubbo.domain.db.ESGoods;
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
public class ESGoodsServiceImpl implements ESGoodsApi {
    @Autowired
    private ESGoodsMapper myGoodsMapper;

    @Override
    public List<ESGoods> selectList() {
        QueryWrapper queryWrapper = new QueryWrapper();
        return myGoodsMapper.selectList(queryWrapper);
    }
}
