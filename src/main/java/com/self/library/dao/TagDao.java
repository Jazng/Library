package com.self.library.dao;

import com.self.library.entity.TagEntity;
import com.self.library.entity.TagExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface TagDao
{
    long countByExample(TagExample example);

    int deleteByExample(TagExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(TagEntity record);

    int insertSelective(TagEntity record);

    List<TagEntity> selectByExample(TagExample example);

    TagEntity selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") TagEntity record, @Param("example") TagExample example);

    int updateByExample(@Param("record") TagEntity record, @Param("example") TagExample example);

    int updateByPrimaryKeySelective(TagEntity record);

    int updateByPrimaryKey(TagEntity record);
}