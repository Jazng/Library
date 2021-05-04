package com.self.library.entity;

import com.self.library.constant.LibraryConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author Administrator
 * @Title: 基础实体类
 * @Description: 每个类都有的属性提取出来
 * @Date 2021-04-17 17:03
 * @Version: 1.0
 */
@Data
@ApiModel("实体公共部分")
public class BaseEntity implements Serializable
{
    private static final long serialVersionUID = -4936915402126894679L;
    @ApiModelProperty(value = "创建者", example = LibraryConstant.CREATE_USER)
    private String createUser;
    @ApiModelProperty("创建时间（ps：插入数据库时自动插入当前系统时间）")
    private Date createDate;
    @ApiModelProperty(value = "修改者", example = LibraryConstant.MODIFY_USER)
    private String modifyUser;
    @ApiModelProperty("创建时间（ps：更新数据库时自动插入当前系统时间）")
    private Date modifyDate;
}
