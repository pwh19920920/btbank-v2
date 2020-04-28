package com.spark.bitrade.api.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.spark.bitrade.repository.entity.MinerPrizeQuizeTransaction;
import com.spark.bitrade.repository.entity.PrizeQuizeRecord;
import com.spark.bitrade.util.DateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 页面展示活动的VO 如果用户登陆返回用户是否参与活动状态
 * 如果用户没有登陆 返回活动信息
 * @author qiuyuanjie
 * @time 2020.01.02.15:27
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "竞猜活动展示")
public class PrizeQuizeVo {
    /**
     * 涨投注人数
     */
    @ApiModelProperty(value = "涨投注人数", example = "")
    private Integer upNum;

    /**
     * 跌投注人数
     */
    @ApiModelProperty(value = "跌投注人数", example = "")
    private Integer downNum;

    /**
     * 总投注人数
     */
    @ApiModelProperty(value = "总投注人数", example = "")
    private Integer totalNum;

    /**
     * 跌投注金额
     */
    @ApiModelProperty(value = "跌投注金额", example = "")
    private BigDecimal downAmount;

    /**
     * 涨投注金额
     */
    @ApiModelProperty(value = "涨投注金额", example = "")
    private BigDecimal upAmount;

    /**
     * 总投注金额
     */
    @ApiModelProperty(value = "总投注金额", example = "")
    private BigDecimal totalAmount;

    /**
     * 是否参与活动 0 未参与 1参与
     */
    @ApiModelProperty(value = "是否参与活动", example = "")
    private Integer isActive;

    /**
     * 开奖时间
     */
    @ApiModelProperty(value = "开奖时间", example = "")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date rewardResultTime;

    /**
     * 活动id
     */
    @ApiModelProperty(value = "当前活动的唯一标识 id")
    private Integer activityId;

    /**
     * 竞猜状态，0未开始，1开始，2结束
     */
    @ApiModelProperty(value = "竞猜状态，0未开始，1开始，2结束", example = "")
    private Integer type;

    @ApiModelProperty(value = "当前活动状态:0 活动开始用户已经投过注了 不可投注 1 活动开始用户还没投注 可投注")
    private Integer status;

    @ApiModelProperty(value = "倒计时时间")
    private Date timeStamp;

    @ApiModelProperty(value = "最小投注金额")
    private Long minAmount;
    @ApiModelProperty(value = "最大投注金额")
    private Long maxAmount;


    public static PrizeQuizeVo transfer(PrizeQuizeRecord record,MinerPrizeQuizeTransaction transaction){
        PrizeQuizeVo vo = new PrizeQuizeVo();
        if (null != record) {
            vo.setActivityId(record.getId());
            vo.setUpNum(record.getUpNum());
            vo.setDownNum(record.getDownNum());
            vo.setUpAmount(record.getUpAmount());
            vo.setDownAmount(record.getDownAmount());
            vo.setRewardResultTime(record.getRewardResultTime());
            vo.setTotalAmount(record.getTotalAmount());
            vo.setTotalNum(record.getTotalNum());
            vo.setType(record.getType());
            Date finalizeTime = record.getFinalizeTime();
            Date now = new Date();

            vo.setTimeStamp( record.getFinalizeTime() );
            if (null != transaction) {
                vo.setStatus(0);
                vo.setType(1);
            } else {
                vo.setStatus(1);
                vo.setType(1);
            }
            int i = now.compareTo(finalizeTime);
            //如果当前时间大于了最后的投注时间 则不能投注
            if (i > 0){
                vo.setStatus(0);
                vo.setType(3);
            }

            //如果当前时间大于了活动时间 则活动结束
            i = now.compareTo(record.getRewardResultTime());
            if ( i > 0){
                vo.setStatus(0);
                vo.setType(2);
            }
        }
        return vo;
    }


}
