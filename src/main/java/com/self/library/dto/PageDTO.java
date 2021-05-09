package com.self.library.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author Administrator
 * @Title: 分页
 * @Description: 分页实体
 * @Date 2021-05-08 22:06
 * @Version: 1.0
 */
@Data
@ApiModel("分页包装")
public class PageDTO
{
    @ApiModelProperty(value = "页码", example = "1")
    private Integer pageNum;
    @ApiModelProperty(value = "每页数量", example = "10")
    private Integer pageSize;
    @ApiModelProperty(value = "排序字段", example = "id")
    private String property;
    @ApiModelProperty(value = "升降序：asc || desc", example = "升序")
    private String order;
}
