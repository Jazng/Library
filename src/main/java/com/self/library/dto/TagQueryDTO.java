package com.self.library.dto;

import com.self.library.entity.TagEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author Administrator
 * @Title:
 * @Description:
 * @Date 2021-05-08 22:41
 * @Version: 1.0
 */
@Data
@ApiModel("标签分页模糊查询包装")
public class TagQueryDTO extends PageDTO
{
    @ApiModelProperty(value = "标签实体：需要使用哪个字段模糊查询就传哪个字段")
    private TagEntity tag;
}
