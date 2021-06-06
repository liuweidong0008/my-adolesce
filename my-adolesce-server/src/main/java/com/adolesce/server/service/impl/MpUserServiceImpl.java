package com.adolesce.server.service.impl;


import com.adolesce.common.bo.MpUser;
import com.adolesce.common.mapper.MpUserMapper;
import com.adolesce.server.service.IMpUserService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author lwd
 * @since 2021-05-18
 */
@Service
public class MpUserServiceImpl extends ServiceImpl<MpUserMapper, MpUser> implements IMpUserService {
    @Autowired
    private MpUserMapper mpUserMapper;

    @Override
    public List<MpUser> queryByNameCustom(String name) {
        return mpUserMapper.queryByNameCustom(name);
    }

    @Override
    public IPage<MpUser> queryPageByNameCustom(Page page, String name) {
        return mpUserMapper.queryByNameCustom(page,name);
    }
}
