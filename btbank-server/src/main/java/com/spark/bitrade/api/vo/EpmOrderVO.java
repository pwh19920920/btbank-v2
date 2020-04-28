package com.spark.bitrade.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spark.bitrade.repository.entity.EnterpriseMinerTransaction;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * EpmOrderVO
 *
 * @author biu
 * @since 2019/12/25 15:39
 */
@Data
@ApiModel("企业矿工挖矿订单-开放接口")
public class EpmOrderVO {

    @ApiModelProperty(value = "ID", example = "")
    private Long id;

    /**
     * 会员ID
     */
    @ApiModelProperty(value = "会员ID", example = "")
    private Long memberId;

    /**
     * 矿工ID
     */
    @ApiModelProperty(value = "矿工ID", example = "")
    private Integer minerId;

    /**
     * 数量
     */
    @ApiModelProperty(value = "数量", example = "")
    private BigDecimal amount;

    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号", example = "")
    private String orderSn;

    /**
     * 状态 0：处理中 1：已完成
     */
    @ApiModelProperty(value = "状态", example = "")
    private Integer state;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    public static EpmOrderVO of(EnterpriseMinerTransaction tx) {
        EpmOrderVO vo = new EpmOrderVO();

        vo.setId(tx.getId());
        vo.setMemberId(tx.getMemberId());
        vo.setMinerId(tx.getMinerId());
        vo.setAmount(tx.getAmount());
        vo.setOrderSn(tx.getOrderSn());
        vo.setCreateTime(tx.getCreateTime());
        vo.setUpdateTime(tx.getUpdateTime());
        vo.setState(tx.getStatus());
        return vo;
    }
}
