package com.spark.bitrade.api.vo;

import com.spark.bitrade.constant.MinerGrade;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author
 * @time 2019.11.12 15:29
 */
@ApiModel("我的奖励")
@Data
public class MyRewardListVO extends RespObjectList<MyRewardVO> {

    @ApiModelProperty("矿工等级  1银牌  2金牌")
    int minerGrade = MinerGrade.SILVER_MINER.getGradeId();

    @ApiModelProperty("已获得奖励")
    BigDecimal gotRewardTotal = BigDecimal.ZERO;

    @ApiModelProperty("推荐挖矿佣金奖励")
    BigDecimal recommendTotal = BigDecimal.ZERO;


}

