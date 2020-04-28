package com.spark.bitrade.biz.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.api.dto.MinerAssetDTO;
import com.spark.bitrade.api.vo.*;
import com.spark.bitrade.biz.ActivityRedpacketService;
import com.spark.bitrade.biz.MinerService;
import com.spark.bitrade.biz.MinerWebSocketService;
import com.spark.bitrade.biz.PlanAssetService;
import com.spark.bitrade.constant.*;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.WalletChangeRecord;
import com.spark.bitrade.enums.MessageCode;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.*;
import com.spark.bitrade.repository.service.*;
import com.spark.bitrade.service.BtBankConfigService;
import com.spark.bitrade.service.IMService;
import com.spark.bitrade.service.IMemberApiService;
import com.spark.bitrade.service.MemberWalletService;
import com.spark.bitrade.trans.Tuple2;
import com.spark.bitrade.util.AssertUtil;
import com.spark.bitrade.util.BigDecimalUtil;
import com.spark.bitrade.util.IdWorkByTwitter;
import com.spark.bitrade.util.MessageRespResult;
import com.sun.tools.javac.util.Assert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author davi
 */
@Slf4j
@Service
public class MinerServiceImpl implements MinerService {

    @Autowired
    private BtBankMinerBalanceService minerBalanceService;
    @Autowired
    private MemberWalletService memberWalletService;
    @Autowired
    private BtBankMinerBalanceTransactionService minerBalanceTransactionService;
    @Autowired
    private BtBankMinerOrderService minerOrderService;
    @Autowired
    private BtBankMinerOrderTransactionService minerOrderTransactionService;
    @Autowired
    private IdWorkByTwitter idWorkByTwitter;
    @Autowired
    private BtBankConfigService btBankConfigService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MinerWebSocketService minerWebSocketService;

    @Autowired
    private BtBankRebateRecordService rebateRecordService;

    @Autowired
    private BtBankMinerGradeNoteService minerGradeNoteService;

    @Autowired
    private RedPackExperienceGoldService redPackExperienceGoldService;

    @Autowired
    private PlanAssetService planAssetService;

    @Autowired
    private IMemberApiService iMemberApiService;
    @Autowired
    private ActivityRedpacketService activityRedpacketService;
    @Autowired
    private BtBankFinancialBalanceService btBankFinancialBalanceService;
    @Autowired
    private MemberExperienceWalletService memberExperienceWalletService;

    @Autowired
    private ImMemberService imMemberService;

    @Autowired
    private IMService imService;

