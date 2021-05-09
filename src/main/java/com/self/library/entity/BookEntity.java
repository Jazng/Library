package com.self.library.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(exclude = "id", callSuper = false)
@ApiModel("书籍实体")
public class BookEntity extends BaseEntity
{
    private static final long serialVersionUID = 9075427265406850111L;

    @ApiModelProperty(value = "书籍ID", example = "1")
    private Integer id;
    @ApiModelProperty(value = "书籍名称", example = "Java核心技术卷一", required = true)
    private String bookName;
    @ApiModelProperty(value = "书籍编号(ISBN)", example = "9787510095207", required = true)
    private String bookNo;
    @ApiModelProperty(value = "书籍价格", example = "79.9", required = true)
    private BigDecimal price;
    @ApiModelProperty(value = "是否展示", example = "true", required = true)
    private Boolean show;
    @ApiModelProperty(value = "书籍页数", example = "9787510095207")
    private Integer pageNum;
    @ApiModelProperty(value = "标签ID", example = "1", required = true)
    private Integer tagId;
    @ApiModelProperty(value = "出版社ID", example = "1", required = true)
    private Integer publishId;
    @ApiModelProperty(value = "作者", example = "李狗蛋")
    private String author;
    @ApiModelProperty(value = "出版日期", example = "2010-07-15")
    private Date publishDate;
}