package com.spark.bitrade.constant;

import com.spark.bitrade.constants.MsgCode;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.util.MessageRespResult;

/**
 * btbank错误码定义
 *
 * @author archx
 * @since 2019/5/8 18:05
 */
public enum BtBankMsgCode implements MsgCode {

    /**
     * 定单已经被抢或被派出
     */
    ORDERS_HAVE_LOOTED_OR_DISPATCHED(71000, "ORDERS_HAVE_LOOTED_OR_DISPATCHED"),

    /**
     * 收单异常
     */
    ORDER_RECEIVED_ABNORMAL(71001, "ORDER_RECEIVED_ABNORMAL"),

    /**
     * 转入开关关闭,活动暂时关闭，敬请期待
     */
    TURN_IN_SWITCH_OFF(71002, "TURN_IN_SWITCH_OFF"),

    /**
     * 单次转入不能低于最小限额
     */
    BELOW_THE_MINIMUM(71003, "BELOW_THE_MINIMUM"),

    /**
     * 抢单失败
     */
    FAILED_TO_SNATCH_THE_ORDER(71004, "FAILED_TO_SNATCH_THE_ORDER"),

    /**
     * 载入系统配置失败
     */
    FAILED_TO_LOAD_SYSTEM_CONFIGURATION(71005, "FAILED_TO_LOAD_SYSTEM_CONFIGURATION"),

    /**
     * 暂无可解锁订单
     */
    UNLOCKING_ORDERS_ARE_NOT_AVAILABLE(71006, "UNLOCKING_ORDERS_ARE_NOT_AVAILABLE"),

    /**
     * 添加抢单订单记录失败
     */
    FAILED_TO_ADD_GRAB_ORDER_RECORD(71007, "FAILED_TO_ADD_GRAB_ORDER_RECORD"),

    /**
     * 添加抢单本金转出记录失败
     */
    ADD_ORDER_PRINCIPAL_TRANSFER_RECORD_FAILED(71008, "ADD_ORDER_PRINCIPAL_TRANSFER_RECORD_FAILED"),

    /**
     * 添加抢单本金佣金记录失败
     */
    FAILED_TO_RECORD_THE_PRINCIPAL_COMMISSION(71009, "FAILED_TO_RECORD_THE_PRINCIPAL_COMMISSION"),

    /**
     * 余额变动失败
     */
    BALANCE_CHANGE_FAILED(71010, "BALANCE_CHANGE_FAILED"),

    /**
     * 派单开关已关闭
     */
    DELIVERY_SWITCH_IS_OFF(71011, "DELIVERY_SWITCH_IS_OFF"),

    /**
     * 暂无符合派单的订单
     */
    NO_ORDER_IN_LINE_WITH_THE_ORDER(71012, "NO_ORDER_IN_LINE_WITH_THE_ORDER"),

    /**
     * 修改订单失败
     */
    FAILED_TO_MODIFY_THE_ORDER(71013, "FAILED_TO_MODIFY_THE_ORDER"),

    /**
     * 添加订单记录失败
     */
    FAILED_TO_ADD_ORDER_RECORD(71014, "FAILED_TO_ADD_ORDER_RECORD"),

    /**
     * 添加余额记录失败
     */
    FAILED_TO_ADD_BALANCE_RECORD(71015, "FAILED_TO_ADD_BALANCE_RECORD"),

    /**
     * 余额记录可用不足
     */
    INSUFFICIENT_BALANCE_RECORD_AVAILABLE(71016, "INSUFFICIENT_BALANCE_RECORD_AVAILABLE"),

    /**
     * 暂时无法抢单，请稍后再试
     */
    UNABLE_TO_SNATCH_THE_ORDER(71017, "UNABLE_TO_SNATCH_THE_ORDER"),
    /**
     * 订单不存在
     */
    ORDER_NOT_EXIST(71018, "ORDER_NOT_EXIST"),

    /**
     * 申请金牌矿工失败
     */
    APPLY_GOLD_MINER_FAIL(71019, "APPLY_GOLD_MINER_FAIL"),


    /**
     * 升级时推荐人数不足
     */
    RECOMMEND_MINER_NUMBER_NOT_ENOUGH(71020, "RECOMMEND_MINER_NUMBER_NOT_ENOUGH"),


