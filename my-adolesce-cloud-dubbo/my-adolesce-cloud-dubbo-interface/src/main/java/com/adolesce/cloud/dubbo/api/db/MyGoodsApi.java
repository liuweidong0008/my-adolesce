package com.adolesce.cloud.dubbo.api.db;


import com.adolesce.cloud.dubbo.domain.db.MyGoods;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lwd
 * @since 2021-05-18
 */
public interface MyGoodsApi {
    List<MyGoods> selectList(QueryWrapper queryWrapper);
}
