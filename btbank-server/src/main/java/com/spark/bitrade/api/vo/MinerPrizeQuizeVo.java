package com.spark.bitrade.api.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spark.bitrade.repository.entity.MinerPrizeQuizeTransaction;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户参与竞猜活动记录VO
 * @author qiuyuanjie
 * @time 2020.01.03.10:48
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "矿工参与竞猜记录VO")
public class MinerPrizeQuizeVo {
    /*
    *投注总人数
     */
    int totalNum;
    /*
    *投涨人数
     */
    int upNum;
    /*
    *投跌人数
     */
    int downNum;
    /*
    *投跌总金额
     */
    BigDecimal totalAmount;
    /*
    *投涨总金额
     */
    BigDecimal upAmount;
    /*
    *投跌总金额
     */
    BigDecimal downAmout;
    /**
     * 开奖时间
     */
    @ApiModelProperty(value = "开奖时间", example = "")
    private Date rewardResultTime;

    /**
     * 竞猜结果0-跌1-涨
     */
    @ApiModelProperty(value = "竞猜结果0-跌1-涨", example = "")
    private Integer prizeQuizeResult;

    /**
     * 投注金额
     */
    @ApiModelProperty(value = "投注金额", example = "")
    private BigDecimal amount;

    /**
     * 竞猜状态 0待公布 1压中 2未压中
     */
    @ApiModelProperty(value = "竞猜状态 0待公布 1压中 2未压中", example = "")
    private Integer guessStatus;

    /**
     * 分红金额
     */
    @ApiModelProperty(value = "分红金额", example = "")
    private Double reward;

    @ApiModelProperty(value = "竞猜币种")
    private String coin_unit = "BT";

    public static MinerPrizeQuizeVo transfer(MinerPrizeQuizeTransaction transaction){
        MinerPrizeQuizeVo vo = new MinerPrizeQuizeVo();
        vo.setRewardResultTime(transaction.getRewardReleaseTime());
        //显示用户的竞猜记录
        vo.setPrizeQuizeResult(transaction.getPrizeQuizeType());
        vo.setAmount(transaction.getAmount());
        vo.setGuessStatus(transaction.getGuessStatus());
        vo.setReward(transaction.getReward());
        return vo;
    }
}
