package com.self.library.dto;

import com.self.library.utils.NumberUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * @Author Administrator
 * @Title: 返回数据
 * @Description: 封装结果
 * @Date 2021-04-17 16:27
 * @Version: 1.0
 */
@Data
@ToString
@NoArgsConstructor
@ApiModel("返回结果通用包装")
public class ResultDTO<T extends Serializable> implements Serializable
{
    private static final long serialVersionUID = -5778850189983197660L;

    /**
     * 接口返回成功代码
     */
    public static final int SUCCESS = 1;
    /**
     * 接口返回失败代码
     */
    public static final int FAIL = 0;

    public static final String CODE_STRING = "code";
    public static final String DATA_STRING = "data";
    public static final String MSG_STRING = "msg";

    /**
     * 默认返回信息，一般为success，当遇到异常则为fail
     */
    @ApiModelProperty(value = "响应信息")
    private String msg = "success";

    /**
     * 异常信息
     */
    @ApiModelProperty("异常信息")
    private String exception;
    /**
     * 异常编码
     */
    @ApiModelProperty("异常编码")
    private String exceptionCode;

    /**
     * 默认返回成功
     */
    @ApiModelProperty(value = "响应编码", example = "1")
    private int code = SUCCESS;
    /**
     * 返回数据
     */
    @ApiModelProperty("响应体")
    private T data;

    public ResultDTO(Collection<? extends Serializable> data)
    {
        this.data = (T) data;
    }

    public ResultDTO(Map<? extends Serializable, ? extends Serializable> data)
    {
        this.data = (T) data;
    }

    /**
     * 返回成功
     *
     * @param data
     */
    public ResultDTO(T data)
    {
        super();
        this.data = data;
    }

    /**
     * 异常返回
     *
     * @param e
     */
    public ResultDTO(Throwable e)
    {
        super();
        this.msg = e.toString();
        this.code = FAIL;
    }

    /**
     * 自定义
     *
     * @param msg
     */
    public ResultDTO(String msg, int code)
    {
        super();
        this.msg = msg;
        this.code = code;
    }

    public ResultDTO(int code, T data)
    {
        super();
        this.code = code;
        this.data = data;
    }

    public ResultDTO(String msg, int code, String exceptionCode)
    {
        super();
        this.msg = msg;
        this.code = code;
        this.exceptionCode = exceptionCode;
    }

    public boolean ok()
    {
        return code == SUCCESS;
    }

    public boolean failed()
    {
        return code == FAIL;
    }

    public boolean noData()
    {
        return failed() || NumberUtils.isNoneValue(data);
    }
}
