package com.spark.bitrade.constant;

/**
 * OTC矿池订单状态
 * <p>
 * 订单状态{0:新订单,1:未付款,2:已付款,3:申诉中,4:已完成}
 *
 * @author biu
 * @since 2019/11/28 14:38
 */
public enum OtcMinerOrderStatus {

    New(0, "新订单"),

    Unpaid(1, "未付款"),

    Paid(2, "已付款"),

    Completed(3, "已完成"),

    Appeal(4, "申诉中"),

    Close(5, "已关闭"),
    Cancel(6, "已取消"),
    None(-1, "未知状态");

    private final int code;
    private final String description;

    OtcMinerOrderStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
