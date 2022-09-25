package com.adolesce.cloud.dubbo.domain.db;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public abstract class BasePojo implements Serializable {
    @TableField(fill = FieldFill.INSERT) //MP自动填充
    private LocalDateTime createTime;

    /*@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)*/
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}