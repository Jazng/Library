package com.self.library.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.self.library.dto.AdminDTO;
import com.self.library.dto.ResultDTO;
import com.self.library.entity.UserEntity;
import com.self.library.service.AdminService;
import com.self.library.utils.JWTUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static com.self.library.constant.LibraryConstant.*;
import static com.self.library.dto.ResultDTO.FAIL;
import static com.self.library.dto.ResultDTO.SUCCESS;

/**
 * @Author Administrator
 * @Title: 登录&注册接口
 * @Description:
 * @Date 2021-04-17 16:21
 * @Version: 1.0
 */
@RestController
@RequestMapping("/library/admin")
@Api(tags = "账户相关接口")
@Slf4j
public class AdminController
{
    @Autowired
    private AdminService adminService;

    @PostMapping("/register")
    @ApiOperation("注册接口")
    @ApiImplicitParam(name = "userEntity", value = "注册信息", required = true)
    public ResultDTO<ArrayList<String>> register(@RequestBody UserEntity userEntity)
    {
        ResultDTO<ArrayList<String>> result = null;
        try
        {
            if (userEntity != null && StringUtils.isNotBlank(userEntity.getUsername())
                    && StringUtils.isNotBlank(userEntity.getNickname())
                    && userEntity.getRole() != null
                    && StringUtils.isNotBlank(userEntity.getPhone())
                    && userEntity.getSex() != null)
            {
                Pair<Boolean, List<String>> pair = adminService.register(userEntity);
                return new ResultDTO<>(pair.getRight());
            }
            else
            {
                result = new ResultDTO<>(Collections.singletonList(REGISTER_ENTER_MSG));
            }
            result.setCode(FAIL);
        }
        catch (Exception e)
        {
            log.error(REGISTER_ERROR_MSG, e);
        }
        return result;
    }

    @PostMapping("/login")
    @ApiOperation("登录接口")
    @ApiImplicitParam(name = "userEntity", value = "登录信息（ps：只需要username和password）", required = true)
    public ResultDTO<AdminDTO> login(@RequestBody UserEntity userEntity)
    {
        AdminDTO<UserEntity> adminDTO = new AdminDTO<>();
        try
        {
            if (userEntity != null && StringUtils.isNotBlank(userEntity.getUsername()) && StringUtils.isNotBlank(userEntity.getPassword()))
            {
                Pair<Boolean, UserEntity> pair = adminService.login(userEntity);
                if (pair.getLeft())
                {
                    UserEntity user = pair.getRight();
                    if (user != null)
                    {
                        Map<String, Object> map = new HashMap<>(4);
                        map.put(USER_ID, user.getId());
                        map.put(USERNAME, user.getUsername());
                        map.put(NICKNAME, user.getNickname());
                        map.put(ROLE, user.getRole());
                        map.put(PHONE, user.getPhone());
                        map.put(EMAIL, user.getEmail());
                        map.put(AGE, user.getAge());
                        map.put(SEX, user.getSex());
                        String token = JWTUtils.getToken(map);
                        adminDTO.setToken(token);
                        adminDTO.setEntity(user);
                        adminDTO.setMsg(LOGIN_SUCCESS_MSG);
                        return new ResultDTO<>(adminDTO);
                    }
                }
                adminDTO.setMsg(LOGIN_ERROR_MSG);
            }
            else
            {
                adminDTO.setMsg(LOGIN_ENTER_MSG);
            }
        }
        catch (Exception e)
        {
            log.error(LOGIN_ERROR, e);
        }

        return new ResultDTO<>(FAIL, adminDTO);
    }

    @PostMapping("/destroy")
    @ApiOperation("注销接口")
    @ApiImplicitParam(name = "userEntity", value = "注销信息（ps：只需要username和password）", required = true)
    public ResultDTO<String> destroy(@RequestBody UserEntity userEntity)
    {
        try
        {
            if (userEntity != null)
            {
                boolean destroy = adminService.destroy(userEntity);
                if (destroy)
                {
                    return new ResultDTO<>(DESTROY_SUCCESS);
                }
            }
        }
        catch (Exception e)
        {
            log.error(DESTROY_ERROR, e);
        }
        return new ResultDTO<>(FAIL, DESTROY_FAIL);
    }

    @PostMapping("/modify")
    @ApiOperation("修改账户信息接口")
    @ApiImplicitParam(name = "userEntity", value = "修改项信息（需要修改什么就传什么）", required = true)
    public ResultDTO<String> modify(@RequestBody UserEntity userEntity)
    {
        try
        {
            if (userEntity != null && userEntity.getId() != null && userEntity.getId() > Integer.parseInt(COMMON_ZERO))
            {
                boolean modify = adminService.modify(userEntity);
                if (modify)
                {
                    return new ResultDTO<>(MODIFY_SUCCESS);
                }
            }
        }
        catch (Exception e)
        {
            log.error(USER + MODIFY_ERROR, e);
        }
        return new ResultDTO<>(FAIL, MODIFY_FAIL);
    }


    @GetMapping("/findById")
    @ApiOperation("根据用户ID查询信息")
    @ApiImplicitParam(name = "id", value = "用户ID", dataType = "integer", paramType = "query", required = true, defaultValue = "1", example = "2")
    public ResultDTO<UserEntity> findById(@RequestParam("id") Integer id)
    {
        try
        {
            if (id != null && id > 0)
            {
                UserEntity user = adminService.findById(id);
                if (user != null)
                {
                    return new ResultDTO<>(user);
                }
            }
        }
        catch (Exception e)
        {
            log.error(QUERY_USER_ERROR, e);
        }
        return new ResultDTO<>(FAIL, null);
    }

    @GetMapping("/logout")
    @ApiOperation("退出登录接口")
    @ApiImplicitParam(name = "id", value = "用户ID", dataType = "integer", required = true, defaultValue = "1", example = "1")
    public ResultDTO<Integer> logout(@RequestParam("id") Integer id, HttpServletRequest request)
    {
        if (id != null && id > 0)
        {
            //由于有拦截器加持，能走到这里的必定是token验证正确的
            Pair<Boolean, DecodedJWT> pair = JWTUtils.verify(request.getHeader("Authorization"));
            DecodedJWT decodedJWT = pair.getRight();
            Integer userId = decodedJWT.getClaim(USER_ID).asInt();
            if (id.equals(userId))
            {
                ResultDTO<Integer> result = new ResultDTO<>(LOGOUT_SUCCESS, SUCCESS);
                result.setData(id);
                return result;
            }
        }
        return new ResultDTO<>(LOGOUT_FAIL, FAIL);
    }
}
