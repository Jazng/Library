package com.self.library.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Administrator
 * @Title:
 * @Description:
 * @Date 2021-05-15 10:24
 * @Version: 1.0
 */
@Data
public class BorrowDTO implements Serializable
{
    private static final long serialVersionUID = -328362664514926281L;

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
    @ApiModelProperty(value = "借阅此书数量",example = "1")
    private Integer count;
    @ApiModelProperty(value = "借阅书籍ID集合")
    private List<Integer> bookIds;
}
