package com.adolesce.cloud.db.mapper;


import com.adolesce.cloud.dubbo.domain.db.MpAddress;
import com.adolesce.cloud.dubbo.domain.db.MpUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author lwd
 * @since 2021-05-18
 */
public interface MpUserMapper extends BaseMapper<MpUser> {

    List<MpUser> queryByNameCustom(Map<String,Object> params);

    IPage<MpUser> queryByNameCustom(IPage page, Map<String,Object> params);

    List<MpUser> selectMpUserByParams(Map<String, Object> params);

    List<MpAddress> selectMpAddressByParams(Map<String, Object> params);
}
