package com.self.library.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "publishName", callSuper = false)
@ApiModel("出版社实体")
public class PublishEntity extends BaseEntity
{
    private static final long serialVersionUID = 5369490225762695946L;

    @ApiModelProperty(value = "出版社ID", example = "1")
    private Integer id;
    @ApiModelProperty(value = "出版社名称", example = "机械工业出版社")
    private String publishName;
}