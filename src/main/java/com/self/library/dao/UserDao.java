package com.self.library.dao;

import com.self.library.entity.UserEntity;
import com.self.library.entity.UserExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UserDao
{
    Long countByExample(UserExample example);

    Integer deleteByExample(UserExample example);

    Integer deleteByPrimaryKey(Integer id);

    Integer insert(UserEntity record);

    Integer insertSelective(UserEntity record);

    List<UserEntity> selectByExample(UserExample example);

    UserEntity selectByPrimaryKey(Integer id);

    Integer updateByExampleSelective(@Param("record") UserEntity record, @Param("example") UserExample example);

    Integer updateByExample(@Param("record") UserEntity record, @Param("example") UserExample example);

    Integer updateByPrimaryKeySelective(UserEntity record);

    Integer updateByPrimaryKey(UserEntity record);
}