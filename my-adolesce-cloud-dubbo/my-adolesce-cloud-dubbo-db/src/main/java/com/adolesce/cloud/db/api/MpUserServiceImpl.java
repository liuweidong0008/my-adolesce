package com.adolesce.cloud.db.api;


import com.adolesce.cloud.db.mapper.MpUserMapper;
import com.adolesce.cloud.dubbo.api.db.MpUserApi;
import com.adolesce.cloud.dubbo.domain.db.MpAddress;
import com.adolesce.cloud.dubbo.domain.db.MpUser;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lwd
 * @since 2021-05-18
 */
@DubboService
public class MpUserServiceImpl extends ServiceImpl<MpUserMapper, MpUser> implements MpUserApi {
    @Autowired
    private MpUserMapper mpUserMapper;

    @Override
    public List<MpUser> queryByNameCustom(String name) {
        return mpUserMapper.queryByNameCustom(name);
    }

    @Override
    public IPage<MpUser> queryPageByNameCustom(IPage page, String name) {
        return mpUserMapper.queryByNameCustom(page, name);
    }

    @Override
    public List<MpUser> selectMpUserByParams(Map<String, Object> params) {
        return mpUserMapper.selectMpUserByParams(params);
    }

    @Override
    public List<MpAddress> selectMpAddressByParams(Map<String, Object> params) {
        return mpUserMapper.selectMpAddressByParams(params);
    }
}
