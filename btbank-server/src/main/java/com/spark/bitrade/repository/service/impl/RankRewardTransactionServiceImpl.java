package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.biz.impl.MinerRebateServiceImpl;
import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.entity.WalletChangeRecord;
import com.spark.bitrade.enums.MessageCode;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.*;
import com.spark.bitrade.repository.mapper.RankRewardTransactionMapper;
import com.spark.bitrade.repository.service.OtcConfigDataDictService;
import com.spark.bitrade.repository.service.RankRewardConfigService;
import com.spark.bitrade.repository.service.RankRewardTransactionService;
import com.spark.bitrade.service.MemberWalletService;
import com.spark.bitrade.util.BigDecimalUtil;
import com.spark.bitrade.util.BigDecimalUtils;
import com.spark.bitrade.util.IdWorkByTwitter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

/**
 * 奖励金额流水(RankRewardTransaction)表服务实现类
 *
 * @author daring5920
 * @since 2019-12-17 15:14:10
 */
@Service("rankRewardTransactionService")
@Slf4j
public class RankRewardTransactionServiceImpl extends ServiceImpl<RankRewardTransactionMapper, RankRewardTransaction> implements RankRewardTransactionService {

    @Autowired
    private RankRewardTransactionMapper rankRewardTransactionMapper;

    @Autowired
    MemberWalletService memberWalletService;

    @Autowired
    OtcConfigDataDictService otcConfigDataDictService;

    @Autowired
    private RankRewardConfigService rankRewardConfigService;

    @Autowired
    private IdWorkByTwitter idWorkByTwitter;

    @Override
    public void getAndInsertReward() {
        //查询每日收益排行榜
        List<RankRewardTransaction> dayRewardList = rankRewardTransactionMapper.getDayReward(LocalDate.now().minusDays(1));
        //Assert.isTrue(dayRewardList.size() != 0, "查询每日收益排行榜");
        //发放每日收益排行榜奖励
        updateRank(dayRewardList);
        //查询每日推广收益排行榜
        List<RankRewardTransaction> dayExtensionList = rankRewardTransactionMapper.getDayExtensionReward(LocalDate.now().minusDays(1));
        //发放每日推广收益排行榜奖励
        updateRank(dayExtensionList);
    }

    @Override
    public void insertTotalReward() {
        //查询累计收益排行榜
        List<RankRewardTransaction> totalList = rankRewardTransactionMapper.getTotalReward();
        totalList.forEach(rank ->{
            rank.setRewardLevel(totalList.indexOf(rank)+1);
        });
        this.saveBatch(totalList);
    }

    @Override
    public List<RankRewardTransaction> getRankListByType(RankRewardTransaction rankRewardTransaction) {

        return rankRewardTransactionMapper.getRankListByType(rankRewardTransaction );
    }

