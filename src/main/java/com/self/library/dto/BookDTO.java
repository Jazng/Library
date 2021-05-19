package com.self.library.dto;

import com.self.library.entity.PublishEntity;
import com.self.library.entity.TagEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author Administrator
 * @Title:
 * @Description:
 * @Date 2021-05-19 20:48
 * @Version: 1.0
 */
@Data
@EqualsAndHashCode(exclude = {"id", "lend"}, callSuper = false)
@ApiModel("书籍包装")
public class BookDTO extends BaseDTO
{
    private static final long serialVersionUID = -3911578384683027202L;

    @ApiModelProperty(value = "书籍ID", example = "1")
    private Integer id;
    @ApiModelProperty(value = "书籍名称", example = "Java核心技术卷一", required = true)
    private String bookName;
    @ApiModelProperty(value = "书籍编号(ISBN)", example = "9787510095207", required = true)
    private String bookNo;
    @ApiModelProperty(value = "书籍价格", example = "79.9")
    private BigDecimal price;
    @ApiModelProperty(value = "是否展示", example = "true")
    private Boolean show;
    @ApiModelProperty(value = "书籍数量", example = "10")
    private Integer count;
    @ApiModelProperty(value = "借出数量", example = "5")
    private Integer lend;
    @ApiModelProperty(value = "标签ID", example = "1", required = true)
    private Integer tagId;
    @ApiModelProperty(value = "出版社ID", example = "1", required = true)
    private Integer publishId;
    @ApiModelProperty(value = "作者", example = "李狗蛋")
    private String author;
    @ApiModelProperty(value = "出版日期", example = "2010-07-15")
    private Date publishDate;
    @ApiModelProperty(value = "标签")
    private TagEntity tag;
    @ApiModelProperty(value = "出版社")
    private PublishEntity publish;
}
