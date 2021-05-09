package com.self.library.service;

import com.self.library.dto.TagQueryDTO;
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
    Integer saveTag(TagEntity tag);

    Integer saveTags(List<TagEntity> tags);

    List<TagEntity> pageTags(TagQueryDTO tagQuery);

    TagEntity findById(Integer id);

    Integer deleteTag(Integer id);

    Integer modify(TagEntity tag);

    List<TagEntity> findAll();
}
