package com.self.library.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.self.library.constant.LibraryConstant;
import com.self.library.dao.BorrowDao;
import com.self.library.dto.PageDTO;
import com.self.library.entity.BorrowEntity;
import com.self.library.entity.BorrowExample;
import com.self.library.service.BorrowService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author Administrator
 * @Title:
 * @Description:
 * @Date 2021-05-15 10:38
 * @Version: 1.0
 */
@Service
@Slf4j
public class BorrowServiceImpl implements BorrowService
{
    @Autowired
    private BorrowDao borrowDao;

    @Override
    public Integer save(BorrowEntity entity)
    {
        Integer count = null;
        try
        {
            count = borrowDao.insertSelective(entity);
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.INSERT_ERROR, e);
        }

        return count;
    }

    @Override
    @Transactional
    public Integer saveList(List<BorrowEntity> entities)
    {
        Integer count = null;
        try
        {
            count = borrowDao.saveList(entities);
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.INSERT_ERROR, e);
        }
        return count;
    }

    @Override
    public PageInfo<BorrowEntity> page(PageDTO<BorrowEntity> page)
    {
        PageInfo<BorrowEntity> pageInfo = null;
        try
        {
            //没传赋予默认值
            if (page.getPageNum() == null)
            {
                page.setPageNum(1);
            }
            if (page.getPageSize() == null)
            {
                page.setPageSize(10);
            }
            if (page.getProperty() == null)
            {
                page.setProperty("id");
            }
            if (page.getOrder() == null)
            {
                page.setOrder("asc");
            }
            BorrowExample example = new BorrowExample();
            BorrowEntity entity = page.getEntity();
            if (entity != null)
            {
                String name = entity.getName();
                Integer sex = entity.getSex();
                Integer age = entity.getAge();
                String phone = entity.getPhone();
                String address = entity.getAddress();
                Integer bookId = entity.getBookId();
                Integer count = entity.getCount();
                BorrowExample.Criteria criteria = null;
                //查询条件
                if (StringUtils.isNotBlank(name) || sex != null || age != null || StringUtils.isNotBlank(phone) || StringUtils.isNotBlank(address) || bookId != null || count != null)
                {
                    criteria = example.createCriteria();
                }
                if (criteria != null)
                {
                    if (StringUtils.isNotBlank(name))
                    {
                        criteria.andNameLike(LibraryConstant.PERCENT + name + LibraryConstant.PERCENT);
                    }
                    if (sex != null)
                    {
                        criteria.andSexEqualTo(sex);
                    }
                    if (age != null)
                    {
                        criteria.andAgeEqualTo(age);
                    }
                    if (StringUtils.isNotBlank(phone))
                    {
                        criteria.andPhoneLike(LibraryConstant.PERCENT + phone + LibraryConstant.PERCENT);
                    }
                    if (StringUtils.isNotBlank(address))
                    {
                        criteria.andAddressLike(LibraryConstant.PERCENT + address + LibraryConstant.PERCENT);
                    }
                    if (bookId != null)
                    {
                        criteria.andBookIdEqualTo(bookId);
                    }
                    if (count != null)
                    {
                        criteria.andCountEqualTo(count);
                    }
                }
            }
            //排序
            example.setOrderByClause(page.getProperty() + StringUtils.SPACE + page.getOrder());
            //分页
            PageHelper.startPage(page.getPageNum(), page.getPageSize());
            List<BorrowEntity> list = borrowDao.selectByExample(example);
            pageInfo = new PageInfo<>(list);
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.QUERY_ERROR, e);
        }
        return pageInfo;
    }

    @Override
    public BorrowEntity findById(Integer id)
    {
        try
        {
            return borrowDao.selectByPrimaryKey(id);
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.QUERY_ERROR, e);
        }
        return null;
    }

    @Override
    public Integer delete(Integer id)
    {
        try
        {
            return borrowDao.deleteByPrimaryKey(id);
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.DELETE_ERROR, e);
        }
        return null;
    }

    @Override
    public Integer modify(BorrowEntity entity)
    {
        try
        {
            return borrowDao.updateByPrimaryKeySelective(entity);
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.MODIFY_ERROR, e);
        }
        return null;
    }

    @Override
    public List<BorrowEntity> findAll()
    {
        return borrowDao.selectByExample(new BorrowExample());
    }
}
