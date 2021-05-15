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
    long countByExample(BookExample example);

    int deleteByExample(BookExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(BookEntity record);

    int insertSelective(BookEntity record);

    List<BookEntity> selectByExample(BookExample example);

    BookEntity selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") BookEntity record, @Param("example") BookExample example);

    int updateByExample(@Param("record") BookEntity record, @Param("example") BookExample example);

    int updateByPrimaryKeySelective(BookEntity record);

    int updateByPrimaryKey(BookEntity record);

    Integer saveBooks(@Param("list") List<BookEntity> list);
}