    /**
     * 非银牌矿工无法申请金牌旷工
     */
    ONLY_SILVER_CAN_APPLY_GLOD(71021, "ONLY_SILVER_CAN_APPLY_GLOD"),

    /**
     * 有其它申请存在
     */
    HAVE_ANOTHER_MINER_GRADE_APPLY(71022, "HAVE_ANOTHER_MINER_GRADE_APPLY"),
    /**
     * 金牌矿工申请通过
     */
    APPLY_GOLD_MINER_PASSED(71023, "APPLY_GOLD_MINER_PASSED"),
    /**
     * 金牌矿工申请被拒绝
     */
    APPLY_GOLD_MINER_REFUSED(71024, "APPLY_GOLD_MINER_REFUSED"),

    /**
     * 释放奖励失败
     */
    RELEASE_REWARD_FAILED(71025, "RELEASE_REWARD_FAILED"),

    /**
     * WebSOCKET 推送订单状态 改变
     */
    WEBSOCKET_ORDER_STATUS_CHANGED(71026, "ORDER_STATUS_CHANGED"),

    /**
     * 用户可以申请金牌矿工
     */
    MINER_CAN_APPLY_TO_GOLD(71027, "MINER_CAN_APPLY_TO_GOLD"),

    /**
     * 用户申请金牌矿工成功
     */
    MINER_APPLY_GOLD_SUCCESS(71028, "MINER_APPLY_GOLD_SUCCESS"),

    /**
     * 不支持的支付方式
     */
    UNSUPPORTED_PAYMENT_METHOD(71029, "UNSUPPORTED_PAYMENT_METHOD"),

    /**
     * 银行账户不存在
     */
    BANK_ACCOUNT_NOT_EXIST(71030, "BANK_ACCOUNT_NOT_EXIST"),

    /**
     * 非法的转账数量
     */
    ILLEGAL_TRANSFER_AMOUNT(71031, "ILLEGAL_TRANSFER_AMOUNT"),

    /**
     * 非法的抢单
     */
    ILLEGAL_OTC_BUY(71032, "ILLEGAL_OTC_BUY"),

    /**
     * 提现进行中
     */
    WITHDRAWAL_IN_PROGRESS(71033, "WITHDRAWAL_IN_PROGRESS"),

    /**
     * 非认证商家
     */
    NON_CERTIFIED_MERCHANT(71034, "NON_CERTIFIED_MERCHANT"),

    /**
     * 非认证用户
     */
    NON_CERTIFIED_MEMBER(71035, "NON_CERTIFIED_MEMBER"),

    /**
     * 不能抢自己的单
     */
    FORBID_GRAB_OWN(71036, "FORBID_GRAB_OWN"),


    /**
     * 补贴支付帐户用户不存在
     */
    SALE_REWARD_PAY_MEMBER_NOT_EXISTS(71037, "MEMBER_NOT_EXISTS"),

    /**
     * 您有未完成的订单，请先完成
     */
    HAVE_AN_UNFINISHED_ORDER(71038, "HAVE_AN_UNFINISHED_ORDER"),
    /**
     * 订单不存在
     */
    OTC_ORDER_NOT_EXSITS(71039, "OTC_ORDER_NOT_EXSITS"),
    /**
     * 订单需要先释放
     */
    OTC_ORDER_NEED_RELEASE(71040, "OTC_ORDER_NEED_RELEASE"),
    /**
     * 最低提现金额限制
     */
    MIN_WITHDRAW_AMOUNT(71041, "MIN_WITHDRAW_AMOUNT"),
    /**
     * 提现余额不足
     */
    INSUFFICIENT_WITHDRAWAL_BALANCE(71042, "INSUFFICIENT_WITHDRAWAL_BALANCE"),
    /**
     * 发放收益排行榜奖励失败
     */
    RELEASE_RANK_REWARD_FAILED(71043, "RELEASE_RANK_REWARD_FAILED"),

