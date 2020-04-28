package com.spark.bitrade.biz.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.spark.bitrade.biz.MinerOtcOrderService;
import com.spark.bitrade.biz.OtcConfigService;
import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.constant.OtcConfigType;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.constants.MsgCode;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.enums.MessageCode;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.AdvertiseOperationHistory;
import com.spark.bitrade.repository.entity.OtcOrder;
import com.spark.bitrade.repository.entity.OtcOrderSubsidyPerDay;
import com.spark.bitrade.repository.service.AdvertiseOperationHistoryService;
import com.spark.bitrade.repository.service.OtcOrderService;
import com.spark.bitrade.repository.service.OtcOrderSubsidyPerDayService;
import com.spark.bitrade.service.IMemberApiService;
import com.spark.bitrade.service.MemberWalletService;
import com.spark.bitrade.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.language.bm.BeiderMorseEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author ww
 * @time 2019.11.29 10:42
 */
@Slf4j
@Service
public class MinerOtcOrderServiceImpl implements MinerOtcOrderService {


    @Autowired
    OtcConfigService configService;

    @Autowired
    OtcOrderSubsidyPerDayService subsidyPerDayService;

    @Autowired
    OtcOrderService orderService;

    @Autowired
    private MemberWalletService memberWalletService;

    @Autowired
    AdvertiseOperationHistoryService operationHistoryService;

    @Autowired
    IMemberApiService memberApiService;


