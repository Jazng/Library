package com.self.library.service.impl;

import com.self.library.dao.UserDao;
import com.self.library.entity.UserEntity;
import com.self.library.entity.UserExample;
import com.self.library.service.AdminService;
import com.zeng.extension.functional.FunctionalUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static com.self.library.constant.LibraryConstant.*;

/**
 * @Author Administrator
 * @Title:
 * @Description:
 * @Date 2021-04-17 17:29
 * @Version: 1.0
 */
@Service
@Slf4j
public class AdminServiceImpl implements AdminService
{
    @Autowired
    private Executor executor;

    @Autowired
    private UserDao userDao;

    /**
     * 若不满足，返回所有不满足条件信息，而不是遇到不满足就立即返回，二是收集所有不满足信息一并返回
     *
     * @param userEntity 封装注册信息
     * @return 返回注册成功与否和具体信息的集合
     */
    @Override
    public Pair<Boolean, List<String>> register(UserEntity userEntity)
    {
        List<String> result = new ArrayList<>(4);

        try
        {
            //开线程池根据不同的条件查询数据库中是否已经有此账号
            List<CompletableFuture<Pair<Boolean, String>>> futureList = new ArrayList<>(4);
            CompletableFuture<Pair<Boolean, String>> usernameFuture = CompletableFuture.supplyAsync(() ->
            {
                //查询封装实体
                UserExample example = new UserExample();
                //查询实体
                UserExample.Criteria criteria = example.createCriteria();
                criteria.andUsernameEqualTo(userEntity.getUsername());
                criteria.andDestroyNotEqualTo(Boolean.TRUE);
                List<UserEntity> list = userDao.selectByExample(example);
                if (CollectionUtils.isNotEmpty(list))
                {
                    return Pair.of(Boolean.TRUE, USERNAME_EXIST);
                }
                return Pair.of(Boolean.FALSE, StringUtils.EMPTY);
            }, executor);
            futureList.add(usernameFuture);

            CompletableFuture<Pair<Boolean, String>> fourFuture = CompletableFuture.supplyAsync(() ->
            {
                UserExample example = new UserExample();
                UserExample.Criteria criteria = example.createCriteria();
                criteria.andNicknameEqualTo(userEntity.getNickname());
                criteria.andRoleEqualTo(userEntity.getRole());
                criteria.andAgeEqualTo(userEntity.getAge());
                criteria.andSexEqualTo(userEntity.getSex());
                criteria.andDestroyNotEqualTo(Boolean.TRUE);
                List<UserEntity> list = userDao.selectByExample(example);
                if (CollectionUtils.isNotEmpty(list))
                {
                    return Pair.of(Boolean.TRUE, NICKNAME_ROLE_AGE_SEX_EXIST);
                }
                return Pair.of(Boolean.FALSE, StringUtils.EMPTY);
            }, executor);
            futureList.add(fourFuture);

            CompletableFuture<Pair<Boolean, String>> phoneFuture = CompletableFuture.supplyAsync(() ->
            {
                UserExample example = new UserExample();
                UserExample.Criteria criteria = example.createCriteria();
                criteria.andPhoneEqualTo(userEntity.getPhone());
                criteria.andDestroyNotEqualTo(Boolean.TRUE);
                List<UserEntity> list = userDao.selectByExample(example);
                if (CollectionUtils.isNotEmpty(list))
                {
                    return Pair.of(Boolean.TRUE, PHONE_EXIST);
                }
                return Pair.of(Boolean.FALSE, StringUtils.EMPTY);
            }, executor);
            futureList.add(phoneFuture);

            CompletableFuture.allOf(futureList.toArray(new CompletableFuture[futureList.size()])).join();

            futureList.stream().distinct().map(FunctionalUtils.function(CompletableFuture::get)).filter(Pair::getLeft).map(Pair::getRight).forEach(result::add);

            if (CollectionUtils.isEmpty(result))
            {
                if (userEntity.getCreateUser() == null)
                {
                    userEntity.setCreateUser(CREATE_USER);
                }
                userEntity.setDestroy(Boolean.FALSE);
                int count = userDao.insertSelective(userEntity);
                if (count == 1)
                {
                    result.add(REGISTER_SUCCESS_MSG);
                    return Pair.of(Boolean.TRUE, result);
                }
            }
        }
        catch (Exception e)
        {
            log.error(REGISTER_FAIL_MSG, e);
            result.add(REGISTER_FAIL_MSG);
        }
        return Pair.of(Boolean.FALSE, result);
    }

    /**
     * @param userEntity 封装账号和密码
     * @return 若返回键值对左边是true代表此账号和密码对应数据库有值，此时右边有数据则登录成功，无数据则为权限不够；若左边为false，右边必未null，及没有此账号
     */
    @Override
    public Pair<Boolean, UserEntity> login(UserEntity userEntity)
    {
        UserEntity user = new UserEntity();
        boolean isHave = false;
        try
        {
            UserExample example = new UserExample();
            UserExample.Criteria criteria = example.createCriteria();
            criteria.andUsernameEqualTo(userEntity.getUsername());
            criteria.andPasswordEqualTo(userEntity.getPassword());
            criteria.andDestroyNotEqualTo(Boolean.TRUE);
            List<UserEntity> userList = userDao.selectByExample(example);
            example.clear();
            if (CollectionUtils.isNotEmpty(userList))
            {
                isHave = true;
                user = userList.stream().filter(entity -> entity.getRole() == 1).findFirst().orElse(new UserEntity());

            }
        }
        catch (Exception e)
        {
            log.error(LOGIN_ERROR, e);
        }
        Integer role = user.getRole();
        return Pair.of(isHave, role != null && role.equals(1) ? user : null);
    }

    @Override
    public boolean destroy(UserEntity userEntity)
    {
        boolean result = false;
        try
        {
            UserExample example = new UserExample();
            UserExample.Criteria criteria = example.createCriteria();
            criteria.andUsernameEqualTo(userEntity.getUsername());
            criteria.andPasswordEqualTo(userEntity.getPassword());
            userEntity.setDestroy(Boolean.TRUE);
            userEntity.setModifyUser(MODIFY_USER);
            int count = userDao.updateByExampleSelective(userEntity, example);
            if (count > 0)
            {
                result = true;
            }
        }
        catch (Exception e)
        {
            log.error(DESTROY_ERROR, e);
        }

        return result;
    }

    @Override
    public boolean modify(UserEntity userEntity)
    {
        boolean result = false;
        try
        {
            userEntity.setModifyUser(MODIFY_USER);
            Integer count = userDao.updateByPrimaryKeySelective(userEntity);
            if (count == 1)
            {
                result = true;
            }
        }
        catch (Exception e)
        {
            log.error(USER + MODIFY_ERROR, e);
        }

        return result;
    }

    @Override
    public UserEntity findById(Integer id)
    {
        try
        {
            return userDao.selectByPrimaryKey(id);
        }
        catch (Exception e)
        {
            log.error(QUERY_USER_ERROR, e);
        }

        return null;
    }
}
