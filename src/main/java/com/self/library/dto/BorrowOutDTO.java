package com.self.library.dto;

import com.self.library.entity.BookEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author Administrator
 * @Title:
 * @Description:
 * @Date 2021-05-16 13:54
 * @Version: 1.0
 */
@Data
@ApiModel("借阅返回封装")
public class BorrowOutDTO extends BaseDTO
{
    private static final long serialVersionUID = 9116702765733514071L;

    @ApiModelProperty(value = "借阅ID", example = "1")
    private Integer id;
    @ApiModelProperty(value = "借阅人姓名", example = "王杰斌")
    private String name;
    @ApiModelProperty(value = "借阅人性别", example = "0")
    private Integer sex;
    @ApiModelProperty(value = "借阅人年龄", example = "20")
    private Integer age;
    @ApiModelProperty(value = "借阅人电话", example = "17325879869")
    private String phone;
    @ApiModelProperty(value = "借阅人地址", example = "四川省成都市锦江区东大街")
    private String address;
    @ApiModelProperty(value = "借阅此书数量", example = "1")
    private Integer count;
    @ApiModelProperty(value = "借阅天数", example = "1")
    private Integer borrowDay;
    @ApiModelProperty(value = "归还日期", example = "2021-03-02")
    private Date returnDate;
    @ApiModelProperty(value = "状态", example = "1")
    private Integer status;
    @ApiModelProperty(value = "开始实际归还日期", example = "2021-03-02")
    private Date actualDate;
    @ApiModelProperty(value = "借阅书籍")
    private BookEntity book;
}
