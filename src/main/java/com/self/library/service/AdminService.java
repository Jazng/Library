package com.self.library.service;

import com.self.library.entity.UserEntity;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * @Author Administrator
 * @Title:
 * @Description:
 * @Date 2021-04-17 17:28
 * @Version: 1.0
 */
public interface AdminService
{
    public Pair<Boolean, List<String>> register(UserEntity userEntity);

    public Pair<Boolean, UserEntity> login(UserEntity userEntity);

    public boolean destroy(UserEntity userEntity);

    public boolean modify(UserEntity userEntity);

    public UserEntity findById(Integer id);
}
