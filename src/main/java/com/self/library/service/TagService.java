package com.self.library.service;

import com.github.pagehelper.PageInfo;
import com.self.library.dto.PageDTO;
import com.self.library.entity.TagEntity;

import java.util.List;

/**
 * @Author Administrator
 * @Title:
 * @Description:
 * @Date 2021-05-07 21:32
 * @Version: 1.0
 */
public interface TagService
{
    Integer save(TagEntity entity);

    Integer saveList(List<TagEntity> entities);

    PageInfo<TagEntity> page(PageDTO<TagEntity> page);

    TagEntity findById(Integer id);

    Integer delete(Integer id);

    Integer modify(TagEntity entity);

    List<TagEntity> findAll();
}
