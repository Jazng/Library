package com.self.library.dao;

import com.self.library.entity.BookEntity;
import com.self.library.entity.BookExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface BookDao
{
    Long countByExample(BookExample example);

    Integer deleteByExample(BookExample example);

    Integer deleteByPrimaryKey(Integer id);

    Integer insert(BookEntity record);

    Integer insertSelective(BookEntity record);

    List<BookEntity> selectByExample(BookExample example);

    BookEntity selectByPrimaryKey(Integer id);

    Integer updateByExampleSelective(@Param("record") BookEntity record, @Param("example") BookExample example);

    Integer updateByExample(@Param("record") BookEntity record, @Param("example") BookExample example);

    Integer updateByPrimaryKeySelective(BookEntity record);

    Integer updateByPrimaryKey(BookEntity record);

    Integer saveBooks(@Param("list") List<BookEntity> list);

    Integer updateCount(BookEntity entity);

    Integer updateMinusList(@Param("list") List<BookEntity> list);

    Integer updatePlusList(@Param("list") List<BookEntity> list);

    Integer updateMinusOne(BookEntity book);

    Integer updatePlusOne(BookEntity book);
}