package com.self.library.constant;

/**
 * @Author Administrator
 * @Title: 公共常量
 * @Description: 取消使用魔法值
 * @Date 2021-04-17 12:56
 * @Version: 1.0
 */
public interface LibraryConstant
{
    //JWT Token秘钥
    String TOKEN_KEY = "Library&Management&System";

    //JWT
    String USER_ID = "id";
    String USERNAME = "username";
    String NICKNAME = "nickname";
    String ROLE = "role";
    String PHONE = "phone";
    String EMAIL = "email";
    String AGE = "age";
    String SEX = "sex";


    /**
     * 数据库默认创建者
     */
    String CREATE_USER = "library_creater";
    /**
     * 数据库默认修改者
     */
    String MODIFY_USER = "library_modifier";

    //中文符号
    String CHINESE_COMMA = "，";
    String CHINESE_COLON = "：";
    String CHINESE_SEMICOLON = "；";

    //英文符号
    String ENGLISH_COMMA = ",";
    String ENGLISH_COLON = ":";
    String ENGLISH_SEMICOLON = ";";
    String PERCENT = "%";

    //拦截路径
    String INTERCEPT_ALL = "/library/**";
    String ADMIN_REGISTER = "/library/admin/register";
    String ADMIN_LOGIN = "/library/admin/login";

    //注册公共信息
    String REGISTER_SUCCESS_MSG = "注册成功";
    String REGISTER_FAIL_MSG = "注册失败";
    String REGISTER_ERROR_MSG = "注册出错";
    String REGISTER_ENTER_MSG = "信息填写不完整，请填写完整";
    String USERNAME_EXIST = "账号已存在，请重新输入";
    String NICKNAME_ROLE_AGE_SEX_EXIST = "基本信息已存在，请勿重复注册";
    String PHONE_EXIST = "手机号码已存在，请更换";

    //登录公共信息
    String LOGIN_SUCCESS_MSG = "登录成功";
    String LOGIN_ERROR = "登录出错";
    String LOGIN_FAIL_MSG = "账号或密码错误";
    String LOGIN_ERROR_MSG = "账号或密码错误";
    String LOGIN_NO_PERMISSION_MSG = "账号权限不够";
    String LOGIN_ENTER_MSG = "账号或密码为空，请核对";

    //注销账号和修改账户信息
    String DESTROY_SUCCESS = "注销成功";
    String DESTROY_FAIL = "注销失败";
    String DESTROY_ERROR = "注销出错";
    String MODIFY_SUCCESS = "修改成功";
    String MODIFY_FAIL = "修改失败";
    String MODIFY_ERROR = "修改出错";
    String LOGOUT_SUCCESS = "退出成功";
    String LOGOUT_FAIL = "退出失败";

    //公共
    String COMMON_ZERO = "0";
    String COMMON_ONE = "1";
    String RESULT_FAIL = "fail";
    String RETRY = "请重试";

    String USER = "账户";

    //查询
    String QUERY_USER_ERROR = "查询用户出错";

    //新增
    String INSERT_SUCCESS = "添加成功";
    String INSERT_FAIL = "添加失败";
    String INSERT_ERROR = "添加出错";

    //查询
    String QUERY_SUCCESS = "查询成功";
    String QUERY_FAIL = "查询失败";
    String QUERY_ERROR = "查询出错";

    //删除
    String DELETE_SUCCESS = "删除成功";
    String DELETE_FAIL = "删除失败";
    String DELETE_ERROR = "删除出错";
}
