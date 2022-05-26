package com.adolesce.cloud.dubbo.dto;

import com.adolesce.cloud.dubbo.domain.db.HeiMaStudentMsg;
import lombok.Data;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2022/2/28 13:49
 */
@Data
public class HeiMaStudentMsgDto extends HeiMaStudentMsg {
    private static final long serialVersionUID = 1L;
    /**
     * 当前查询页
     */
    private Integer currentPage;
    /**
     * 一页数据条数
     */
    private Integer pageSize;
    /**
     * 跨域数据校验项
     */
    private String callback;

    /**
     * 开始年龄
     */
    private Integer startAge;
    /**
     * 结束年龄
     */
    private Integer endAge;
    /**
     * 开始薪资
     */
    private Integer startSalary;
    /**
     * 结束薪资
     */
    private Integer endSalary;

    @Override
    public String toString() {
        return super.toString();
    }
}
