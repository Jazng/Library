package com.self.library.controller;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.self.library.constant.LibraryConstant;
import com.self.library.dto.ResultDTO;
import com.self.library.dto.TagQueryDTO;
import com.self.library.entity.TagEntity;
import com.self.library.service.TagService;
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
 * @Date 2021-05-07 21:15
 * @Version: 1.0
 */
@RestController
@RequestMapping("/library/tag")
@Api(tags = "标签相关接口")
@Slf4j
public class TagController
{
    @Autowired
    private TagService tagService;

    @PostMapping("/saveTag")
    @ApiOperation("新增单个标签接口")
    @ApiImplicitParam(name = "tag", value = "新增加的标签实体", required = true)
    public ResultDTO<Integer> saveTag(@RequestBody TagEntity tag, HttpServletRequest request)
    {
        try
        {
            if (tag != null)
            {
                //由于有拦截器加持，能走到这里的必定是token验证正确的
                Pair<Boolean, DecodedJWT> pair = JWTUtils.verify(request.getHeader("Authorization"));
                String username = pair.getRight().getClaim(LibraryConstant.USERNAME).asString();
                tag.setCreateUser(username);
                return new ResultDTO<>(tagService.saveTag(tag));
            }
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.INSERT_ERROR, e);
            return new ResultDTO<>(LibraryConstant.INSERT_ERROR, ResultDTO.FAIL);
        }
        return new ResultDTO<>(LibraryConstant.INSERT_FAIL, ResultDTO.FAIL);
    }

    @PostMapping("/saveTags")
    @ApiOperation("批量新增标签接口")
    @ApiImplicitParam(name = "tags", value = "标签实体集合", required = true)
    public ResultDTO<Integer> saveTags(@RequestBody List<TagEntity> tags, HttpServletRequest request)
    {
        try
        {
            if (CollectionUtils.isNotEmpty(tags))
            {
                List<TagEntity> newList = tags.stream().filter(tag -> StringUtils.isNotBlank(tag.getTagName())).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(newList))
                {
                    //由于有拦截器加持，能走到这里的必定是token验证正确的
                    Pair<Boolean, DecodedJWT> pair = JWTUtils.verify(request.getHeader("Authorization"));
                    String username = pair.getRight().getClaim(LibraryConstant.USERNAME).asString();
                    newList.forEach(tag -> tag.setCreateUser(username));
                    return new ResultDTO<>(tagService.saveTags(newList));
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

    @PostMapping("/pageTags")
    @ApiOperation("分页查询接口")
    @ApiImplicitParam(name = "page", value = "分页信息包装", required = true)
    public ResultDTO<ArrayList<TagEntity>> pageTags(@RequestBody TagQueryDTO tagQuery)
    {
        try
        {
            if (tagQuery == null)
            {
                tagQuery = new TagQueryDTO();
            }
            return new ResultDTO<>(tagService.pageTags(tagQuery));
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.QUERY_ERROR, e);
            return new ResultDTO<>(LibraryConstant.QUERY_ERROR, ResultDTO.FAIL);
        }
    }

    @GetMapping("/findById")
    @ApiOperation("根据ID查询标签接口")
    @ApiImplicitParam(name = "id", value = "标签ID", required = true, dataType = "integer", defaultValue = "1", example = "2")
    public ResultDTO<TagEntity> findById(@RequestParam("id") Integer id)
    {
        try
        {
            if (id != null)
            {
                return new ResultDTO<>(tagService.findById(id));
            }
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.QUERY_ERROR, e);
            return new ResultDTO<>(LibraryConstant.QUERY_ERROR, ResultDTO.FAIL);
        }
        return new ResultDTO<>(LibraryConstant.QUERY_FAIL, ResultDTO.FAIL);
    }

    @GetMapping("/deleteTag")
    @ApiOperation("删除单个标签接口")
    @ApiImplicitParam(name = "id", value = "标签ID", required = true, dataType = "integer", defaultValue = "1", example = "2")
    public ResultDTO<Integer> deleteTag(@RequestParam("id") Integer id)
    {
        try
        {
            if (id != null)
            {
                Integer count = tagService.deleteTag(id);
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
    @ApiOperation("修改标签接口")
    @ApiImplicitParam(name = "tag", value = "标签实体", required = true)
    public ResultDTO<Integer> modify(@RequestBody TagEntity tag, HttpServletRequest request)
    {
        try
        {
            if (tag != null && tag.getId() != null && tag.getId() > Integer.parseInt(LibraryConstant.COMMON_ZERO))
            {
                String token = request.getHeader("Authorization");
                Pair<Boolean, DecodedJWT> pair = JWTUtils.verify(token);
                DecodedJWT decoded = pair.getRight();
                Claim claim = decoded.getClaim(LibraryConstant.USERNAME);
                String username = claim.asString();
                tag.setModifyUser(username);
                Integer count = tagService.modify(tag);
                if (count != null)
                {
                    return new ResultDTO<>(count);
                }
            }
        }
        catch (NumberFormatException e)
        {
            log.error(LibraryConstant.MODIFY_ERROR, e);
            return new ResultDTO<>(LibraryConstant.MODIFY_ERROR, ResultDTO.FAIL);
        }
        return new ResultDTO<>(LibraryConstant.MODIFY_FAIL, ResultDTO.FAIL);
    }

    @GetMapping("/findAll")
    @ApiOperation("查询所有标签接口")
    public ResultDTO<ArrayList<TagEntity>> findAll()
    {
        try
        {
            return new ResultDTO<>(tagService.findAll());
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.QUERY_ERROR);
            return new ResultDTO<>(LibraryConstant.QUERY_ERROR, ResultDTO.FAIL);
        }
    }
}
