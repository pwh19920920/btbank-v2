package com.spark.bitrade.api.vo;

import com.spark.bitrade.repository.entity.EnterpriseMiner;
import com.spark.bitrade.util.StatusUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * EpmMinerVO
 *
 * @author biu
 * @since 2019/12/25 15:36
 */
@Data
@ApiModel("企业矿工-开放接口")
public class EpmMinerVO {

    @ApiModelProperty(value = "矿工ID")
    private Integer id;

    @ApiModelProperty(value = "会员ID")
    private Long memberId;

    @ApiModelProperty(value = "余额")
    private String balance;

    @ApiModelProperty(value = "是否有效")
    private boolean available;

    public static EpmMinerVO of(EnterpriseMiner em) {
        EpmMinerVO vo = new EpmMinerVO();

        vo.setId(em.getId());
        vo.setMemberId(em.getMemberId());
        vo.setBalance(em.getBalance().stripTrailingZeros().toPlainString());
        vo.setAvailable(StatusUtils.equals(1, em.getStatus()));

        return vo;
    }
}
