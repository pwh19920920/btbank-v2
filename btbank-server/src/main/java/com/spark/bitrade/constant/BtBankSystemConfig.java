package com.spark.bitrade.constant;

public class BtBankSystemConfig {

    public static final String BT_BANK_MINER_CONFIG = "BT_BANK_MINER_CONFIG";


    public static final String REDIS_MINER_ORDER_PREFIX = "entity:btbank:minner:order:";

    public static final String REDIS_DICT_PREFIX = "entity:btbank:dict:";
    public static final String REDIS_OTC_DICT_PREFIX = "entity:btbank:dict:otc:";
    public static final String REDIS_FOREIGN_DICT_PREFIX = "entity:btbank:dict:foreign:";

    public static final String REDIS_PRIZE_QUIZE_PREFIX = "entity:btbank:prizequize:dict:";


    /**
     * 秒杀抢单佣金比例    金牌/银牌创世矿工抢单佣金比例
     */
    public static final String SECKILL_COMMISSION_RATE = "SECKILL_COMMISSION_RATE";

    /**
     * 派单佣金比例   金牌/银牌创世矿工派单佣金比例
     */
    public static final String DISPATCH_COMMISSION_RATE = "DISPATCH_COMMISSION_RATE";

    /**
     * 固定佣金比例    金牌/银牌创世矿工固定佣金比例
     */
    public static final String FIXED_COMMISSION_RATE = "FIXED_COMMISSION_RATE";
    /**
     * 矿池最低划转金额
     */
    public static final String MINIMUM_TRANSFER_AMOUNT = "MINIMUM_TRANSFER_AMOUNT";

    /**
     * 接单开关
     */
    public static final String RECEIVING_ORDER_SWITCH = "RECEIVING_ORDER_SWITCH";

    /**
     * 转入开关
     */
    public static final String TRANSFER_SWITCH = "TRANSFER_SWITCH";

    /**
     * 抢单开关
     */
    public static final String SECKILL_SWITCH = "SECKILL_SWITCH";

    /**
     * 派单开关
     */
    public static final String DISPATCH_SWITCH = "DISPATCH_SWITCH";

    /**
     * 奖励财务账户
     */
    public static final String BTBANK_REWARD_SOURCE = "BTBANK_REWARD_SOURCE";

    /**
     * 派单时间，分钟
     */
    public static final String DISPATCH_TIME = "DISPATCH_TIME";

    /**
     * 抢单解锁时间，分钟
     */
    public static final String UNLOCK_TIME = "UNLOCK_TIME";

    /**
     * App自动刷新矿池抢单列表间隔，秒
     */
    public static final String AUTO_REFRESH_RATE = "AUTO_REFRESH_RATE";


    /**
     * 银牌矿工佣金比例
     */
    public static final String SILVER_MINER_COMMISSION_RATE = "SILVER_MINER_COMMISSION_RATE";

    /**
     * 推荐新矿工注册后，新矿工7日内获得的挖矿收益，推荐人可获得100%的推荐收益结束时间
     */
    public static final String RECOMMEND_NEW_MINER_ENDTIME = "RECOMMEND_NEW_MINER_ENDTIME";

    /**
     * 金牌矿工佣金比例
     */
    public static final String GOLD_MINER_COMMISSION_RATE = "GOLD_MINER_COMMISSION_RATE";

    /**
     * 直推新矿工佣金比例
     */
    public static final String RECOMMEND_NEW_MINER_COMMISSION_RATE = "RECOMMEND_NEW_MINER_COMMISSION_RATE";

    /**
     * 企业矿工佣金发放账号
     */
    public static final String ENTERPRISE_MINER_COMMISSION_ACCOUNT = "ENTERPRISE_MINER_COMMISSION_ACCOUNT";

    /**
     * 企业矿池归集账号
     */
    public static final String ENTERPRISE_MINER_RECEIVE_ACCOUNT = "ENTERPRISE_MINER_RECEIVE_ACCOUNT";

    /**
     * 企业挖矿佣金比例
     */
    public static final String ENTERPRISE_MINER_COMMISSION_RATE = "ENTERPRISE_MINER_COMMISSION_RATE";
    /**
     * 企业矿池最低划转比例
     */
    public static final String ENTERPRISE_MINIMUM_TRANSFER_AMOUNT = "ENTERPRISE_MINIMUM_TRANSFER_AMOUNT";

    /**
     * 转盘活动开关 1：开启 0：关闭
     */
    public static final String TURNTABLE_ACTIVITY_SWITCH = "TURNTABLE_ACTIVITY_SWITCH";

    /**
     * 转盘活动 新用户注册时间
     */
    public static final String TURNTABLE_MINER_REGISTRATION_TIME = "TURNTABLE_MINER_REGISTRATION_TIME";

