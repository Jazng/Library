package com.self.library.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "tagName", callSuper = false)
@ApiModel("标签实体")
public class TagEntity extends BaseEntity
{
    private static final long serialVersionUID = 1322438924707495994L;

    @ApiModelProperty(value = "标签ID", example = "1")
    private Integer id;
    @ApiModelProperty(value = "标签名", example = "计算机")
    private String tagName;
}