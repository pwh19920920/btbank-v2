package com.spark.bitrade.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import com.spark.bitrade.core.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TransactionType implements BaseEnum {
    /**
     * 0 充值
     */
    RECHARGE("充值"),
    /**
     * 1 提现
     */
    WITHDRAW("提现"),
    /**
     * 2 转账
     */
    TRANSFER_ACCOUNTS("转账"),
    /**
     * 3 币币交易
     */
    EXCHANGE("币币交易"),
    /**
     * 4 法币买入
     */
    OTC_BUY("法币买入"),
    /**
     * 5 法币卖出
     */
    OTC_SELL("法币卖出"),
    /**
     * 6 活动奖励
     */
    ACTIVITY_AWARD("活动奖励"),
    /**
     * 7 推广奖励
     */
    PROMOTION_AWARD("推广奖励"),
    /**
     * 8 划转
     */
    TRANSFER("划转"),
    /**
     * 9 投票
     */
    VOTE("投票"),
    /**
     * 10 人工充值
     */
    ADMIN_RECHARGE("人工充值"),
    /**
     * 11 配对
     */
    MATCH("配对"),
    /**
     * 12 币币交易返佣奖励
     */
    EXCHANGE_PROMOTION_AWARD("币币交易返佣奖励"),
    /**
     * 13 币币交易合伙人奖励
     */
    EXCHANGE_PARTNER_AWARD("币币交易合伙人奖励"),
    /**
     * 14 商家认证保证金
     */
    BUSINESS_DEPOSIT("商家认证保证金"),
    /**
     * 15 锁仓充值
     */
    ADMIN_LOCK_RECHARGE("锁仓充值"),
    /**
     * 16 锁仓活动
     */
    ADMIN_LOCK_ACTIVITY("锁仓活动"),
    /**
     * 17 手动调账
     */
    ADMIN_ADJUST_BALANCE("手动调账"),
    /**
     * 18 理财锁仓
     */
    FINANCIAL_ACTIVITY("理财锁仓"),
    /**
     * 19 三方支付
     */
    THIRD_PAY("三方支付"),
    /**
     * 20 SLB节点产品
     */
    QUANTIFY_ACTIVITY("SLB节点产品"),
    /**
     * 21 SLB节点产品共识奖励
     */
    LOCK_COIN_PROMOTION_AWARD("SLB节点产品共识奖励"),
    /**
     * 22 STO锁仓
     */
    STO_ACTIVITY("STO锁仓"),
    /**
     * 23 STO推荐奖励
     */
    LOCK_COIN_PROMOTION_AWARD_STO("STO推荐奖励"),
    /**
     * 24 广告手续费
     */
    ADVERTISE_FEE("广告手续费"),
    /**
     * 25 闪兑
     */
    EXCHANGE_FAST("闪兑"),
    /**
     * 26 IEO锁仓活动
     */
    IEO_ACTIVITY("IEO锁仓活动"),
    /**
     * 27 活期宝活动
     */
    HQB_ACTIVITY("活期宝活动"),
    /**
     * 28 本人锁仓奖励
     */
    GOLD_KEY_OWN("本人锁仓奖励"),
    /**
     * 29 金钥匙团队锁仓
     */
    GOLD_KEY_TEAM("团队锁仓奖励"),
    /**
     * 30 BCC赋能计划
     */
    ENERGIZE_LOCK("BCC赋能计划"),
    /**
     * 31 参与布朗活动
     */
    SLP_LOCK("参与布朗活动"),
    /**
     * 32 布朗活动释放收益
     */
    SLP_LOCK_RELEASE("布朗活动释放收益"),
    /**
     * 33 超级合伙人手续费20%奖励
     */
    SUPER_PARTNER_AWARD("超级合伙人手续费20%奖励"),
    /**
     * 34 超级合伙人锁仓
     */
    SUPER_PARTNER_LOCK("超级合伙人锁仓"),
    /**
     * 35 退出超级合伙人
     */
    SUPER_PARTNER_EXIT("退出超级合伙人"),
    /**
     * 36 违约退出社区
     */
    SUPER_EXIT_COMMUNITY("违约退出社区"),
    /**
     * 37 超级合伙人活跃成员奖励
     */
    SUPER_PARTNER_ACTIVE_AWARD("超级合伙人活跃成员奖励"),
    /**
     * 38 微信/支付宝-直接支付
     */
    DIRECT_PAY("微信/支付宝-直接支付"),
    /**
     * 39 UTT活动锁仓
     */
    LOCK_UTT("UTT活动锁仓"),
    /**
     * 40 UTT活动释放
     */
    UNLOCK_UTT("UTT活动释放"),
    /**
     * 41 微信/支付宝-直接支付收益归集
     */
    DIRECT_PAY_PROFIT("微信/支付宝-直接支付收益归集"),
    /**
     * 42 法币交易手续费归集
     */
    OTC_JY_RATE_FEE("法币交易手续费归集"),
    /**
     * 43 孵化区锁仓
     */
    INCUBOTORS_LOCK("孵化区锁仓"),
    /**
     * 44 孵化区解仓
     */
    INCUBOTORS_UNLOCK("孵化区解仓"),
    /**
     * 法币广告手续费归集
     */
    ADVERTISE_FEE_COLLECTION("法币广告手续费归集"),
    /**
     * BB交易手续费归集
     */
    EXCHANGE_FEE_COLLECTION("BB交易手续费归集"),
    /**
     * 直属矿工推荐奖励 47
     */
    DIRECT_MINER_REWARD("直属矿工推荐奖励"),
    /**
     * 金牌矿工奖励 48
     */
    GOLDEN_MINER_REWARD("金牌矿工奖励"),
    /**
     * 商家OTC出售奖励 49
     */
    OTC_BUSINESS_SALE_REWARED("商家OTC出售奖励"),
    /**
     * 平台补贴 50
     */
    OTC_BUSINESS_SALE_REWARED_PLATFORM("平台补贴"),

    /**
     * 提现冻结51
     */
    OTC_WITHDRAW_FROZEN("提现冻结"),

    /**
     * 煤球2.1.1
     * 商家挖矿佣金52
     */
    BUSINESS_MINNER_REWARD("商家挖矿佣金"),

    /**
     * 每日收益排行奖励 53
     */
    REVENUE_DAY_RANKING_REWARD("每日收益排行奖励"),

    /**
     * 每日推广收益排行奖励 54
     */
    REVENUE_DAY_EXTENSION_RANKING_REWARD("每日推广收益排行奖励"),

    /**
     * 理财活动（锁仓 为负数，（释放，撤销） 为正数） 55
     */
    FINANCIAL_ACTIVITY_LOCK("大宗挖矿"),
    /**
     * 理财活动利息 56
     */
    FINANCIAL_ACTIVITY_PROFIT("大宗挖矿利息"),

    /**
     * 企业矿工转出 57
     */
    ENTERPRISE_TRANSFER_OUT("转出到企业矿池"),

    /**
     * 企业矿工转入 58
     */
    ENTERPRISE_TRANSFER_IN("从企业矿池转入"),

    /**
     * 企业矿工挖矿归集 59
     */
    ENTERPRISE_MINER_COLLECT("企业矿工挖矿归集"),

    /**
     * 企业矿工挖矿奖励 60
     */
    ENTERPRISE_MINER_REWARD("企业矿工挖矿奖励"),
    /**
     * BTC竞猜 61
     */
    PRIZEQUIZE_TRANSFER_FREEZE("竞猜投注锁定"),
    /**
     * BTC竞猜 62
     */
    PRIZEQUIZE_TRANSFER_FAIL("竞猜失败"),
    /**
     * BTC竞猜 63
     */
    PRIZEQUIZE_TRANSFER_REWARD("竞猜分红"),

    /**
     * BTC竞猜 64
     */
    PRIZEQUIZE_TRANSFER_REALSE("竞猜投注解锁"),

    /**
     * BTC竞猜 65
     */
    PRIZEQUIZE_TRANSFER_COLLECT("竞猜归集"),

    /**
     * 幸运大转盘兑换 66
     */
    TURNTABLE_EXCHANGE_CHANGE("幸运大转盘"),

    /**
     * 大宗挖矿 67
     */
    FINANCIAL_ACTIVITY_RECOMMEND_REWARD("大宗挖矿直推佣金"),
    /**
     * 线上购汇 68
     */
    FOREIGN_EXCHANGE_ONLINE("线上购汇"),
    /**
     * 线下取现 69
     */
    FOREIGN_EXCHANGE_OFFLINE("线下取现"),
    /**
     * 换汇归集   70
     */
    FOREIGN_EXCHANGE_COLLECT("换汇归集"),
    /**
     * 取消购汇  71
     */
    FOREIGN_EXCHANGE_CANCEL("取消换汇"),

    /**
     * 取消换汇手续费  72
     */
    FOREIGN_EXCHANGE_SERVICE_CHARGE("取消换汇手续费"),

    /**
     * otc提提现取消解冻73
     */
    OTC_WITHDRAW_FROZEN_CANCEL("提现取消解冻"),
    //74
    NEW_MEMBER_EXPERIENCE_AMOUNT("新矿工体验金"),
    //75
    NEW_MEMBER_EXPERIENCE_AMOUNT_LOCK("新矿工体验金锁仓"),
    //76
    NEW_MEMBER_RELEASE_AMOUNT_LOCK("新矿工体验金释放"),
    //77
    CREDIT_CARD_COMMISSION("(信用卡手续费)代还手续费返还"),
    //78
    CREDIT_CARD_COMMISSION_LOCK("(信用卡手续费)代还手续费返还-锁仓"),
    //79
    CREDIT_CARD_COMMISSION_RELEASE("(信用卡手续费)代还手续费返还-释放"),
    //80
    WELFARE_NEW_PACKET_BUY("新人福利挖矿"),
    //81设计保留
    WELFARE_NEW_PACKET_REFUND("新人福利挖矿撤回"),
    //82
    WELFARE_NEW_PACKET_INTEREST("新人福利挖矿利息"),
    //83
    WELFARE_NEW_PACKET_REWARD("新人福利挖矿直推佣金"),
    //84
    WELFARE_INCR_PACKET_BUY("增值福利挖矿"),
    //85设计保留
    WELFARE_INCR_PACKET_REFUND("增值福利挖矿撤回"),
    //86
    WELFARE_INCR_PACKET_INTEREST("增值福利挖矿利息"),
    //87
    WELFARE_INCR_PACKET_REWARD("增值福利挖矿直推佣金"),

    EXT2("占位，扩展9"),

    EXT3("占位，扩展9"),
    ;

    private String cnName;

    @Override
    @JsonValue
    public int getOrdinal() {
        return this.ordinal();
    }

    public static TransactionType valueOfOrdinal(int ordinal) {
        TransactionType[] values = TransactionType.values();
        for (TransactionType transactionType : values) {
            int o = transactionType.getOrdinal();
            if (o == ordinal) {
                return transactionType;
            }
        }
        return null;
    }
}
