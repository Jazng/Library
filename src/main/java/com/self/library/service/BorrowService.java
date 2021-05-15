package com.self.library.service;

import com.github.pagehelper.PageInfo;
import com.self.library.dto.PageDTO;
import com.self.library.entity.BookEntity;
import com.self.library.entity.BorrowEntity;

import java.util.List;

/**
 * @Author Administrator
 * @Title:
 * @Description:
 * @Date 2021-05-15 10:38
 * @Version: 1.0
 */
public interface BorrowService
{
    Integer save(BorrowEntity entity);

    Integer saveList(List<BorrowEntity> entities);

    PageInfo<BorrowEntity> page(PageDTO<BorrowEntity> page);

    BorrowEntity findById(Integer id);

    Integer delete(Integer id);

    Integer modify(BorrowEntity entity);

    List<BorrowEntity> findAll();
}
