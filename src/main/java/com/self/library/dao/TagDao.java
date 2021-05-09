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
    Long countByExample(TagExample example);

    Integer deleteByExample(TagExample example);

    Integer deleteByPrimaryKey(Integer id);

    Integer insert(TagEntity record);

    Integer insertSelective(TagEntity record);

    List<TagEntity> selectByExample(TagExample example);

    TagEntity selectByPrimaryKey(Integer id);

    Integer updateByExampleSelective(@Param("record") TagEntity record, @Param("example") TagExample example);

    Integer updateByExample(@Param("record") TagEntity record, @Param("example") TagExample example);

    Integer updateByPrimaryKeySelective(TagEntity record);

    Integer updateByPrimaryKey(TagEntity record);

    Integer saveTags(@Param("tags") List<TagEntity> tags);
}