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

    //拦截路径
    String ADMIN_ALL = "/library/admin/**";
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

    //公共
    String COMMON_ZERO = "0";
    String COMMON_ONE = "1";
    String RESULT_FAIL = "fail";
    String RETRY = "请重试";

    String USER = "账户";

    //查询
    String QUERY_USER_ERROR = "查询用户出错";
}
