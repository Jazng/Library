package com.self.library.controller;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.pagehelper.PageInfo;
import com.self.library.constant.LibraryConstant;
import com.self.library.dto.*;
import com.self.library.entity.BorrowEntity;
import com.self.library.service.BorrowService;
import com.self.library.utils.JWTUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
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
                entity.setSerialNo(UUID.randomUUID().toString());
                Integer count = borrowService.save(entity);
                if (count != null && count > 0)
                {
                    return new ResultDTO<>(count);
                }
                else if (count != null && count == 0)
                {
                    return new ResultDTO<>(LibraryConstant.SAVE_COUNT_OVERFLOW, ResultDTO.FAIL);
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

    @PostMapping("/saveList")
    @ApiOperation("批量新增借阅接口")
    @ApiImplicitParam(name = "borrowDTO", value = "借阅实体集合", required = true)
    public ResultDTO<Integer> saveList(@RequestBody BorrowInDTO borrowInDTO, HttpServletRequest request)
    {
        try
        {
            if (borrowInDTO != null && CollectionUtils.isNotEmpty(borrowInDTO.getBooks()))
            {
                String serialNo = UUID.randomUUID().toString();
                List<BorrowEntity> list = borrowInDTO.getBooks().stream().map(book ->
                {
                    BorrowEntity entity = null;
                    if (book.getId() != null)
                    {
                        entity = new BorrowEntity();
                        BeanUtils.copyProperties(borrowInDTO, entity);
                        entity.setSerialNo(serialNo);
                        entity.setBookId(book.getId());
                        entity.setCount(book.getCount());
                        entity.setStatus(0);
                    }
                    return entity;
                }).filter(Objects::nonNull).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(list))
                {
                    //由于有拦截器加持，能走到这里的必定是token验证正确的
                    Pair<Boolean, DecodedJWT> pair = JWTUtils.verify(request.getHeader("Authorization"));
                    String username = pair.getRight().getClaim(LibraryConstant.USERNAME).asString();
                    list.forEach(entity -> entity.setCreateUser(username));
                    Integer count = borrowService.saveList(list);
                    if (count != null && count > 0)
                    {
                        return new ResultDTO<>(count);
                    }
                    else if (count != null && count == 0)
                    {
                        return new ResultDTO<>(LibraryConstant.SOME_SAVE_COUNT_OVERFLOW, ResultDTO.FAIL);
                    }
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
    public ResultDTO<PageInfo<BorrowOutDTO>> page(@RequestBody PageDTO<BorrowInDTO> page)
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
    public ResultDTO<BorrowOutDTO> findById(@RequestParam("id") Integer id)
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

    @GetMapping("/deleteBySerialNo")
    @ApiOperation("根据借阅号删除借阅接口")
    @ApiImplicitParam(name = "serialNo", value = "借阅号", required = true, dataType = "string")
    public ResultDTO<Integer> deleteBySerialNo(@RequestParam("serialNo") String serialNo)
    {
        try
        {
            if (serialNo != null)
            {
                Integer count = borrowService.deleteBySerialNo(serialNo);
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
    public ResultDTO<Boolean> modify(@RequestBody BorrowEntity entity, HttpServletRequest request)
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
                Boolean result = borrowService.modify(entity);
                if (result)
                {
                    return new ResultDTO<>(result);
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
    public ResultDTO<ArrayList<BorrowOutDTO>> findAll()
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

    @GetMapping("/findBySerialNo")
    @ApiOperation("根据借阅号查询借阅接口")
    @ApiImplicitParam(name = "serialNo", value = "借阅号", required = true, dataType = "integer", defaultValue = "1", example = "2")
    public ResultDTO<ArrayList<BorrowOutDTO>> findBySerialNo(@RequestParam("serialNo") String serialNo)
    {
        try
        {
            if (serialNo != null)
            {
                return new ResultDTO<>(borrowService.findBySerialNo(serialNo));
            }
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.QUERY_ERROR, e);
            return new ResultDTO<>(LibraryConstant.QUERY_ERROR, ResultDTO.FAIL);
        }
        return new ResultDTO<>(LibraryConstant.QUERY_FAIL, ResultDTO.FAIL);
    }

    @GetMapping("/return")
    @ApiOperation("归还接口")
    @ApiImplicitParam(name = "id", value = "归还实体", required = true)
    public ResultDTO<Boolean> returnOne(@RequestParam("id") Integer id, HttpServletRequest request)
    {
        try
        {
            if (id != null && id > 0)
            {
                String token = request.getHeader("Authorization");
                Pair<Boolean, DecodedJWT> pair = JWTUtils.verify(token);
                DecodedJWT decoded = pair.getRight();
                Claim claim = decoded.getClaim(LibraryConstant.USERNAME);
                String username = claim.asString();
                BorrowEntity borrow = new BorrowEntity();
                borrow.setId(id);
                borrow.setStatus(1);
                //实际归还日期
                borrow.setActualDate(new Date());
                borrow.setModifyUser(username);
                Boolean success = borrowService.returnOne(borrow);
                if (success)
                {
                    return new ResultDTO<>(success);
                }
            }
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.RETURN_ERROR, e);
            return new ResultDTO<>(LibraryConstant.RETURN_ERROR, ResultDTO.FAIL);
        }
        return new ResultDTO<>(LibraryConstant.RETURN_FAIL, ResultDTO.FAIL);
    }

    @GetMapping("/returnAll")
    @ApiOperation("归还所有接口")
    @ApiImplicitParam(name = "serialNo", value = "归还实体", required = true)
    public ResultDTO<Boolean> returnBook(@RequestParam("serialNo") String serialNo, HttpServletRequest request)
    {
        try
        {
            if (StringUtils.isNotBlank(serialNo))
            {
                String token = request.getHeader("Authorization");
                Pair<Boolean, DecodedJWT> pair = JWTUtils.verify(token);
                DecodedJWT decoded = pair.getRight();
                Claim claim = decoded.getClaim(LibraryConstant.USERNAME);
                String username = claim.asString();
                BorrowEntity borrow = new BorrowEntity();
                borrow.setSerialNo(serialNo);
                borrow.setStatus(1);
                borrow.setActualDate(new Date());
                borrow.setModifyUser(username);
                Boolean success = borrowService.returnAll(borrow);
                if (success)
                {
                    return new ResultDTO<>(success);
                }
            }
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.RETURN_ERROR, e);
            return new ResultDTO<>(LibraryConstant.RETURN_ERROR, ResultDTO.FAIL);
        }
        return new ResultDTO<>(LibraryConstant.RETURN_FAIL, ResultDTO.FAIL);
    }

    @GetMapping("/seven")
    @ApiOperation("最近7天的借阅信息")
    public ResultDTO<SevenDTO<BorrowOutDTO>> seven()
    {
        try
        {
            List<SevenDTO<BorrowEntity>> queryList = new ArrayList<>(8);
            //时区
            ZoneId zoneId = ZoneId.systemDefault();
            //获取7天的每天的区间
            for (int i = 0; i < LibraryConstant.SEVEN; i++)
            {
                //当前日期，不带时间
                LocalDate now = LocalDate.now();
                SevenDTO<BorrowEntity> sevenDTO = new SevenDTO<>();
                //start
                LocalDate ones = now.minusDays(i);
                ZonedDateTime onesZoned = ones.atStartOfDay(zoneId);
                Instant onesIns = onesZoned.toInstant();
                Date oneStart = Date.from(onesIns);
                //end
                LocalDate onee = ones.plusDays(1);
                ZonedDateTime oneeZoned = onee.atStartOfDay(zoneId);
                Instant oneeIns = oneeZoned.toInstant();
                Date oneEnd = Date.from(oneeIns);
                Pair<Date, Date> pair = Pair.of(oneStart, oneEnd);
                sevenDTO.setPair(pair);
                sevenDTO.setDate(ones);
                queryList.add(sevenDTO);
            }
            if (CollectionUtils.isNotEmpty(queryList))
            {
                return new ResultDTO<>(borrowService.seven(queryList));
            }
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.QUERY_ERROR, e);
            return new ResultDTO<>(LibraryConstant.QUERY_ERROR, ResultDTO.FAIL);
        }
        return new ResultDTO<>(LibraryConstant.QUERY_FAIL, ResultDTO.FAIL);
    }
}
