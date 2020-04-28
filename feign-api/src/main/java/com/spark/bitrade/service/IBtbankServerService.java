package com.spark.bitrade.service;

import com.spark.bitrade.constant.FeignServiceConstant;
import com.spark.bitrade.util.MessageRespResult;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * BT OPEN API 内部调用
 *
 * @author
 * @time
 */
@FeignClient(FeignServiceConstant.BTBANK_SERVER)
@RequestMapping("btbank")
public interface IBtbankServerService {

    @PostMapping("inner/schedule/auto/dispatch")
    MessageRespResult autoDispatch();

    @PostMapping("inner/schedule/auto/unlock")
    MessageRespResult autoUnlock();

    /**
     * 大宗挖矿释放推荐奖励
     *
     * @return 处理结果
     */
    @PostMapping("inner/schedule/auto/recommendUnlock")
    MessageRespResult recommendUnlock();

    @PostMapping("inner/schedule/auto/updateActivity")
    MessageRespResult autoUpdateActivity();

    @PostMapping("inner/schedule/auto/profitUnlock")
    MessageRespResult autoProfitUnlock();

    @PostMapping("inner/schedule/auto/process")
    MessageRespResult<String> autoProcess();

    /**
     * 处理矿工返利释放
     *
     * @return 处理结果
     */
    @PostMapping("inner/schedule/miner/rebate")
    MessageRespResult minerRebate();

    /**
     * 生成订单统计报告
     *
     * @return 处理结果
     */
    @PostMapping("inner/schedule/miner/orderStatisticalReport")
    MessageRespResult orderStatisticalReport();

    @PostMapping("inner/schedule/miner/dispatchOtcSaleReward")
    void dispatchOtcSaleReward();

    @PostMapping("inner/schedule/miner/dispatchOtcSaleRewardForDay")
    void dispatchOtcSaleRewardForDay();

    /**
     * 处理矿工返利释放
     *
     * @return 处理结果
     */
    @PostMapping("inner/schedule/miner/checkRebate")
    MessageRespResult checkMinerRebate(@RequestParam("begin") String begin);

    /**
     * 总报表统计查询
     *
     * @return
     */
    @PostMapping("statistics/report/generalStatement")
    MessageRespResult generalStatement();

    /**
     * 矿池订单汇总统计
     *
     * @return
     */
    @PostMapping("statistics/report/statMinerOrderTotal")
    MessageRespResult statMinerOrderTotal();

    /**
     * 挖矿汇总统计
     *
     * @return
     */
    @PostMapping("statistics/report/statMinerTotal")
    MessageRespResult statMinerTotal();

    /**
     * 企业挖矿汇总统计
     *
     * @return
     */
    @PostMapping("statistics/report/statEnterpriseMineTotal")
    MessageRespResult statEnterpriseMineTotal();

    /**
     * 收益排行榜奖励发放
     *
     * @return
     */
    @PostMapping("rank/reward/rankReward")
    MessageRespResult rankReward();

    /**
     * 累计收益数据写入
     *
     * @return
     */
    @PostMapping("rank/reward/totalRankReward")
    MessageRespResult totalRankReward();

    /**
     * 用户
     *
     * @return
     */
    @PostMapping("memberStatistics/report/memberStatistics")
    MessageRespResult memberStatics();

    /**
     * 生成第二天竞猜活动任务
     *
     * @return
     */
    @PostMapping("inner/schedulePrizQuize/generatePrizeQuizeRecord")
    MessageRespResult generatePrizeQuizeRecord();

    /**
     * 更新BTC价格
     *
     * @return
     */
    @PostMapping("inner/schedulePrizQuize/updateAmount")
    MessageRespResult updateAmount(String btcPrice);

    /**
     * 更新用户竞猜结果
     *
     * @return
     */
    @PostMapping("inner/schedulePrizQuize/updateMinerResult")
    MessageRespResult updateMinerResult();

