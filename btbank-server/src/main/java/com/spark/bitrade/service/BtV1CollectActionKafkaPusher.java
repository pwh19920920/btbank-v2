package com.spark.bitrade.service;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * BtV1CollectActionKafkaPusher
 *
 * @author biu
 * @since 2019/12/2 10:23
 */
@Slf4j
@Component
public class BtV1CollectActionKafkaPusher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public BtV1CollectActionKafkaPusher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * 订单申诉创建通知
     * <p>
     * exp.  @CollectActionEvent(collectType = CollectActionEventType.OTC_APPEAL_ORDER, memberId = "#user.getId()", refId = "#order.getOrderSn()")
     *
     * @param memberId 会员id
     * @param orderSn  OTC 订单编号
     */
    public void pushOtcOrderAppealCreated(Long memberId, Long orderSn) {
        JSONObject data = new JSONObject();
        data.put("collectType", "OTC_APPEAL_ORDER");
        data.put("memberId", memberId);
        data.put("refId", orderSn + "");
        data.put("createTime", new Date());
        data.put("locale", "zh_CN");

        log.info("推送订单申诉创建事件 data -> {}", data.toJSONString());
        kafkaTemplate.send("msg-collectcarrier", "OTC", data.toJSONString());
    }

    /**
     * 新订单创建通知
     * <p>
     * exp. @CollectActionEvent(collectType = CollectActionEventType.OTC_ADD_ORDER, memberId = "#user.getId()", refId = "#order.getOrderSn()")
     *
     * @param memberId 会员id
     * @param orderSn  OTC 订单编号
     */
    public void pushOtcOrderCreated(Long memberId, Long orderSn) {
        JSONObject data = new JSONObject();
        data.put("collectType", "OTC_ADD_ORDER");
        data.put("memberId", memberId);
        data.put("refId", orderSn + "");
        data.put("createTime", new Date());
        data.put("locale", "zh_CN");

        log.info("推送新订单创建事件 data -> {}", data.toJSONString());
        kafkaTemplate.send("msg-collectcarrier", "OTC", data.toJSONString());
    }
}
