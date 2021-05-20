package com.self.library.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.self.library.constant.LibraryConstant;
import com.self.library.dao.BookDao;
import com.self.library.dao.PublishDao;
import com.self.library.dto.PageDTO;
import com.self.library.entity.BookExample;
import com.self.library.entity.PublishEntity;
import com.self.library.entity.PublishExample;
import com.self.library.service.PublishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Administrator
 * @Title:
 * @Description:
 * @Date 2021-05-09 12:25
 * @Version: 1.0
 */
@Service
@Slf4j
public class PublishServiceImpl implements PublishService
{
    @Autowired
    private PublishDao publishDao;

    @Autowired
    private BookDao bookDao;

    @Override
    public Integer save(PublishEntity entity)
    {
        Integer count = null;
        try
        {
            String name = entity.getPublishName();
            if (StringUtils.isNotBlank(name))
            {
                PublishExample example = new PublishExample();
                PublishExample.Criteria criteria = example.createCriteria();
                criteria.andPublishNameEqualTo(name);
                List<PublishEntity> findList = publishDao.selectByExample(example);
                if (CollectionUtils.isEmpty(findList))
                {
                    count = publishDao.insertSelective(entity);
                }
            }
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.INSERT_ERROR, e);
        }

        return count;
    }

    @Override
    @Transactional
    public Integer saveList(List<PublishEntity> entities)
    {
        Integer count = null;
        try
        {
            List<String> nameList = entities.stream().map(PublishEntity::getPublishName).collect(Collectors.toList());
            PublishExample example = new PublishExample();
            PublishExample.Criteria criteria = example.createCriteria();
            criteria.andPublishNameIn(nameList);
            List<PublishEntity> findList = publishDao.selectByExample(example);
            List<PublishEntity> insertList = null;
            if (CollectionUtils.isNotEmpty(findList))
            {
                insertList = entities.stream().filter(entity -> !findList.contains(entity)).collect(Collectors.toList());
            }
            else
            {
                insertList = entities;
            }

            if (CollectionUtils.isNotEmpty(insertList))
            {
                count = publishDao.savePublishes(insertList);
            }
            else
            {
                count = 0;
            }
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.INSERT_ERROR, e);
        }
        return count;
    }

    @Override
    public PageInfo<PublishEntity> page(PageDTO<PublishEntity> page)
    {
        PageInfo<PublishEntity> pageInfo = null;
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
            PublishExample example = new PublishExample();
            PublishEntity entity = page.getEntity();
            if (entity != null)
            {
                String name = entity.getPublishName();
                if (StringUtils.isNotBlank(name))
                {
                    PublishExample.Criteria criteria = example.createCriteria();
                    //模糊查询条件
                    criteria.andPublishNameLike(LibraryConstant.PERCENT + name + LibraryConstant.PERCENT);
                }
            }
            //排序
            example.setOrderByClause(page.getProperty() + StringUtils.SPACE + page.getOrder());
            //分页
            PageHelper.startPage(page.getPageNum(), page.getPageSize());
            List<PublishEntity> list = publishDao.selectByExample(example);
            pageInfo = new PageInfo<>(list);
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.QUERY_ERROR, e);
        }
        return pageInfo;
    }

    @Override
    public PublishEntity findById(Integer id)
    {
        try
        {
            return publishDao.selectByPrimaryKey(id);
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
            BookExample example = new BookExample();
            BookExample.Criteria criteria = example.createCriteria();
            criteria.andPublishIdEqualTo(id);
            Long count = bookDao.countByExample(example);
            return (count != null && count > 0) ? -1 : publishDao.deleteByPrimaryKey(id);
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.DELETE_ERROR, e);
        }
        return null;
    }

    @Override
    public Integer modify(PublishEntity entity)
    {
        try
        {
            return publishDao.updateByPrimaryKeySelective(entity);
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.MODIFY_ERROR, e);
        }
        return null;
    }

    @Override
    public List<PublishEntity> findAll()
    {
        return publishDao.selectByExample(new PublishExample());
    }
}
