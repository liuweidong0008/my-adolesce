package com.adolesce.cloud.dubbo.api.db;


import com.adolesce.cloud.dubbo.domain.db.MpAddress;
import com.adolesce.cloud.dubbo.domain.db.MpUser;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author lwd
 * @since 2021-05-18
 */
public interface MpUserApi extends IService<MpUser> {

    List<MpUser> queryByNameCustom(String name);

    IPage<MpUser> queryPageByNameCustom(IPage page, String name);

    List<MpUser> selectMpUserByParams(Map<String,Object> params);

    List<MpAddress> selectMpAddressByParams(Map<String,Object> params);
}
