package com.self.library.dto;

import com.self.library.entity.UserEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author Administrator
 * @Title:
 * @Description:
 * @Date 2021-04-17 22:42
 * @Version: 1.0
 */
@Data
@ApiModel("用户信息及令牌包装")
public class AdminDTO implements Serializable
{
    private static final long serialVersionUID = 2614922734178753380L;
    @ApiModelProperty(value = "登录权限令牌")
    private String token;
    @ApiModelProperty("用户实体")
    private UserEntity user;
    @ApiModelProperty("错误信息")
    private String msg;
}
