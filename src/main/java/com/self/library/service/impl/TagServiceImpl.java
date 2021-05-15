package com.self.library.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.self.library.constant.LibraryConstant;
import com.self.library.dao.TagDao;
import com.self.library.dto.PageDTO;
import com.self.library.entity.TagEntity;
import com.self.library.entity.TagExample;
import com.self.library.service.TagService;
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
 * @Date 2021-05-07 21:32
 * @Version: 1.0
 */
@Service
@Slf4j
public class TagServiceImpl implements TagService
{
    @Autowired
    private TagDao tagDao;

    @Override
    public Integer save(TagEntity entity)
    {
        Integer count = null;
        try
        {
            String name = entity.getTagName();
            if (StringUtils.isNotBlank(name))
            {
                TagExample example = new TagExample();
                TagExample.Criteria criteria = example.createCriteria();
                criteria.andTagNameEqualTo(name);
                List<TagEntity> findList = tagDao.selectByExample(example);
                if (CollectionUtils.isEmpty(findList))
                {
                    count = tagDao.insertSelective(entity);
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
    public Integer saveList(List<TagEntity> entities)
    {
        Integer count = null;
        try
        {
            List<String> nameList = entities.stream().map(TagEntity::getTagName).collect(Collectors.toList());
            TagExample example = new TagExample();
            TagExample.Criteria criteria = example.createCriteria();
            criteria.andTagNameIn(nameList);
            List<TagEntity> tagList = tagDao.selectByExample(example);
            List<TagEntity> insertList = null;
            if (CollectionUtils.isNotEmpty(tagList))
            {
                insertList = entities.stream().filter(entity -> !tagList.contains(entity)).collect(Collectors.toList());
            }
            else
            {
                insertList = entities;
            }

            if (CollectionUtils.isNotEmpty(insertList))
            {
                count = tagDao.saveTags(insertList);
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
    public PageInfo<TagEntity> page(PageDTO<TagEntity> page)
    {
        PageInfo<TagEntity> pageInfo = null;
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
            TagExample example = new TagExample();
            TagEntity entity = page.getEntity();
            if (entity != null)
            {
                String name = entity.getTagName();
                if (StringUtils.isNotBlank(name))
                {
                    TagExample.Criteria criteria = example.createCriteria();
                    //模糊查询条件
                    criteria.andTagNameLike(LibraryConstant.PERCENT + name + LibraryConstant.PERCENT);
                }
            }
            //排序
            example.setOrderByClause(page.getProperty() + StringUtils.SPACE + page.getOrder());
            //分页
            PageHelper.startPage(page.getPageNum(), page.getPageSize());
            List<TagEntity> list = tagDao.selectByExample(example);
            pageInfo = new PageInfo<>(list);
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.QUERY_ERROR, e);
        }
        return pageInfo;
    }

    @Override
    public TagEntity findById(Integer id)
    {
        try
        {
            return tagDao.selectByPrimaryKey(id);
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
            return tagDao.deleteByPrimaryKey(id);
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.DELETE_ERROR, e);
        }
        return null;
    }

    @Override
    public Integer modify(TagEntity entity)
    {
        try
        {
            return tagDao.updateByPrimaryKeySelective(entity);
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.MODIFY_ERROR, e);
        }
        return null;
    }

    @Override
    public List<TagEntity> findAll()
    {
        return tagDao.selectByExample(new TagExample());
    }
}
