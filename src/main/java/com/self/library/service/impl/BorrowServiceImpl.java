package com.self.library.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.self.library.constant.LibraryConstant;
import com.self.library.dao.BookDao;
import com.self.library.dao.BorrowDao;
import com.self.library.dto.BorrowInDTO;
import com.self.library.dto.BorrowOutDTO;
import com.self.library.dto.PageDTO;
import com.self.library.dto.SevenDTO;
import com.self.library.entity.BookEntity;
import com.self.library.entity.BookExample;
import com.self.library.entity.BorrowEntity;
import com.self.library.entity.BorrowExample;
import com.self.library.service.BorrowService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

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

    @Autowired
    private BookDao bookDao;

    @Autowired
    private Executor executor;

    @Override
    @Transactional
    public Integer save(BorrowEntity entity)
    {
        Integer count = null;
        try
        {
            Integer bookId = entity.getBookId();
            Integer borrowDay = entity.getBorrowDay();
            if (borrowDay == null || borrowDay <= 0)
            {
                entity.setBorrowDay(1);
                borrowDay = 1;
            }
            if (bookId != null && bookId > 0)
            {
                BookEntity bookEntity = bookDao.selectByPrimaryKey(bookId);
                Integer existCount = bookEntity.getCount();
                Integer existLend = bookEntity.getLend();
                if (existLend < existCount && entity.getCount() <= (existCount - existLend))
                {
                    //设置归还日期
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime returnDate = now.plusDays(borrowDay);
                    ZoneId zoneId = ZoneId.systemDefault();
                    ZonedDateTime zonedDateTime = returnDate.atZone(zoneId);
                    Instant instant = zonedDateTime.toInstant();
                    entity.setReturnDate(Date.from(instant));
                    entity.setStatus(0);
                    //需要更新的书籍
                    BookEntity book = new BookEntity();
                    book.setId(bookId);
                    book.setCount(entity.getCount());
                    book.setModifyUser(entity.getCreateUser());
                    //保存借阅
                    CompletableFuture<Integer> saveFuture = CompletableFuture.supplyAsync(() -> borrowDao.insertSelective(entity), executor);
                    //更新借出
                    CompletableFuture<Integer> updateFuture = CompletableFuture.supplyAsync(() -> bookDao.updateCount(book), executor);
                    CompletableFuture.allOf(saveFuture, updateFuture).join();
                    Integer save = saveFuture.get();
                    Integer update = updateFuture.get();
                    count = save.equals(update) ? save : null;
                }
                else
                {
                    count = 0;
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
    public Integer saveList(List<BorrowEntity> entities)
    {
        Integer count = null;
        try
        {
            List<Integer> bookIds = entities.stream().map(BorrowEntity::getBookId).distinct().collect(Collectors.toList());
            List<BorrowEntity> saveList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(bookIds))
            {
                BookExample example = new BookExample();
                BookExample.Criteria criteria = example.createCriteria();
                criteria.andIdIn(bookIds);
                List<BookEntity> books = bookDao.selectByExample(example);
                if (CollectionUtils.isNotEmpty(books))
                {
                    books.forEach(book ->
                    {
                        Integer existCount = book.getCount();
                        Integer existLend = book.getLend();
                        if (existCount > existLend)
                        {
                            for (BorrowEntity borrow : entities)
                            {
                                if (book.getId().equals(borrow.getBookId()) && borrow.getCount() <= (existCount - existLend))
                                {
                                    saveList.add(borrow);
                                    break;
                                }
                            }
                        }
                    });
                }
            }
            if (CollectionUtils.isNotEmpty(saveList) && saveList.size() == entities.size())
            {
                //保存借阅
                Integer borrowDay = saveList.get(0).getBorrowDay();
                LocalDateTime returnDate = LocalDateTime.now().plusDays(borrowDay);
                ZoneId zoneId = ZoneId.systemDefault();
                ZonedDateTime zonedDateTime = returnDate.atZone(zoneId);
                Instant instant = zonedDateTime.toInstant();
                Date date = Date.from(instant);
                saveList.forEach(b -> b.setReturnDate(date));
                CompletableFuture<Integer> saveFuture = CompletableFuture.supplyAsync(() -> borrowDao.saveList(saveList), executor);
                //更新借出
                List<BookEntity> updateList = saveList.stream().map(borrow ->
                {
                    BookEntity book = new BookEntity();
                    book.setId(borrow.getBookId());
                    book.setLend(borrow.getCount());
                    book.setModifyUser(borrow.getCreateUser());
                    return book;
                }).collect(Collectors.toList());
                CompletableFuture<Integer> updateFuture = CompletableFuture.supplyAsync(() -> bookDao.updatePlusList(updateList));
                CompletableFuture.allOf(saveFuture, updateFuture).join();
                Integer save = saveFuture.get();
                Integer update = updateFuture.get();
                count = (save > 0 && update > 0) ? save : null;
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
    public PageInfo<BorrowOutDTO> page(PageDTO<BorrowInDTO> page)
    {
        PageInfo<BorrowOutDTO> pageInfo = null;
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
            BorrowInDTO entity = page.getEntity();
            if (entity != null)
            {
                String name = entity.getName();
                Integer sex = entity.getSex();
                Integer age = entity.getAge();
                String phone = entity.getPhone();
                String address = entity.getAddress();
                Integer count = entity.getCount();
                Integer borrowDay = entity.getBorrowDay();
                Date startReturnDate = entity.getStartReturnDate();
                Date endReturnDate = entity.getEndReturnDate();
                boolean returnDate = startReturnDate != null && endReturnDate != null;
                Integer status = entity.getStatus();
                Date startActualDate = entity.getStartActualDate();
                Date endActualDate = entity.getEndActualDate();
                boolean actualDate = startActualDate != null && endActualDate != null;
                List<BookEntity> books = entity.getBooks();
                List<String> bookNames = null;
                if (CollectionUtils.isNotEmpty(books))
                {
                    bookNames = books.stream().map(BookEntity::getBookName).distinct().collect(Collectors.toList());
                }
                List<Integer> bookIds = null;
                if (CollectionUtils.isNotEmpty(bookNames))
                {
                    BookExample bookExample = new BookExample();
                    BookExample.Criteria criteria = bookExample.createCriteria();
                    criteria.andBookNameIn(bookNames);
                    List<BookEntity> bookList = bookDao.selectByExample(bookExample);
                    if (CollectionUtils.isNotEmpty(bookList))
                    {
                        bookIds = bookList.stream().map(BookEntity::getId).distinct().collect(Collectors.toList());
                    }
                }
                BorrowExample.Criteria criteria = null;
                //查询条件
                if (StringUtils.isNotBlank(name) || sex != null || age != null || StringUtils.isNotBlank(phone) || StringUtils.isNotBlank(address)
                        || count != null || CollectionUtils.isNotEmpty(bookIds) || borrowDay != null || returnDate || status != null || actualDate)
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
                    if (CollectionUtils.isNotEmpty(bookIds))
                    {
                        criteria.andBookIdIn(bookIds);
                    }
                    if (count != null)
                    {
                        criteria.andCountEqualTo(count);
                    }
                    if (borrowDay != null)
                    {
                        criteria.andBorrowDayEqualTo(borrowDay);
                    }
                    if (returnDate)
                    {
                        criteria.andReturnDateBetween(startReturnDate, endReturnDate);
                    }
                    if (status != null)
                    {
                        criteria.andStatusEqualTo(status);
                    }
                    if (actualDate)
                    {
                        criteria.andActualDateBetween(startActualDate, endActualDate);
                    }
                }
            }
            //排序
            example.setOrderByClause(page.getProperty() + StringUtils.SPACE + page.getOrder());
            //分页
            PageHelper.startPage(page.getPageNum(), page.getPageSize());
            List<BorrowEntity> list = borrowDao.selectByExample(example);
            List<Integer> bookIds = list.stream().map(BorrowEntity::getBookId).distinct().collect(Collectors.toList());
            List<BookEntity> bookList = null;
            if (CollectionUtils.isNotEmpty(bookIds))
            {
                BookExample bookExample = new BookExample();
                BookExample.Criteria criteria = bookExample.createCriteria();
                criteria.andIdIn(bookIds);
                bookList = bookDao.selectByExample(bookExample);
            }
            List<BorrowOutDTO> borrowOutDTOS = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(bookList))
            {
                for (BorrowEntity borrow : list)
                {
                    BorrowOutDTO borrowOutDTO = new BorrowOutDTO();
                    BeanUtils.copyProperties(borrow, borrowOutDTO);
                    for (BookEntity book : bookList)
                    {
                        if (borrow.getBookId().equals(book.getId()))
                        {
                            borrowOutDTO.setBook(book);
                            break;
                        }
                    }
                    borrowOutDTOS.add(borrowOutDTO);
                }
            }
            if (CollectionUtils.isNotEmpty(borrowOutDTOS))
            {
                pageInfo = new PageInfo<>(borrowOutDTOS);
            }
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.QUERY_ERROR, e);
        }
        return pageInfo;
    }

    @Override
    public BorrowOutDTO findById(Integer id)
    {
        try
        {
            BorrowEntity borrow = borrowDao.selectByPrimaryKey(id);
            BookExample example = new BookExample();
            BookExample.Criteria criteria = example.createCriteria();
            criteria.andIdEqualTo(borrow.getBookId());
            List<BookEntity> books = bookDao.selectByExample(example);
            BorrowOutDTO borrowOutDTO = null;
            if (CollectionUtils.isNotEmpty(books))
            {
                borrowOutDTO = new BorrowOutDTO();
                BeanUtils.copyProperties(borrow, borrowOutDTO);
                borrowOutDTO.setBook(books.get(0));
            }
            return borrowOutDTO;
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
    public Integer deleteBySerialNo(String serialNo)
    {
        try
        {
            BorrowExample example = new BorrowExample();
            BorrowExample.Criteria criteria = example.createCriteria();
            criteria.andSerialNoEqualTo(serialNo);
            return borrowDao.deleteByExample(example);
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.DELETE_ERROR, e);
        }
        return null;
    }

    @Override
    @Transactional
    public Boolean modify(BorrowEntity entity)
    {
        try
        {
            String modifyUser = entity.getModifyUser();
            List<CompletableFuture<Integer>> futureList = new ArrayList<>(2);

            Integer newCount = entity.getCount();
            if (newCount != null)
            {
                BorrowEntity borrow = borrowDao.selectByPrimaryKey(entity.getId());
                Integer bookId = borrow.getBookId();
                Integer oldCount = borrow.getCount();
                //旧值
                BookEntity oldBook = new BookEntity();
                oldBook.setId(bookId);
                oldBook.setLend(oldCount);
                oldBook.setModifyUser(modifyUser);
                //新值
                BookEntity newBook = new BookEntity();
                newBook.setId(bookId);
                newBook.setLend(newCount);
                newBook.setModifyUser(modifyUser);

                CompletableFuture<Integer> bookFuture = CompletableFuture.supplyAsync(() ->
                {
                    BookEntity book = bookDao.selectByPrimaryKey(bookId);
                    Integer midLend = book.getLend() - oldCount;
                    Integer bookCount = book.getCount();
                    return bookCount > midLend && newCount <= (bookCount - midLend);
                }, executor).thenApplyAsync(result -> result ? bookDao.updateMinusOne(oldBook) : 0, executor)
                        .thenApplyAsync(result -> (result != null && result > 0 ? bookDao.updatePlusOne(newBook) : 0), executor)
                        .whenCompleteAsync((r, e) ->
                        {
                            if (e != null)
                            {
                                log.error(LibraryConstant.MODIFY_ERROR, e);
                            }
                            else
                            {
                                log.info(r.toString());
                            }
                        }, executor);
                futureList.add(bookFuture);
            }

            CompletableFuture<Integer> borrowFuture = CompletableFuture.supplyAsync(() -> borrowDao.updateByPrimaryKeySelective(entity), executor);
            futureList.add(borrowFuture);

            CompletableFuture.allOf(futureList.toArray(new CompletableFuture[futureList.size()])).join();
            boolean success = true;
            for (CompletableFuture<Integer> future : futureList)
            {
                Integer result = future.get();
                if (result == null || result <= 0)
                {
                    success = false;
                    break;
                }
            }

            return success;
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.MODIFY_ERROR, e);
        }
        return null;
    }

    @Override
    public List<BorrowOutDTO> findAll()
    {
        List<BorrowOutDTO> list = new ArrayList<>();
        ;
        try
        {
            List<BorrowEntity> findList = borrowDao.selectByExample(new BorrowExample());
            List<Integer> bookIds = findList.stream().map(BorrowEntity::getBookId).distinct().collect(Collectors.toList());
            List<BookEntity> books = null;
            if (CollectionUtils.isNotEmpty(bookIds))
            {
                BookExample example = new BookExample();
                BookExample.Criteria criteria = example.createCriteria();
                criteria.andIdIn(bookIds);
                books = bookDao.selectByExample(example);
            }
            if (CollectionUtils.isNotEmpty(books))
            {
                for (BorrowEntity borrow : findList)
                {
                    BorrowOutDTO borrowOutDTO = new BorrowOutDTO();
                    BeanUtils.copyProperties(borrow, borrowOutDTO);
                    for (BookEntity book : books)
                    {
                        if (borrow.getBookId().equals(book.getId()))
                        {
                            borrowOutDTO.setBook(book);
                            break;
                        }
                    }
                    list.add(borrowOutDTO);
                }
            }
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.QUERY_ERROR, e);
        }
        return list;
    }

    @Override
    public List<BorrowOutDTO> findBySerialNo(String serialNo)
    {
        List<BorrowOutDTO> list = new ArrayList<>();
        try
        {
            BorrowExample example = new BorrowExample();
            BorrowExample.Criteria criteria = example.createCriteria();
            criteria.andSerialNoEqualTo(serialNo);
            List<BorrowEntity> borrows = borrowDao.selectByExample(example);
            List<Integer> bookIds = borrows.stream().map(BorrowEntity::getBookId).distinct().collect(Collectors.toList());
            List<BookEntity> books = null;
            if (CollectionUtils.isNotEmpty(bookIds))
            {
                BookExample bookExample = new BookExample();
                BookExample.Criteria bookCriteria = bookExample.createCriteria();
                bookCriteria.andIdIn(bookIds);
                books = bookDao.selectByExample(bookExample);
            }
            if (CollectionUtils.isNotEmpty(books))
            {
                for (BorrowEntity borrow : borrows)
                {
                    BorrowOutDTO borrowOutDTO = new BorrowOutDTO();
                    BeanUtils.copyProperties(borrow, borrowOutDTO);
                    for (BookEntity book : books)
                    {
                        if (borrow.getBookId().equals(book.getId()))
                        {
                            borrowOutDTO.setBook(book);
                            break;
                        }
                    }
                    list.add(borrowOutDTO);
                }
            }
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.QUERY_ERROR, e);
        }
        return list;
    }

    @Override
    @Transactional
    public Boolean returnOne(BorrowEntity entity)
    {
        try
        {
            List<CompletableFuture<Integer>> futureList = new ArrayList<>(2);
            BorrowEntity borrow = borrowDao.selectByPrimaryKey(entity.getId());
            Integer count = borrow.getCount();
            if (count != null && count > 0)
            {
                CompletableFuture<Integer> bookFuture = CompletableFuture.supplyAsync(() ->
                {
                    BookEntity book = new BookEntity();
                    book.setId(borrow.getBookId());
                    book.setLend(borrow.getCount());
                    book.setModifyUser(entity.getModifyUser());
                    return bookDao.updateMinusOne(book);
                }, executor);
                futureList.add(bookFuture);
            }
            CompletableFuture<Integer> borrowFuture = CompletableFuture.supplyAsync(() -> borrowDao.updateByPrimaryKeySelective(entity), executor);
            futureList.add(borrowFuture);
            CompletableFuture.allOf(futureList.toArray(new CompletableFuture[futureList.size()])).join();
            boolean success = true;
            for (CompletableFuture<Integer> future : futureList)
            {
                Integer result = future.get();
                if (result == null || result <= 0)
                {
                    success = false;
                    break;
                }
            }

            return success;
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.MODIFY_ERROR, e);
        }
        return null;
    }

    @Override
    @Transactional
    public Boolean returnAll(BorrowEntity entity)
    {
        try
        {
            List<CompletableFuture<Integer>> futureList = new ArrayList<>(2);

            BorrowExample borrowExample = new BorrowExample();
            BorrowExample.Criteria borrowCriteria = borrowExample.createCriteria();
            borrowCriteria.andSerialNoEqualTo(entity.getSerialNo());
            List<BorrowEntity> borrows = borrowDao.selectByExample(borrowExample);
            if (CollectionUtils.isNotEmpty(borrows))
            {
                List<BookEntity> list = new ArrayList<>();
                borrows.forEach(borrow ->
                {
                    BookEntity book = new BookEntity();
                    book.setId(borrow.getBookId());
                    book.setLend(borrow.getCount());
                    book.setModifyUser(entity.getModifyUser());
                    list.add(book);
                });
                if (CollectionUtils.isNotEmpty(list))
                {
                    CompletableFuture<Integer> bookFuture = CompletableFuture.supplyAsync(() -> bookDao.updateMinusList(list), executor);
                    futureList.add(bookFuture);
                }
            }

            CompletableFuture<Integer> borrowFuture = CompletableFuture.supplyAsync(() ->
            {
                BorrowExample example = new BorrowExample();
                BorrowExample.Criteria criteria = example.createCriteria();
                criteria.andSerialNoEqualTo(entity.getSerialNo());
                return borrowDao.updateByExampleSelective(entity, example);
            }, executor);
            futureList.add(borrowFuture);
            boolean success = true;
            for (CompletableFuture<Integer> future : futureList)
            {
                Integer result = future.get();
                if (result == null || result <= 0)
                {
                    success = false;
                    break;
                }
            }

            return success;
        }
        catch (Exception e)
        {
            log.error(LibraryConstant.MODIFY_ERROR, e);
        }
        return null;
    }

    @Override
    public List<SevenDTO<BorrowOutDTO>> seven(List<SevenDTO<BorrowEntity>> queryList)
    {
        List<SevenDTO<BorrowOutDTO>> list = new ArrayList<>(8);

        try
        {
            //线程集合，所有线程放入一个集合，一次性交给线程池异步执行
            List<CompletableFuture<SevenDTO<BorrowOutDTO>>> futureList = new ArrayList<>(8);
            queryList.forEach(seven ->
            {
                //线程
                CompletableFuture<SevenDTO<BorrowOutDTO>> future = CompletableFuture.supplyAsync(() ->
                {
                    Pair<Date, Date> pair = seven.getPair();
                    BorrowExample example = new BorrowExample();
                    BorrowExample.Criteria criteria = example.createCriteria();
                    criteria.andCreateDateBetween(pair.getLeft(), pair.getRight());
                    List<BorrowEntity> borrows = borrowDao.selectByExample(example);
                    //书籍ID
                    List<Integer> bookIds = borrows.stream().map(BorrowEntity::getBookId).distinct().collect(Collectors.toList());
                    List<BookEntity> bookList = new ArrayList<>();
                    if (CollectionUtils.isNotEmpty(bookIds))
                    {
                        BookExample bookExample = new BookExample();
                        BookExample.Criteria bookCriteria = bookExample.createCriteria();
                        bookCriteria.andIdIn(bookIds);
                        //查找书籍
                        bookList = bookDao.selectByExample(bookExample);
                    }
                    //组装
                    AtomicReference<List<BookEntity>> atomic = new AtomicReference<>(bookList);
                    List<BorrowOutDTO> borrowOutList = borrows.stream().map(borrow ->
                    {
                        BorrowOutDTO borrowOutDTO = new BorrowOutDTO();
                        BeanUtils.copyProperties(borrow, borrowOutDTO);
                        List<BookEntity> books = atomic.get();
                        if (CollectionUtils.isNotEmpty(books))
                        {
                            for (BookEntity book : books)
                            {
                                if (borrow.getBookId().equals(book.getId()))
                                {
                                    borrowOutDTO.setBook(book);
                                    break;
                                }
                            }
                        }
                        return borrowOutDTO;
                    }).collect(Collectors.toList());
                    SevenDTO<BorrowOutDTO> sevenDTO = new SevenDTO<>();
                    sevenDTO.setDate(seven.getDate());
                    sevenDTO.setList(borrowOutList);
                    sevenDTO.setCount(borrows.size());
                    return sevenDTO;
                }, executor);
                futureList.add(future);
            });
            if (CollectionUtils.isNotEmpty(futureList))
            {
                CompletableFuture[] futures = new CompletableFuture[futureList.size()];
                //异步执行
                CompletableFuture.allOf(futureList.toArray(futures)).join();
                //循环获取每个线程的结果
                for (CompletableFuture<SevenDTO<BorrowOutDTO>> future : futureList)
                {
                    SevenDTO<BorrowOutDTO> sevenDTO = future.get();
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
