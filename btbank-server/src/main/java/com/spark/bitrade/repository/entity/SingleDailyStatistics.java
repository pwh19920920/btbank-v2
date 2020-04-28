package com.spark.bitrade.repository.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * (SingleDailyStatistics)表实体类
 *
 * @author daring5920
 * @since 2020-03-23 13:42:21
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "")
public class SingleDailyStatistics {

    @TableId
    @ApiModelProperty(value = "", example = "")
    private Long id;

    /**
     * 统计时间
     */
    @ApiModelProperty(value = "统计时间", example = "")
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间", example = "")
    private Date updateTime;

    /**
     * 会员id
     */
    @ApiModelProperty(value = "会员id", example = "")
    private Long memberId;

    /**
     * 手机号码
     */
    @ApiModelProperty(value = "手机号码", example = "")
    private String phoneNumber;

    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名", example = "")
    private String name;

    /**
     * 会员类型
     */
    @ApiModelProperty(value = "会员类型{0:普通用户,1:实名用户,2:认证商家,3:内部商家}", example = "")
    private Integer memberType;

    /**
     * 额度
     */
    @ApiModelProperty(value = "额度", example = "")
    private Double amount;

    /**
     * 交易类型 0:otc购买、1:otc出售、2:云端转入、3：云端转出
     */
    @ApiModelProperty(value = "交易类型 0:otc购买、1:otc出售、2:云端转入、3：云端转出", example = "")
    private Integer transactionType;


}