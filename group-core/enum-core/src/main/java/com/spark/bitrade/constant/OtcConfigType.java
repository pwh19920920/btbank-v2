package com.spark.bitrade.constant;

/**
 * @author ww
 * @time 2019.11.29 10:07
 */
public class OtcConfigType {
    //自动下架最低余额
    public static final String OTC_AD_AUTO_DOWN_BALANCE = "OTC_AD_AUTO_DOWN_BALANCE";
    //创建OTC订单最小全额限制
    public static final String OTC_AD_CREATE_LOW_BALANCE_LIMIT = "OTC_AD_CREATE_LOW_BALANCE_LIMIT";
    //商家销售奖励比例
    public static final String OTC_BUSINESS_SALE_REWARD_RATE = "OTC_BUSINESS_SALE_REWARD_RATE";
    /*
    出售广告奖励账户
     */
    public static final String OTC_SALE_REWARD_PAY_ACCOUNT = "OTC_SALE_REWARD_PAY_ACCOUNT";

    /*
    出售广告累计在线时长
     */
    public static final String OTC_SALE_REWARD_SUBSIDY_AD_ONLINE_DURATION = "OTC_SALE_REWARD_SUBSIDY_AD_ONLINE_DURATION";

    /*
    出售广告收益补贴最大额
     */
    public static final String OTC_SALE_REWARD_SUBSIDY_MAX = "OTC_SALE_REWARD_SUBSIDY_MAX";

    /*
    商家出售广告补贴排除用户ID（多个以英文文逗号分开）
     */
    public static final String OTC_SALE_REWARD_SUBSIDY_ACCOUNT_EXCLUDE = "OTC_SALE_REWARD_SUBSIDY_ACCOUNT_EXCLUDE";

    /**
     * OTC一键提现服务费比例
     */
    public static final String OTC_WITHDRAW_SERVICE_RATE = "OTC_WITHDRAW_SERVICE_RATE";
    /**
     * OTC提现限制开始时间
     */
    public static final String OTC_WITHDRAW_LIMIT_MIN = "OTC_WITHDRAW_LIMIT_MIN";
    /**
     * OTC提现限制结束时间
     */
    public static final String OTC_WITHDRAW_LIMIT_MAX = "OTC_WITHDRAW_LIMIT_MAX";
    /**
     * OTC提现限制排队中的订单数目
     */
    public static final String OTC_DIG_ORDER_MAX_DISPLAY = "OTC_DIG_ORDER_MAX_DISPLAY";
    /**
     * 00:00-08:00上调提现手续费倍数
     */
    public static final String UPOTC_WITHDRAW_SERVICE_RATE = "UPOTC_WITHDRAW_SERVICE_RATE";
    /**
     * OTC一键提现处罚中服务费比例
     */
    public static final String OTC_WITHDRAW_PUNISHMENT_SERVICE_RATE = "OTC_WITHDRAW_PUNISHMENT_SERVICE_RATE";

    /**
     * otc商家挖矿奖励比例
     */
    public static final String OTC_MINER_COMMISSION_RATE = "OTC_MINER_COMMISSION_RATE";

    /**
     * 获取暂停   商家发布的广告连续下架时间
     */
    public static final String OTC_PAUSED_BIZ_AD_OFF_HOUR_SPAN = "OTC_PAUSED_BIZ_AD_OFF_HOUR_SPAN";

    /**
     * 获取当长时间休业后暂停商业时间
     */
    public static final String OTC_PAUSED_BIZ_HOURS_WHEN_CLOSING_LONG_TIME = "OTC_PAUSED_BIZ_HOURS_WHEN_CLOSING_LONG_TIME";

    /**
     * OTC 出金 人脸识别验证开关
     */
    public static final String OTC_FACE_AUTHENTICATION_SWITCH = "OTC_FACE_AUTHENTICATION_SWITCH";
}
