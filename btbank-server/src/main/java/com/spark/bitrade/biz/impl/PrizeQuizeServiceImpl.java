package com.spark.bitrade.biz.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.api.vo.MinerPrizeQuizeVo;
import com.spark.bitrade.api.vo.PrizeQuizeRecordVO;
import com.spark.bitrade.api.vo.PrizeQuizeVo;
import com.spark.bitrade.api.vo.QueryVo;
import com.spark.bitrade.biz.PrizeQuizeService;
import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.constant.BtBankPrizeQuizConfig;
import com.spark.bitrade.constant.BtBankSystemConfig;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.WalletChangeRecord;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.GuessingConfigDataDict;
import com.spark.bitrade.repository.entity.MinerPrizeQuizeTransaction;
import com.spark.bitrade.repository.entity.PrizeQuizeRecord;
import com.spark.bitrade.repository.service.GuessingConfigDataDictService;
import com.spark.bitrade.repository.service.MinerPrizeQuizeTransactionService;
import com.spark.bitrade.repository.service.PrizeQuizeRecordService;
import com.spark.bitrade.service.MemberWalletService;
import com.spark.bitrade.util.DateUtil;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.util.SpringContextUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * @author qiuyuanjie
 * @time 2020.01.02.10:34
 */
@Slf4j
@Service
@AllArgsConstructor
public class PrizeQuizeServiceImpl  implements PrizeQuizeService {
    private GuessingConfigDataDictService guessingConfigDataDictService;
    private PrizeQuizeRecordService prizeQuizeRecordService;
    private RedisTemplate redisTemplate;
    private MinerPrizeQuizeTransactionService minerPrizeQuizeTransactionService;
    private MemberWalletService memberWalletService;

    @SuppressWarnings("unchecked")
    public Object getConfig(String key) {
        String redisKey = BtBankSystemConfig.REDIS_PRIZE_QUIZE_PREFIX + key;
        Object o = redisTemplate.opsForValue().get(redisKey);
        if (o == null) {
            GuessingConfigDataDict one = guessingConfigDataDictService.findFirstByDictIdAndDictKey(BtBankPrizeQuizConfig.BT_BANK_QUIZE_CONFIG,
                    key);
            if (one != null) {
                o = one.getDictVal();
                redisTemplate.opsForValue().set(redisKey, o);
            }
        }
        if (o == null) {
            log.warn(" BtBankSystemConfig [{}] value not exists", key);
        }
        return o;
    }
    @Override
    public <T> T getConfig(String key, Function<Object, T> convert, T defaultValue) {
        Object value = getService().getConfig(key);
        if (value == null) {
            return defaultValue;
        }
        return convert.apply(value);
    }
    @Override
    public int getCntByCoinUnit(String coinUnit) {
        QueryWrapper<PrizeQuizeRecord> query = new QueryWrapper<>();
        query.eq("coin_unit", coinUnit);
        return prizeQuizeRecordService.count(query);
    }

    @Override
    public boolean existRecord(String coinUnit, Date startTime) {
        QueryWrapper<PrizeQuizeRecord> query = new QueryWrapper<>();
        query.eq("coin_unit", coinUnit).eq("start_time",startTime);
        return  prizeQuizeRecordService.count(query)  == 0 ? false:true ;
    }

    @Override
    public PrizeQuizeRecord queryRecordByStartTime(String coinUnit, Date startTime) {
        QueryWrapper<PrizeQuizeRecord> query = new QueryWrapper<>();
        query.eq("coin_unit", coinUnit).eq("start_time",startTime);
        return prizeQuizeRecordService.getOne(query);
    }

