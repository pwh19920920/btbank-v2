package com.spark.bitrade.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.spark.bitrade.biz.OtcConfigService;
import com.spark.bitrade.biz.OtcMinerService;
import com.spark.bitrade.biz.support.OtcOrderAppealDelayManager;
import com.spark.bitrade.constant.OtcMinerOrderStatus;
import com.spark.bitrade.consumer.dto.OtcOrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * OtcOrderEventConsumer
 *
 * @author biu
 * @since 2019/12/1 16:21
 */
@Slf4j
@Component
public class OtcOrderEventConsumer implements InitializingBean, DisposableBean {

    private OtcMinerService otcMinerService;
    private ExecutorService executorService;

    private OtcOrderAppealDelayManager delayManager;
    private OtcConfigService otcConfigService;
    @Autowired
    public void setOtcMinerService(OtcMinerService otcMinerService) {
        this.otcMinerService = otcMinerService;
    }

    @Autowired
    public void setDelayManager(OtcOrderAppealDelayManager delayManager) {
        this.delayManager = delayManager;
    }
    @Autowired
    public void setOtcConfigService(OtcConfigService otcConfigService){
        this.otcConfigService=otcConfigService;
    }
    @KafkaListener(topics = "bt-otc-advise")
    public void consume(ConsumerRecord<String, String> record) {
        String value = record.value();
        log.info("receive advise value -> {}", value);

        try {
            OtcOrderEvent event = JSON.parseObject(value, OtcOrderEvent.class);

            // 处理OTC订单
            executorService.execute(() -> {
                try {
                    this.handle(event);
                } catch (RuntimeException ex) {
                    log.error("事件处理出错 event = {}, err = {}", event, ex.getMessage());
                }
            });
            Integer size = otcConfigService.getValue("OTC_DIG_ORDER_MAX_DISPLAY", (v) -> Integer.parseInt(v), 5);
            otcMinerService.updateQueueStatus(size);
        } catch (JSONException ex) {
            log.error("invalid value ['{}']", value);
        }
    }

    private void handle(OtcOrderEvent event) {

        OtcOrderEvent.EventType type = event.getEventType();

        if (type == OtcOrderEvent.EventType.NONE) {
            log.error("不支持的事件类型");
            return;
        }

        String refId = event.getRefId();

        if (!StringUtils.hasText(refId)) {
            log.error("refId 不能为空");
        }

        // 取消订单
        if (type == OtcOrderEvent.EventType.OTC_CANCEL_ORDER) {
            // 还原为新订单状态
            otcMinerService.updateOrderStatus(refId, OtcMinerOrderStatus.Unpaid, OtcMinerOrderStatus.New);
            return;
        }
        // 已付款
        if (type == OtcOrderEvent.EventType.OTC_PAY_CASH) {
            boolean val = otcMinerService.updateOrderStatus(refId, OtcMinerOrderStatus.Unpaid, OtcMinerOrderStatus.Paid);
            if (val) {
                log.info("开始监控放币订单 orderSn = {}", refId);
                delayManager.deposit(refId);
            }
        }
        // 已放币
        if (type == OtcOrderEvent.EventType.OTC_PAY_COIN) {
            boolean val = otcMinerService.updateOrderStatus(refId, OtcMinerOrderStatus.Paid, OtcMinerOrderStatus.Completed);
            if (val) {
                delayManager.remove(refId);
            }
        }
        // 申诉完成
        if (type == OtcOrderEvent.EventType.OTC_APPEAL_ORDER_COMPLETE) {
            otcMinerService.appealCompleted(NumberUtils.toLong(refId, 0L));
        }
    }

    @Override
    public void destroy() throws Exception {
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        executorService = new ThreadPoolExecutor(3, 64,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                r -> {
                    Thread thread = new Thread(r);
                    thread.setName("OTCEventConsumer");
                    return thread;
                });
    }
}
