package com.adolesce.consul.provider.service;

import com.adolesce.consul.provider.dao.GoodsDao;
import com.adolesce.consul.provider.domain.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Goods 业务层
 */
@Service
public class GoodsService {
    @Autowired
    private GoodsDao goodsDao;

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    public Goods findOne(int id) {
        return goodsDao.findOne(id);
    }
}