    @Transactional
    @Override
    public boolean minerJoinQuiz(String coinUnit,Long prieQuizeId,MinerPrizeQuizeTransaction minerPrizeQuizeTransaction) {
        if(minerPrizeQuizeTransaction.getAmount() ==null || BigDecimal.ZERO.compareTo(minerPrizeQuizeTransaction.getAmount()) == 1){
            throw new BtBankException(BtBankMsgCode.PRIZE_QUIZE_AMOUNT_INVALID);
        }
        PrizeQuizeRecord prizeQuizeRecord = prizeQuizeRecordService.getById(prieQuizeId);
        if(prizeQuizeRecord!=null){
            Boolean addprizeQuizeRecordStatic = false;
            // 跌
            if(minerPrizeQuizeTransaction.getPrizeQuizeType()==0){
                addprizeQuizeRecordStatic = prizeQuizeRecordService.lambdaUpdate()
                        .setSql("total_num = total_num +" + 1 )
                        .setSql("down_num = down_num +" + 1 )
                        .setSql("total_amount = total_amount +" +  minerPrizeQuizeTransaction.getAmount() )
                        .setSql("down_amount = down_amount +" +  minerPrizeQuizeTransaction.getAmount() )
                        .eq(PrizeQuizeRecord::getId, prieQuizeId).update();
                //涨
            }else if(minerPrizeQuizeTransaction.getPrizeQuizeType()==1){
                addprizeQuizeRecordStatic = prizeQuizeRecordService.lambdaUpdate()
                        .setSql("total_num = total_num +" + 1 )
                        .setSql("up_num = up_num +" + 1 )
                        .setSql("total_amount = total_amount +" +  minerPrizeQuizeTransaction.getAmount() )
                        .setSql("up_amount = up_amount +" +  minerPrizeQuizeTransaction.getAmount() )
                        .eq(PrizeQuizeRecord::getId, prieQuizeId).update();
            }
            return addprizeQuizeRecordStatic;
        }
        return false;
    }

    @Override
    public MessageRespResult<PrizeQuizeVo> getCurrentPrize(Long memberId) {
        PrizeQuizeRecord record = getPrize();
        PrizeQuizeVo vo = null;
        //判断用户是否登陆
        if (null == memberId){
            //未登陆
            vo = PrizeQuizeVo.transfer(record,null);
            vo.setMinAmount(getMinAmount());
            vo.setMaxAmount(getMaxAmount());
            return MessageRespResult.success4Data(vo);
        }else{
            //登陆
            QueryWrapper<MinerPrizeQuizeTransaction> minerWrapper = new QueryWrapper<>();
            Integer id = record.getId();
            minerWrapper.eq("prie_quize_id",id);
            minerWrapper.eq("member_id",memberId);
            MinerPrizeQuizeTransaction transaction = minerPrizeQuizeTransactionService.getOne(minerWrapper);
            vo = PrizeQuizeVo.transfer(record,transaction);
            vo.setMinAmount(getMinAmount());
            vo.setMaxAmount(getMaxAmount());
            return MessageRespResult.success4Data(vo);
        }
    }

    @Override
    public PrizeQuizeRecord getPrize() {
        QueryWrapper<PrizeQuizeRecord> wrapper = new QueryWrapper<>();
        Date now = new Date();
        //创建时间要小于等于现在
        wrapper.le("start_time",now);
        //开奖时间要大于等于现在
        wrapper.ge("reward_result_time",now);
        //活动要进行中
        wrapper.and(e-> e.eq("type",1).or().eq("type",3));
        //获取当日活动记录
        PrizeQuizeRecord record = prizeQuizeRecordService.getOne(wrapper);
        if (null == record) {
            throw new BtBankException(BtBankMsgCode.ACTIVITY_NOT_START);
        }
        if (record.getType() == 3){
            throw new BtBankException(BtBankMsgCode.TURNTABLE_ACTIVITY_PAUSED);
        }
        return record;
    }

    @Override
    public MessageRespResult<IPage<PrizeQuizeRecordVO>> getPrizeResult(QueryVo queryVo) {
        QueryWrapper<PrizeQuizeRecord> wrapper = new QueryWrapper<>();
        //竞猜状态，0未开始，1开始，2结束
        wrapper.eq("type",2);
        wrapper.orderByDesc("reward_result_time");
        IPage<PrizeQuizeRecord> ipage = queryVo.toPage();
        IPage<PrizeQuizeRecord> prizeQuizeRecordIPage = prizeQuizeRecordService.page(ipage, wrapper);
        List<PrizeQuizeRecord> records = prizeQuizeRecordIPage.getRecords();
        //将竞猜结果转换成一个竞猜结果VO展示
        List<PrizeQuizeRecordVO> collect = records.stream().map(PrizeQuizeRecordVO::transfer).filter(e -> null != e).collect(Collectors.toList());
        IPage<PrizeQuizeRecordVO> page = new Page<PrizeQuizeRecordVO>();
        page.setRecords(collect);
        page.setTotal(collect.size());
        page.setSize(ipage.getSize());
        return MessageRespResult.success4Data(page);
    }