    @Override
    public MinerAssetDTO queryMinerAsset(Long memberId) {
        BtBankMinerBalance minerBalance = this.checkAndCreateBalanceRecord(memberId);
        MinerAssetDTO dto = new MinerAssetDTO();
        dto.setUsedAmount(minerBalance.getBalanceAmount());
        dto.setLockedAmount(minerBalance.getLockAmount());
        dto.setRewardAmount(minerBalance.getProcessingRewardSum());
        dto.setGotRewardAmount(minerBalance.getGotRewardSum());
        List<BigDecimal> total =
                Arrays.asList(
                        minerBalance.getBalanceAmount(),
                        minerBalance.getLockAmount(),
                        minerBalance.getProcessingRewardSum());
        Optional<BigDecimal> totalAmount = total.stream().reduce(BigDecimal::add);
        dto.setTotalAmount(totalAmount.orElse(BigDecimal.ZERO));
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void transferAsset(BigDecimal amount, Long memberId) {
        // 全局配置允许转出
        String transferSwitch =
                (String) btBankConfigService.getConfig(BtBankSystemConfig.TRANSFER_SWITCH);
        if (!transferSwitch.equalsIgnoreCase("1")) {
            throw new BtBankException(BtBankMsgCode.TURN_IN_SWITCH_OFF);
        }

        this.checkAndCreateBalanceRecord(memberId);

        // amount 超过全局配置的最低划转金额
        String minimumAmountString =
                (String) btBankConfigService.getConfig(BtBankSystemConfig.MINIMUM_TRANSFER_AMOUNT);
        BigDecimal minimumAmount = new BigDecimal(minimumAmountString);
        if (amount.compareTo(minimumAmount) < 0) {
            throw new BtBankException(BtBankMsgCode.BELOW_THE_MINIMUM);
        }

        // 远程扣减资产
        /*MessageRespResult<Boolean> respResult =
                memberWalletService.optionMemberWalletBalance(TransactionType.TRANSFER_ACCOUNTS, memberId, "BT", "BT", amount.negate(), 0L, "btbank划转到矿池");
        */
        // 远程划转成功，增加资产转入矿池记录
        BtBankMinerBalanceTransaction tx = new BtBankMinerBalanceTransaction();
        tx.setMemberId(memberId);
        tx.setId(idWorkByTwitter.nextId());
        tx.setCreateTime(new Date());
        tx.setRefId(null);
        tx.setBalance(amount);
        tx.setMoney(amount);
        tx.setType(1);
        /*MessageRespResult<Boolean> respResult =
                memberWalletService.optionMemberWalletBalance(TransactionType.TRANSFER_ACCOUNTS, memberId, "BT", "BT", amount.negate(), 0L, "btbank划转到矿池");*/
        WalletChangeRecord record = memberWalletService.tryTrade(TransactionType.TRANSFER_ACCOUNTS, memberId, "BT", "BT", amount.negate(), tx.getId(), "btbank划转到矿池");
        if (record == null) {
            log.error("可用转入矿池失败  member_id = {}, amount = {}", memberId, amount.negate());
            throw new BtBankException(MessageCode.ACCOUNT_BALANCE_INSUFFICIENT);
        }


        /*Boolean succeeded = Objects.isNull(respResult)?Boolean.FALSE:(Boolean.valueOf(respResult.getData()) );

        if (!succeeded) {
            log.warn("transfer amount into miner pool failed. memberId({}) amount({})", memberId, amount);
            throw new BtBankException(MessageCode.ACCOUNT_BALANCE_INSUFFICIENT);
        }*/

        try {

            if (!minerBalanceTransactionService.save(tx)) {
                log.warn("save BtBankMinerBalanceTransaction failed. {}", tx);
                throw new BtBankException(MessageCode.INCORRECT_STATE);
            }
            // 账户资产增加
            if (minerBalanceService.updateIncBalanceAmount(memberId, amount) <= 0) {
                log.warn("update BtBankMinerBalance failed. memberId({}) amount({})", memberId, amount);
                throw new BtBankException(MessageCode.INCORRECT_STATE);
            }
            checkAndSetSilverMiner(amount, memberId);
            boolean b = memberWalletService.confirmTrade(record.getMemberId(), record.getId());
            if (!b) {
                log.error("可用转入矿池失败 record = {}", record);
                throw new BtBankException(MessageCode.ACCOUNT_BALANCE_INSUFFICIENT);
            }
        } catch (RuntimeException ex) {
            log.error("可用转入矿池失败  txId = {}, err = {}", record.getId(), ex.getMessage());
            boolean b = memberWalletService.rollbackTrade(record.getMemberId(), record.getId());
            log.info("可用转入矿池失败  result = {}, record = {}", b, record);
            throw ex;
        }
        try {
            MessageRespResult<Member> memberRespResult = iMemberApiService.getMember(memberId);
            // 参与挖矿
            planAssetService.doUnlock(memberRespResult.getData(), new BigDecimal("20"), 1, null, tx.getId());

            BtBankMinerBalanceTransaction btBankMinerBalanceTransaction = minerBalanceTransactionService.lambdaQuery()
                    .eq(BtBankMinerBalanceTransaction::getMemberId, tx.getMemberId())
                    .eq(BtBankMinerBalanceTransaction::getType, MinerBalanceTransactionType.TRANSFER_IN.getValue())
                    .orderByAsc(BtBankMinerBalanceTransaction::getCreateTime).one();

            if (Objects.isNull(btBankMinerBalanceTransaction) || (btBankMinerBalanceTransaction.getId().equals(tx.getId())
                    && btBankConfigService.isFirestMineConfig(btBankMinerBalanceTransaction)
                    && new BigDecimal("100").compareTo(btBankMinerBalanceTransaction.getMoney()) != 1)) {
                // 推荐矿工
                planAssetService.doUnlock(memberRespResult.getData(), new BigDecimal("20"), 2, null, null);
                //红包升级，推荐有效矿工给奖随机金额红包奖励 add mahao
                activityRedpacketService.processRecommendRedPack(memberRespResult.getData());
            }
        } catch (Exception e) {
            // 处理红包，异常不抛出
        }
    }


    /**
     * 转入一次成功后，检查并设置银牌矿工身份
     *
     * @param amount
     * @param memberId
     */
    public void checkAndSetSilverMiner(BigDecimal amount, Long memberId) {
        // amount目前未使用，未来可能有单次最低与累积总量要求，

        // 仅当当前身份状态是普通时，才设置为银牌矿工
        minerBalanceService.lambdaUpdate()
                .set(BtBankMinerBalance::getMinerGrade, MinerGrade.SILVER_MINER.getGradeId())
                .eq(BtBankMinerBalance::getMemberId, memberId)
                .eq(BtBankMinerBalance::getMinerGrade, MinerGrade.NONE.getGradeId())
                .update();
    }

    @Override
    public MinerBalanceVO getMinerBalance(Long memberId) {

        BtBankMinerBalance bankMinerBalance = checkAndCreateBalanceRecord(memberId);
        MinerBalanceVO minerBalanceVO = new MinerBalanceVO();
        BeanUtils.copyProperties(bankMinerBalance, minerBalanceVO);

        List types = Arrays.asList(MinerBalanceTransactionType.DISPATCH_COMMISSION_TRANSFER_IN.getValue(),
                MinerBalanceTransactionType.GRAB_COMMISSION_TRANSFER_IN.getValue(),
                MinerBalanceTransactionType.FIEXD_COMMISSION_TRANSFER_IN.getValue());

        BigDecimal yesterdayCommission = minerBalanceTransactionService.getYestodayMinerBalanceTransactionsSumByMemberId(memberId, types);

        if (yesterdayCommission == null) {
            yesterdayCommission = BigDecimal.ZERO;
        }

        minerBalanceVO.setYestodayRewardSum(yesterdayCommission);

        BigDecimal redBagLockAmount = redPackExperienceGoldService.getRedBagLockAmount(memberId);
        MemberExperienceWallet bt = memberExperienceWalletService.findByMemberIdAndCoinId(memberId, "BT");

        minerBalanceVO.setRedBagLockAmount(redBagLockAmount.add(Optional.ofNullable(bt.getLockBalance()).orElse(BigDecimal.ZERO)));
        BtBankFinancialBalance btBankFinancialBalance = btBankFinancialBalanceService.findFirstByMemberId(memberId);
        if (btBankFinancialBalance != null) {

            minerBalanceVO.setFinancialBalance(btBankFinancialBalance.getBalanceAmount());
        } else {
            minerBalanceVO.setFinancialBalance(BigDecimal.ZERO);
        }

        return minerBalanceVO;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public BtBankMinerOrderTransaction grabMineOrder(Long memberId, Long orderId) {


        String seckillSwitch = btBankConfigService.getConfig(BtBankSystemConfig.SECKILL_SWITCH).toString();

        if (!"1".equals(seckillSwitch)) {
            log.error("暂时无法抢单，请稍后再试 id: {}", orderId);
            throw new BtBankException(BtBankMsgCode.UNABLE_TO_SNATCH_THE_ORDER);
        }

        Object o = redisTemplate.opsForValue().get(BtBankSystemConfig.REDIS_MINER_ORDER_PREFIX + orderId);
        BtBankMinerOrder minerOrder = null;
        if (o == null) {
            minerOrder = minerOrderService.getById(orderId);
            redisTemplate.opsForValue().set(BtBankSystemConfig.REDIS_MINER_ORDER_PREFIX + orderId, minerOrder);
        } else {
            minerOrder = (BtBankMinerOrder) o;
        }

        if (minerOrder == null) {
            log.error("订单不存在 id: {}", orderId);
            throw new BtBankException(BtBankMsgCode.ORDER_NOT_EXIST);

        } else if (minerOrder.getStatus() > 0) {
            log.error("订单已经被抢或被派出 id: {}", minerOrder);
            throw new BtBankException(BtBankMsgCode.ORDERS_HAVE_LOOTED_OR_DISPATCHED);
        } else {


            log.info("抢订单  {}", minerOrder);

            BtBankMinerBalance minerBalance = checkAndCreateBalanceRecord(memberId);

            if (minerBalance.getBalanceAmount().compareTo(minerOrder.getMoney()) < 0) {
                log.error("可用余额不足 memberId: {}", memberId);
                throw new BtBankException(MessageCode.ACCOUNT_BALANCE_INSUFFICIENT);
            } else {

                log.info("抢订单用户  {}", minerBalance);

                // 修改订单状态
                minerOrder.setStatus(MinerOrderTransactionType.SECKILLED_ORDER.getValue());
                minerOrder.setMemberId(minerBalance.getMemberId());
                minerOrder.setProcessTime(new Date());

                if (minerOrderService.grabMinerOrderByIdWithStatus(minerOrder, 0) > 0) {

                    // 计算收益
                    //2020-3-30需求 判断矿工等级 然后查询不同收益比例
                    Integer minerGrade = minerBalance.getMinerGrade();
                    String seckillRate = btBankConfigService.getConfig(BtBankSystemConfig.SECKILL_COMMISSION_RATE).toString();
                    if(minerGrade==1){
                        seckillRate=btBankConfigService.getConfig(BtBankSystemConfig.SILVER_MINER_GRAB_COMMISSION_RATE).toString();
                    }
                    BigDecimal commissinRate = new BigDecimal(seckillRate);
                    minerBalance.setBalanceAmount(
                            BigDecimalUtil.sub(minerBalance.getBalanceAmount(), minerOrder.getMoney()));

                    BigDecimal reward = BigDecimalUtil.mul2down(minerOrder.getMoney(), commissinRate, 8);
                    // 添加用户收益统计
                    minerBalance.setProcessingRewardSum(minerBalance.getProcessingRewardSum().add(reward));
                    minerBalance.setLockAmount(minerOrder.getMoney());


                    List<BtBankMinerBalanceTransaction> minerBalanceTransactions =
                            minerBalanceTransactionService.list(
                                    new QueryWrapper<BtBankMinerBalanceTransaction>()
                                            .eq("member_id", memberId)
                                            .eq("type", MinerBalanceTransactionType.TRANSFER_IN.getValue())
                                            .gt("balance", 0).orderByAsc("create_time"));

                    BigDecimal needPay = minerOrder.getMoney();

                    for (BtBankMinerBalanceTransaction transaction : minerBalanceTransactions) {
                        if (BigDecimalUtil.gt0(needPay)) {

                            BigDecimal tmpPayDecimal = needPay.compareTo(transaction.getBalance()) > 0 ? transaction.getBalance() : needPay;

                            if (minerBalanceTransactionService.spendBalanceWithIdAndBalance(transaction.getId(), tmpPayDecimal) > 0) {
                                needPay = BigDecimalUtil.sub(needPay, tmpPayDecimal);
                            }//;.updateById(transaction);

                        } else {
                            break;
                        }
                    }

                    if (BigDecimalUtil.gt0(needPay)) {
                        log.error("可用余额不足 memberId: {}", memberId);
                        throw new BtBankException(MessageCode.ACCOUNT_BALANCE_INSUFFICIENT);
                    }


                    // 添加订单记录
                    BtBankMinerOrderTransaction minerOrderTransaction = new BtBankMinerOrderTransaction();
                    minerOrderTransaction.setId(idWorkByTwitter.nextId());
                    minerOrderTransaction.setMemberId(minerBalance.getMemberId());
                    minerOrderTransaction.setCreateTime(new Date());
                    minerOrderTransaction.setMinerOrderId(minerOrder.getId());
                    minerOrderTransaction.setMoney(minerOrder.getMoney());
                    minerOrderTransaction.setRewardAmount(reward);
                    minerOrderTransaction.setType(MinerOrderTransactionType.SECKILLED_ORDER.getValue());

                    Integer unlockTimeSpan = Integer.valueOf(btBankConfigService.getConfig(BtBankSystemConfig.UNLOCK_TIME).toString());

                    //-----
//
//                    Calendar calendar = Calendar.getInstance();
//                    calendar.setTime(new Date());
//                    calendar.set(Calendar.HOUR_OF_DAY, 0);
//                    calendar.set(Calendar.MINUTE, 0);
//                    calendar.set(Calendar.SECOND, 0);
//                    minerOrderTransaction.setUnlockTime(DateUtil.addMinToDate(calendar.getTime(), unlockTimeSpan));

                    // 写资金流水 修改资金
                    //

                    if (lockMinerBalanceAndAddProcessingReward(minerBalance.getId(), minerBalance.getMemberId(), minerOrderTransaction.getId(), minerOrder.getMoney(), reward, minerOrder.getId()) > 0) {
                        //minerOrderTransactionService.save(minerOrderTransaction);
                        minerOrderTransactionService.insertGrabOrDepatchOrder(minerOrderTransaction, unlockTimeSpan);
                        //清除订单缓存
                        redisTemplate.delete(BtBankSystemConfig.REDIS_MINER_ORDER_PREFIX + orderId);
                        minerWebSocketService.sendNewOrderStatusToAllClient(minerOrder);

                        log.info("抢单完成 {}", minerBalance);
                        return minerOrderTransaction;
                    }
                }
            }
        }

        log.error("抢单失败 memberId: {}", memberId);
        throw new BtBankException(BtBankMsgCode.FAILED_TO_SNATCH_THE_ORDER);
    }


    /**
     * 抢单锁创 并添加记录
     *
     * @param minerBalanceId
     * @param memberId
     * @param minerOrderTransactionId
     * @param money
     * @param reward
     * @param orderId
     * @return true
     * @time 2019.10.26 1:23
     */

    @Override
    public int lockMinerBalanceAndAddProcessingReward(Long minerBalanceId, Long memberId, Long minerOrderTransactionId, BigDecimal money, BigDecimal reward, Long orderId) {

        int result = minerBalanceService.grabSuccAndUpdate(minerBalanceId, money, reward);
        if (result > 0) {
            BtBankMinerBalanceTransaction transaction = new BtBankMinerBalanceTransaction();
            transaction.setMemberId(memberId);
            transaction.setId(idWorkByTwitter.nextId());
            transaction.setCreateTime(new Date());
            transaction.setMoney(money);
            transaction.setOrderTransactionId(minerOrderTransactionId);
            transaction.setType(MinerBalanceTransactionType.GRABBED_LOCKS.getValue());
            transaction.setRefId(orderId);
            minerBalanceTransactionService.save(transaction);

            BtBankMinerBalanceTransaction grabcomissionTransferTransaction = new BtBankMinerBalanceTransaction();
            grabcomissionTransferTransaction.setMemberId(memberId);
            grabcomissionTransferTransaction.setId(idWorkByTwitter.nextId());
            grabcomissionTransferTransaction.setCreateTime(new Date());
            grabcomissionTransferTransaction.setMoney(reward);
            grabcomissionTransferTransaction.setOrderTransactionId(minerOrderTransactionId);
            grabcomissionTransferTransaction.setType(MinerBalanceTransactionType.GRAB_COMMISSION_TRANSFER_IN.getValue());
            grabcomissionTransferTransaction.setRefId(orderId);
            minerBalanceTransactionService.save(grabcomissionTransferTransaction);

        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Tuple2<ApplyGoldMinerCode, String> getLastApplyStatus(Long memberId) {
        BtBankMinerGradeNote gradeNote = getLastGoldMinerApplyRecord(memberId);
        if (Objects.isNull(gradeNote)) {
            return new Tuple2(ApplyGoldMinerCode.NO_RECORD, "");
        }

        if (!gradeNote.getIsOperation() && gradeNote.getType() == MinerGradeNoteType.PENDING.getCode()) {
            return new Tuple2(ApplyGoldMinerCode.PENDING, "");
        }

        if (gradeNote.getType() == MinerGradeNoteType.FAILD.getCode()) {
            return new Tuple2(ApplyGoldMinerCode.APPLY_FAILED, gradeNote.getRemark());
        }

        if (gradeNote.getType() == MinerGradeNoteType.PASS.getCode()) {
            return new Tuple2(ApplyGoldMinerCode.ALREADY_GOLD_MINER, "");
        }

        return new Tuple2(ApplyGoldMinerCode.NO_RECORD, "");
    }


    @SuppressWarnings("unchecked")
    @Override
    public Tuple2<ApplyGoldMinerCode, String> tryApplyUpgradeToGold(Long memberId) {
        BtBankMinerBalance miner = minerBalanceService.findFirstByMemberId(memberId);
        log.info("{}", miner);
        if (miner.getMinerGrade() == MinerGrade.GOLD_MINER.getGradeId()) {
            return new Tuple2(ApplyGoldMinerCode.ALREADY_GOLD_MINER, "");
        }

        BtBankMinerGradeNote gradeNote = getLastGoldMinerApplyRecord(memberId);
        log.info("last grade note:{}", gradeNote);
        if (Objects.nonNull(gradeNote)) {
            if (!gradeNote.getIsOperation() && gradeNote.getType() == MinerGradeNoteType.PENDING.getCode()) {
                return new Tuple2(ApplyGoldMinerCode.PENDING, "");
            }
            if (gradeNote.getType() == MinerGradeNoteType.FAILD.getCode()) {
                return new Tuple2(ApplyGoldMinerCode.APPLY_FAILED, gradeNote.getRemark());
            }
        }

        if (miner.getMinerGrade() < MinerGrade.SILVER_MINER.getGradeId()
                || !checkGoldMinerInviterCountRule(memberId)) {
            return new Tuple2(ApplyGoldMinerCode.Ineligible, "");
        }

        return new Tuple2(ApplyGoldMinerCode.CONFORM, "");
    }

    private BtBankMinerGradeNote getLastGoldMinerApplyRecord(Long memberId) {
        return minerGradeNoteService.findLastRecordByMemberId(memberId);
    }

    private boolean checkGoldMinerInviterCountRule(Long memberId) {
        int recommendedCount = getRecommandAndChargeSuccMemberCount(memberId);
        log.info("checkGoldMinerInviterCountRule: memberId({}) count({})", memberId, recommendedCount);
        // 直推矿工大于等于100人
        return recommendedCount >= 100;
    }

    @Override
    public void applyUpgradeToGold(Long memberId) {
        Tuple2<ApplyGoldMinerCode, String> tuple2 = tryApplyUpgradeToGold(memberId);
        ApplyGoldMinerCode status = tuple2.getFirst();
        if (status == ApplyGoldMinerCode.CONFORM || status == ApplyGoldMinerCode.APPLY_FAILED) {
            BtBankMinerGradeNote minerGradeNote = new BtBankMinerGradeNote();
            minerGradeNote.setId(idWorkByTwitter.nextId());
            minerGradeNote.setMemberId(memberId);
            Date now = new Date();
            minerGradeNote.setCreateTime(now);
            minerGradeNote.setUpdateTime(now);
            minerGradeNote.setType(MinerGradeNoteType.PENDING.getCode());
            minerGradeNote.setIsOperation(false);
            minerGradeNote.setRemark("用户申请金牌矿工");
            if (!minerGradeNoteService.save(minerGradeNote)) {
                throw new BtBankException(BtBankMsgCode.APPLY_GOLD_MINER_FAIL);
            }
            return;
        }

        log.info("applyUpgradeToGold faild. {}", status);
        throw new BtBankException(BtBankMsgCode.APPLY_GOLD_MINER_FAIL);
    }


    /**
     * 初始化用户信息
     *
     * @param memberId
     * @return
     */


    @Override
    public int getRecommandAndChargeSuccMemberCount(Long memberId) {
        return minerBalanceService.getRecommandAndChargeSuccMemberCount(memberId);
    }

    @Override
    public MinerRecommandListVO getRecommandList(Long memberId, int size, int current) {

        MinerRecommandListVO minerRecommandListVO = minerBalanceService.getRecommandList(memberId, size, current);
        return minerRecommandListVO;
    }

    @Override
    public MinerRewardListVO getRewardList(Long memberId, int size, int cuurent) {

        MinerRewardListVO minerRewardListVO = new MinerRewardListVO();

        BtBankMinerBalance bankMinerBalance = checkAndCreateBalanceRecord(memberId);
        minerRewardListVO.setGotSharedReward(bankMinerBalance.getGotSharedRewardSum());
        minerRewardListVO.setMinerGrade(bankMinerBalance.getMinerGrade());
        IPage<BtBankRebateRecordVO> btBankRebateRecordIPage = rebateRecordService.getRebateRecordAndNamePage(new Page<BtBankRebateRecordVO>(cuurent, size), memberId);

//        IPage<BtBankRebateRecord> btBankRebateRecordIPage = rebateRecordService.page(new Page<>(cuurent, size),
//                new QueryWrapper<BtBankRebateRecord>().eq("rebate_member_id", memberId).orderByDesc("create_time")
//        );
        minerRewardListVO.setContent(btBankRebateRecordIPage.getRecords());
        minerRewardListVO.setTotalElements(btBankRebateRecordIPage.getTotal());

        return minerRewardListVO;
    }

    @Override
    public MyRewardListVO getMyRewards(Long memberId, Long cuurent, Long size) {
        MyRewardListVO myRewardListVO = rebateRecordService.getMyRewards(cuurent, size, memberId);
        BtBankMinerBalance bankMinerBalance = checkAndCreateBalanceRecord(memberId);
        myRewardListVO.setMinerGrade(bankMinerBalance.getMinerGrade());
        return myRewardListVO;
    }

    @Override
    public MinerOrderTransactionsVO getMinerOrderTransactionsByMemberId(Long memberId, List<Integer> types, int page, int size) {
        return minerOrderTransactionService.getMinerOrderTransactionsByMemberId(memberId, types, page, size);
    }

    @Override
    public MinerOrderTransactionsVO getMinerOrderTransactionsByMemberId(Long memberId, int page, int size) {
        return getMinerOrderTransactionsByMemberId(memberId, new ArrayList<Integer>(), page, size);
    }

    @Override
    public MinerOrdersVO getMinerOrdersByMemberId(
            Long memberId, List<Integer> types, int page, int size) {
        return minerOrderService.getMinerOrdersByMemberId(memberId, types, page, size);
    }

    @Override
    public MinerOrdersVO getMyMinerOrdersByMemberId(
            Long memberId, List<Integer> types, int page, int size) {
        return minerOrderService.getMinerOrdersByMemberIdOrderByProcessCreateTime(memberId, types, page, size);
    }


    @Override
    public MinerOrdersVO getMinerOrdersByMemberId(Long memberId, int page, int size) {
        return getMinerOrdersByMemberId(memberId, new ArrayList<Integer>(), page, size);
    }

    @Override
    public MinerOrdersVO getMinerOrders(List<Integer> types, int page, int size) {
        return minerOrderService.getMinerOrders(types, page, size);
    }

    @Override
    public MinerOrdersVO getMinerOrders(int page, int size) {
        return getMinerOrders(new ArrayList<>(), page, size);
    }
    @Override
    public MinerOrdersVO getMinerOrders(int page, int size,Integer type) {
        return  minerOrderService.getMinerOrders( page, size,type);
    }
    @Override
    public MinerBalanceTransactionsVO getMinerBalanceTransactionsByMemberId(
            Long memberId, List<Integer> types, int page, int size, String range) {
        return minerBalanceTransactionService.getMinerBalanceTransactionsByMemberId(
                memberId, types, page, size, range);
    }

    @Override
    public MinerBalanceTransactionsVO getMinerBalanceTransactionsByMemberId(
            Long memberId, int page, int size, String range) {
        return getMinerBalanceTransactionsByMemberId(memberId, new ArrayList<Integer>(), page, size, range);
    }

    private BtBankMinerBalance checkAndCreateBalanceRecord(Long memberId) {
        BtBankMinerBalance memberBalance = minerBalanceService.findFirstByMemberId(memberId);
        if (memberBalance == null) {
            memberBalance = new BtBankMinerBalance();
            memberBalance.setMemberId(memberId);
            memberBalance.setId(idWorkByTwitter.nextId());
            memberBalance.setBalanceAmount(BigDecimal.ZERO);
            memberBalance.setGotRewardSum(BigDecimal.ZERO);
            memberBalance.setLockAmount(BigDecimal.ZERO);
            memberBalance.setProcessingRewardSum(BigDecimal.ZERO);
            Date now = new Date();
            memberBalance.setCreateTime(now);
            memberBalance.setUpdateTime(now);
            memberBalance.setGotSharedRewardSum(BigDecimal.ZERO);
            memberBalance.setMinerGrade(MinerGrade.NONE.getGradeId());
            minerBalanceService.save(memberBalance);
        }
        return memberBalance;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized MinerImVo validMiner(Member member) {
        MinerImVo minerImVo = null;
        ImMember imMember = imMemberService.getImMemberByMemberId(member.getId());
        if (imMember !=null) {
            // IM已经注册了
            minerImVo = new MinerImVo();
            minerImVo.setAccid(imMember.getAccid());
            minerImVo.setToken(imMember.getToken());
            minerImVo.setMemberId(imMember.getMemberId());
            minerImVo.setUserType(imMember.getUserType());
            minerImVo.setMobile(imMember.getMobile());
            minerImVo.setRealName(imMember.getName());
            minerImVo.setEmail(imMember.getEmail());
            minerImVo.setIcon(imMember.getIcon());
            minerImVo.setGender(imMember.getGender());
            return minerImVo;
        } else {
            // IM 没有注册
            BtBankMinerBalanceTransaction miner = minerBalanceTransactionService.findByTypeAndMemberId(member.getId());
            if (miner == null) {
                // 说明不是有效矿工
                throw new BtBankException(BtBankMsgCode.NOT_EFFECTIVE_MINER);
            } else {
                // 有效矿工
                String minerGrade = "";
                BtBankMinerBalance minerBalance = minerBalanceService.findFirstByMemberId(miner.getMemberId());
                if (minerBalance == null) {
                    minerGrade = "1";
                } else {
                    minerGrade = minerBalance.getMinerGrade().toString();
                }
                // 调用 IM云信网添加用户接口
                MessageRespResult<com.spark.bitrade.entity.chat.ImResult> result = imService.addMiner(minerBalance.getMemberId(), minerGrade, 1);
                if ("200".equals(result.getCode())) {
                    throw new BtBankException(BtBankMsgCode.IMSEVER_CALL_FAILED);
                }
                minerImVo = new MinerImVo();
                minerImVo.setAccid(imMember.getAccid());
                minerImVo.setMemberId(imMember.getMemberId());
                minerImVo.setUserType(imMember.getUserType());
                minerImVo.setMobile(imMember.getMobile());
            }
        }
        return minerImVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized MinerImVo cheakMiner(String phone) {
        MinerImVo minerImVo = null;
        MessageRespResult<Member> respResult = iMemberApiService.getMemberByPhoneOrEmail(0, phone);
        if (respResult.getData() == null) {
            throw new BtBankException(BtBankMsgCode.MEMBER_NOT_EXIS);
        }
        Member member = respResult.getData();
        BtBankMinerBalanceTransaction miner = minerBalanceTransactionService.findByTypeAndMemberId(member.getId());
        if (miner == null) {
            // 说明不是有效矿工
            throw new BtBankException(BtBankMsgCode.NOT_EFFECTIVE_MINER);
        }
        ImMember imMember = imMemberService.getImMemberByMemberId(member.getId());
        if (imMember !=null) {
            // IM已经注册了
            minerImVo = new MinerImVo();
            minerImVo.setAccid(imMember.getAccid());
            minerImVo.setMemberId(imMember.getMemberId());
            minerImVo.setUserType(imMember.getUserType());
            minerImVo.setMobile(imMember.getMobile());
        } else {
            // 旷工等级
            String minerGrade = "";
            BtBankMinerBalance minerBalance = minerBalanceService.findFirstByMemberId(miner.getMemberId());
            if (minerBalance == null) {
                minerGrade = "1";
            } else {
                minerGrade = minerBalance.getMinerGrade().toString();
            }
            // 调用 IM云信网添加用户接口
            MessageRespResult<com.spark.bitrade.entity.chat.ImResult> result = imService.addMiner(minerBalance.getMemberId(), minerGrade, 1);
            if ("200".equals(result.getCode())) {
                throw new BtBankException(BtBankMsgCode.IMSEVER_CALL_FAILED);
            }
            minerImVo = new MinerImVo();
            minerImVo.setAccid(imMember.getAccid());
            minerImVo.setMemberId(imMember.getMemberId());
            minerImVo.setUserType(imMember.getUserType());
            minerImVo.setMobile(imMember.getMobile());
        }
        return minerImVo;
    }
}
