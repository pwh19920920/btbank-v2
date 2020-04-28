package com.spark.bitrade.trans;

import lombok.Data;

import java.math.BigDecimal;

/**
 *  币币交易折率
 *
 * @author young
 * @time 2019.09.04 10:36
 */
@Data
public class DiscountRate {
    /**
     * 买出折扣率
     */
    private BigDecimal buyDiscount;
    /**
     * 卖入折扣率
     */
    private BigDecimal sellDiscount;

    public DiscountRate(BigDecimal buyDiscount, BigDecimal sellDiscount) {
        if (buyDiscount.compareTo(BigDecimal.ONE) > 0) {
            this.buyDiscount = BigDecimal.ONE;
        } else {
            this.buyDiscount = buyDiscount;
        }

        if (sellDiscount.compareTo(BigDecimal.ONE) > 0) {
            this.sellDiscount = BigDecimal.ONE;
        } else {
            this.sellDiscount = sellDiscount;
        }
    }
}
