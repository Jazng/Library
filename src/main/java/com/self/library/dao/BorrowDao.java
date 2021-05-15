package com.self.library.dao;

import com.self.library.entity.BorrowEntity;
import com.self.library.entity.BorrowExample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface BorrowDao
{
    Long countByExample(BorrowExample example);

    Integer deleteByExample(BorrowExample example);

    Integer deleteByPrimaryKey(Integer id);

    Integer insert(BorrowEntity record);

    Integer insertSelective(BorrowEntity record);

    List<BorrowEntity> selectByExample(BorrowExample example);

    BorrowEntity selectByPrimaryKey(Integer id);

    Integer updateByExampleSelective(@Param("record") BorrowEntity record, @Param("example") BorrowExample example);

    Integer updateByExample(@Param("record") BorrowEntity record, @Param("example") BorrowExample example);

    Integer updateByPrimaryKeySelective(BorrowEntity record);

    Integer updateByPrimaryKey(BorrowEntity record);

    Integer saveList(@Param("list") List<BorrowEntity> list);
}