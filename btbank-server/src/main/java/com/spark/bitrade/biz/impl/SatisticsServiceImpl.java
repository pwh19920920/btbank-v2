package com.spark.bitrade.biz.impl;

import com.spark.bitrade.biz.SatisticsService;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.repository.entity.*;
import com.spark.bitrade.repository.service.*;
import com.spark.bitrade.util.BigDecimalUtils;
import com.spark.bitrade.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * * @author Administrator * @time 2019.12.16 15:07
 */
@Service
@Slf4j
public class SatisticsServiceImpl implements SatisticsService {
    @Autowired
    private BtBankMinerOrderTotalService btBankMinerOrderTotalService;
    @Autowired
    private BtBankMinerTotalService btBankMinerTotalService;
    @Autowired
    private EnterpriseMinerTotalService enterpriseMinerTotalService;
    @Autowired
    private TotalDailyAmountService totalDailyAmountService;
    @Autowired
    private BtBankMinerPerRankService btBankMinerPerRankService;
    @Autowired
    private BtBankMinerBalanceTransactionService btBankMinerBalanceTransactionService;
    @Autowired
    private SingleDailyStatisticsService singleDailyStatisticsService;
    @Autowired
    private SingleDealStatisticsService singleDealStatisticsService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean statMinerOrderTotal() {
        log.info("矿池订单汇总，数据统计开始----->");

        // 查询表中最新一条记录
        BtBankMinerOrderTotal btBankMinerOrderTotal = btBankMinerOrderTotalService
                .lambdaQuery()
                .orderByDesc(BtBankMinerOrderTotal::getTime).last("limit 1").one();

        // 从表中最后一天开始，查询并存入数据；如果没有数据，则从活动开始时间10-26查询并存入数据
        LocalDate startTime = LocalDate.of(2019, 10, 26);

        if (btBankMinerOrderTotal != null) {
            log.info("矿池订单汇总，查询已统计的最新时间----->time-{}---------", DateFormatUtils.format(btBankMinerOrderTotal.getTime(), "yyyy-MM-dd"));
            // 查询需存入的统计数据
            Date time = btBankMinerOrderTotal.getTime();
            Instant instant = time.toInstant();
            ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
            startTime = zdt.toLocalDate().plusDays(1);

        }

        while (startTime.isBefore(LocalDate.now())) {
            BtBankMinerOrderTotal orderTotal = this.getMinerOrderTotal(startTime.toString());

            log.info("矿池订单汇总，数据存储开始-------time-{}---------", startTime);
            boolean result = btBankMinerOrderTotalService.save(orderTotal);
            if (!result) {
                log.info("矿池订单汇总，数据存储失败-------time-{}---------", startTime);
                throw new MessageCodeException(CommonMsgCode.FAILURE);
            }

            startTime = startTime.plusDays(1);
        }

        log.info("矿池订单汇总，数据统计结束----->");
        return true;
    }

    /**
     * 矿池订单汇总统计数据
     *
     * @param startTime
     * @return
     */
    private BtBankMinerOrderTotal getMinerOrderTotal(String startTime) {
        BtBankMinerOrderTotal orderTotal = new BtBankMinerOrderTotal();
        orderTotal = btBankMinerOrderTotalService.grabAndSendTotalList(startTime);
        BtBankMinerOrderTotal orderTotal2 = btBankMinerOrderTotalService.getFixList(startTime);
        if (orderTotal2 != null) {
            orderTotal.setFixCount(orderTotal2.getFixCount());
            orderTotal.setFixPeople(orderTotal2.getFixPeople());
            orderTotal.setFixSum(orderTotal2.getFixSum());
        }
        BigDecimal needUnlockTotalAmount = btBankMinerOrderTotalService.getMineTotalByDay(startTime);
        orderTotal.setNeedUnlockTotalAmount(needUnlockTotalAmount);

        Date time = new Date(Date.parse(startTime.replace("-", "/")));
        orderTotal.setTime(time);

        orderTotal.setCreateTime(new Date());
        orderTotal.setUpdateTime(new Date());

        return orderTotal;
    }

