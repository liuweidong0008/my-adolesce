package com.adolesce.cloud.db.mapper;


import com.adolesce.cloud.dubbo.domain.db.BatisAddress;
import com.adolesce.cloud.dubbo.domain.db.BatisUser;

import java.util.List;
import java.util.Map;

public interface BatisUserXmlMapper {
    Integer insert(BatisUser paramUsers);

    void batchInsert(List<BatisUser> usersList);

    void deleteById(Long paramLong);

    void deleteByIdsWithList(List<Long> ids);

    void deleteByIdsWithMap(Map<String, Object> ids);

    void deleteByIdsStr1(String ids, String password);

    void deleteByIdsStr2(Map<String, Object> params);

    void deleteByIdsStr3(BatisUser batisUser);

    void update(BatisUser paramUsers);

    BatisUser getById(Long paramLong);

    List<BatisUser> queryByParam(BatisUser user);

    List<BatisUser> queryPageByName(String name);

    List<BatisUser> selectBatisUserByParams(Map<String,Object> params);

    List<BatisAddress> selectBatisAddressByParams(Map<String,Object> params);

    List<Map<String,Object>> queryResltWithMap();
}