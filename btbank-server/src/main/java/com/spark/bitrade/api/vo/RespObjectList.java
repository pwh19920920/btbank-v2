package com.spark.bitrade.api.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * @author ww
 * @time 2019.10.25 20:09
 */
@Data
@ApiModel
public class RespObjectList<T> {
    List<T> content;
    Long totalElements;
}
