package com.spark.bitrade.api.controller;

import com.spark.bitrade.biz.*;
import com.spark.bitrade.repository.entity.EnterpriseMinerTransaction;
import com.spark.bitrade.repository.service.EnterpriseMinerTransactionService;
import com.spark.bitrade.util.MessageRespResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * ExposeController
 *
 * @author biu
 * @since 2019/12/4 21:07
 */
@Slf4j
@RestController
@RequestMapping("/inner/expose")
@AllArgsConstructor
public class ExposeController {

    private OtcLimitService otcLimitService;
    private OtcMinerService otcMinerService;
    private EnterpriseMiningService miningService;
    private EnterpriseMinerTransactionService transactionService;
    private TurntableService turntableService;
    private WelfareService welfareService;

    /**
     * 查看用户禁止操作的BT
     *
     * @param memberId id
     * @return resp
     */
    @GetMapping("/forbid")
    public MessageRespResult<BigDecimal> forbid(@RequestParam("memberId") Long memberId) {
        BigDecimal forbid = otcLimitService.forbidToWithdrawAndTransferOut(memberId);
        return MessageRespResult.success4Data(forbid);
    }

    /**
     * 是否是内部商家
     *
     * @param memberId id
     * @return resp
     */
    @GetMapping("/isInnerAccount")
    public MessageRespResult<Boolean> isInnerAccount(@RequestParam("memberId") Long memberId) {
        return MessageRespResult.success4Data(otcLimitService.isInnerMerchant(memberId));
    }

    /**
     * 手动触发自动申诉
     *
     * @param orderSn sn
     * @return resp
     */
    @RequestMapping("/appeal")
    public MessageRespResult appeal(@RequestParam("orderSn") String orderSn) {
        otcMinerService.appeal(orderSn);
        return MessageRespResult.success();
    }

    /**
     * 手动归集处理
     *
     * @param txId txId
     * @return resp
     */
    @RequestMapping("/enterprise/collect")
    public MessageRespResult collect(@RequestParam("txId") Long txId) {
        EnterpriseMinerTransaction tx = transactionService.getById(txId);
        if (tx != null) {
            miningService.collect(tx);
        }
        return MessageRespResult.success();
    }

    /**
     * 手动发放奖励
     *
     * @param txId txId
     * @return resp
     */
    @RequestMapping("/enterprise/reward")
    public MessageRespResult reward(@RequestParam("txId") Long txId) {
        EnterpriseMinerTransaction tx = transactionService.getById(txId);
        if (tx != null) {
            miningService.reward(tx);
        }
        return MessageRespResult.success();
    }

    /**
     * 手动发放奖品
     *
     * @param id 中奖记录
     * @return resp
     */
    @RequestMapping("/turntable/giveOut")
    public MessageRespResult giveOut(@RequestParam("id") Long id) {
        turntableService.giveOut(id);
        return MessageRespResult.success();
    }

    /**
     * 自动创建当天的福利包活动
     *
     * @return resp
     */
    @RequestMapping("/welfare/autoCreate")
    public MessageRespResult welfareAutoCreate() {
        boolean b = welfareService.autoCreateWelfarePacket();
        return MessageRespResult.success("create -> " + b);
    }

    /**
     * 福利包释放处理
     *
     * @return resp
     */
    @RequestMapping("/welfare/release")
    public MessageRespResult welfareRelease() {
        welfareService.release();
        return MessageRespResult.success();
    }

    /**
     * 福利包直推领取状态同步
     *
     * @return resp
     */
    @RequestMapping("/welfare/reward")
    public MessageRespResult welfareReward() {
        welfareService.checkRewardStatus();
        return MessageRespResult.success();
    }
}
