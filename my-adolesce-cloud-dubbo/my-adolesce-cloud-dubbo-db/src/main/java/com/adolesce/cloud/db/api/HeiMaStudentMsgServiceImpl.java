package com.adolesce.cloud.db.api;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.adolesce.cloud.db.mapper.HeiMaStudentMsgMapper;
import com.adolesce.cloud.dubbo.api.db.HeiMaStudentMsgApi;
import com.adolesce.cloud.dubbo.domain.db.HeiMaStudentMsg;
import com.adolesce.cloud.dubbo.dto.HeiMaStudentMsgDto;
import com.adolesce.common.vo.Response;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2022/2/28 15:40
 */
@Slf4j
@DubboService
public class HeiMaStudentMsgServiceImpl extends ServiceImpl<HeiMaStudentMsgMapper, HeiMaStudentMsg> implements HeiMaStudentMsgApi {
    private final static SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 分页条件查询
     *
     * @return
     */
    @Override
    public Response queryByParams(HeiMaStudentMsgDto studentMsgDto) {
        log.info("查询学生信息，参数：{}", studentMsgDto.toString());
        Map<String, Object> resultMap = new HashMap<>();

        Page page = new Page(studentMsgDto.getCurrentPage(), studentMsgDto.getPageSize());
        LambdaQueryWrapper<HeiMaStudentMsg> queryWrapper = this.buildQueryWrapper(studentMsgDto);
        page(page, queryWrapper);
        List<HeiMaStudentMsg> records = page.getRecords();
        if (CollectionUtil.isNotEmpty(records)) {
            records = records.stream().map(studentMsg -> {
                if(ObjectUtil.isNotEmpty(studentMsg.getGraduateTime())){
                    studentMsg.setGraduateTimeStr(DATEFORMAT.format(studentMsg.getGraduateTime()));
                }
                if(ObjectUtil.isNotEmpty(studentMsg.getRuzhiTime())){
                    studentMsg.setRuzhiTimeStr(DATEFORMAT.format(studentMsg.getRuzhiTime()));
                }
                return studentMsg;
            }).collect(Collectors.toList());
        }

        resultMap.put("currentPage", studentMsgDto.getCurrentPage());
        resultMap.put("data", records);
        resultMap.put("allCount", page.getTotal());
        resultMap.put("pageCount", page.getPages());

        return Response.success(resultMap);
    }

    /**
     * 根据ID删除
     * @param id
     * @return
     */
    @Override
    public void deleteById(Long id) {
        log.info("删除学生信息，参数：{}", id);
        removeById(id);
    }

    @Override
    public void saveStudentMsg(HeiMaStudentMsgDto studentMsgDto) throws ParseException {
        if(ObjectUtil.isNotEmpty(studentMsgDto)){
            //新增
            if(ObjectUtil.isEmpty(studentMsgDto.getId())){
                HeiMaStudentMsg heiMaStudentMsg = new HeiMaStudentMsg();
                BeanUtils.copyProperties(studentMsgDto,heiMaStudentMsg);
                if(!StringUtils.isEmpty(studentMsgDto.getRuzhiTimeStr())){
                    heiMaStudentMsg.setRuzhiTime(DATEFORMAT.parse(studentMsgDto.getRuzhiTimeStr()));
                }
                if(!StringUtils.isEmpty(studentMsgDto.getGraduateTimeStr())){
                    heiMaStudentMsg.setGraduateTime(DATEFORMAT.parse(studentMsgDto.getGraduateTimeStr()));
                }
                heiMaStudentMsg.setCreateTime(new Date());
                heiMaStudentMsg.setUpdateTime(new Date());
                save(heiMaStudentMsg);
            //修改
            }else{
                HeiMaStudentMsg heiMaStudentMsg = new HeiMaStudentMsg();
                BeanUtils.copyProperties(studentMsgDto,heiMaStudentMsg);
                if(!StringUtils.isEmpty(studentMsgDto.getRuzhiTimeStr())){
                    heiMaStudentMsg.setRuzhiTime(DATEFORMAT.parse(studentMsgDto.getRuzhiTimeStr()));
                }
                if(!StringUtils.isEmpty(studentMsgDto.getGraduateTimeStr())){
                    heiMaStudentMsg.setGraduateTime(DATEFORMAT.parse(studentMsgDto.getGraduateTimeStr()));
                }
                //updateWrapper.set(City::getProvince,null)
                heiMaStudentMsg.setUpdateTime(new Date());
                updateById(heiMaStudentMsg);
            }
        }
    }

