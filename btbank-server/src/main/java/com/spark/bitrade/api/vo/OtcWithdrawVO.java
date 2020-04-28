package com.spark.bitrade.api.vo;

import com.spark.bitrade.repository.entity.BusinessMinerOrder;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * OtcWithdrawVO
 *
 * @author biu
 * @since 2019/11/28 13:34
 */
@Data
@ApiModel("OTC提现订单VO")
public class OtcWithdrawVO {

    @ApiModelProperty("订单ID")
    private Long id;

    @ApiModelProperty("出售用户ID")
    private Long sellId;

    @ApiModelProperty("订单价格")
    private BigDecimal amount;

    @ApiModelProperty("手续费")
    private BigDecimal fee;

    @ApiModelProperty("奖励金额")
    private BigDecimal rewardAmount;

    @ApiModelProperty("订单状态{0:新订单,1:未付款,2:已付款,3:申诉中,4:已完成}")
    private Integer status;

    @ApiModelProperty("关联ID")
    private String refId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    public static OtcWithdrawVO of(BusinessMinerOrder order) {
        OtcWithdrawVO vo = new OtcWithdrawVO();

        vo.setId(order.getId());
        vo.setSellId(order.getSellId());
        vo.setAmount(order.getAmount());
        vo.setFee(order.getFee());
        vo.setRewardAmount(order.getRewardAmount());
        vo.setStatus(order.getStatus());

        vo.setRefId(order.getRefId());
        vo.setCreateTime(order.getCreateTime());
        vo.setUpdateTime(order.getUpdateTime());

        return vo;
    }
}
