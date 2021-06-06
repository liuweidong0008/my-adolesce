package com.adolesce.common.mapper;

import com.adolesce.common.bo.BatisUser;

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
}