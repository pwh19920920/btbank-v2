package com.spark.bitrade.consumer.dto;

import lombok.Data;

/**
 * OtcOrderEvent
 * <p>
 * 订单事件通知时， refId = orderSn
 *
 * @author biu
 * @since 2019/12/1 16:22
 */
@Data
public class OtcOrderEvent {

    private String type;
    private Long memberId;
    private String refId;
    private Long timestamp;

    public EventType getEventType() {
        for (EventType value : EventType.values()) {
            if (value.name().equals(type)) {
                return value;
            }
        }
        return EventType.NONE;
    }

    public enum EventType {
        NONE, OTC_CANCEL_ORDER, OTC_PAY_CASH, OTC_PAY_COIN, OTC_APPEAL_ORDER_COMPLETE
    }
}
