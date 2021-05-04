package com.self.library.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("用户实体")
public class UserEntity extends BaseEntity
{
    private static final long serialVersionUID = 907300784365768032L;

    @ApiModelProperty("用户ID")
    private Integer id;
    @ApiModelProperty(value = "用户名",example = "zhangsan")
    private String username;
    @ApiModelProperty(value = "密码",example = "123456")
    private String password;
    @ApiModelProperty(value = "昵称",example = "张三")
    private String nickname;
    @ApiModelProperty(value = "角色",example = "1")
    private Integer role;
    @ApiModelProperty(value = "电话号码",example = "17325879516")
    private String phone;
    @ApiModelProperty(value = "邮箱",example = "xxx@163.com")
    private String email;
    @ApiModelProperty(value = "年龄",example = "20")
    private Integer age;
    @ApiModelProperty(value = "性别",example = "男")
    private Integer sex;
    @ApiModelProperty(value = "是否注销")
    private Boolean destroy;
}