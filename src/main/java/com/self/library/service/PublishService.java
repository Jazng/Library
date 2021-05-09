package com.self.library.service;

import com.github.pagehelper.PageInfo;
import com.self.library.dto.PageDTO;
import com.self.library.entity.PublishEntity;

import java.util.List;

/**
 * @Author Administrator
 * @Title:
 * @Description:
 * @Date 2021-05-09 12:25
 * @Version: 1.0
 */
public interface PublishService
{
    Integer save(PublishEntity entity);

    Integer saveList(List<PublishEntity> entities);

    PageInfo<PublishEntity> page(PageDTO<PublishEntity> page);

    PublishEntity findById(Integer id);

    Integer delete(Integer id);

    Integer modify(PublishEntity entity);

    List<PublishEntity> findAll();
}
