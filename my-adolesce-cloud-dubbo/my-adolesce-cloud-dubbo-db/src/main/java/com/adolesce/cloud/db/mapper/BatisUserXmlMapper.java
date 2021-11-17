package com.adolesce.cloud.db.mapper;


import com.adolesce.cloud.dubbo.domain.db.BatisAddress;
import com.adolesce.cloud.dubbo.domain.db.BatisUser;

import java.util.List;
import java.util.Map;

public interface BatisUserXmlMapper {
    void insert(BatisUser paramUsers);

    void batchInsert(List<BatisUser> usersList);

    void deleteById(Long paramLong);

    void deleteByIds(Map<String, Object> ids);

    void deleteByIdsStr(String ids);

    void update(BatisUser paramUsers);

    BatisUser getById(Long paramLong);

    List<BatisUser> queryByParam(BatisUser user);

    List<BatisUser> queryPageByName(String name);

    List<BatisUser> selectBatisUserByParams(Map<String,Object> params);

    List<BatisAddress> selectBatisAddressByParams(Map<String,Object> params);
}