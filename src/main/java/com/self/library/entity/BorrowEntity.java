package com.self.library.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("借阅实体")
public class BorrowEntity extends BaseEntity
{
    private static final long serialVersionUID = 4979323130801625227L;

    @ApiModelProperty(value = "借阅ID",example = "1")
    private Integer id;
    @ApiModelProperty(value = "借阅人姓名",example = "王杰斌")
    private String name;
    @ApiModelProperty(value = "借阅人性别",example = "0")
    private Integer sex;
    @ApiModelProperty(value = "借阅人年龄",example = "20")
    private Integer age;
    @ApiModelProperty(value = "借阅人电话",example = "17325879869")
    private String phone;
    @ApiModelProperty(value = "借阅人地址",example = "四川省成都市锦江区东大街")
    private String address;
    @ApiModelProperty(value = "借阅书籍ID",example = "1")
    private Integer bookId;
    @ApiModelProperty(value = "借阅此书数量",example = "1")
    private Integer count;
}