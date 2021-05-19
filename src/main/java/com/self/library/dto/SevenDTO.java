package com.self.library.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * @Author Administrator
 * @Title:
 * @Description:
 * @Date 2021-05-15 19:17
 * @Version: 1.0
 */
@Data
@ApiModel("最新七天信息")
public class SevenDTO<T extends Serializable> implements Serializable
{
    private static final long serialVersionUID = -2672299155357524374L;

    @ApiModelProperty("日期")
    private LocalDate date;
    @ApiModelProperty("查询日期区间")
    private Pair<Date, Date> pair;
    @ApiModelProperty("此日期的数据")
    private List<T> list;
    @ApiModelProperty("需要计算数量时使用")
    private Integer count;
}
