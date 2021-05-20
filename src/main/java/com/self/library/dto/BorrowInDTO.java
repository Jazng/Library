package com.self.library.dto;

import com.self.library.entity.BookEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author Administrator
 * @Title:
 * @Description:
 * @Date 2021-05-15 10:24
 * @Version: 1.0
 */
@Data
@ApiModel("借阅查询封装")
public class BorrowInDTO extends BaseDTO
{
    private static final long serialVersionUID = 3012429996730457357L;

    @ApiModelProperty(value = "借阅人姓名",example = "王杰斌")
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
    @ApiModelProperty(value = "开始归还日期", example = "2021-03-02")
    private Date startReturnDate;
    @ApiModelProperty(value = "结束归还日期", example = "2021-03-02")
    private Date endReturnDate;
    @ApiModelProperty(value = "状态", example = "1")
    private Integer status;
    @ApiModelProperty(value = "开始实际归还日期", example = "2021-03-02")
    private Date startActualDate;
    @ApiModelProperty(value = "结束实际归还日期", example = "2021-03-02")
    private Date endActualDate;
    @ApiModelProperty(value = "借阅书籍集合")
    private List<BookEntity> books;
}
