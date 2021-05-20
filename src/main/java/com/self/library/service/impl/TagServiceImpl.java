package com.self.library.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.self.library.constant.LibraryConstant;
import com.self.library.dao.BookDao;
import com.self.library.dao.TagDao;
import com.self.library.dto.PageDTO;
import com.self.library.dto.TagDTO;
import com.self.library.entity.BookEntity;
import com.self.library.entity.BookExample;
import com.self.library.entity.TagEntity;
import com.self.library.entity.TagExample;
import com.self.library.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    @Autowired
    private BookDao bookDao;

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
    public PageInfo<TagDTO> page(PageDTO<TagEntity> page)
    {
        PageInfo<TagDTO> pageInfo = null;
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
            PageInfo<TagEntity> tagPage = new PageInfo<>(list);
            pageInfo = new PageInfo<>();
            BeanUtils.copyProperties(tagPage, pageInfo);
            List<Integer> ids = null;
            if (CollectionUtils.isNotEmpty(list))
            {
                ids = list.stream().map(TagEntity::getId).distinct().collect(Collectors.toList());
            }
            List<BookEntity> books = null;
            if (CollectionUtils.isNotEmpty(ids))
            {
                BookExample bookExample = new BookExample();
                BookExample.Criteria criteria = bookExample.createCriteria();
                criteria.andTagIdIn(ids);
                books = bookDao.selectByExample(bookExample);
            }
            List<TagDTO> tagList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(books))
            {
                for (TagEntity tag : list)
                {
                    TagDTO tagDTO = new TagDTO();
                    BeanUtils.copyProperties(tag, tagDTO);
                    List<BookEntity> bookList = new ArrayList<>();
                    for (BookEntity book : books)
                    {
                        if (book.getTagId().equals(tag.getId()))
                        {
                            bookList.add(book);
                        }
                    }
                    tagDTO.setBooks(bookList);
                    tagList.add(tagDTO);
                }
            }
            pageInfo.setList(tagList);
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.QUERY_ERROR, e);
        }
        return pageInfo;
    }

    @Override
    public TagDTO findById(Integer id)
    {
        try
        {
            TagEntity tag = tagDao.selectByPrimaryKey(id);
            TagDTO tagDTO = new TagDTO();
            if (tag != null)
            {
                BeanUtils.copyProperties(tag, tagDTO);
                BookExample bookExample = new BookExample();
                BookExample.Criteria criteria = bookExample.createCriteria();
                criteria.andTagIdEqualTo(tag.getId());
                List<BookEntity> books = bookDao.selectByExample(bookExample);
                tagDTO.setBooks(books);
            }
            return tagDTO;
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
    public List<TagDTO> findAll()
    {
        try
        {
            List<TagEntity> list = tagDao.selectByExample(new TagExample());
            List<Integer> ids = null;
            if (CollectionUtils.isNotEmpty(list))
            {
                ids = list.stream().map(TagEntity::getId).distinct().collect(Collectors.toList());
            }
            List<BookEntity> books = null;
            if (CollectionUtils.isNotEmpty(ids))
            {
                BookExample bookExample = new BookExample();
                BookExample.Criteria criteria = bookExample.createCriteria();
                criteria.andTagIdIn(ids);
                books = bookDao.selectByExample(bookExample);
            }
            List<TagDTO> tagList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(books))
            {
                for (TagEntity tag : list)
                {
                    TagDTO tagDTO = new TagDTO();
                    BeanUtils.copyProperties(tag, tagDTO);
                    List<BookEntity> bookList = new ArrayList<>();
                    for (BookEntity book : books)
                    {
                        if (book.getTagId().equals(tag.getId()))
                        {
                            bookList.add(book);
                        }
                    }
                    tagDTO.setBooks(bookList);
                    tagList.add(tagDTO);
                }
            }
            return tagList;
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.QUERY_ERROR);
        }
        return null;
    }
}