    @Override
    public Boolean statMinerTotal() {
        log.info("挖矿汇总，数据统计开始----->");

        // 查询表中最新一条记录
        BtBankMinerTotal btBankMinerTotal = btBankMinerTotalService
                .lambdaQuery()
                .orderByDesc(BtBankMinerTotal::getTime).last("limit 1").one();

        // 从表中最后一天开始，查询并存入数据；如果没有数据，则从活动开始时间10-26查询并存入数据
        LocalDate startTime = LocalDate.of(2019, 10, 26);

        if (btBankMinerTotal != null) {
            log.info("挖矿汇总，查询已统计的最新时间----->time-{}---------", DateFormatUtils.format(btBankMinerTotal.getTime(), "yyyy-MM-dd"));
            // 查询需存入的统计数据
            Date time = btBankMinerTotal.getTime();
            Instant instant = time.toInstant();
            ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
            startTime = zdt.toLocalDate().plusDays(1);

        }

        while (startTime.isBefore(LocalDate.now())) {
            BtBankMinerTotal minerTotal = this.getMinerTotal(startTime.toString());

            log.info("挖矿汇总，数据存储开始-------time-{}---------", startTime);
            boolean result = btBankMinerTotalService.save(minerTotal);
            if (!result) {
                log.info("挖矿汇总，数据存储失败-------time-{}---------", startTime);
                throw new MessageCodeException(CommonMsgCode.FAILURE);
            }

            startTime = startTime.plusDays(1);
        }

        log.info("挖矿汇总，数据统计结束----->");
        return true;
    }

    /**
     * 矿池订单汇总统计数据
     *
     * @param startTime
     * @return
     */
    private BtBankMinerTotal getMinerTotal(String startTime) {
        BtBankMinerTotal minerTotal = new BtBankMinerTotal();
        // 转入本金总额,次数,人数
        minerTotal = btBankMinerTotalService.getPrincipal(startTime);
        // 结算佣金总额
        BigDecimal reward = btBankMinerTotalService.getReward(startTime);
        // 结算本金总额
        BigDecimal settle = btBankMinerTotalService.getSettle(startTime);
        // 直推、金牌佣金
        Map<String, BigDecimal> directAndGoldRebate = btBankMinerTotalService.getDirectAndGoldRebate(startTime);
        BigDecimal directRebateAmount = directAndGoldRebate.get("directRebateAmount");
        BigDecimal goldRebateAmount = directAndGoldRebate.get("goldRebateAmount");

        if (minerTotal != null) {
            minerTotal.setReward(BigDecimalUtils.add(directRebateAmount, goldRebateAmount).add(reward));
            minerTotal.setMoney(settle);
        }

        Date time = new Date(Date.parse(startTime.replace("-", "/")));
        minerTotal.setTime(time);

        minerTotal.setCreateTime(new Date());
        minerTotal.setUpdateTime(new Date());

        return minerTotal;
    }

    @Override
    public Boolean statEnterpriseMineTotal() {
        log.info("企业挖矿汇总，数据统计开始----->");

        // 查询表中最新一条记录
        EnterpriseMinerTotal total = enterpriseMinerTotalService
                .lambdaQuery()
                .orderByDesc(EnterpriseMinerTotal::getTime).last("limit 1").one();

        // 从表中最后一天开始，查询并存入数据；如果没有数据，则从活动开始时间12-30查询并存入数据
        LocalDate startTime = LocalDate.of(2019, 12, 30);

        if (total != null) {
            log.info("企业挖矿汇总，查询已统计的最新时间----->time-{}---------", DateFormatUtils.format(total.getTime(), "yyyy-MM-dd"));
            // 查询需存入的统计数据
            Date time = total.getTime();
            Instant instant = time.toInstant();
            ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
            startTime = zdt.toLocalDate().plusDays(1);
        }

        while (startTime.isBefore(LocalDate.now())) {
            EnterpriseMinerTotal enterpriseMinerTotal = this.getEnterpriseMinerTotal(startTime.toString());

            log.info("企业挖矿汇总，数据存储开始-------time-{}---------", startTime);
            boolean result = enterpriseMinerTotalService.save(enterpriseMinerTotal);
            if (!result) {
                log.info("企业挖矿汇总，数据存储失败-------time-{}---------", startTime);
                throw new MessageCodeException(CommonMsgCode.FAILURE);
            }

            startTime = startTime.plusDays(1);
        }

        log.info("企业挖矿汇总，数据统计结束----->");
        return true;
    }

    @Override
    public Boolean statTotalDailyAmount() {
        log.info("用户资产每日汇总，数据统计开始---------->");
        Boolean flag = totalDailyAmountService.statTotalDailyAmount();
        log.info("用户资产每日汇总，数据统计结束---------->");
        return flag;
    }

    /**
     * 企业挖矿汇总数据统计
     *
     * @param startTime
     * @return
     */
    private EnterpriseMinerTotal getEnterpriseMinerTotal(String startTime) {
        EnterpriseMinerTotal total = new EnterpriseMinerTotal();
        // 转入次数、人数、总额
        total = enterpriseMinerTotalService.getInto(startTime);
        // 转出、挖矿、佣金
        EnterpriseMinerTotal total1 = enterpriseMinerTotalService.getSendAndMineAndReward(startTime);

        total.setSendSum(total1.getSendSum());
        total.setMineSum(total1.getMineSum());
        total.setRewardSum(total1.getRewardSum());

        Date time = new Date(Date.parse(startTime.replace("-", "/")));
        total.setTime(time);
        total.setCreateTime(new Date());
        total.setUpdateTime(new Date());

        return total;
    }


