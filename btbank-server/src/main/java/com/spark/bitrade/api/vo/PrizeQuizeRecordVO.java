package com.spark.bitrade.api.vo;

import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.spark.bitrade.repository.entity.PrizeQuizeRecord;
import lombok.Data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * PrizeQuizeRecordVO
 *
 * @author qiuyuanjie
 * @since 2020-01-02 09:58:28
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "往期竞猜记录")
public class PrizeQuizeRecordVO {

    /**
     * 开奖时间
     */
    @ApiModelProperty(value = "开奖时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date rewardResultTime;

    /**
     * 竞猜结果0-跌1-涨
     */
    @ApiModelProperty(value = "竞猜结果0-跌1-涨", example = "")
    private Integer priQuizeResult;

    /**
     * 最高分红
     */
    @ApiModelProperty(value = "最高分红", example = "")
    private BigDecimal maxReward;

    /**
     * 总投注金额
     */
    @ApiModelProperty(value = "总投注金额", example = "")
    private BigDecimal totalAmount;


    public static PrizeQuizeRecordVO transfer(PrizeQuizeRecord record){
        PrizeQuizeRecordVO vo = new PrizeQuizeRecordVO();
        vo.setRewardResultTime(record.getRewardResultTime());
        vo.setMaxReward(record.getMaxReward());
        Integer result = record.getPriQuizeResult();
        if (result == null) {
            return null;
        }
        vo.setPriQuizeResult(result);
        //竞猜结果是涨，则跌的总金额为奖金池
        if (result.equals(1)){
            vo.setTotalAmount(record.getDownAmount());
        }else{
            vo.setTotalAmount(record.getUpAmount());
        }
        return vo;
    }

}