    /**
     * 转盘抽奖BT奖品发放账户
     */
    public static final String TURNTABLE_REWARD_ACCOUNT = "TURNTABLE_REWARD_ACCOUNT";

    /**
     * 最低取现BT数量
     */
    public static final String EXCHANGE_LIMIT = "EXCHANGE_LIMIT";
    /**
     * 线上取现手续费
     */
    public static final String EXCHANGE_ONLINE_RATE = "EXCHANGE_ONLINE_RATE";
    /**
     * 线下取现手续费
     */
    public static final String EXCHANGE_OFFLINE_RATE = "EXCHANGE_OFFLINE_RATE";
    /**
     * 线上取现开关板1开启，0关闭
     */
    public static final String EXCHANGE_ONLINE_SWITCH = "EXCHANGE_ONLINE_SWITCH";
    /**
     * 线下取现关板1开启，0
     */
    public static final String EXCHANGE_OFFLINE_SWITCH = "EXCHANGE_OFFLINE_SWITCH";

    /**
     * 换汇中心账号
     */
    public static final String EXCHANGE_COLLECT_ACCOUNT = "EXCHANGE_COLLECT_ACCOUNT";
    /**
     * 换汇中心账号
     */
    public static final String EXCHANGE_LIMITONLINE = "EXCHANGE_LIMITONLINE";
    /**
     * 新矿工注册福利开关
     */
    public static final String NEW_MEMBER_EXPERIENCE_SWITCH = "NEW_MEMBER_EXPERIENCE_SWITCH";
    /**
     * 新矿工注册福利 收益满足条件
     */
    public static final String NEW_MEMBER_EXPERIENCE_AMOUNT_PROFIT = "NEW_MEMBER_EXPERIENCE_AMOUNT_PROFIT";
    /**
     * 新矿工注册福利 增送的体验金
     */
    public static final String NEW_MEMBER_EXPERIENCE_GIVE_AMOUNT = "NEW_MEMBER_EXPERIENCE_GIVE_AMOUNT";
    /**
     * 新矿工注册福利 注册时间
     */
    public static final String NEW_MEMBER_EXPERIENCE_REGISTER_TIME = "NEW_MEMBER_EXPERIENCE_REGISTER_TIME";
    /**
     * 商家挖矿订单列表最大显示数量
     */
    public static final String OTC_DIG_ORDER_MAX_DISPLAY = "OTC_DIG_ORDER_MAX_DISPLAY";
    /**
     * 老用户矿工福利 累计收益满足条件
     */
    public static final String OLD_MEMBER_INVITER_PROFIT_TOTAL_CONDITION = "OLD_MEMBER_INVITER_PROFIT_TOTAL_CONDITION";
    /**
     * 老用户矿工福利 奖励金额
     */
    public static final String OLD_MEMBER_INVITER_WARD_AMOUNT = "OLD_MEMBER_INVITER_WARD_AMOUNT";

    /**
     * 银牌创世矿工注册截止时间
     */
    public static final String SILVER_CREATION_REGISTER_TIME_END = "SILVER_CREATION_REGISTER_TIME_END";
    /**
     * 银牌矿工抢单佣金比例
     */
    public static final String SILVER_MINER_GRAB_COMMISSION_RATE = "SILVER_MINER_GRAB_COMMISSION_RATE";
    /**
     * 银牌矿工派单佣金比例
     */
    public static final String SILVER_MINER_DISPATCH_COMMISSION_RATE = "SILVER_MINER_DISPATCH_COMMISSION_RATE";
    /**
     * 银牌矿工固定佣金收益比例
     */
    public static final String SILVER_MINER_FIXED_COMMISSION_RATE = "SILVER_MINER_FIXED_COMMISSION_RATE";
    /**
     * 4月1日后直推矿工注册前30天/31-60天收益比例
     */
    public static final String APRIL_ONE_REGISTER_30_31_60_PROFIT_RATE="APRIL_ONE_REGISTER_30_31_60_PROFIT_RATE";
    /**
     *
     */
    public static final String CREDIT_CARD_COMMISSION_RELEASE_RATE="CREDIT_CARD_COMMISSION_RELEASE_RATE";
    /**
     * 渠道号
     */
    public static final String CREDIT_PARTNER_NO="CREDIT_PARTNER_NO";
    /**
     * 链上优选URL
     */
    public static final String CREDIT_GET_TOKEN_URL="CREDIT_GET_TOKEN_URL";
    /**
     * 新人福利包购买资格封顶数 默认10
     */
    public static final String WELFARE_NEW_TOP_LIMIT = "WELFARE_NEW_TOP_LIMIT";
    /**
     * 新人福利包赠送购买资格有效天数 默认21
     */
    public static final String WELFARE_NEW_EXPIRED_DAYS = "WELFARE_NEW_EXPIRED_DAYS";
}