    /**
     * 业绩排名快照
     *
     * @return
     */
    @Override
    public Boolean statPerRanking() {
        log.info("---------------------业绩排名快照，数据统计开始---------->");

        // 当日已统计，则不再统计
        LocalDate date = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateStr = date.format(fmt);
        List<BtBankMinerPerRank> list = btBankMinerPerRankService.lambdaQuery().eq(BtBankMinerPerRank::getTime, dateStr).list();
        if (list != null && list.size() > 0) {
            log.info("---------------------业绩排名快照，当天数据已统计，time-{}--------------", dateStr);
            return false;
        }

        // 查询有效矿工
        Long[] miners = btBankMinerBalanceTransactionService.getValidMiners();
        //直推人数统计
        for (Long minerId : miners) {
            BtBankMinerPerRank btBankMinerPerRank = btBankMinerPerRankService.getSub(minerId);
            btBankMinerPerRank.setMemberId(minerId);
            //业绩统计
            BigDecimal money = btBankMinerPerRankService.getPer(minerId);
            btBankMinerPerRank.setMoney(money);
            btBankMinerPerRank.setUpdateTime(new Date());
            boolean res = btBankMinerPerRankService.save(btBankMinerPerRank);
            if (!res) {
                log.info("---------------------业绩排名快照,数据存储失败---entity-{}", btBankMinerPerRank);
            }
        }

        log.info("---------------------业绩排名快照，数据统计结束------------");
        return true;
    }

    @Override
    public Boolean statSingleTransactionOTC() {
        log.info("---------------------单个交易日OTC统计，数据统计开始---------->");
        String startTime;
        String endTime;
        // 当前时间在16点前，统计昨天16点到今天16点数据；反之，统计今天16点到明天16点数据
        StringBuilder sb = new StringBuilder();
        String timeStr = sb.append(LocalDate.now().toString()).append(" ").append("16:00:00").toString();
        Date now = DateUtil.parseDate(timeStr, "yyyy-MM-dd HH:mm:ss");

        if (new Date().before(now)) {
            //当前时间在16点前
            startTime = LocalDate.now().minusDays(1).toString() + " 16:00:00";
            endTime = timeStr;
        } else {
            //当前时间>=16点
            startTime = timeStr;
            endTime = LocalDate.now().plusDays(1).toString() + " 16:00:00";
        }
        SingleDealStatistics singleDealStatistics = singleDealStatisticsService.staSingleTransactionOTC(startTime, endTime);
        if (Objects.isNull(singleDealStatistics)) {
            log.info("-----------单个交易日OTC统计，存储数据开始-没有相关数据------------------");
            return true;
        }
        Map<String, Object> columnMap = new HashMap<String, Object>() {
            {
                put("type", "0");
            }
        };
        boolean b = singleDealStatisticsService.removeByMap(columnMap);
        if (!b) {
            log.info("-----------单个交易日OTC统计，删除历史数据失败----------");
        }
        boolean save = singleDealStatisticsService.save(singleDealStatistics);
        if (!save) {
            log.info("-----------单个交易日OTC统计，存储数据失败---startTime-{}--endTime-{}--entity-{}---", startTime, endTime, singleDealStatistics);
            throw new MessageCodeException(CommonMsgCode.FAILURE);
        }
        log.info("---------------------单个交易日OTC统计，数据统计结束------------");
        return true;
    }

