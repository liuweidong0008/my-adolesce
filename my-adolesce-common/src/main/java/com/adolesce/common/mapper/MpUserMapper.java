package com.adolesce.common.mapper;


import com.adolesce.common.bo.MpUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lwd
 * @since 2021-05-18
 */
public interface MpUserMapper extends BaseMapper<MpUser> {

    List<MpUser> queryByNameCustom(@Param("name") String name);

    IPage<MpUser> queryByNameCustom(Page page, @Param("name")String name);
}
