package com.self.library.dao;

import com.self.library.entity.PublishEntity;
import com.self.library.entity.PublishExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface PublishDao
{
    long countByExample(PublishExample example);

    int deleteByExample(PublishExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(PublishEntity record);

    int insertSelective(PublishEntity record);

    List<PublishEntity> selectByExample(PublishExample example);

    PublishEntity selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") PublishEntity record, @Param("example") PublishExample example);

    int updateByExample(@Param("record") PublishEntity record, @Param("example") PublishExample example);

    int updateByPrimaryKeySelective(PublishEntity record);

    int updateByPrimaryKey(PublishEntity record);
}