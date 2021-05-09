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
    Long countByExample(PublishExample example);

    Integer deleteByExample(PublishExample example);

    Integer deleteByPrimaryKey(Integer id);

    Integer insert(PublishEntity record);

    Integer insertSelective(PublishEntity record);

    List<PublishEntity> selectByExample(PublishExample example);

    PublishEntity selectByPrimaryKey(Integer id);

    Integer updateByExampleSelective(@Param("record") PublishEntity record, @Param("example") PublishExample example);

    Integer updateByExample(@Param("record") PublishEntity record, @Param("example") PublishExample example);

    Integer updateByPrimaryKeySelective(PublishEntity record);

    Integer updateByPrimaryKey(PublishEntity record);

    Integer savePublishes(@Param("publishes") List<PublishEntity> publishes);
}