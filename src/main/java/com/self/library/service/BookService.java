package com.self.library.service;

import com.github.pagehelper.PageInfo;
import com.self.library.dto.BookDTO;
import com.self.library.dto.PageDTO;
import com.self.library.dto.SevenDTO;
import com.self.library.entity.BookEntity;

import java.util.List;

/**
 * @Author Administrator
 * @Title:
 * @Description:
 * @Date 2021-05-09 20:33
 * @Version: 1.0
 */
public interface BookService
{
    Integer save(BookEntity entity);

    Integer saveList(List<BookEntity> entities);

    PageInfo<BookDTO> page(PageDTO<BookEntity> page);

    BookDTO findById(Integer id);

    Integer delete(Integer id);

    Integer modify(BookEntity entity);

    List<BookDTO> findAll();

    List<SevenDTO<BookEntity>> seven(List<SevenDTO<BookEntity>> queryList);
}
