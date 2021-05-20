package com.self.library.service;

import com.github.pagehelper.PageInfo;
import com.self.library.dto.PageDTO;
import com.self.library.dto.TagDTO;
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

    PageInfo<TagDTO> page(PageDTO<TagEntity> page);

    TagDTO findById(Integer id);

    Integer delete(Integer id);

    Integer modify(TagEntity entity);

    List<TagDTO> findAll();
}
