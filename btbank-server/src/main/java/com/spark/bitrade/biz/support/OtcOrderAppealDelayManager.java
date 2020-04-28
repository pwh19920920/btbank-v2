package com.spark.bitrade.biz.support;

import com.spark.bitrade.api.vo.OtcWithdrawVO;
import com.spark.bitrade.biz.OtcMinerService;
import com.spark.bitrade.exception.BtBankException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * OtcOrderAppealDelayManager
 *
 * @author biu
 * @since 2019/12/1 17:42
 */
@Slf4j
@Component
public class OtcOrderAppealDelayManager implements InitializingBean, DisposableBean {

    private BlockingQueue<DelayedItem<String>> delayedItems = new DelayQueue<>();
    private Map<String, DelayedItem<String>> delayedItemMap = new ConcurrentHashMap<>();

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private ExecutorService appealExecutor;
    private boolean runnable = false;

    private OtcMinerService otcMinerService;

    public OtcOrderAppealDelayManager(OtcMinerService otcMinerService) {
        this.otcMinerService = otcMinerService;
    }

    @Override
    public void destroy() throws Exception {
        runnable = false;
        appealExecutor.shutdown();
        executorService.shutdown();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        runnable = true;

        appealExecutor = new ThreadPoolExecutor(3, 64, 30, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                (r) -> {
                    Thread thread = new Thread(r);
                    thread.setName("AppealDelayManager");
                    return thread;
                });

        // 初始化数据
        List<OtcWithdrawVO> orders = otcMinerService.findPaidOrders();
        // 当前秒
        long current = Calendar.getInstance().getTimeInMillis() / 1000;
        // 30分钟  2020.1.29修改为15分钟。
        long diff = 15 * 60;

        log.info("托管服务初始化,已付款订单 size = {}", orders.size());
        for (OtcWithdrawVO order : orders) {
            DelayedItem<String> item = null;
            long paid = order.getUpdateTime().getTime() / 1000;

            long timeout = 1;
            if (current - paid < diff) {
                timeout = current - paid;
            }

            item = new DelayedItem<>(order.getRefId(), timeout);
            delayedItems.add(item);
            delayedItemMap.put(order.getRefId(), item);
            log.info("托管服务添加 order_id = {}, timeout = {} ", item.getBody(), timeout);
        }


        executorService.execute(() -> {
            while (runnable) {
                try {
                    DelayedItem<String> item = delayedItems.take();
                    delayedItemMap.remove(item.getBody());

                    appealExecutor.execute(() -> otcMinerService.appeal(item.getBody()));

                } catch (InterruptedException ex) {
                    log.error("获取延时对象超时", ex);
                } catch (RuntimeException ex) {
                    log.error("处理延时对象失败", ex);
                }
            }
        });
    }

    public void deposit(String orderSn) {
        DelayedItem<String> item = delayedItemMap.get(orderSn);
        if (item != null) {
            log.warn("警告:>>>>> 不应该出现的场景，orderSn 存在重复异常");
            delayedItemMap.remove(orderSn);
            delayedItems.remove(item);
        }
        //修改自动申诉为15分钟2020.1.20
        item = new DelayedItem<>(orderSn, 15 * 60);
        try {
            if (!delayedItems.add(item)) {
                throw new RuntimeException("Deposit order interrupted");
            }
            delayedItemMap.put(orderSn, item);
        } catch (RuntimeException ex) {
            log.error("托管订单到延时队列失败 order_sn = {}", orderSn);
            throw new BtBankException(500, ex.getMessage());
        }
    }

    public void remove(String orderSn) {
        DelayedItem<String> item = delayedItemMap.get(orderSn);
        if (item != null) {
            delayedItemMap.remove(orderSn);
            delayedItems.remove(item);
        }
    }
}
