package com.spark.bitrade.trans;

import com.spark.bitrade.constant.ExchangeOrderDirection;
import com.spark.bitrade.entity.ExchangeCoin;
import com.spark.bitrade.entity.ExchangeOrder;
import com.spark.bitrade.entity.ExchangeTrade;
import lombok.Data;

import java.math.BigDecimal;

/**
 *  交易结算
 *
 * @author young
 * @time 2019.09.03 15:12
 */
@Data
public class TradeSettleDelta {
    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 订单号
     */
    private String orderId;

    /**
     * 关联订单号
     */
    private String refOrderId;

    /**
     * 交易对
     */
    private String symbol;

    /**
     * 成交数量
     */
    private BigDecimal amount;

    /**
     * 成交额
     */
    private BigDecimal turnover;

    /**
     * 成交价
     */
    private BigDecimal price;

    /**
     * 基币USD汇率
     */
    private BigDecimal baseUsdRate;

    /**
     * 收入币
     */
    private String incomeSymbol;
    /**
     * 收入币数量
     */
    private BigDecimal incomeCoinAmount;

    /**
     * 支出币
     */
    private String outcomeSymbol;

    /**
     * 支出币数量
     */
    private BigDecimal outcomeCoinAmount;

    /**
     * 收入手续费（tips：未优惠的手续费）
     */
    private BigDecimal fee;

    /**
     * 收入真实手续费
     */
    private BigDecimal realFee;
    /**
     * 收入优惠的手续费（买入订单收取coin,卖出订单收取baseCoin
     */
    private BigDecimal feeDiscount;

    /**
     * 结算
     *
     * @param order        订单信息
     * @param trade        撮合信息
     * @param exchangeCoin 交易对配置信息
     * @param discountRate 用户折扣率
     * @return
     */
    public static TradeSettleDelta settle(final ExchangeOrder order, final ExchangeTrade trade,
                                          final ExchangeCoin exchangeCoin, final DiscountRate discountRate) {
        TradeSettleDelta delta = new TradeSettleDelta();
        delta.setMemberId(order.getMemberId());
        delta.setOrderId(order.getOrderId());
        delta.setAmount(trade.getAmount());
        delta.setPrice(trade.getPrice());
        delta.setBaseUsdRate(trade.getBaseUsdRate());
        delta.setSymbol(trade.getSymbol());

        //设置成交的关联订单号
        if (order.getOrderId().equalsIgnoreCase(trade.getSellOrderId())) {
            delta.setRefOrderId(trade.getBuyOrderId());
        } else {
            delta.setRefOrderId(trade.getSellOrderId());
        }

        if (order.getDirection() == ExchangeOrderDirection.BUY) {
            //成交额
            delta.setTurnover(trade.getBuyTurnover());
            //手续费，买入时扣交易币
            delta.setFee(trade.getAmount().multiply(exchangeCoin.getFee()).setScale(exchangeCoin.getCoinScale(), BigDecimal.ROUND_UP));
            //买币优惠的手续费
            delta.setFeeDiscount(calculateFeeDiscount(delta.getFee(), exchangeCoin.getFeeBuyDiscount(), discountRate.getBuyDiscount()));

            //买入时获得交易币
            delta.setIncomeSymbol(order.getCoinSymbol());
            //增加可用的币，买入的时候获得交易币(减去手续费)
            delta.setIncomeCoinAmount(trade.getAmount().subtract(delta.getFee().subtract(delta.getFeeDiscount())));

            //买入时用 基币 支付
            delta.setOutcomeSymbol(order.getBaseSymbol());
            //扣除支付的币，买入的时候算成交额（基本）
            delta.setOutcomeCoinAmount(delta.getTurnover());
        } else {
            //成交额
            delta.setTurnover(trade.getSellTurnover());
            //手续费，买入时扣基币
            delta.setFee(delta.getTurnover().multiply(exchangeCoin.getFee()).setScale(exchangeCoin.getBaseCoinScale(), BigDecimal.ROUND_UP));
            //卖币优惠的手续费
            delta.setFeeDiscount(calculateFeeDiscount(delta.getFee(), exchangeCoin.getFeeSellDiscount(), discountRate.getSellDiscount()));

            //卖出时获得基币
            delta.setIncomeSymbol(order.getBaseSymbol());
            //增加可用的币,卖出的时候获得基币(减去手续费)
            delta.setIncomeCoinAmount(delta.getTurnover().subtract(delta.getFee().subtract(delta.getFeeDiscount())));

            //买入时用 交易币 支付
            delta.setOutcomeSymbol(order.getCoinSymbol());
            //扣除支付的币，卖出的算成交量（交易币）
            delta.setOutcomeCoinAmount(trade.getAmount());
        }
        delta.setRealFee(delta.getFee().subtract(delta.getFeeDiscount()));

        return delta;
    }

    /**
     * 计算优惠的手续费
     *
     * @param fee             手续费
     * @param feeDiscountRate 交易对配置的折扣率
     * @param discountRate    用户折扣率
     * @return
     */
    private static BigDecimal calculateFeeDiscount(BigDecimal fee, BigDecimal feeDiscountRate, BigDecimal discountRate) {
        BigDecimal feeDiscount;

        if (feeDiscountRate.compareTo(BigDecimal.ONE) >= 0) {
            //手续费全部优惠
            feeDiscount = fee;
        } else {
            //卖币优惠手续费数量
            feeDiscount = fee.multiply(feeDiscountRate).setScale(fee.scale(), BigDecimal.ROUND_DOWN);
            //优惠后的当前手续费
            BigDecimal remainingFee = fee.subtract(feeDiscount);
            //计算 当前会员可优惠手续费数量
            BigDecimal memberFeeDiscount = remainingFee.multiply(discountRate).setScale(fee.scale(), BigDecimal.ROUND_DOWN);

            feeDiscount = feeDiscount.add(memberFeeDiscount).setScale(fee.scale(), BigDecimal.ROUND_DOWN);
        }

        return feeDiscount;
    }
}