    /**
     * 更新活动状态失败
     */
    UPDATE_ACTIVITY_FAILED(71044, "UPDATE_ACTIVITY_FAILED"),
    /**
     * 查找活动失败
     */
    FIND_ACTIVITY_FAILED(71045, "FIND_ACTIVITY_FAILED"),
    /**
     * 已经参加过活动
     */
    ALREADY_ATTEND_ACTIVITY(71046, "ALREADY_ATTEND_ACTIVITY"),
    /**
     * 活动未开始
     */
    ACTIVITY_NOT_START(71047, "ACTIVITY_NOT_START"),
    /**
     * 活动已结束
     */
    ACTIVITY_END(71048, "ACTIVITY_END"),
    /**
     * 参加份额超过个人最大限制
     */
    ACTIVITY_OVER_MAX_LIMIT(71049, "ACTIVITY_OVER_MAX_LIMIT"),
    /**
     * 参加份额超过剩余份额
     */
    ACTIVITY_OVER_MAX(71050, "ACTIVITY_OVER_MAX"),
    /**
     * 购买份数不能为负数
     */
    ACTIVITY_PURCHASE_NUM_ERROR(71051, "ACTIVITY_PURCHASE_NUM_ERROR"),


    /**
     * 操作过于频繁frequency
     */
    ACTIVITY_PURCHASE_FREQUENCY(71052, "ACTIVITY_PURCHASE_FREQUENCY"),
    /**
     * 企业矿工不可用
     */
    ENTERPRISE_MINER_UNAVAILABLE(71053, "ENTERPRISE_MINER_UNAVAILABLE"),

    /**
     * 企业矿工矿池余额不足
     */
    ENTERPRISE_MINER_BALANCE_NOT_ENOUGH(71054, "ENTERPRISE_MINER_BALANCE_NOT_ENOUGH"),

    /**
     * 企业矿工未找到 不是有效的企业矿工
     */
    ENTERPRISE_MINER_NOT_FOUND(71055, "ENTERPRISE_MINER_NOT_FOUND"),

    /**
     * 企业矿工挖矿订单未找到
     */
    ENTERPRISE_MINER_ORDER_NOT_FOUND(71056, "ENTERPRISE_MINER_ORDER_NOT_FOUND"),

    /**
     * 企业矿工挖矿订单已存在
     */
    ENTERPRISE_MINER_ORDER_EXISTS(71057, "ENTERPRISE_MINER_ORDER_EXISTS"),

    /**
     * 企业矿工申请不存在
     */
    ENTERPRISE_MINER_APPLY_NOT_FOUND(71058, "ENTERPRISE_MINER_APPLY_NOT_FOUND"),
    /**
     * 企业矿工申请已存在
     */
    ENTERPRISE_MINER_APPLY_EXIST(71059, "ENTERPRISE_MINER_APPLY_EXIST"),
    /**
     * 企业矿工申请类型错误
     */
    ENTERPRISE_MINER_APPLY_TYPE_ERROR(71060, "ENTERPRISE_MINER_APPLY_TYPE_ERROR"),
    /**
     * 已经是企业矿工
     */
    ENTERPRISE_MINER_IS_ALLREADY(71061, "ENTERPRISE_MINER_IS_ALLREADY"),

    /**
     * 已经不是企业矿工
     */
    ENTERPRISE_MINER_IS_NOTEXIST(71062, "ENTERPRISE_MINER_IS_ALLREADY"),
    /**
     * 企业矿池有可用余额，请先转出
     */
    ENTERPRISE_MINER_BALANCE_NOTZERO(71063, "ENTERPRISE_MINER_BALANCE_NOTZERO"),

    /**
     * 划转数量不能少于5000BT
     */
    ENTERPRISE_MINER_TRANSFER_MINLIMIT(71064, "ENTERPRISE_MINER_TRANSFER_MINLIMIT"),
    /**
     * 用户可用余额不足
     */
    ENTERPRISE_MINER_BALANCE_NOTENOUGH(71065, "ENTERPRISE_MINER_BALANCE_NOTENOUGH"),

    /**
     * 企业矿池可用余额不足
     */
    ENTERPRISE_MINER_ENTERPRIZE_BALANCE_NOTENOUGH(71066, "ENTERPRISE_MINER_ENTERPRIZE_BALANCE_NOTENOUGH"),

