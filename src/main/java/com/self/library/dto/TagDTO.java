package com.self.library.dto;

import com.self.library.entity.BookEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Author Administrator
 * @Title:
 * @Description:
 * @Date 2021-05-20 22:17
 * @Version: 1.0
 */
@Data
@EqualsAndHashCode(of = "tagName", callSuper = false)
@ApiModel("标签包装")
public class TagDTO extends BaseDTO
{
    private static final long serialVersionUID = 5237593246139051183L;

    @ApiModelProperty(value = "标签ID", example = "1")
    private Integer id;
    @ApiModelProperty(value = "标签名", example = "计算机")
    private String tagName;
    @ApiModelProperty(value = "标签下的所有书籍")
    private List<BookEntity> books;
}
