package com.spark.bitrade.api.vo;

import com.spark.bitrade.constant.MinerGrade;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author ww
 * @time 2019.11.12 15:29
 */
@ApiModel("推广收益类")
@Data
public class MinerRewardListVO extends RespObjectList<BtBankRebateRecordVO> {


    @ApiModelProperty("矿工等级  1银牌  2金牌")
    int minerGrade = MinerGrade.SILVER_MINER.getGradeId();

    @ApiModelProperty("获取的推广收益")
    BigDecimal gotSharedReward = BigDecimal.ZERO;


}