    /**
     * 请填写正确中文姓名
     */
    ENTERPRISE_MINER_NEMAE_CHINESE(71067, "ENTERPRISE_MINER_NEMAE_CHINESE"),
    /**
     * 请填写正确手机号码
     */
    ENTERPRISE_MINER_MOBILE_PHONE(71068, "ENTERPRISE_MINER_MOBILE_PHONE"),
    /**
     * 请填写正确身份证号码
     */
    ENTERPRISE_MINER_ID_CARD(71069, "ENTERPRISE_MINER_ID_CARD"),
    /**
     * 请填上传身份证正面图片
     */
    ENTERPRISE_MINER_ID_CARD_FRONT(71070, "ENTERPRISE_MINER_ID_CARD_FRONT"),
    /**
     * 请填上传身份证反面图片
     */
    ENTERPRISE_MINER_ID_CARD_BACK(71071, "ENTERPRISE_MINER_ID_CARD_BACK"),
    /**
     * 请填上传手持身份证图片
     */
    ENTERPRISE_MINER_ID_CARD_HAND(71072, "ENTERPRISE_MINER_ID_CARD_HAND"),
    /**
     * 请填上传营业执照图片
     */
    ENTERPRISE_MINER_BUSINESS_LICENSE(71073, "ENTERPRISE_MINER_BUSINESS_LICENSE"),

    /**
     * 请填上传营业执照图片
     */
    ENTERPRISE_MINER_EMAIL(71074, "ENTERPRISE_MINER_EMAIL"),

    /**
     * 投注金额不能为负数
     */
    PRIZE_QUIZE_AMOUNT_INVALID(71075, "PRIZE_QUIZE_AMOUNT_INVALID"),

    /**
     * 投注失败
     */
    PRIZE_QUIZE_TRANFERIN_ERROR(71076, "PRIZE_QUIZE_TRANFERIN_ERROR"),

    /**
     * 投注活动失败
     */
    PRIZE_QUIZE_JOIN_ERROR(71077, "PRIZE_QUIZE_JOIN_ERROR"),

    /**
     * 活动不存在
     */
    TURNTABLE_ACTIVITY_NOT_FOUND(71078, "TURNTABLE_ACTIVITY_NOT_FOUND"),

    /**
     * 活动已暂停
     */
    TURNTABLE_ACTIVITY_PAUSED(71079, "TURNTABLE_ACTIVITY_PAUSED"),

    /**
     * 活动未开始
     */
    TURNTABLE_ACTIVITY_NOT_STARTED(71080, "TURNTABLE_ACTIVITY_NOT_STARTED"),

    /**
     * 活动已结束
     */
    TURNTABLE_ACTIVITY_STOPPED(71081, "TURNTABLE_ACTIVITY_STOPPED"),

    /**
     * 可用次数不足
     */
    TURNTABLE_CHANCE_NOT_ENOUGH(71082, "TURNTABLE_CHANCE_NOT_ENOUGH"),

    /**
     * 可用次数处理失败
     */
    TURNTABLE_CHANCE_PROCESS_FAILED(71083, "TURNTABLE_CHANCE_PROCESS_FAILED"),

    /**
     * 奖品未找到
     */
    TURNTABLE_WINNING_NOT_FOUND(71084, "TURNTABLE_WINNING_NOT_FOUND"),

    /**
     * 奖品已发放
     */
    TURNTABLE_WINNING_GIVE_OUT(71085, "TURNTABLE_WINNING_GIVE_OUT"),

    /**
     * 奖品未发放
     */
    TURNTABLE_WINNING_NOT_GIVE_OUT(71086, "TURNTABLE_WINNING_NOT_GIVE_OUT"),

    /**
     * 奖品已确定收货
     */
    TURNTABLE_WINNING_COMPLETED(71087, "TURNTABLE_WINNING_COMPLETED"),


    /**
     * 活动最小金额
     */
    PRIZE_MIN_AMOUNT(71090, "PRIZE_MIN_AMOUNT"),

    /**
     * 活动最大金额
     */
    PRIZE_MAX_AMOUNT(71091, "PRIZE_MAX_AMOUNT"),

    /**
     * 没有可领取的红包
     */
    NO_RED_PACKET_PIK(71092, "NO_RED_PACKET_PIK"),
    /**
     * 不是有效矿工
     */
    NOT_EFECT_MINER(71093, "NOT_EFECT_MINER"),

