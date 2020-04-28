package com.spark.bitrade.biz.support;

import com.spark.bitrade.biz.TurntableGiveOutService;
import com.spark.bitrade.repository.entity.TurntableWinning;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TurntableGiveOutDispathcer
 *
 * @author biu
 * @since 2020/1/8 17:59
 */
@Slf4j
@Component
public class TurntableGiveOutDispatcher implements InitializingBean, DisposableBean {

    private ExecutorService executorService = Executors.newFixedThreadPool(3);

    private TurntableGiveOutService giveOutService;

    public void dispatch(TurntableWinning winning) {
        log.info("提交奖品发放请求 [ id = {}, act_id = {}, prize_id = {}, prize_amount = {} ]",
                winning.getId(), winning.getActId(), winning.getPrizeId(), winning.getPrizeAmount());
        executorService.execute(() -> {
            try {
                giveOutService.handle(winning);
            } catch (RuntimeException ex) {
                log.error("奖品发放操作失败 [ id = {}, err = '{}'", winning.getId(), ex.getMessage());
            }
        });
    }

    @Autowired
    public void setGiveOutService(TurntableGiveOutService giveOutService) {
        this.giveOutService = giveOutService;
    }

    @Override
    public void destroy() throws Exception {
        executorService.shutdown();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }
}
