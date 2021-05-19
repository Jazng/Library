package com.self.library.service;

import com.github.pagehelper.PageInfo;
import com.self.library.dto.BorrowInDTO;
import com.self.library.dto.BorrowOutDTO;
import com.self.library.dto.PageDTO;
import com.self.library.dto.SevenDTO;
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

    PageInfo<BorrowOutDTO> page(PageDTO<BorrowInDTO> page);

    BorrowOutDTO findById(Integer id);

    Integer delete(Integer id);

    Integer deleteBySerialNo(String serialNo);

    Boolean modify(BorrowEntity entity);

    List<BorrowOutDTO> findAll();

    List<BorrowOutDTO> findBySerialNo(String serialNo);

    Boolean returnOne(BorrowEntity entity);

    Boolean returnAll(BorrowEntity entity);

    List<SevenDTO<BorrowOutDTO>> seven(List<SevenDTO<BorrowEntity>> queryList);
}
