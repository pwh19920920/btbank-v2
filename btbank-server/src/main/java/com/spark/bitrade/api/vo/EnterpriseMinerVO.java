package com.spark.bitrade.api.vo;

import com.spark.bitrade.repository.entity.EnterpriseMiner;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * EnterpriseMinerVO
 *
 * @author biu
 * @since 2019/12/23 17:25
 */
@Data
@ApiModel("企业矿工")
public class EnterpriseMinerVO {

    @ApiModelProperty(value = "矿工ID")
    private Integer id;

    @ApiModelProperty(value = "会员ID")
    private Long memberId;

    @ApiModelProperty(value = "余额")
    private BigDecimal balance;

    @ApiModelProperty(value = "已挖矿总额")
    private BigDecimal outlay;

    @ApiModelProperty(value = "累计收益")
    private BigDecimal reward;

    @ApiModelProperty(value = "昨日收益")
    private BigDecimal yesterday;

    @ApiModelProperty(value = "是否有效")
    private boolean available;

    public static EnterpriseMinerVO of(EnterpriseMiner em) {
        EnterpriseMinerVO vo = new EnterpriseMinerVO();

        vo.setId(em.getId());
        vo.setMemberId(em.getMemberId());
        vo.setBalance(em.getBalance());
        vo.setReward(em.getRewardSum());
        vo.setOutlay(em.getOutlaySum());

        vo.setAvailable(1 == em.getStatus() ? true : false);
        // yesterday 昨日收益统计

        return vo;
    }
}