    /**
     * 扣失败用户竞猜资金
     *
     * @return
     */
    @PostMapping("inner/schedulePrizQuize/drawAmount")
    MessageRespResult drawAmount();


    /**
     * 释放用户竞猜金额和奖励
     *
     * @return
     */
    @PostMapping("inner/schedulePrizQuize/realseAmount")
    MessageRespResult realseAmount();

    /**
     * 活动结束释放锁仓金额
     *
     * @return
     */
    @PostMapping("inner/sheduleActivityRedPacket/realseLockAmount")
    MessageRespResult realseLockAmount();

    /**
     * 定时添加矿工到网易云信
     *
     * @return
     */
    @PostMapping("inner/sheduleImController/auto/registermember")
    MessageRespResult registermember();

    /**
     * 定时添加系统用户到网易云信
     *
     * @return
     */
    @PostMapping("inner/sheduleImController/auto/registersysuser")
    MessageRespResult registersysuser();

    /**
     * 定时添加系统客服到网易云信
     *
     * @return
     */
    @PostMapping("inner/sheduleImController/auto/registerkefumember")
    MessageRespResult registerkefumember();


    /**
     * 换汇归集
     *
     * @return
     */
    @PostMapping("inner/scheduleForeignLineController/auto/foreigncollect")
    MessageRespResult foreigncollect();

    /**
     * 换汇退款
     *
     * @return
     */
    @PostMapping("inner/scheduleForeignLineController/auto/foreignRefound")
    MessageRespResult foreignRefound();

    /**
     * kafka异常修复异常订单问题
     *
     * @return
     */
    @PostMapping("inner/schedule/auto/recoveryOTCOrderStatus")
    MessageRespResult recoveryOTCOrderStatus();


    /**
     * 3月8日 新矿工累计收益到600BT释放体验金
     *
     * @return
     */
    @PostMapping("inner/schedule/auto/releaseProfit")
    MessageRespResult autoReleaseProfit();

    /**
     * 刷新汇率缓存和图片地址
     *
     * @return
     */
    @PostMapping("inner/scheduleForeignLineController/auto/foreignupdateimage")
    MessageRespResult foreignupdateimage();

    /**
     * 用户资产每日统计
     *
     * @return
     */
    @PostMapping("statistics/report/statUserAssets")
    MessageRespResult statUserAssets();

    /**
     * 3月8日 老矿工推荐福利
     *
     * @return
     */
    @PostMapping("inner/schedule/auto/oldMemberRelease")
    MessageRespResult oldMemberRelease();

    /**
     * 业绩排名快照
     *
     * @return
     */
    @PostMapping("statistics/report/statPerRanking")
    MessageRespResult statPerRanking();

    /**
     * 单个交易日云端转入转出（内部转账）排名统计
     *
     * @return
     */
    @PostMapping("statistics/report/statFastPayRank")
    MessageRespResult statFastPayRank();

    /**
     * 单个交易日OTC统计
     *
     * @return
     */
    @PostMapping("statistics/report/statSingleTransactionOTC")
    MessageRespResult statSingleTransactionOTC();

    /**
     * 单个交易日OTC购买排名
     *
     * @return
     */
    @PostMapping("statistics/report/statOTCBuyRank")
    MessageRespResult statOTCBuyRank();

    /**
     * 单个交易日OTC出售排名
     *
     * @return
     */
    @PostMapping("statistics/report/statOTCSellRank")
    MessageRespResult statOTCSellRank();

    /**
     * 自动创建当天的福利包活动
     *
     * @return resp
     */
    @RequestMapping("inner/expose/welfare/autoCreate")
    MessageRespResult welfareAutoCreate();

    /**
     * 福利包释放处理
     *
     * @return resp
     */
    @RequestMapping("inner/expose/welfare/release")
    MessageRespResult welfareRelease();


    /**
     * 福利包直推领取状态同步
     *
     * @return resp
     */
    @RequestMapping("inner/expose/welfare/reward")
    MessageRespResult welfareReward();
}
