package com.adolesce.server.javabasic.hmtest.parselog;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2022/2/28 13:49
 */
@Data

@TableName("app_log ")
public class AppLog implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 日志ID
     */
    @TableId(type = IdType.AUTO)
    @TableField("Log_Id")
    private Long logId;

    /**
     * 日志时间
     */
    @TableField("Log_Time")
    private Date logTime;

    /**
     * 线程名
     */
    @TableField("Thread_Name")
    private String threadName;

    /**
     * 日志级别
     */
    @TableField("Thread_Name")
    private String logLeval;

    /**
     * 类名
     */
    @TableField("Class_Name")
    private String className;

    /**
     * 日志详情
     */
    @TableField("Log_Info")
    private String logInfo;
}