    NOT_JOIN_CHAT_ROOM(71094, "NOT_JOIN_CHAT_ROOM"),
    /**
     * 请填写完所有信息
     */
    FOREIGN_BANN_INFO(71095, "CHECK_YOUR_FORM"),
    /**
     * 获取汇率失败
     */
    FOREIGN_EXCHANGE_ERROR(71096, "FOREIGN_EXCHANGE_ERROR"),
    /**
     * 请选择换汇币种
     */
    FOREIGN_EXCHANGE_SYMBOL(71097, "FOREIGN_EXCHANGE_SYMBOL"),
    /**
     * 线下换汇地址未填写
     */
    FOREIGN_EXCHANGE_ADDRESS(71098, "FOREIGN_EXCHANGE_ADDRESS"),
    /**
     * 换汇配置错误
     */
    FOREIGN_EXCHANGE_CONFIG(71099, "FOREIGN_EXCHANGE_CONFIG"),
    /**
     * 购买最小数量
     */
    FOREIGN_EXCHANGE_LIMLMIT(71100, "FOREIGN_EXCHANGE_LIMLMIT"),
    /**
     * 换汇扣款失败
     */
    FOREIGN_EXCHANGE_FAIL(71101, "FOREIGN_EXCHANGE_FAIL"),
    /**
     * 请选择银行卡
     */
    FOREIGN_EXCHANGE_BANK(71102, "FOREIGN_EXCHANGE_BANK"),

    /**
     * 操作过于频繁frequency
     */
    FOREIGN_EXCHANGE_FREQUENCY(71103, "FOREIGN_EXCHANGE_FREQUENCY"),
    /**
     * 订单不存在
     */
    FOREIGN_EXCHANGE_ORDER_NOTEIST(71104, "FOREIGN_EXCHANGE_ORDER_NOTEIST"),
    /**
     * 订单已经取消
     */
    FOREIGN_EXCHANGE_ORDER_CANCELED(71105, "FOREIGN_EXCHANGE_ORDER_CANCELED"),
    /**
     * 订单已完成不能取消
     */
    FOREIGN_EXCHANGE_ORDER_FOBIDCANCEL(71106, "FOREIGN_EXCHANGE_ORDER_FOBIDCANCEL"),

    /**
     * 填写的信息超过了长度
     */
    FOREIGN_OVER_LENGTH(71107, "MESSAGE_OVER_LENGTH"),

    /**
     * 功能暂时关闭，敬请期待
     */
    FOREIGN_EXCHANGE_SWITCH(71108, "FOREIGN_EXCHANGE_SWITCH"),
    /**
     * 银行卡不存在
     */
    FOREIGN_EXCHANGE_BANK_NOTEXIST(71109, "FOREIGN_EXCHANGE_BANK_NOTEXIST"),
    /**
     * 换汇地址不存在
     */
    FOREIGN_EXCHANGE_ADDRESS_NOTEXIST(71110, "FOREIGN_EXCHANGE_ADDRESS_NOTEXIST"),
    /**
     * 保存银行卡失败
     */
    FOREIGN_EXCHANGE_BANKSAVE_ERROR(71111, "FOREIGN_EXCHANGE_BANKSAVE_ERROR"),
    /**
     * 修改银行卡失败
     */
    FOREIGN_EXCHANGE_BANKUPDATE_ERROR(71112, "FOREIGN_EXCHANGE_BANKUPDATE_ERROR"),
    /**
     * 取消otc提现失败
     */
    CANCEL_OTC_ORDER_ERROR(71113, "CANCEL_OTC_ORDER_ERROR"),
    /**
     * 不能取消订单
     */
    CAN_NOT_CANCEL_OTC_ORDER(71114, "CAN_NOT_CANCEL_OTC_ORDER"),
    /**
     * 订单参数异常
     */
    OTC_ORDER_ILLEGAL(71115, "OTC_ORDER_ILLEGAL"),
    /**
     * 凌晨0:00-8:00单笔限额5万
     */
    OVER_FLOW_LIMIT_AMOUNT1(71116, "OVER_FLOW_LIMIT_AMOUNT1"),
    /**
     * 提现单笔限额30万
     */
    OVER_FLOW_LIMIT_AMOUNT2(71117, "OVER_FLOW_LIMIT_AMOUNT2"),
    /**
     * 未绑定手机号
     */
    MOBILE_NO_NOT_FIND(71118, "MOBILE_NO_NOT_FIND"),
    /**
     * 领取记录不存在
     */
    PENDING_WARD_NOT_FIND(71119, "PENDING_WARD_NOT_FIND"),
    /**
     * 非法操作该记录不属于你
     */
    THIS_RECORD_IS_NOT_YOU(71120, "THIS_RECORD_IS_NOT_YOU"),
    /**
     * 您的挖矿收益积分不足，无法领取
     */
    SCORE_INSUFFICIENT_CREDIT_BALANCE(71121, "SCORE_INSUFFICIENT_CREDIT_BALANCE"),
    /**
     * 该积分已领取
     */
    SCORE_HAS_RECEIVED(71122, "SCORE_HAS_RECEIVED"),
    /**
     * 4月1日之后注册的用户不能参加该活动
     */
    YOU_CANT_JOIN_ACTIVITIES(71123, "YOU_CANT_JOIN_ACTIVITIES"),

