package com.self.library.controller;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.self.library.constant.LibraryConstant;
import com.self.library.dto.PageDTO;
import com.self.library.dto.ResultDTO;
import com.self.library.entity.PublishEntity;
import com.self.library.service.PublishService;
import com.self.library.utils.JWTUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Administrator
 * @Title:
 * @Description:
 * @Date 2021-05-09 12:23
 * @Version: 1.0
 */
@RestController
@RequestMapping("/library/publish")
@Slf4j
@Api(tags = "出版社相关接口")
public class PublishController
{
    @Autowired
    private PublishService publishService;

    @PostMapping("/save")
    @ApiOperation("新增单个出版社接口")
    @ApiImplicitParam(name = "entity", value = "新增加的出版社实体", required = true)
    public ResultDTO<Integer> save(@RequestBody PublishEntity entity, HttpServletRequest request)
    {
        try
        {
            if (entity != null)
            {
                //由于有拦截器加持，能走到这里的必定是token验证正确的
                Pair<Boolean, DecodedJWT> pair = JWTUtils.verify(request.getHeader("Authorization"));
                String username = pair.getRight().getClaim(LibraryConstant.USERNAME).asString();
                entity.setCreateUser(username);
                return new ResultDTO<>(publishService.save(entity));
            }
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.INSERT_ERROR, e);
            return new ResultDTO<>(LibraryConstant.INSERT_ERROR, ResultDTO.FAIL);
        }
        return new ResultDTO<>(LibraryConstant.INSERT_FAIL, ResultDTO.FAIL);
    }

    @PostMapping("/saveList")
    @ApiOperation("批量新增出版社接口")
    @ApiImplicitParam(name = "entities", value = "出版社实体集合", required = true)
    public ResultDTO<Integer> saveList(@RequestBody List<PublishEntity> entities, HttpServletRequest request)
    {
        try
        {
            if (CollectionUtils.isNotEmpty(entities))
            {
                List<PublishEntity> newList = entities.stream().filter(publish -> StringUtils.isNotBlank(publish.getPublishName())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(newList))
                {
                    //由于有拦截器加持，能走到这里的必定是token验证正确的
                    Pair<Boolean, DecodedJWT> pair = JWTUtils.verify(request.getHeader("Authorization"));
                    String username = pair.getRight().getClaim(LibraryConstant.USERNAME).asString();
                    newList.forEach(entity -> entity.setCreateUser(username));
                    return new ResultDTO<>(publishService.saveList(newList));
                }
            }
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.INSERT_ERROR, e);
            return new ResultDTO<>(LibraryConstant.INSERT_ERROR, ResultDTO.FAIL);
        }
        return new ResultDTO<>(LibraryConstant.INSERT_FAIL, ResultDTO.FAIL);
    }

    @PostMapping("/page")
    @ApiOperation("分页和模糊查询查询接口")
    @ApiImplicitParam(name = "page", value = "分页和模糊查询条件信息包装", required = true)
    public ResultDTO<ArrayList<PublishEntity>> page(@RequestBody PageDTO<PublishEntity> page)
    {
        try
        {
            if (page == null)
            {
                page = new PageDTO<>();
            }
            return new ResultDTO<>(publishService.page(page));
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.QUERY_ERROR, e);
            return new ResultDTO<>(LibraryConstant.QUERY_ERROR, ResultDTO.FAIL);
        }
    }

    @GetMapping("/findById")
    @ApiOperation("根据ID查询出版社接口")
    @ApiImplicitParam(name = "id", value = "出版社ID", required = true, dataType = "integer", defaultValue = "1", example = "2")
    public ResultDTO<PublishEntity> findById(@RequestParam("id") Integer id)
    {
        try
        {
            if (id != null)
            {
                return new ResultDTO<>(publishService.findById(id));
            }
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.QUERY_ERROR, e);
            return new ResultDTO<>(LibraryConstant.QUERY_ERROR, ResultDTO.FAIL);
        }
        return new ResultDTO<>(LibraryConstant.QUERY_FAIL, ResultDTO.FAIL);
    }

    @GetMapping("/delete")
    @ApiOperation("删除单个出版社接口")
    @ApiImplicitParam(name = "id", value = "出版社ID", required = true, dataType = "integer", defaultValue = "1", example = "2")
    public ResultDTO<Integer> delete(@RequestParam("id") Integer id)
    {
        try
        {
            if (id != null)
            {
                Integer count = publishService.delete(id);
                if (count > 0)
                {
                    return new ResultDTO<>(count);
                }
                else if (count == 0)
                {
                    return new ResultDTO<>(LibraryConstant.DELETE_FAIL, ResultDTO.FAIL);
                }
            }
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.DELETE_ERROR, e);
        }
        return new ResultDTO<>(LibraryConstant.DELETE_ERROR, ResultDTO.FAIL);
    }

    @PostMapping("/modify")
    @ApiOperation("修改出版社接口")
    @ApiImplicitParam(name = "publish", value = "出版社实体", required = true)
    public ResultDTO<Integer> modify(@RequestBody PublishEntity publish, HttpServletRequest request)
    {
        try
        {
            if (publish != null && publish.getId() != null && publish.getId() > Integer.parseInt(LibraryConstant.COMMON_ZERO))
            {
                String token = request.getHeader("Authorization");
                Pair<Boolean, DecodedJWT> pair = JWTUtils.verify(token);
                DecodedJWT decoded = pair.getRight();
                Claim claim = decoded.getClaim(LibraryConstant.USERNAME);
                String username = claim.asString();
                publish.setModifyUser(username);
                Integer count = publishService.modify(publish);
                if (count != null)
                {
                    return new ResultDTO<>(count);
                }
            }
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.MODIFY_ERROR, e);
            return new ResultDTO<>(LibraryConstant.MODIFY_ERROR, ResultDTO.FAIL);
        }
        return new ResultDTO<>(LibraryConstant.MODIFY_FAIL, ResultDTO.FAIL);
    }

    @GetMapping("/findAll")
    @ApiOperation("查询所有出版社接口")
    public ResultDTO<ArrayList<PublishEntity>> findAll()
    {
        try
        {
            return new ResultDTO<>(publishService.findAll());
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.QUERY_ERROR);
            return new ResultDTO<>(LibraryConstant.QUERY_ERROR, ResultDTO.FAIL);
        }
    }
}
