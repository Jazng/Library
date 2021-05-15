package com.self.library.controller;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.pagehelper.PageInfo;
import com.self.library.constant.LibraryConstant;
import com.self.library.dto.BorrowDTO;
import com.self.library.dto.PageDTO;
import com.self.library.dto.ResultDTO;
import com.self.library.entity.BorrowEntity;
import com.self.library.service.BorrowService;
import com.self.library.utils.JWTUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
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
 * @Date 2021-05-15 10:37
 * @Version: 1.0
 */
@RestController
@RequestMapping("/library/borrow")
@Api(tags = "借阅相关接口")
@Slf4j
public class BorrowController
{
    @Autowired
    private BorrowService borrowService;

    @PostMapping("/save")
    @ApiOperation("新增单个借阅接口")
    @ApiImplicitParam(name = "entity", value = "新增加的借阅实体", required = true)
    public ResultDTO<Integer> save(@RequestBody BorrowEntity entity, HttpServletRequest request)
    {
        try
        {
            if (entity != null)
            {
                //由于有拦截器加持，能走到这里的必定是token验证正确的
                Pair<Boolean, DecodedJWT> pair = JWTUtils.verify(request.getHeader("Authorization"));
                String username = pair.getRight().getClaim(LibraryConstant.USERNAME).asString();
                entity.setCreateUser(username);
                return new ResultDTO<>(borrowService.save(entity));
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
    @ApiOperation("批量新增借阅接口")
    @ApiImplicitParam(name = "borrowDTO", value = "借阅实体集合", required = true)
    public ResultDTO<Integer> saveList(@RequestBody BorrowDTO borrowDTO, HttpServletRequest request)
    {
        try
        {
            if (borrowDTO != null && CollectionUtils.isNotEmpty(borrowDTO.getBookIds()))
            {
                List<BorrowEntity> list = borrowDTO.getBookIds().stream().map(id ->
                {
                    BorrowEntity entity = new BorrowEntity();
                    BeanUtils.copyProperties(borrowDTO, entity);
                    entity.setBookId(id);
                    return entity;
                }).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(list))
                {
                    //由于有拦截器加持，能走到这里的必定是token验证正确的
                    Pair<Boolean, DecodedJWT> pair = JWTUtils.verify(request.getHeader("Authorization"));
                    String username = pair.getRight().getClaim(LibraryConstant.USERNAME).asString();
                    list.forEach(entity -> entity.setCreateUser(username));
                    return new ResultDTO<>(borrowService.saveList(list));
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
    public ResultDTO<PageInfo<BorrowEntity>> page(@RequestBody PageDTO<BorrowEntity> page)
    {
        try
        {
            if (page == null)
            {
                page = new PageDTO<>();
            }
            return new ResultDTO<>(borrowService.page(page));
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.QUERY_ERROR, e);
            return new ResultDTO<>(LibraryConstant.QUERY_ERROR, ResultDTO.FAIL);
        }
    }

    @GetMapping("/findById")
    @ApiOperation("根据ID查询借阅接口")
    @ApiImplicitParam(name = "id", value = "借阅ID", required = true, dataType = "integer", defaultValue = "1", example = "2")
    public ResultDTO<BorrowEntity> findById(@RequestParam("id") Integer id)
    {
        try
        {
            if (id != null)
            {
                return new ResultDTO<>(borrowService.findById(id));
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
    @ApiOperation("删除单个借阅接口")
    @ApiImplicitParam(name = "id", value = "借阅ID", required = true, dataType = "integer", defaultValue = "1", example = "2")
    public ResultDTO<Integer> delete(@RequestParam("id") Integer id)
    {
        try
        {
            if (id != null)
            {
                Integer count = borrowService.delete(id);
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
    @ApiOperation("修改借阅接口")
    @ApiImplicitParam(name = "entity", value = "借阅实体", required = true)
    public ResultDTO<Integer> modify(@RequestBody BorrowEntity entity, HttpServletRequest request)
    {
        try
        {
            if (entity != null && entity.getId() != null && entity.getId() > Integer.parseInt(LibraryConstant.COMMON_ZERO))
            {
                String token = request.getHeader("Authorization");
                Pair<Boolean, DecodedJWT> pair = JWTUtils.verify(token);
                DecodedJWT decoded = pair.getRight();
                Claim claim = decoded.getClaim(LibraryConstant.USERNAME);
                String username = claim.asString();
                entity.setModifyUser(username);
                Integer count = borrowService.modify(entity);
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
    @ApiOperation("查询所有书籍接口")
    public ResultDTO<ArrayList<BorrowEntity>> findAll()
    {
        try
        {
            return new ResultDTO<>(borrowService.findAll());
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.QUERY_ERROR);
            return new ResultDTO<>(LibraryConstant.QUERY_ERROR, ResultDTO.FAIL);
        }
    }
}