    @Override
    public IPage<MinerPrizeQuizeVo> minerPrizeRecord(Long memberId,QueryVo queryVo) {
//        QueryWrapper<MinerPrizeQuizeTransaction> wrapper = new QueryWrapper<>();
//        wrapper.eq("member_id",memberId);
//        wrapper.orderByDesc("reward_release_time");
        IPage<MinerPrizeQuizeTransaction> page = queryVo.toPage();
        List<MinerPrizeQuizeVo> list = minerPrizeQuizeTransactionService.getMinerTransaction(memberId, page);

        IPage<MinerPrizeQuizeVo> result = new Page<>();
        result.setTotal(list.size());
        result.setSize(page.getSize());
        result.setRecords(list);
        return result;
    }

    @Override
    public Boolean collect(PrizeQuizeRecord prizeQuizeRecord) {
        //查询归集账号
        // 归集到账户
        Long memberId = getService().getConfig(BtBankPrizeQuizConfig.PRIZE_QUIZ_COLLECT_ACCOUNT , v -> Long.parseLong(v.toString()), 0L);
        if (memberId == 0) {
            throw new IllegalArgumentException("未找到归集账户 ENTERPRISE_MINER_RECEIVE_ACCOUNT 配置");
        }
        QueryWrapper<MinerPrizeQuizeTransaction> wrapper = new QueryWrapper<>();
        wrapper.eq("prie_quize_id",prizeQuizeRecord.getId())
                .ne("prize_quize_type",prizeQuizeRecord.getPriQuizeResult() ).eq("release_status",0);
        List<MinerPrizeQuizeTransaction> minerPrizeQuizeTransactionLst = minerPrizeQuizeTransactionService.list(wrapper);
        if(minerPrizeQuizeTransactionLst.size()==0){
            log.info("没有要扣款的记录{}",prizeQuizeRecord);
            return true;
        }
        for (MinerPrizeQuizeTransaction minerPrizeQuizeTransaction :minerPrizeQuizeTransactionLst){
            getService().handleCollect(memberId,minerPrizeQuizeTransaction);
        }
        return true ;
    }

    @Override
    public Boolean release(PrizeQuizeRecord prizeQuizeRecord) {
        QueryWrapper<MinerPrizeQuizeTransaction> wrapper = new QueryWrapper<>();
        wrapper.eq("prie_quize_id",prizeQuizeRecord.getId())
                .eq("prize_quize_type",prizeQuizeRecord.getPriQuizeResult() ).eq("release_status",0);
        List<MinerPrizeQuizeTransaction> minerPrizeQuizeTransactionLst = minerPrizeQuizeTransactionService.list(wrapper);
        if(minerPrizeQuizeTransactionLst.size()==0){
            log.info("没有要计算分红的记录{}",prizeQuizeRecord);
            return true;
        }
        Long memberId = getService().getConfig(BtBankPrizeQuizConfig.PRIZE_QUIZ_REWARD_ACCOUNT , v -> Long.parseLong(v.toString()), 0L);
        if (memberId == 0) {
            throw new IllegalArgumentException("未找到奖励账户 PRIZE_QUIZ_REWARD_ACCOUNT 配置");
        }


        /* BigDecimal rate = getService().getConfig(BtBankPrizeQuizConfig.PRIZE_QUIZ_RATE , v -> (new BigDecimal(v.toString())),BigDecimal.ZERO);
        if (rate == BigDecimal.ZERO) {
            throw new IllegalArgumentException("未找到奖励账户 PRIZE_QUIZ_RATE 配置");
        }*/
        BigDecimal totalReward =  prizeQuizeRecord.getRewardAmount();//.multiply(rate); //压中方应得的金额
        BigDecimal totalRewardAmount = BigDecimal.ZERO;

        if(prizeQuizeRecord.getPriQuizeResult() == 0 ){
            totalRewardAmount = prizeQuizeRecord.getDownAmount();

        }else{
            totalRewardAmount = prizeQuizeRecord.getUpAmount();
        }
        if(BigDecimal.ZERO.compareTo(totalRewardAmount)>=0){
            log.info("该活动没有压中方{}",prizeQuizeRecord);
            return true;
        }
        BigDecimal perCentAmount = totalReward.divide(totalRewardAmount,8,BigDecimal.ROUND_DOWN);
        MinerPrizeQuizeTransaction maxminerPrizeQuizeTransaction  = minerPrizeQuizeTransactionService.queryMaxAmount(prizeQuizeRecord.getId().longValue());
        prizeQuizeRecord.setMaxReward(perCentAmount.multiply(maxminerPrizeQuizeTransaction.getAmount()).setScale(2,BigDecimal.ROUND_DOWN));
        prizeQuizeRecord.setMaxRewardMemberId(maxminerPrizeQuizeTransaction.getMemberId());
        if(prizeQuizeRecordService.updateMaxReward(prizeQuizeRecord)){
            log.info("更新当前活动最大竞猜获得金额{}",prizeQuizeRecord);
        }
        for (MinerPrizeQuizeTransaction minerPrizeQuizeTransaction :minerPrizeQuizeTransactionLst){

            if(minerPrizeQuizeTransaction.getReleaseStatus()==null||minerPrizeQuizeTransaction.getReleaseStatus()==0){
                // 释放本金
                try {
                    getService().handleRelease(minerPrizeQuizeTransaction);
                }catch (Exception e){
                    e.getMessage();

                }

            }
            //分红金额与参与金额有关
            BigDecimal rewardAmount = perCentAmount.multiply(minerPrizeQuizeTransaction.getAmount()).setScale(2,BigDecimal.ROUND_DOWN);
            if(minerPrizeQuizeTransaction.getRewardStatus()==null||minerPrizeQuizeTransaction.getRewardStatus()==0){
                // 释放奖励
                try {
                    getService().reward(minerPrizeQuizeTransaction,rewardAmount,memberId);
                }catch (Exception e){
                    e.getMessage();
                }

            }
        }
        prizeQuizeRecordService.updateRewardRealseTime(prizeQuizeRecord);
        return true;
    }



