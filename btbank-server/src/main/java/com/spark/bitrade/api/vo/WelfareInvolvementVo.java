package com.spark.bitrade.api.vo;

import com.spark.bitrade.repository.entity.WelfareActivity;
import com.spark.bitrade.repository.entity.WelfareInvolvement;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;


/**
 * WelfareInvolvementVo
 *
 * @author biu
 * @since 2020/4/16 16:12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("参与明细视图对象")
public class WelfareInvolvementVo extends WelfareInvolvement {

    @ApiModelProperty("锁仓时间：单位秒")
    private Long lockTime;

    @ApiModelProperty("活动描述")
    private String remark;

    @ApiModelProperty("收益比率")
    private BigDecimal earningRate;


    public WelfareInvolvementVo fill(WelfareActivity activity) {
        if (activity != null) {
            this.setLockTime(activity.getLockTime());
            this.setRemark(activity.getRemark());
            this.setEarningRate(activity.getEarningRate());
        }
        return this;
    }

    public static WelfareInvolvementVo of(WelfareInvolvement involvement) {
        WelfareInvolvementVo vo = new WelfareInvolvementVo();
        vo.setId(involvement.getId());
        vo.setActId(involvement.getActId());
        vo.setActName(involvement.getActName());
        vo.setActType(involvement.getActType());
        vo.setOpenningTime(involvement.getOpenningTime());
        vo.setClosingTime(involvement.getClosingTime());
        vo.setReleaseTime(involvement.getReleaseTime());
        vo.setMemberId(involvement.getMemberId());
        vo.setUsername(involvement.getUsername());
        vo.setAmount(involvement.getAmount());
        vo.setInviteId(involvement.getInviteId());
        vo.setRefId(involvement.getRefId());
        // vo.setRefundRefId();
        vo.setEarningUnreleasedAmount(involvement.getEarningUnreleasedAmount());
        vo.setEarningReleaseAmount(involvement.getEarningReleaseAmount());
        vo.setEarningReleaseTime(involvement.getEarningReleaseTime());
        vo.setEarningRefId(involvement.getRefId());
        vo.setRecommendAmount(involvement.getRecommendAmount());
        vo.setRecommendReleaseTime(involvement.getRecommendReleaseTime());
        vo.setRefundRefId(involvement.getRefundRefId());
        vo.setRecommendStatus(involvement.getRecommendStatus());
        vo.setGoldRefId(involvement.getGoldRefId());
        vo.setStatus(involvement.getStatus());
        vo.setReleaseStatus(involvement.getReleaseStatus());
        vo.setCreateTime(involvement.getCreateTime());
        vo.setUpdateTime(involvement.getUpdateTime());
        return vo;
    }
}
