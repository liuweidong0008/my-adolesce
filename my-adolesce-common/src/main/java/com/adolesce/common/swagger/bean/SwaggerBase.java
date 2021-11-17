package com.adolesce.common.swagger.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Administrator
 * @version 1.0
 * @description: TODO
 * @date 2021/10/13 0:00
 */
@Data
@ApiModel(value="分页基础对象",description="用于封装分页信息")
public class SwaggerBase {
    @JsonProperty("pagesize")
    @ApiModelProperty(value="每页元素个数",name="pageSize",dataType = "Integer",required = true, example="10")
    private Integer pageSize;
    @ApiModelProperty(value="查询第几页",name="pageNum",dataType = "Integer",required = true, example="1")
    private Integer pageNum;
    private String desc;
}