    /**
     * 单个交易日云端转入转出（内部转账）排名
     *
     * @return
     */
    @Override
    public Boolean statFastPayRank() {
        log.info("---------------------单个交易日云端转入转出排名，数据统计开始------------>");
        // 清除已有数据
        log.info("---------------------单个交易日云端转入转出排名，删除历史数据开始------------>");
        Map<String, Object> coumnMap = new HashMap<>();
        coumnMap.put("transaction_type", "2");
        boolean res = true;
        res = singleDailyStatisticsService.removeByMap(coumnMap);
        if (!res) {
            log.info("-----------单个交易日云端转入排名，删除历史数据失败----------");
        }
        coumnMap.clear();
        coumnMap.put("transaction_type", "3");
        res = singleDailyStatisticsService.removeByMap(coumnMap);
        if (!res) {
            log.info("-----------单个交易日云端转出排名，删除历史数据失败----------");
        }

        String startTime;
        String endTime;
        // 当前时间在16点前，统计昨天16点到今天16点数据；反之，统计今天16点到明天16点数据
        String timeStr = LocalDate.now().toString() + " 16:00:00";
        Date time = DateUtil.parseDate(timeStr, "yyyy-MM-dd HH:mm:ss");

        if (new Date().before(time)) {
            //当前时间在16点前
            startTime = LocalDate.now().minusDays(1).toString() + " 16:00:00";
            endTime = timeStr;
        } else {
            //当前时间>=16点
            startTime = timeStr;
            endTime = LocalDate.now().plusDays(1).toString() + " 16:00:00";
        }


        log.info("---------------------单个交易日云端转入排名，数据统计开始------------");
        List<SingleDailyStatistics> receiveRanks = singleDailyStatisticsService.findPayFastReceiveRank(startTime, endTime);
        this.payFastToSave(receiveRanks, startTime, endTime);

        log.info("---------------------单个交易日云端转出排名，数据统计开始------------");
        List<SingleDailyStatistics> payRanks = singleDailyStatisticsService.findPayFastPayRank(startTime, endTime);
        this.payFastToSave(payRanks, startTime, endTime);

        log.info("---------------------单个交易日云端转入转出排名，数据统计结束------------");
        return true;
    }

    private Boolean payFastToSave(List<SingleDailyStatistics> statistics, String startTime, String endTime) {
        if (Objects.isNull(statistics) || statistics.isEmpty()) {
            log.info("-----------存储数据开始-没有相关数据------------------");
        }
        log.info("-----------存储数据开始-size={}------------------", statistics.size());
        statistics.forEach(singleDailyStatistics -> {
            boolean result = singleDailyStatisticsService.save(singleDailyStatistics);
            if (!result) {
                log.info("-----------存储数据失败---startTime-{}--endTime-{}--entity-{}---", startTime, endTime, singleDailyStatistics);
            }
        });
        return true;
    }

    @Override
    public Boolean statOTCTransactionBuy() {
        return rankTransaction("0", "OTC购买", "16:00:00", 0);
    }

    @Override
    public Boolean statOTCTransactionSell() {
        return rankTransaction("1", "OTC购买", "16:00:00", 1);
    }

    /**
     * 通用交易日统计方法
     *
     * @param type            交易类型
     * @param transactionName 交易名称
     * @param time            统计时间
     * @param method          调用方法 0:otc购买 1:otc出售
     * @return
     */
    private Boolean rankTransaction(String type, String transactionName, String time, int method) {
        log.info("---------------------单个交易日" + transactionName + "排名，数据统计开始------------>");
        // 清除已有数据
        Map<String, Object> coumnMap = new HashMap<>();
        coumnMap.put("transaction_type", type);
        boolean res = singleDailyStatisticsService.removeByMap(coumnMap);
        if (!res) {
            log.info("-----------单个交易" + transactionName + "排名，删除历史数据失败----------");
        }
        String startTime;
        String endTime;
        // 当前时间在16点前，统计昨天16点到今天16点数据；反之，统计今天16点到明天16点数据
        StringBuilder sb = new StringBuilder();
        String timeStr = sb.append(LocalDate.now().toString()).append(" ").append(time).toString();
        Date now = DateUtil.parseDate(timeStr, "yyyy-MM-dd HH:mm:ss");

        if (new Date().before(now)) {
            //当前时间在16点前
            startTime = LocalDate.now().minusDays(1).toString() + " 16:00:00";
            endTime = timeStr;
        } else {
            //当前时间>=16点
            startTime = timeStr;
            endTime = LocalDate.now().plusDays(1).toString() + " 16:00:00";
        }
        List<SingleDailyStatistics> statistics = null;
        if (method == 0) {
            statistics = singleDailyStatisticsService.recordOTCBuyRank(startTime, endTime);
        } else if (method == 1) {
            statistics = singleDailyStatisticsService.recordOTCSellRank(startTime, endTime);
        }
        if (Objects.isNull(statistics) || statistics.isEmpty()) {
            log.info("-----------单个交易日" + transactionName + "排名，存储数据开始-没有相关数据------------------");
            return true;
        }
        log.info("-----------单个交易日" + transactionName + "排名，存储数据开始-size={}------------------", statistics.size());
        statistics.forEach(singleDailyStatistics -> {
            boolean result = singleDailyStatisticsService.save(singleDailyStatistics);
            if (!result) {
                log.info("-----------单个交易日" + transactionName + "排名，存储数据失败---startTime-{}--endTime-{}--entity-{}---", startTime, endTime, singleDailyStatistics);
                throw new MessageCodeException(CommonMsgCode.FAILURE);
            }
        });

        log.info("---------------------单个交易日" + transactionName + "排名，数据统计结束------------");
        return true;
    }
}
