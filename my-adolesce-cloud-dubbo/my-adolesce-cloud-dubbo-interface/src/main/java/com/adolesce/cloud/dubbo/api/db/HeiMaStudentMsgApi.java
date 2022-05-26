package com.adolesce.cloud.dubbo.api.db;

import com.adolesce.cloud.dubbo.domain.db.HeiMaStudentMsg;
import com.adolesce.cloud.dubbo.dto.HeiMaStudentMsgDto;
import com.adolesce.common.vo.Response;
import com.baomidou.mybatisplus.extension.service.IService;

import java.text.ParseException;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2022/2/28 15:30
 */
public interface HeiMaStudentMsgApi extends IService<HeiMaStudentMsg> {

    Response queryByParams(HeiMaStudentMsgDto studentMsgDto) throws ParseException;

    void deleteById(Long id);

    void saveStudentMsg(HeiMaStudentMsgDto studentMsgDto) throws ParseException;

    Response queryById(HeiMaStudentMsgDto studentMsgDto);
}
