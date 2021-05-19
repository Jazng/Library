package com.self.library.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.self.library.constant.LibraryConstant;
import com.self.library.dao.BookDao;
import com.self.library.dao.BorrowDao;
import com.self.library.dao.PublishDao;
import com.self.library.dao.TagDao;
import com.self.library.dto.BookDTO;
import com.self.library.dto.PageDTO;
import com.self.library.dto.SevenDTO;
import com.self.library.entity.*;
import com.self.library.service.BookService;
import com.zeng.extension.functional.FunctionalUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
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
    private BorrowDao borrowDao;

    @Autowired
    private Executor executor;

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
                //线程1
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
                //线程2
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
                    entity.setLend(0);
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
                //线程1
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
                //线程2
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
                    insert.setLend(0);
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
    public PageInfo<BookDTO> page(PageDTO<BookEntity> page)
    {
        PageInfo<BookDTO> pageInfo = null;
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
            PageInfo<BookEntity> bookPage = new PageInfo<>(list);
            //标签线程
            CompletableFuture<List<TagEntity>> tagFuture = CompletableFuture.supplyAsync(() ->
            {
                List<TagEntity> tags = null;
                List<Integer> tagIds = list.stream().map(BookEntity::getTagId).distinct().collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(tagIds))
                {
                    TagExample tagExample = new TagExample();
                    TagExample.Criteria criteria = tagExample.createCriteria();
                    criteria.andIdIn(tagIds);
                    tags = tagDao.selectByExample(tagExample);
                }
                return tags;
            }, executor);
            //出版社线程
            CompletableFuture<List<PublishEntity>> publishFuture = CompletableFuture.supplyAsync(() ->
            {
                List<PublishEntity> publishes = null;
                List<Integer> publishIds = list.stream().map(BookEntity::getPublishId).distinct().collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(publishIds))
                {
                    PublishExample publishExample = new PublishExample();
                    PublishExample.Criteria criteria = publishExample.createCriteria();
                    criteria.andIdIn(publishIds);
                    publishes = publishDao.selectByExample(publishExample);
                }
                return publishes;
            }, executor);
            //异步执行
            CompletableFuture.allOf(tagFuture, publishFuture).join();
            //标签
            List<TagEntity> tags = tagFuture.get();
            //出版社
            List<PublishEntity> publishes = publishFuture.get();
            List<BookDTO> bookList = list.stream().map(book ->
            {
                //复制属性
                BookDTO bookDTO = new BookDTO();
                BeanUtils.copyProperties(book, bookDTO);
                //找到相应的tag
                if (CollectionUtils.isNotEmpty(tags))
                {
                    for (TagEntity tag : tags)
                    {
                        if (book.getTagId().equals(tag.getId()))
                        {
                            bookDTO.setTag(tag);
                            break;
                        }
                    }
                }
                //找到相应publish
                if (CollectionUtils.isNotEmpty(publishes))
                {
                    for (PublishEntity publish : publishes)
                    {
                        if (book.getPublishId().equals(publish.getId()))
                        {
                            bookDTO.setPublish(publish);
                            break;
                        }
                    }
                }
                //返回新的对象
                return bookDTO;
            }).collect(Collectors.toList());
            pageInfo = new PageInfo<>();
            BeanUtils.copyProperties(bookPage, pageInfo);
            pageInfo.setList(bookList);
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.QUERY_ERROR, e);
        }
        return pageInfo;
    }

    @Override
    public BookDTO findById(Integer id)
    {
        try
        {
            BookEntity book = bookDao.selectByPrimaryKey(id);
            BookDTO bookDTO = null;
            if (book != null)
            {
                bookDTO = new BookDTO();
                BeanUtils.copyProperties(book, bookDTO);
                //标签
                CompletableFuture<TagEntity> tagFuture = CompletableFuture.supplyAsync(() -> tagDao.selectByPrimaryKey(book.getTagId()), executor);
                //出版社
                CompletableFuture<PublishEntity> publishFuture = CompletableFuture.supplyAsync(() -> publishDao.selectByPrimaryKey(book.getPublishId()), executor);
                //异步执行
                CompletableFuture.allOf(tagFuture, publishFuture).join();
                TagEntity tag = tagFuture.get();
                PublishEntity publish = publishFuture.get();
                bookDTO.setTag(tag);
                bookDTO.setPublish(publish);
            }

            return bookDTO;
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
    public List<BookDTO> findAll()
    {
        List<BookDTO> bookList = null;
        try
        {
            List<BookEntity> list = bookDao.selectByExample(new BookExample());
            //标签线程
            CompletableFuture<List<TagEntity>> tagFuture = CompletableFuture.supplyAsync(() ->
            {
                List<TagEntity> tags = null;
                List<Integer> tagIds = list.stream().map(BookEntity::getTagId).distinct().collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(tagIds))
                {
                    TagExample tagExample = new TagExample();
                    TagExample.Criteria criteria = tagExample.createCriteria();
                    criteria.andIdIn(tagIds);
                    tags = tagDao.selectByExample(tagExample);
                }
                return tags;
            }, executor);
            //出版社线程
            CompletableFuture<List<PublishEntity>> publishFuture = CompletableFuture.supplyAsync(() ->
            {
                List<PublishEntity> publishes = null;
                List<Integer> publishIds = list.stream().map(BookEntity::getPublishId).distinct().collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(publishIds))
                {
                    PublishExample publishExample = new PublishExample();
                    PublishExample.Criteria criteria = publishExample.createCriteria();
                    criteria.andIdIn(publishIds);
                    publishes = publishDao.selectByExample(publishExample);
                }
                return publishes;
            }, executor);
            //异步执行
            CompletableFuture.allOf(tagFuture, publishFuture).join();
            //标签
            List<TagEntity> tags = tagFuture.get();
            //出版社
            List<PublishEntity> publishes = publishFuture.get();
            bookList = list.stream().map(book ->
            {
                //复制属性
                BookDTO bookDTO = new BookDTO();
                BeanUtils.copyProperties(book, bookDTO);
                //找到相应的tag
                if (CollectionUtils.isNotEmpty(tags))
                {
                    for (TagEntity tag : tags)
                    {
                        if (book.getTagId().equals(tag.getId()))
                        {
                            bookDTO.setTag(tag);
                            break;
                        }
                    }
                }
                //找到相应publish
                if (CollectionUtils.isNotEmpty(publishes))
                {
                    for (PublishEntity publish : publishes)
                    {
                        if (book.getPublishId().equals(publish.getId()))
                        {
                            bookDTO.setPublish(publish);
                            break;
                        }
                    }
                }
                //返回新的对象
                return bookDTO;
            }).collect(Collectors.toList());
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.QUERY_ERROR);
        }
        return bookList;
    }

    @Override
    public List<SevenDTO<BookEntity>> seven(List<SevenDTO<BookEntity>> queryList)
    {
        List<SevenDTO<BookEntity>> list = new ArrayList<>(8);

        try
        {
            //线程集合，所有线程放入一个集合，一次性交给线程池异步执行
            List<CompletableFuture<SevenDTO<BookEntity>>> futureList = new ArrayList<>(8);
            queryList.forEach(seven ->
            {
                //线程：FunctionalUtils为学长所创依赖
                CompletableFuture<SevenDTO<BookEntity>> future = CompletableFuture.supplyAsync(FunctionalUtils.supplier(() ->
                {
                    Pair<Date, Date> pair = seven.getPair();
                    //子线程1
                    CompletableFuture<Integer> bookFuture = CompletableFuture.supplyAsync(() ->
                    {
                        //某天书籍总数
                        BookExample example = new BookExample();
                        BookExample.Criteria criteria = example.createCriteria();
                        criteria.andCreateDateLessThanOrEqualTo(pair.getLeft());
                        List<BookEntity> books = bookDao.selectByExample(example);

                        //计算书籍总数
                        Integer bookCount = 0;
                        if (CollectionUtils.isNotEmpty(books))
                        {
                            for (BookEntity book : books)
                            {
                                bookCount += book.getCount();
                            }
                        }
                        return bookCount;
                    }, executor);
                    //子线程2
                    CompletableFuture<Integer> borrowFuture = CompletableFuture.supplyAsync(() ->
                    {
                        BorrowExample example = new BorrowExample();
                        BorrowExample.Criteria criteria = example.createCriteria();
                        criteria.andCreateDateLessThanOrEqualTo(pair.getLeft());
                        List<BorrowEntity> borrows = borrowDao.selectByExample(example);
                        Integer borrowCount = 0;
                        if (CollectionUtils.isNotEmpty(borrows))
                        {
                            for (BorrowEntity borrow : borrows)
                            {
                                borrowCount += borrow.getCount();
                            }
                        }
                        return borrowCount;
                    }, executor);
                    CompletableFuture.allOf(bookFuture, borrowFuture).join();
                    Integer bookCount = bookFuture.get();
                    Integer borrowCount = borrowFuture.get();
                    SevenDTO<BookEntity> sevenDTO = new SevenDTO<>();
                    sevenDTO.setDate(seven.getDate());
                    sevenDTO.setCount(bookCount - borrowCount);
                    return sevenDTO;
                }), executor);
                futureList.add(future);
            });
            if (CollectionUtils.isNotEmpty(futureList))
            {
                CompletableFuture[] futures = new CompletableFuture[futureList.size()];
                //异步执行
                CompletableFuture.allOf(futureList.toArray(futures)).join();
                //循环获取每个线程的结果
                for (CompletableFuture<SevenDTO<BookEntity>> future : futureList)
                {
                    SevenDTO<BookEntity> sevenDTO = future.get();
                    if (sevenDTO != null)
                    {
                        list.add(sevenDTO);
                    }
                }
            }
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.QUERY_ERROR, e);
        }
        return list;
    }
}
