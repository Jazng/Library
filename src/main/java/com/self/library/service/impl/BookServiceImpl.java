package com.self.library.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.self.library.constant.LibraryConstant;
import com.self.library.dao.BookDao;
import com.self.library.dao.PublishDao;
import com.self.library.dao.TagDao;
import com.self.library.dto.PageDTO;
import com.self.library.entity.*;
import com.self.library.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @Author Administrator
 * @Title:
 * @Description:
 * @Date 2021-05-09 20:33
 * @Version: 1.0
 */
@Service
@Slf4j
public class BookServiceImpl implements BookService
{
    @Autowired
    private BookDao bookDao;

    @Autowired
    private TagDao tagDao;

    @Autowired
    private PublishDao publishDao;

    @Autowired
    private ThreadPoolTaskExecutor executor;

    @Override
    public Integer save(BookEntity entity)
    {
        List<CompletableFuture> futureList = new ArrayList<>();
        Integer count = null;
        try
        {
            String name = entity.getBookName();
            Boolean show = entity.getShow();
            Integer tagId = entity.getTagId();
            Integer publishId = entity.getPublishId();
            if (show == null)
            {
                entity.setShow(Boolean.FALSE);
            }
            if (tagId == null)
            {
                CompletableFuture<TagEntity> tagFirstFuture = CompletableFuture.supplyAsync(() ->
                {
                    TagEntity tag = null;
                    TagExample example = new TagExample();
                    PageHelper.startPage(1, 1);
                    List<TagEntity> tagList = tagDao.selectByExample(example);
                    if (CollectionUtils.isNotEmpty(tagList))
                    {
                        tag = tagList.get(0);
                    }
                    return tag;
                }, executor);
                futureList.add(tagFirstFuture);
            }
            if (publishId == null)
            {
                CompletableFuture<PublishEntity> publishFirstFuture = CompletableFuture.supplyAsync(() ->
                {
                    PublishEntity publish = null;
                    PublishExample example = new PublishExample();
                    PageHelper.startPage(1, 1);
                    List<PublishEntity> publishList = publishDao.selectByExample(example);
                    if (CollectionUtils.isNotEmpty(publishList))
                    {
                        publish = publishList.get(0);
                    }
                    return publish;
                }, executor);
                futureList.add(publishFirstFuture);
            }
            if (StringUtils.isNotBlank(name))
            {
                BookExample example = new BookExample();
                BookExample.Criteria criteria = example.createCriteria();
                criteria.andBookNameEqualTo(name);
                List<BookEntity> findList = bookDao.selectByExample(example);
                if (CollectionUtils.isEmpty(findList))
                {
                    TagEntity tag = null;
                    PublishEntity publish = null;
                    if (CollectionUtils.isNotEmpty(futureList))
                    {
                        CompletableFuture[] futures = new CompletableFuture[futureList.size()];
                        CompletableFuture.allOf(futureList.toArray(futures)).join();
                        for (CompletableFuture future : futureList)
                        {
                            Object tp = future.get();
                            if (tp != null)
                            {
                                if (tp instanceof TagEntity)
                                {
                                    tag = (TagEntity) tp;
                                }
                                else if (tp instanceof PublishEntity)
                                {
                                    publish = (PublishEntity) tp;
                                }
                            }
                        }
                    }
                    if (tag != null)
                    {
                        entity.setTagId(tag.getId());
                    }
                    if (publish != null)
                    {
                        entity.setPublishId(publish.getId());
                    }
                    count = bookDao.insertSelective(entity);
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
    public Integer saveList(List<BookEntity> entities)
    {
        Integer count = null;
        try
        {
            List<String> nameList = entities.stream().map(BookEntity::getBookName).collect(Collectors.toList());
            BookExample example = new BookExample();
            BookExample.Criteria criteria = example.createCriteria();
            criteria.andBookNameIn(nameList);
            List<BookEntity> findList = bookDao.selectByExample(example);
            List<BookEntity> insertList = null;
            if (CollectionUtils.isNotEmpty(findList))
            {
                List<String> findNames = findList.stream().map(BookEntity::getBookName).collect(Collectors.toList());
                insertList = entities.stream().filter(entity -> !findNames.contains(entity.getBookName())).collect(Collectors.toList());
            }
            else
            {
                insertList = entities;
            }

            if (CollectionUtils.isNotEmpty(insertList))
            {
                //线程
                CompletableFuture<TagEntity> tagFirstFuture = CompletableFuture.supplyAsync(() ->
                {
                    TagEntity tag = null;
                    TagExample tagExample = new TagExample();
                    PageHelper.startPage(1, 1);
                    List<TagEntity> tagList = tagDao.selectByExample(tagExample);
                    if (CollectionUtils.isNotEmpty(tagList))
                    {
                        tag = tagList.get(0);
                    }
                    return tag;
                }, executor);
                //线程
                CompletableFuture<PublishEntity> publishFirstFuture = CompletableFuture.supplyAsync(() ->
                {
                    PublishEntity publish = null;
                    PublishExample publishExample = new PublishExample();
                    PageHelper.startPage(1, 1);
                    List<PublishEntity> publishList = publishDao.selectByExample(publishExample);
                    if (CollectionUtils.isNotEmpty(publishList))
                    {
                        publish = publishList.get(0);
                    }
                    return publish;
                }, executor);
                CompletableFuture.allOf(tagFirstFuture, publishFirstFuture).join();
                TagEntity tag = tagFirstFuture.get();
                PublishEntity publish = publishFirstFuture.get();
                insertList.forEach(insert ->
                {
                    if (insert.getTagId() == null && tag != null)
                    {
                        insert.setTagId(tag.getId());
                    }
                    if (insert.getPublishId() == null && publish != null)
                    {
                        insert.setPublishId(publish.getId());
                    }
                });
                count = bookDao.saveBooks(insertList);
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
    public PageInfo<BookEntity> page(PageDTO<BookEntity> page)
    {
        PageInfo<BookEntity> pageInfo = null;
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
            BookExample example = new BookExample();
            BookEntity entity = page.getEntity();
            if (entity != null)
            {
                String name = entity.getBookName();
                String bookNo = entity.getBookNo();
                BigDecimal price = entity.getPrice();
                Boolean show = entity.getShow();
                Integer count = entity.getCount();
                Integer tagId = entity.getTagId();
                Integer publishId = entity.getPublishId();
                String author = entity.getAuthor();
                Date publishDate = entity.getPublishDate();
                BookExample.Criteria criteria = null;
                //查询条件
                if (StringUtils.isNotBlank(name) || StringUtils.isNotBlank(bookNo) || show != null || count != null || tagId != null || publishId != null || StringUtils.isNotBlank(author) || publishDate != null)
                {
                    criteria = example.createCriteria();
                }
                if (criteria != null)
                {
                    if (StringUtils.isNotBlank(name))
                    {
                        criteria.andBookNameLike(LibraryConstant.PERCENT + name + LibraryConstant.PERCENT);
                    }
                    if (StringUtils.isNotBlank(bookNo))
                    {
                        criteria.andBookNoLike(LibraryConstant.PERCENT + bookNo + LibraryConstant.PERCENT);
                    }
                    if (price != null)
                    {
                        criteria.andPriceEqualTo(price);
                    }
                    if (show != null)
                    {
                        criteria.andShowEqualTo(show);
                    }
                    if (count != null)
                    {
                        criteria.andCountEqualTo(count);
                    }
                    if (tagId != null)
                    {
                        criteria.andTagIdEqualTo(tagId);
                    }
                    if (publishId != null)
                    {
                        criteria.andAuthorLike(LibraryConstant.PERCENT + author + LibraryConstant.PERCENT);
                    }
                    if (publishDate != null)
                    {
                        criteria.andPublishDateEqualTo(publishDate);
                    }
                }
            }
            //排序
            example.setOrderByClause(page.getProperty() + StringUtils.SPACE + page.getOrder());
            //分页
            PageHelper.startPage(page.getPageNum(), page.getPageSize());
            List<BookEntity> list = bookDao.selectByExample(example);
            pageInfo = new PageInfo<>(list);
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.QUERY_ERROR, e);
        }
        return pageInfo;
    }

    @Override
    public BookEntity findById(Integer id)
    {
        try
        {
            return bookDao.selectByPrimaryKey(id);
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
            return bookDao.deleteByPrimaryKey(id);
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.DELETE_ERROR, e);
        }
        return null;
    }

    @Override
    public Integer modify(BookEntity entity)
    {
        try
        {
            return bookDao.updateByPrimaryKeySelective(entity);
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.MODIFY_ERROR, e);
        }
        return null;
    }

    @Override
    public List<BookEntity> findAll()
    {
        return bookDao.selectByExample(new BookExample());
    }
}