    //发放各种收益排行榜奖励
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void updateRank(List<RankRewardTransaction> list) {
        List<RankRewardConfig> listConfig = rankRewardConfigService.list();
        Map<Integer,RankRewardConfig> map1 = new HashMap<>();
        Map<Integer,RankRewardConfig> map2 = new HashMap<>();
        for(RankRewardConfig config:listConfig){
            if(config.getRewardType().intValue() == 1){
                map1.put(config.getRewardLevel(),config);
            }else {
                map2.put(config.getRewardLevel(),config);
            }
        }
        for(int i=0;i<list.size();i++){
            RankRewardConfig rankRewardConfig = null;
            if(list.get(i).getRewardType() == 1){
                rankRewardConfig = map1.get(i+1);
            }else {
                rankRewardConfig = map2.get(i+1);
            }
            if(rankRewardConfig.getReward().compareTo(BigDecimal.ZERO) == 0 ){
                Date now = new Date();
                list.get(i).setRewardTime(now);
                list.get(i).setCreateTime(now);
                list.get(i).setUpdateTime(now);
                list.get(i).setReward(BigDecimal.ZERO);
                list.get(i).setRewardLevel(i+1);
                this.save(list.get(i));
            }else {
                MemberWalletService.TradePlan plan = new MemberWalletService.TradePlan();
                try{
                    RankRewardTransaction rankRewardTransaction = rankRewardTransactionMapper.findOne(list.get(i).getMemberId(),LocalDate.now(),list.get(i).getRewardType());
                    Assert.isTrue(rankRewardTransaction == null, "当天收益排行榜奖励已发放过");
                    list.get(i).setRewardLevel(list.indexOf(list.get(i))+1);
                    this.award(list.get(i),plan);
                    if (log.isInfoEnabled()) {
                        log.info("收益排行奖励发放完成，执行远程提交.提交总数 {} 计划:", plan.getQueue().size());
                        plan.getQueue().forEach(y -> {
                            log.info("memberId({}) amount({}) type({})", y.getMemberId(), y.getTradeBalance(), y.getType());
                        });
                    }
                    memberWalletService.confirmPlan(plan);
                }catch (Exception e){
                    if (log.isInfoEnabled() && plan.getQueue().size() > 0) {
                        log.info("收益排行奖励发放失败，执行远程回滚. 回滚总数({}) 计划:", plan.getQueue().size());
                        plan.getQueue().forEach(y -> {
                            log.info("memberId({}) amount({}) type({})", y.getMemberId(), y.getTradeBalance(), y.getType());
                        });
                    }
                    e.printStackTrace();
                    memberWalletService.rollbackPlan(plan);
                }
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void award(RankRewardTransaction rank, MemberWalletService.TradePlan plan) {

        //查询发放奖励钱包
        OtcConfigDataDict oct = otcConfigDataDictService.getValue("OTC_SALE_REWARD_PAY_ACCOUNT");
        Assert.isTrue(oct != null, "查询收益排行榜发放账户失败");
        QueryWrapper<RankRewardConfig> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(true,"reward_level",rank.getRewardLevel())
                    .eq(true,"reward_type",rank.getRewardType());
        RankRewardConfig rankConfig = rankRewardConfigService.getOne(queryWrapper);
        Assert.isTrue(rankConfig != null, "奖励金额配置不完全");
        rank.setId(idWorkByTwitter.nextId());
        rank.setReward(rankConfig.getReward());
        // TCC 预扣除 rewardAmount 数量的奖励佣金
        TransactionType transactionType = null;
        if(rank.getRewardType() == 1){
            transactionType = TransactionType.REVENUE_DAY_RANKING_REWARD;
        }else if(rank.getRewardType() == 2){
            transactionType = TransactionType.REVENUE_DAY_EXTENSION_RANKING_REWARD;
        }
        WalletChangeRecord supplyRecord = memberWalletService.tryTrade(transactionType
                , Long.valueOf(oct.getDictVal())
                , "BT"
                , "BT"
                , rank.getReward().negate()
                , rank.getId()
                , "收益排行榜奖励");
        if (Objects.isNull(supplyRecord)) {
            // 预扣款失败
            log.info("memberWalletService.tryTrade failed. memberId({}) amount({}) TransactionType({})", Long.valueOf(oct.getDictVal()),
                    rank.getReward().negate(), transactionType);
            throw new BtBankException(MessageCode.ACCOUNT_BALANCE_INSUFFICIENT);
        }
        plan.getQueue().add(supplyRecord);

        // 保存奖励流水
        Date now = new Date();
        rank.setRewardTime(now);
        rank.setCreateTime(now);
        rank.setUpdateTime(now);
        if (!this.save(rank)) {
            throw new BtBankException(BtBankMsgCode.RELEASE_RANK_REWARD_FAILED);
        }

        log.debug("保存收益排行榜发放奖励流水记录：{}", rank);

        // 账户可用增加资产
        //TransactionType txType = rebatePlan.getRebateLevel() > 1 ? TransactionType.GOLDEN_MINER_REWARD : TransactionType.DIRECT_MINER_REWARD;
        WalletChangeRecord chargeRecord = memberWalletService.tryTrade(transactionType,
                rank.getMemberId(),
                "BT",
                "BT",
                rank.getReward(),
                rank.getId(),
                "收益排行榜奖励");
        if (Objects.isNull(chargeRecord)) {
            // 预扣款失败
            log.info("memberWalletService.tryTrade Reward credit failed. memberId({}) amount({}) TransactionType({})", rank.getMemberId(),
                    rank.getReward().negate(), transactionType);
            throw new BtBankException(MessageCode.FAILED_ADD_BALANCE);
        }
        plan.getQueue().add(chargeRecord);
    }

}