    @Override
    public Response queryById(HeiMaStudentMsgDto studentMsgDto) {
        log.info("根据ID查询学生信息，参数：{}", studentMsgDto.getId());
        LambdaQueryWrapper<HeiMaStudentMsg> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HeiMaStudentMsg::getId,studentMsgDto.getId());
        HeiMaStudentMsg heiMaStudentMsg = getOne(queryWrapper);

        if (ObjectUtil.isNotEmpty(heiMaStudentMsg)) {
            if(ObjectUtil.isNotEmpty(heiMaStudentMsg.getGraduateTime())){
                heiMaStudentMsg.setGraduateTimeStr(DATEFORMAT.format(heiMaStudentMsg.getGraduateTime()));
            }
            if(ObjectUtil.isNotEmpty(heiMaStudentMsg.getRuzhiTime())){
                heiMaStudentMsg.setRuzhiTimeStr(DATEFORMAT.format(heiMaStudentMsg.getRuzhiTime()));
            }
        }
        return Response.success(heiMaStudentMsg);
    }

    private LambdaQueryWrapper<HeiMaStudentMsg> buildQueryWrapper(HeiMaStudentMsgDto studentMsgDto) {
        LambdaQueryWrapper<HeiMaStudentMsg> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc((HeiMaStudentMsg::getSalary));
        //根据班级精确查询
        if (!StringUtils.isEmpty(studentMsgDto.getJavaBatch())) {
            queryWrapper.eq(HeiMaStudentMsg::getJavaBatch, studentMsgDto.getJavaBatch());
        }
        //根据组精确查询
        if (!StringUtils.isEmpty(studentMsgDto.getGroup())) {
            queryWrapper.eq(HeiMaStudentMsg::getGroup, studentMsgDto.getGroup());
        }
        //根据姓名模糊查询
        if (!StringUtils.isEmpty(studentMsgDto.getName())) {
            queryWrapper.like(HeiMaStudentMsg::getName, studentMsgDto.getName());
        }
        //根据年龄范围查询
        if (!StringUtils.isEmpty(studentMsgDto.getStartAge())) {
            queryWrapper.ge(HeiMaStudentMsg::getAge,studentMsgDto.getStartAge());
        }
        if (!StringUtils.isEmpty(studentMsgDto.getEndAge())) {
            queryWrapper.le(HeiMaStudentMsg::getAge,studentMsgDto.getEndAge());
        }

        //根据学习情况精确查询
        if (!StringUtils.isEmpty(studentMsgDto.getPeacetimeLevel())) {
            queryWrapper.eq(HeiMaStudentMsg::getPeacetimeLevel,studentMsgDto.getPeacetimeLevel());
        }
        //根据学历精确查询
        if (!StringUtils.isEmpty(studentMsgDto.getEducation())) {
            queryWrapper.eq(HeiMaStudentMsg::getEducation,studentMsgDto.getEducation());
        }
        //根据工作模糊查询
        if (!StringUtils.isEmpty(studentMsgDto.getWorkMsg())) {
            queryWrapper.like(HeiMaStudentMsg::getWorkMsg,studentMsgDto.getWorkMsg());
        }
        //根据备注模糊查询
        if (!StringUtils.isEmpty(studentMsgDto.getRemark())) {
            queryWrapper.like(HeiMaStudentMsg::getRemark,studentMsgDto.getRemark());
        }

        //根据就业标识精确查询
        if (!StringUtils.isEmpty(studentMsgDto.getFindWorkFlag())) {
            queryWrapper.eq(HeiMaStudentMsg::getFindWorkFlag,studentMsgDto.getFindWorkFlag());
        }
        //根据关注级别精确查询
        if (!StringUtils.isEmpty(studentMsgDto.getFollowLevel())) {
            queryWrapper.eq(HeiMaStudentMsg::getFollowLevel,studentMsgDto.getFollowLevel());
        }
        //根据有无offer精确查询
        if (!StringUtils.isEmpty(studentMsgDto.getOfferFlag())) {
            queryWrapper.eq(HeiMaStudentMsg::getOfferFlag,studentMsgDto.getOfferFlag());
        }
        //根据薪资范围查询
        if (!StringUtils.isEmpty(studentMsgDto.getStartSalary())) {
            queryWrapper.ge(HeiMaStudentMsg::getSalary,studentMsgDto.getStartSalary());
        }
        if (!StringUtils.isEmpty(studentMsgDto.getEndSalary())) {
            queryWrapper.le(HeiMaStudentMsg::getSalary,studentMsgDto.getEndSalary());
        }
        return queryWrapper;
    }

}
