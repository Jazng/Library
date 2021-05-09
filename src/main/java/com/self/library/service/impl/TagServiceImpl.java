package com.self.library.service.impl;

import com.github.pagehelper.PageHelper;
import com.self.library.constant.LibraryConstant;
import com.self.library.dao.TagDao;
import com.self.library.dto.TagQueryDTO;
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
    public Integer saveTag(TagEntity tag)
    {
        Integer count = null;
        try
        {
            String tagName = tag.getTagName();
            if (StringUtils.isNotBlank(tagName))
            {
                TagExample example = new TagExample();
                TagExample.Criteria criteria = example.createCriteria();
                criteria.andTagNameEqualTo(tagName);
                List<TagEntity> tagList = tagDao.selectByExample(example);
                if (CollectionUtils.isEmpty(tagList))
                {
                    count = tagDao.insertSelective(tag);
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
    public Integer saveTags(List<TagEntity> tags)
    {
        Integer count = null;
        try
        {
            List<String> tagNameList = tags.stream().map(TagEntity::getTagName).collect(Collectors.toList());
            TagExample example = new TagExample();
            TagExample.Criteria criteria = example.createCriteria();
            criteria.andTagNameIn(tagNameList);
            List<TagEntity> tagList = tagDao.selectByExample(example);
            List<TagEntity> insertList = null;
            if (CollectionUtils.isNotEmpty(tagList))
            {
                insertList = tags.stream().filter(tag -> !tagList.contains(tag)).collect(Collectors.toList());
            }
            else
            {
                insertList = tags;
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
    public List<TagEntity> pageTags(TagQueryDTO tagQuery)
    {
        List<TagEntity> tagList = null;
        try
        {
            //没传赋予默认值
            if (tagQuery.getPageNum() == null)
            {
                tagQuery.setPageNum(1);
            }
            if (tagQuery.getPageSize() == null)
            {
                tagQuery.setPageSize(10);
            }
            if (tagQuery.getProperty() == null)
            {
                tagQuery.setProperty("id");
            }
            if (tagQuery.getOrder() == null)
            {
                tagQuery.setOrder("asc");
            }
            TagExample example = new TagExample();
            TagEntity tag = tagQuery.getTag();
            if (tag != null)
            {
                String tagName = tag.getTagName();
                if (StringUtils.isNotBlank(tagName))
                {
                    TagExample.Criteria criteria = example.createCriteria();
                    //模糊查询条件
                    criteria.andTagNameLike(LibraryConstant.PERCENT + tagName + LibraryConstant.PERCENT);
                }
            }
            //排序
            example.setOrderByClause(tagQuery.getProperty() + StringUtils.SPACE + tagQuery.getOrder());
            //分页
            PageHelper.startPage(tagQuery.getPageNum(), tagQuery.getPageSize());
            tagList = tagDao.selectByExample(example);
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.QUERY_ERROR, e);
        }
        return tagList;
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
    public Integer deleteTag(Integer id)
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
    public Integer modify(TagEntity tag)
    {
        try
        {
            return tagDao.updateByPrimaryKeySelective(tag);
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