    /**
     * 不是有效矿工
     */
    NOT_EFFECTIVE_MINER(71124, "NOT_EFFECTIVE_MINER"),

    /**
     * 用户不存在
     */
    MEMBER_NOT_EXIS(71125, "NOT_EFFECTIVE_MINER"),

    /**
     * IM 服务调用失败
     */
    IMSEVER_CALL_FAILED(71126, "IMSEVER_CALL_FAILED"),
    /**
     * 数据重复
     */
    DUPLICATE_DATA(71127, "DUPLICATE_DATA"),
    /**
     * 请绑定手机号
     */
    PLEASE_BING_MOBILE(71128, "PLEASE_BING_MOBILE"),
    /**
     * 福利包活动不存在
     */
    WELFARE_ACTIVITY_NOT_FOUND(71129, "WELFARE_ACTIVITY_NOT_FOUND"),
    /**
     * 福利包活动未开盘
     */
    WELFARE_ACTIVITY_NOT_OPEN(71130, "WELFARE_ACTIVITY_NOT_OPEN"),
    /**
     * 福利包活动已封盘
     */
    WELFARE_ACTIVITY_IS_CLOSED(71131, "WELFARE_ACTIVITY_IS_CLOSED"),
    /**
     * 福利包活动参与明细记录不存在
     */
    WELFARE_INVOLVEMENT_NOT_FOUND(71132, "WELFARE_INVOLVEMENT_NOT_FOUND"),
    /**
     * 福利包撤回失败
     */
    WELFARE_INVOLVEMENT_REFUND_FAILED(71133, "WELFARE_INVOLVEMENT_REFUND_FAILED"),
    /**
     * 福利包不可撤回
     */
    WELFARE_INVOLVEMENT_NO_REFUND(71134, "WELFARE_INVOLVEMENT_NO_REFUND"),
    /**
     * 福利包购买失败
     */
    WELFARE_INVOLVEMENT_BUY_FAILED(71135, "WELFARE_INVOLVEMENT_BUY_FAILED"),
    /**
     * 福利包没有购买资格
     */
    WELFARE_INVOLVEMENT_CAN_NOT_BUY(71136, "WELFARE_INVOLVEMENT_CAN_NOT_BUY"),
    /**
     * 福利包活动参与次数不足
     */
    WELFARE_CHANCE_NOT_ENOUGH(71137, "WELFARE_CHANCE_NOT_ENOUGH"),

    ;


    private final int code;
    private final String message;

    BtBankMsgCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    /**
     * 构建MsgCode类
     *
     * @param code    编码
     * @param message 消息
     * @return
     */
    public static MsgCode of(final int code, final String message) {
        return new MsgCode() {
            @Override
            public int getCode() {
                return code;
            }

            @Override
            public String getMessage() {
                return message;
            }
        };
    }

    /**
     * 转换为响应体
     *
     * @param <T> T
     * @return resp
     */
    public <T> MessageRespResult resp() {
        return new MessageRespResult<T>(code, message);
    }

    public MessageCodeException asException() {
        return new MessageCodeException(this);
    }
}