    @Override
    public void dispatchSaleReward() {


        log.info(" 开始发放商家销售补贴");
        //按用户发放 按日期分组
        List<OtcOrder> needDispatchOrders = orderService.getNeedDispatchOrdersOrder();
        //
        for (OtcOrder order : needDispatchOrders) {
            dispatchSaleOrderReward(order);
        }

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void dispatchSaleOrderReward(OtcOrder order) {


        String payMemberIdString = configService.getValue(OtcConfigType.OTC_SALE_REWARD_PAY_ACCOUNT);
        Long payMemberId = Long.valueOf(payMemberIdString);

        MessageRespResult<Member> member = memberApiService.getMember(payMemberId);
        if (member.getData() == null) {
            throw new BtBankException(BtBankMsgCode.SALE_REWARD_PAY_MEMBER_NOT_EXISTS);
        }
        String excludeIdsString = configService.getValue(OtcConfigType.OTC_SALE_REWARD_SUBSIDY_ACCOUNT_EXCLUDE);
        List<Long> excludeMemeberIds = new ArrayList<>();
        excludeMemeberIds.add(payMemberId);
        Arrays.stream(excludeIdsString.split(",")).forEach(o ->
                excludeMemeberIds.add(Long.valueOf(o)));

        if (excludeMemeberIds.contains(order.getMemberId())) {
            log.info(" 支付专用号和排除帐号不需要发放 {}", order.getMemberId());
            if (null != order.getSaleRewardStatus() && order.getSaleRewardStatus() == 0) {
                //设置订单状态
                order.setSaleRewardStatus(2);
                int re = orderService.updateOrderDispatchSaleRewardStatus(order);

                log.info("排除帐号 的订单 re: {} order id: {}", re, order.getId());
                if(re < 1){
                    throw new BtBankException(CommonMsgCode.UNKNOWN_ERROR);
                }
            }
            return;
        }

        if (null != order.getSaleRewardStatus() && order.getSaleRewardStatus() == 0) {
            //派发收益
            getService().dispatchSaleRewardForOrder(order);
            order.setSaleRewardStatus(1);
            int re = orderService.updateOrderDispatchSaleRewardStatus(order);

            log.info("商家订单补贴 re: {} order id: {}", re, order.getId());
            if(re < 1){
                throw new BtBankException(CommonMsgCode.UNKNOWN_ERROR);
            }

        }
    }

    public void dispatchSaleRewardForMemberOnDate(Date beginTime, Date endTime, Long memberId) {


        // 查总额，再补款
        String subsidyMaxString = configService.getValue(OtcConfigType.OTC_SALE_REWARD_SUBSIDY_MAX);
        BigDecimal subsidyMax = BigDecimal.valueOf(Double.valueOf(subsidyMaxString));

        String payMemberIdString = configService.getValue(OtcConfigType.OTC_SALE_REWARD_PAY_ACCOUNT);
        Long payMemberId = Long.valueOf(payMemberIdString);
        MessageRespResult<Member> member = memberApiService.getMember(payMemberId);
        if (member.getData() == null) {
            throw new BtBankException(BtBankMsgCode.SALE_REWARD_PAY_MEMBER_NOT_EXISTS);
        }
        //

        BigDecimal totalReward = orderService.getTotalSaleReward(beginTime, endTime, memberId);
        totalReward.setScale(2);

        log.info("用户  {}  {} 总销售反利  {}", memberId, endTime, totalReward);
        if (subsidyMax.compareTo(totalReward) > 0) {
            // 保留2位小数
            BigDecimal needSubsidy = BigDecimalUtil.sub(subsidyMax, totalReward).setScale(2, BigDecimal.ROUND_DOWN);


            if (BigDecimalUtil.gt0(needSubsidy)) {

                //添加订单补贴记录
                OtcOrderSubsidyPerDay orderSubsidyPerDay = new OtcOrderSubsidyPerDay();
                orderSubsidyPerDay.setId(IdWorker.getId());
                orderSubsidyPerDay.setSubsidyDate(endTime);
                orderSubsidyPerDay.setMemberId(memberId);
                orderSubsidyPerDay.setAmount(needSubsidy);

                // 远程扣减资产
                MessageRespResult reduceResult =
                        memberWalletService.optionMemberWalletBalance(TransactionType.OTC_BUSINESS_SALE_REWARED_PLATFORM, payMemberId, "BT", "BT", BigDecimalUtil.sub(BigDecimal.ZERO, needSubsidy), orderSubsidyPerDay.getId(), "平台补贴");
                Boolean succeedRe = (Boolean) reduceResult.getData();
                if (succeedRe == null || !succeedRe) {
                    log.warn("transfer amount into miner pool failed. memberId({}) amount({})", payMemberId, needSubsidy);
                    throw new BtBankException(MessageCode.ACCOUNT_BALANCE_INSUFFICIENT);
                }
                // 远程扣减资产
                MessageRespResult respResult =
                        memberWalletService.optionMemberWalletBalance(TransactionType.OTC_BUSINESS_SALE_REWARED_PLATFORM, memberId, "BT", "BT", needSubsidy, orderSubsidyPerDay.getId(), "平台补贴");
                Boolean succeeded = (Boolean) respResult.getData();
                if (succeedRe == null || !succeeded) {
                    log.warn("transfer amount into miner pool failed. memberId({}) amount({})", memberId, needSubsidy);
                    throw new BtBankException(MessageCode.ACCOUNT_BALANCE_INSUFFICIENT);
                }


                subsidyPerDayService.save(orderSubsidyPerDay);
            }
        }
    }

    /**
     * 按订单发放收益
     *
     * @param order
     */
    public void dispatchSaleRewardForOrder(OtcOrder order) {

        String rewardRateString = configService.getValue(OtcConfigType.OTC_BUSINESS_SALE_REWARD_RATE);
        BigDecimal rewardRate = BigDecimal.valueOf(Double.valueOf(rewardRateString));

        String payMemberIdString = configService.getValue(OtcConfigType.OTC_SALE_REWARD_PAY_ACCOUNT);
        Long payMemberId = Long.valueOf(payMemberIdString);


        //保存一下发放补贴记录
        /*OtcOrderSubsidy otcOrderSubsidy = new OtcOrderSubsidy();
        otcOrderSubsidy.setAmount(rewardRate);
        otcOrderSubsidy.setMemberId(order.getMemberId());
        otcOrderSubsidy.setCreateTime(order.getCreateTime());
        otcOrderSubsidy.setId(IdWorker.getId());
        otcOrderSubsidy.setUpdateTime(new Date());
        otcOrderSubsidy.setCreateTime(new Date());
        otcOrderSubsidy.setOrderId(order.getId());
        otcOrderSubsidyService.save(otcOrderSubsidy);*/

        if (BigDecimalUtil.gt0(rewardRate)) {
            BigDecimal reward = BigDecimalUtil.mul2down(rewardRate, order.getNumber(), 2);
            //保留2位小数
            //v ty

            // 远程扣减资产
            MessageRespResult reduceResult =
                    memberWalletService.optionMemberWalletBalance(TransactionType.OTC_BUSINESS_SALE_REWARED, payMemberId, "BT", "BT", BigDecimalUtil.sub(BigDecimal.ZERO, reward), order.getOrderSn(), "商家OTC出售奖励");
            Boolean succeedRe = (Boolean) reduceResult.getData();
            if (succeedRe == null || !succeedRe) {
                log.warn("transfer amount into miner pool failed. memberId({}) amount({})", payMemberId, reward);
                throw new BtBankException(MessageCode.ACCOUNT_BALANCE_INSUFFICIENT);
            }
            // 远程扣减资产
            MessageRespResult respResult =
                    memberWalletService.optionMemberWalletBalance(TransactionType.OTC_BUSINESS_SALE_REWARED, order.getMemberId(), "BT", "BT", reward, order.getOrderSn(), "商家OTC出售奖励");
            Boolean succeeded = (Boolean) respResult.getData();
            if (succeedRe == null || !succeeded) {
                log.warn("transfer amount into miner pool failed. memberId({}) amount({})", order.getMemberId(), reward);
                throw new BtBankException(MessageCode.ACCOUNT_BALANCE_INSUFFICIENT);
            }
        }
    }

    @Override
    public List<Date> getNeedDispatchDateList() {
        return orderService.getNeedDispatchDateList();
    }

    @Override
    public List<Long> getNeedDispatchMemberIds() {
        return orderService.getNeedDispatchMemberIds();

    }

    @Override
    public Date getEarliestDate() {
        return orderService.getEarliestDate();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void dispatchSaleRewardForDay(Date beginTime, Date endTime, Long memberId) {


        //查时长
        OtcOrderSubsidyPerDay existsOrderSubsidyPerDay = subsidyPerDayService.lambdaQuery()
                .eq(OtcOrderSubsidyPerDay::getMemberId, memberId).eq(OtcOrderSubsidyPerDay::getSubsidyDate, endTime).one();
        if (existsOrderSubsidyPerDay != null) {
            //已经有当天发的
            return;
        }

        //计算用户广告在线时长
        String needOnlineDurationString = configService.getValue(OtcConfigType.OTC_SALE_REWARD_SUBSIDY_AD_ONLINE_DURATION);
        Long needOnlineDuration = Long.valueOf(needOnlineDurationString);


        long onlineMinutes = 0;

        List<AdvertiseOperationHistory> historiesAll = new ArrayList<>();
        List<AdvertiseOperationHistory> histories = operationHistoryService.listAdHistroysByMemberId(memberId, beginTime, endTime);
        if (histories == null || histories.size() < 1) {
            AdvertiseOperationHistory advertiseOperationHistory = operationHistoryService.lambdaQuery()
                    .eq(AdvertiseOperationHistory::getMemberId, memberId)
                    .le(AdvertiseOperationHistory::getCreateTime, beginTime)
                    .orderByDesc(AdvertiseOperationHistory::getCreateTime).last(" limit 1").one();
            if (Objects.isNull(advertiseOperationHistory)) {
                return;
            }
            if (advertiseOperationHistory.getNewStatus() == 1) {
                return;
            }
            histories = new ArrayList<>();
            advertiseOperationHistory.setCreateTime(beginTime);
            histories.add(advertiseOperationHistory);
        }

        if (histories.get(0).getNewStatus() == 1) {
            AdvertiseOperationHistory advertiseOperationHistory = new AdvertiseOperationHistory();
            advertiseOperationHistory.setOldStatus(1);
            advertiseOperationHistory.setNewStatus(0);
            advertiseOperationHistory.setCreateTime(beginTime);
            historiesAll.add(advertiseOperationHistory);
        }
        historiesAll.addAll(histories);
        if (historiesAll.get(historiesAll.size() - 1).getNewStatus() == 0) {
            AdvertiseOperationHistory advertiseOperationHistory = new AdvertiseOperationHistory();
            advertiseOperationHistory.setOldStatus(0);
            advertiseOperationHistory.setNewStatus(1);
            advertiseOperationHistory.setCreateTime(endTime);
            historiesAll.add(advertiseOperationHistory);
        }

        for (int i = 0; i < historiesAll.size() - 1; i++) {
            AdvertiseOperationHistory lastHistory = historiesAll.get(i);
            AdvertiseOperationHistory nextHistory = historiesAll.get(i + 1);
            if (lastHistory.getNewStatus() == 0 && nextHistory.getNewStatus() == 1) {
                onlineMinutes = onlineMinutes + BigDecimalUtils.div(
                        new BigDecimal(nextHistory.getCreateTime().getTime() - lastHistory.getCreateTime().getTime()),
                        new BigDecimal("60000")).longValue();
            }
        }

        if (onlineMinutes > 0) {
            log.info("用户  {}  广告总在线时长 {} 分钟", memberId, onlineMinutes);
        }
        if (onlineMinutes >= needOnlineDuration) {
            getService().dispatchSaleRewardForMemberOnDate(beginTime, endTime, memberId);
        }
    }

    public MinerOtcOrderServiceImpl getService(){
        return SpringContextUtil.getBean(MinerOtcOrderServiceImpl.class);
    }
}
