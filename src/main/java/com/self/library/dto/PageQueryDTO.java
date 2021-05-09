package com.self.library.dto;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author Administrator
 * @Title:
 * @Description:
 * @Date 2021-05-09 17:14
 * @Version: 1.0
 */
@Data
@ApiModel("分页结果包装")
public class PageQueryDTO<T extends Serializable> implements Serializable
{
    private static final long serialVersionUID = -3477077517757365208L;

    @ApiModelProperty("分页信息")
    private PageInfo<T> page;
    @ApiModelProperty("分页查询结果")
    private List<T> list;
}
