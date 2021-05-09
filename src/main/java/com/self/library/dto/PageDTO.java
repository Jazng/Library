package com.self.library.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author Administrator
 * @Title: 分页
 * @Description: 分页实体
 * @Date 2021-05-08 22:06
 * @Version: 1.0
 */
@Data
@ApiModel("分页包装")
public class PageDTO<T extends Serializable> implements Serializable
{
    private static final long serialVersionUID = -3865827406857566539L;

    @ApiModelProperty(value = "页码", example = "1")
    private Integer pageNum;
    @ApiModelProperty(value = "每页数量", example = "10")
    private Integer pageSize;
    @ApiModelProperty(value = "排序字段", example = "id")
    private String property;
    @ApiModelProperty(value = "升降序：asc || desc", example = "asc")
    private String order;
    @ApiModelProperty(value = "模糊查询实体：需要使用哪个字段模糊查询就传哪个字段")
    private T entity;
}