    @Transactional
    public boolean reward(MinerPrizeQuizeTransaction minerPrizeQuizeTransaction,BigDecimal rewardAmount,Long memberId) {
        MemberWalletService.TradePlan plan = new MemberWalletService.TradePlan();
        try{
            // 从发放的账户扣款
            WalletChangeRecord record = memberWalletService.tryTrade(
                    TransactionType.PRIZEQUIZE_TRANSFER_REWARD,
                    memberId, "BT", "BT", rewardAmount.negate(),  minerPrizeQuizeTransaction.getId(),
                    "竞猜奖励扣款");

            if(record == null){
                log.error("奖励发放账户扣竞猜金额失败 txId = {}, member_id = {}, amount = {}", minerPrizeQuizeTransaction.getId(),memberId,rewardAmount.abs());
                throw new BtBankException(BtBankMsgCode.PRIZE_QUIZE_TRANFERIN_ERROR);
            }
            plan.getQueue().add(record);

            //收款账户加款
            WalletChangeRecord rewardRecord = memberWalletService.tryTrade(
                    TransactionType.PRIZEQUIZE_TRANSFER_REWARD,
                    minerPrizeQuizeTransaction.getMemberId(), "BT", "BT", rewardAmount.abs(), minerPrizeQuizeTransaction.getId(),
                    "竞猜奖励");
            if (rewardRecord == null) {
                log.error("竞猜发放失败 txId = {}, member_id = {}, amount = {}", minerPrizeQuizeTransaction.getId(), minerPrizeQuizeTransaction.getMemberId(), rewardAmount.abs());
                throw new BtBankException(BtBankMsgCode.PRIZE_QUIZE_TRANFERIN_ERROR);
            }
            plan.getQueue().add(rewardRecord);
            //更新奖励发放状态
            if(minerPrizeQuizeTransactionService.reward(minerPrizeQuizeTransaction,rewardRecord.getId(),rewardAmount.abs())){
                boolean b =  memberWalletService.confirmPlan(plan);
                if (!b) {
                    log.error("确认奖励发放失败 plan = {}", plan);
                    throw new BtBankException(CommonMsgCode.FAILURE);
                } else {
                    return true;
                }

            }else{
                log.error("更改奖励状态失败 txId = {}, member_id = {}, amount = {}", minerPrizeQuizeTransaction.getId(), minerPrizeQuizeTransaction.getMemberId(), rewardAmount.abs());
                throw new BtBankException(BtBankMsgCode.PRIZE_QUIZE_TRANFERIN_ERROR);
            }
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
        return false;
    }

    @Override
    @Transactional
    public Boolean joinPrizeQuize(MinerPrizeQuizeTransaction minerPrizeQuizeTransaction) {
        if(minerPrizeQuizeTransaction.getAmount().compareTo(BigDecimal.ZERO)< 1){
            throw new BtBankException(BtBankMsgCode.PRIZE_QUIZE_AMOUNT_INVALID);
        }
        if(minerPrizeQuizeTransaction.getPrieQuizeId()==null){
            throw new BtBankException(BtBankMsgCode.PRIZE_QUIZE_JOIN_ERROR);
        }
        // 尝试转入
        /* WalletChangeRecord record = walletService.tryTrade(
                TransactionType.PRIZEQUIZE_TRANSFER_FREEZE,
                minerPrizeQuizeTransaction.getMemberId(), "BT", "BT", minerPrizeQuizeTransaction.getAmount().negate(), minerPrizeQuizeTransaction.getId(),
                "竞猜投注锁仓");*/

        //收款账户扣款
        WalletChangeRecord record = memberWalletService.freeze(TransactionType.PRIZEQUIZE_TRANSFER_FREEZE,minerPrizeQuizeTransaction.getMemberId(),"BT", "BT", minerPrizeQuizeTransaction.getAmount(),
                minerPrizeQuizeTransaction.getId(),"竞猜投注锁仓");

        if (record == null) {
            log.error("竞猜锁仓 txId = {}, member_id = {}, amount = {}", minerPrizeQuizeTransaction.getId(), minerPrizeQuizeTransaction.getMemberId(), minerPrizeQuizeTransaction.getAmount().abs());
            throw new BtBankException(BtBankMsgCode.PRIZE_QUIZE_TRANFERIN_ERROR);
        }
        Date now = new Date();
        minerPrizeQuizeTransaction.setRefId(record.getId()+"");
        minerPrizeQuizeTransaction.setGuessStatus(0);
        minerPrizeQuizeTransaction.setRewardStatus(0);
        minerPrizeQuizeTransaction.setReleaseStatus(0);
        minerPrizeQuizeTransaction.setUpdateTime(now);
        minerPrizeQuizeTransaction.setCreateTime(now);
        try {
            if(minerPrizeQuizeTransactionService.save(minerPrizeQuizeTransaction)){
                //计算累加统计
                getService().minerJoinQuiz("BTC",minerPrizeQuizeTransaction.getPrieQuizeId(),minerPrizeQuizeTransaction);
                boolean b = memberWalletService.confirmTrade(minerPrizeQuizeTransaction.getMemberId(), record.getId());
                if (!b) {
                    log.error("确认账户变动失败 record = {}", record);
                    throw new BtBankException(CommonMsgCode.FAILURE);
                } else {
                    return true;
                }
            }
            throw new BtBankException(CommonMsgCode.FAILURE);
        } catch (RuntimeException ex) {
            log.error("参与活动锁仓失败 txId = {}, err = {}", minerPrizeQuizeTransaction.getId(), ex.getMessage());
            boolean b = memberWalletService.rollbackTrade(minerPrizeQuizeTransaction.getMemberId(), record.getId());
            log.info("回滚账户变动 result = {}, record = {}", b, record);
            throw ex;
        }
    }
    @Transactional
    public boolean handleRelease( MinerPrizeQuizeTransaction minerPrizeQuizeTransaction) {
        //系统收款账户归集
        if(minerPrizeQuizeTransaction.getReleaseStatus()!=1){
            // 释放
            WalletChangeRecord record = memberWalletService.realseFreeze(
                    TransactionType.PRIZEQUIZE_TRANSFER_REALSE,
                    minerPrizeQuizeTransaction.getMemberId(), "BT", "BT", minerPrizeQuizeTransaction.getAmount(), minerPrizeQuizeTransaction.getId(),
                    "竞猜解锁");

            if (record == null) {
                log.error("竞猜解锁 txId = {}, member_id = {}, amount = {}", minerPrizeQuizeTransaction.getId(), minerPrizeQuizeTransaction.getMemberId(), minerPrizeQuizeTransaction.getAmount().abs());
                throw new IllegalArgumentException("竞猜解锁失败");
            }
            try {
                if(minerPrizeQuizeTransactionService.release(minerPrizeQuizeTransaction,record.getId())){
                    boolean b = memberWalletService.confirmTrade(minerPrizeQuizeTransaction.getMemberId(), record.getId());
                    if (!b) {
                        log.error("确认账户变动失败 record = {}", record);
                        throw new BtBankException(CommonMsgCode.FAILURE);
                    } else {
                        return true;
                    }
                }
                throw new BtBankException(CommonMsgCode.FAILURE);
            } catch (RuntimeException ex) {
                log.error("竞猜解锁失败 txId = {}, err = {}", minerPrizeQuizeTransaction.getId(), ex.getMessage());
                boolean b = memberWalletService.rollbackTrade(minerPrizeQuizeTransaction.getMemberId(), record.getId());
                log.info("竞猜解锁变动 result = {}, record = {}", b, record);
                throw ex;
            }
        }
        return false;
    }
    @Transactional
    public boolean handleCollect(Long memberId, MinerPrizeQuizeTransaction minerPrizeQuizeTransaction) {
        MemberWalletService.TradePlan plan = new MemberWalletService.TradePlan();
        // 释放
        WalletChangeRecord realseRecord = memberWalletService.realseFreeze(
                TransactionType.PRIZEQUIZE_TRANSFER_REALSE,
                minerPrizeQuizeTransaction.getMemberId(), "BT", "BT", minerPrizeQuizeTransaction.getAmount(), minerPrizeQuizeTransaction.getId(),
                "竞猜解锁");
        try{
            if (realseRecord == null) {
                log.error("释放竞猜冻结 txId = {}, member_id = {}, amount = {}", minerPrizeQuizeTransaction.getId(), minerPrizeQuizeTransaction.getMemberId(), minerPrizeQuizeTransaction.getAmount().abs());
                throw new IllegalArgumentException("归集处理失败");
            }
            //先确认释放，在进行以下业务
            if(memberWalletService.confirmTrade(minerPrizeQuizeTransaction.getMemberId(),realseRecord.getId())){
                try{
                    // 扣款
                    WalletChangeRecord drwaRecord = memberWalletService.tryTrade(
                            TransactionType.PRIZEQUIZE_TRANSFER_FAIL,
                            minerPrizeQuizeTransaction.getMemberId(), "BT", "BT", minerPrizeQuizeTransaction.getAmount().negate(), minerPrizeQuizeTransaction.getId(),
                            "竞猜失败");
                    if (drwaRecord == null) {
                        log.error("竞猜失败扣款失败 txId = {}, member_id = {}, amount = {}", minerPrizeQuizeTransaction.getId(), minerPrizeQuizeTransaction.getMemberId(), minerPrizeQuizeTransaction.getAmount().abs());
                        throw new IllegalArgumentException("竞猜失败方扣款失败");
                    }
                    plan.getQueue().add(drwaRecord);
                    // 尝试加帐
                    WalletChangeRecord record = memberWalletService.tryTrade(
                            TransactionType.PRIZEQUIZE_TRANSFER_COLLECT,
                            memberId, "BT", "BT", minerPrizeQuizeTransaction.getAmount().abs(), minerPrizeQuizeTransaction.getId(),
                            "竞猜失败方扣款金额归集");
                    if (record == null) {
                        log.error("归集处理失败 txId = {}, member_id = {}, amount = {}", minerPrizeQuizeTransaction.getId(), memberId, minerPrizeQuizeTransaction.getAmount().abs());
                        throw new IllegalArgumentException("归集处理失败");
                    }
                    plan.getQueue().add(record);

                    if(minerPrizeQuizeTransactionService.collect(minerPrizeQuizeTransaction,record.getId())){
                        return memberWalletService.confirmPlan(plan);
                    }
                    throw new BtBankException(CommonMsgCode.FAILURE);
                }catch (RuntimeException ex) {
                    if (log.isInfoEnabled() && plan.getQueue().size() > 0) {
                        log.info("失败方失败，执行远程回滚. 回滚总数({}) 计划:", plan.getQueue().size());
                        plan.getQueue().forEach(y -> {
                            log.info("memberId({}) amount({}) type({})", y.getMemberId(), y.getTradeBalance(), y.getType());
                        });
                        ex.printStackTrace();
                        memberWalletService.rollbackPlan(plan);
                    }

                }
            } else {
                throw new BtBankException(CommonMsgCode.FAILURE);
            }
        } catch (RuntimeException ex) {
            log.error("竞猜解锁失败 txId = {}, err = {}", minerPrizeQuizeTransaction.getId(), ex.getMessage());
            boolean b = memberWalletService.rollbackTrade(minerPrizeQuizeTransaction.getMemberId(), realseRecord.getId());
            log.info("竞猜解锁变动 result = {}, record = {}", b, realseRecord);
            throw ex;
        }
        return false;
    }


    @Override
    public PrizeQuizeRecord getPrizeActivityManage(Long activityId) {
        PrizeQuizeRecord record = prizeQuizeRecordService.getById(activityId);
        return record;
    }

    @Override
    public boolean minerIsActivity(PrizeQuizeRecord record, Long memberId) {
        if (null == record) {
            return false;
        }
        QueryWrapper<MinerPrizeQuizeTransaction> wrapper = new QueryWrapper<>();
        wrapper.eq("prie_quize_id",record.getId());
        wrapper.eq("member_id",memberId);
        MinerPrizeQuizeTransaction one = minerPrizeQuizeTransactionService.getOne(wrapper);
        return one == null;
    }

    @Override
    public Long getMinAmount() {
        Long minAmount = getService().getConfig(BtBankPrizeQuizConfig.PRIZE_QUIZ_JOIN_MIN_AMOUT,v -> Long.parseLong(v.toString()),0L);
        return minAmount;
    }

    @Override
    public Long getMaxAmount() {
        Long maxAmount = getService().getConfig(BtBankPrizeQuizConfig.PRIZE_QUIZ_JOIN_MAX_AMOUT,v -> Long.parseLong(v.toString()),0L);
        return maxAmount;
    }

    @Override
    public Boolean minerBetting(Member member, PrizeQuizeRecord record, MinerPrizeQuizeTransaction transaction) {

        //参加活动
        transaction.setMemberId(member.getId());
        transaction.setId(IdWorker.getId());
        transaction.setRewardReleaseTime(record.getRewardReleaseTime());
        transaction.setRealName(member.getRealName());
        transaction.setUserName(member.getUsername());
        transaction.setMobilePhone(member.getMobilePhone());

        return joinPrizeQuize(transaction);
    }

    @Override
    public void checkPrize(PrizeQuizeRecord record,Member member) {
        //如果用户已经有一条投注记录了  则不允许用户投注
        QueryWrapper<MinerPrizeQuizeTransaction> wrapper = new QueryWrapper<>();
        wrapper.eq("member_id",member.getId());
        wrapper.eq("prie_quize_id",record.getId());
        int count = minerPrizeQuizeTransactionService.count(wrapper);
        if (count > 0) {
            throw new BtBankException(BtBankMsgCode.ALREADY_ATTEND_ACTIVITY);
        }
        //校验用户参与活动时间
        Date now = new Date();
        //如果现在小于了活动的创建时间 活动还没开始
        int i = now.compareTo(record.getStartTime());
        if (i < 0){
            throw new BtBankException(BtBankMsgCode.ACTIVITY_NOT_START);
        }
        //如果现在大于了活动的开奖时间 活动已经结束
        i = now.compareTo(record.getRewardResultTime());
        if (i > 0) {
            throw new BtBankException(BtBankMsgCode.ACTIVITY_END);
        }

        //如果现在大于了活动的最后投注时间 活动不允许投注
        i = now.compareTo(record.getFinalizeTime());
        if (i > 0) {
            throw new BtBankException(BtBankMsgCode.PRIZE_QUIZE_JOIN_ERROR);
        }
    }


    @Override
    public PrizeQuizeRecord getPrizeQuizeRecord(){
        String prizeQuizSwitch = (String)getService().getConfig(BtBankPrizeQuizConfig.PRIZE_QUIZ_SWITCH);
        Integer prizeQuizStartTime = getService().getConfig(BtBankPrizeQuizConfig.PRIZE_QUIZ_TIME , v -> Integer.parseInt(v.toString()), 0);
        if(StringUtils.isEmpty(prizeQuizSwitch)){
            throw new IllegalArgumentException("未找到竞猜活动开始的PRIZE_QUIZ_SWITCH 配置");
        }
        if(prizeQuizStartTime==0){
            throw new IllegalArgumentException("未找到竞猜活动开始时间的PRIZE_QUIZ_TIME 配置");
        }
        if("1".equals(prizeQuizSwitch)){
            //更新今天结束价格和明天开始价格
            Calendar calendar =  Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY,0);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);
            calendar.set(Calendar.MILLISECOND,0);
            calendar.add(Calendar.SECOND,prizeQuizStartTime );
            return getService().queryRecordByStartTime("BTC", DateUtil.addDay( calendar.getTime(),-1));
        }else{
            log.debug("获取今天开奖记录失败！");
        }
        return null;
    }

    @Override
    public Boolean updateMinerResult(PrizeQuizeRecord prizeQuizeRecord) {

        //更新结果 竞猜prize_quize_type 与竞猜结果一直压中
        boolean updatePrize =  minerPrizeQuizeTransactionService.lambdaUpdate()
                .setSql("prize_quize_result =  " + prizeQuizeRecord.getPriQuizeResult() )
                .setSql("guess_status = "+ 1 )
                .eq(MinerPrizeQuizeTransaction::getPrieQuizeId, prizeQuizeRecord.getId())
                .eq(MinerPrizeQuizeTransaction::getPrizeQuizeType,prizeQuizeRecord.getPriQuizeResult()).update();
        if(updatePrize){
            log.info("更新压中成功");
        }else{
            log.info("更新压中失败");
        }
        //更新未严重
        boolean updateunPrize =  minerPrizeQuizeTransactionService.lambdaUpdate()
                .setSql("prize_quize_result =  " + prizeQuizeRecord.getPriQuizeResult() )
                .setSql("guess_status = "+ 2 )
                .eq(MinerPrizeQuizeTransaction::getPrieQuizeId, prizeQuizeRecord.getId())
                .ne(MinerPrizeQuizeTransaction::getPrizeQuizeType,prizeQuizeRecord.getPriQuizeResult()).update();
        if(updateunPrize){
            log.info("更新未压中成功");
        }else{
            log.info("更新未压中失败");
        }
        MinerPrizeQuizeVo totalMinerPrizeQuizeVo = minerPrizeQuizeTransactionService.queryTotal(prizeQuizeRecord.getId().longValue());
        if(totalMinerPrizeQuizeVo!=null){
            prizeQuizeRecord.setTotalNum(totalMinerPrizeQuizeVo.getTotalNum());

            prizeQuizeRecord.setTotalAmount(totalMinerPrizeQuizeVo.getTotalAmount());
        }
        MinerPrizeQuizeVo upTotalMinerPrizeQuizeVo =minerPrizeQuizeTransactionService.queryUpTotal(prizeQuizeRecord.getId().longValue());
        if(upTotalMinerPrizeQuizeVo!=null){
            prizeQuizeRecord.setUpNum(upTotalMinerPrizeQuizeVo.getUpNum());
            prizeQuizeRecord.setUpAmount(upTotalMinerPrizeQuizeVo.getUpAmount());
        }
        MinerPrizeQuizeVo downTotalMinerPrizeQuizeVo =minerPrizeQuizeTransactionService.queryDownTotal(prizeQuizeRecord.getId().longValue());
        if(downTotalMinerPrizeQuizeVo!=null){
            prizeQuizeRecord.setDownNum(downTotalMinerPrizeQuizeVo.getDownNum());
            prizeQuizeRecord.setDownAmount(downTotalMinerPrizeQuizeVo.getDownAmout());
        }
        BigDecimal rate = getService().getConfig(BtBankPrizeQuizConfig.PRIZE_QUIZ_RATE , v -> (new BigDecimal(v.toString())),BigDecimal.ZERO);
        if (rate == BigDecimal.ZERO) {
            throw new IllegalArgumentException("未找到奖励账户 PRIZE_QUIZ_RATE 配置");
        }
        // 结果跌
        if(prizeQuizeRecord.getPriQuizeResult()==0){
            prizeQuizeRecord.setPlatformAmount(prizeQuizeRecord.getUpAmount().multiply(rate).setScale(8,BigDecimal.ROUND_DOWN));
            prizeQuizeRecord.setRewardAmount(prizeQuizeRecord.getUpAmount().add(prizeQuizeRecord.getPlatformAmount().negate()));
        }else{
            //结果涨
            prizeQuizeRecord.setPlatformAmount(prizeQuizeRecord.getDownAmount().multiply(rate).setScale(8,BigDecimal.ROUND_DOWN));
            prizeQuizeRecord.setRewardAmount(prizeQuizeRecord.getDownAmount().add(prizeQuizeRecord.getPlatformAmount().negate()));
        }

       boolean updateunRecord = prizeQuizeRecordService.updateById(prizeQuizeRecord);
        if(updateunRecord){
            log.info("更新统计数据");
        }else{
            log.info("更新统计数据");
        }
        return updateunRecord;
    }

    @Override
    public int queryTotalPrizeQuize(PrizeQuizeRecord prizeQuizeRecord) {
        QueryWrapper<MinerPrizeQuizeTransaction> wrapper = new QueryWrapper<>();
        wrapper.eq("prie_quize_id",prizeQuizeRecord.getId());
        return minerPrizeQuizeTransactionService.count(wrapper);
    }

    public PrizeQuizeServiceImpl getService() {
        return SpringContextUtil.getBean(PrizeQuizeServiceImpl.class);
    }
}
