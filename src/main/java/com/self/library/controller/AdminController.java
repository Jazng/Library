package com.self.library.controller;

import com.self.library.dto.AdminDTO;
import com.self.library.dto.ResultDTO;
import com.self.library.entity.UserEntity;
import com.self.library.service.AdminService;
import com.self.library.utils.JWTUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import static com.self.library.constant.LibraryConstant.*;
import static com.self.library.dto.ResultDTO.FAIL;

/**
 * @Author Administrator
 * @Title: 登录&注册接口
 * @Description:
 * @Date 2021-04-17 16:21
 * @Version: 1.0
 */
@RestController
@RequestMapping("/library/admin")
@Api(tags = "登录与注册")
public class AdminController
{
    @Autowired
    private AdminService adminService;

    @PostMapping("/register")
    @ApiOperation("注册接口")
    @ApiImplicitParam(name = "userEntity", value = "注册信息",required = true)
    public ResultDTO<ArrayList<String>> register(@RequestBody UserEntity userEntity)
    {
        ResultDTO<ArrayList<String>> result = null;
        if (userEntity != null && StringUtils.isNotBlank(userEntity.getUsername())
                && StringUtils.isNotBlank(userEntity.getNickname())
                && userEntity.getRole() != null
                && StringUtils.isNotBlank(userEntity.getPhone())
                && userEntity.getSex() != null)
        {
            Pair<Boolean, List<String>> pair = adminService.register(userEntity);
            List<String> right = pair.getRight();
            result = new ResultDTO<>(right);
            if (!pair.getLeft())
            {
                String msg = "";
                Iterator<String> iterator = right.iterator();
                while (iterator.hasNext())
                {
                    String str = iterator.next();
                    if (iterator.hasNext())
                    {
                        msg += str + CHINESE_SEMICOLON;
                    }
                    else
                    {
                        msg += str;
                    }
                }
                result.setMsg(msg);
            }
        }
        else
        {
            result = new ResultDTO<>(REGISTER_ENTER_MSG, FAIL);
        }
        return result;
    }

    @PostMapping("/login")
    @ApiOperation("登录接口")
    @ApiImplicitParam(name = "userEntity", value = "登录信息（ps：只需要username和password）", required = true)
    public ResultDTO<AdminDTO> login(@RequestBody UserEntity userEntity)
    {
        String msg = null;
        if (userEntity != null && StringUtils.isNotBlank(userEntity.getUsername()) && StringUtils.isNotBlank(userEntity.getPassword()))
        {
            Pair<Boolean, UserEntity> pair = adminService.login(userEntity);
            if (pair.getLeft())
            {
                UserEntity user = pair.getRight();
                if (user != null)
                {
                    Map<String, Object> map = new HashMap<>(4);
                    map.put(USERNAME, user.getUsername());
                    map.put(NICKNAME, user.getNickname());
                    map.put(ROLE, user.getRole());
                    map.put(PHONE, user.getPhone());
                    map.put(EMAIL, user.getEmail());
                    map.put(AGE, user.getAge());
                    map.put(SEX, user.getSex());
                    String token = JWTUtils.getToken(map);
                    AdminDTO adminDTO = new AdminDTO();
                    adminDTO.setToken(token);
                    adminDTO.setUser(user);
                    return new ResultDTO<>(adminDTO);
                }
                else
                {
                    msg = LOGIN_NO_PERMISSION_MSG;
                }
            }
            else
            {
                msg = LOGIN_ERROR_MSG;
            }
        }
        else
        {
            msg = LOGIN_ENTER_MSG;
        }

        return new ResultDTO<>(msg, FAIL);
    }
}
