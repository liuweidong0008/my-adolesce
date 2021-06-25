package com.adolesce.server.service;


import com.adolesce.common.bo.MpUser;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lwd
 * @since 2021-05-18
 */
public interface IMpUserService extends IService<MpUser> {

    List<MpUser> queryByNameCustom(String name);

    public IPage<MpUser> queryPageByNameCustom(Page page,String name);

}
