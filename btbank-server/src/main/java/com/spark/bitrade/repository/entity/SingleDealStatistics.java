package com.spark.bitrade.repository.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 交易日统计OTC 交易日为当前天16点至前一天16点
 * @author qiuyuanjie
 * @since 2020-03-25
 */
@SuppressWarnings("serial")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SingleDealStatistics {

    @TableId
    private Long id;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 当前交易日OTC购买总和
     */
    private BigDecimal currentBuyTotal;

    /**
     * 当前交易日OTC出售总和
     */
    private BigDecimal currentSellTotal;

    /**
     * 交易统计类型
     */
    private Integer type;
}
