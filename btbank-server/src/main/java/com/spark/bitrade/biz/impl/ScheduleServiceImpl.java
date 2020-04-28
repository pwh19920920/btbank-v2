package com.spark.bitrade.biz.impl;

import com.spark.bitrade.api.dto.ActivityDTO;
import com.spark.bitrade.api.dto.MemberRateDto;
import com.spark.bitrade.api.dto.UnlockDTO;
import com.spark.bitrade.biz.*;
import com.spark.bitrade.constant.*;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.WalletChangeRecord;
import com.spark.bitrade.enums.MessageCode;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.*;
import com.spark.bitrade.repository.service.*;
import com.spark.bitrade.service.BtBankConfigService;
import com.spark.bitrade.service.IMemberApiService;
import com.spark.bitrade.service.MemberAccountService;
import com.spark.bitrade.service.MemberWalletService;
import com.spark.bitrade.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.spark.bitrade.constant.BtBankSystemConfig.NEW_MEMBER_EXPERIENCE_AMOUNT_PROFIT;
import static com.spark.bitrade.constant.BtBankSystemConfig.NEW_MEMBER_EXPERIENCE_REGISTER_TIME;

/**
 * @author shenzucai
 * @time 2019.10.24 16:35
 */
@Service
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private BtBankConfigService btBankConfigService;

    @Autowired
    private BtBankMinerBalanceService btBankMinerBalanceService;

    @Autowired
    private BtBankMinerBalanceTransactionService btBankMinerBalanceTransactionService;

    @Autowired
    private BtBankMinerOrderService btBankMinerOrderService;

    @Autowired
    private BtBankMinerOrderTransactionService btBankMinerOrderTransactionService;

    @Autowired
    private IdWorkByTwitter idWorkByTwitter;

    @Autowired
    private MemberWalletService memberWalletService;
    @Autowired
    private MinerWebSocketService minerWebSocketService;

    @Value("${btbank.reward.member:70653}")
    private Long memberId;

    @Autowired
    private IMemberApiService iMemberApiService;

    @Autowired
    private PlanAssetService planAssetService;

    @Autowired
    private FinancialActivityManageService financialActivityManageService;

    @Autowired
    private FinancialActivityJoinDetailsService financialActivityJoinDetailsService;

    @Autowired
    private BtBankFinancialBalanceService btBankFinancialBalanceService;

    @Autowired
    private MemberExperienceWalletService memberExperienceWalletService;

    @Autowired
    private MemberScoreBizService memberScoreBizService;
    @Autowired
    private MemberAccountService memberAccountService;
    @Autowired
    private MinerRebateService minerRebateService;
    @Autowired
    private CreditCardCommissionService creditCardCommissionService;
    /**
     * 解锁资产
     *
     * @return true
     * @author shenzucai
     * @time 2019.10.24 16:41
     */
    @Override
    public Boolean unLockAssert() {
        // 查找所以可以解锁的订单
        // 查找所以可以返还的固定收益
        // 读取派单解锁时间
        Object dispatchUnLockTimeO = btBankConfigService.getConfig(BtBankSystemConfig.UNLOCK_TIME);
        if (Objects.isNull(dispatchUnLockTimeO)) {
            throw new BtBankException(71005, "载入系统配置失败");
        }
        Integer dispatchUnLockTime = Integer.valueOf(String.valueOf(dispatchUnLockTimeO));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date zero = calendar.getTime();
        //add|edit|del by  shenzucai 时间： 2019.10.28  原因：增加订单超时取消
        getService().autoCancel(zero);
        List<UnlockDTO> unlockDTOList = btBankMinerOrderTransactionService.listUnlockRecords(DateUtil.addMinToDate(zero, -dispatchUnLockTime));
        if (Objects.isNull(unlockDTOList) || unlockDTOList.size() < 1) {
            throw new BtBankException(71006, "暂无可解锁订单");
        }

        for (UnlockDTO unlockDTO : unlockDTOList) {
            getService().autoUnlockAssert(unlockDTO);
        }



        return Boolean.TRUE;
    }

    /**
     * 取消超时订单
     *
     * @param zero
     * @return true
     * @author shenzucai
     * @time 2019.10.28 10:41
     */
    @Async("taskExecutorAutoUnlock")
    public void autoCancel(Date zero) {
        btBankMinerOrderService.lambdaUpdate().set(BtBankMinerOrder::getStatus, 5)
                .eq(BtBankMinerOrder::getStatus, 0)
                .lt(BtBankMinerOrder::getCreateTime, zero).update();
    }

    /**
     * 解锁
     *
     * @param unlockDTO
     * @return true
     * @author shenzucai
     * @time 2019.10.24 23:03
     */
    @Async("taskExecutorAutoUnlock")
    public void autoUnlockAssert(UnlockDTO unlockDTO) {
        switch (unlockDTO.getType()) {
            case 0:
                // 抢单，派单
                BtBankMinerOrderTransaction btBankMinerOrderTransaction1 = btBankMinerOrderTransactionService.getById(unlockDTO.getId());
                Boolean updateOrder = Boolean.FALSE;
                // 修改订单状态
                if (btBankMinerOrderTransaction1.getType().equals(1)) {
                    getService().unlockOrderAsync(btBankMinerOrderTransaction1
                            , 3
                            , 1
                            , MinerOrderTransactionType.SECKILLED_ORDER_FINISHED
                            , "添加抢单订单记录失败"
                            , MinerBalanceTransactionType.GRAB_PRINCIPAL_TRANSFER_OUT
                            , "添加抢单本金转出记录失败"
                            , MinerBalanceTransactionType.GRAB_COMMISSION_TRANSFER_OUT
                            , "添加抢单本金佣金记录失败");

                } else {
                    // 派单
                    getService().unlockOrderAsync(btBankMinerOrderTransaction1
                            , 4
                            , 2
                            , MinerOrderTransactionType.DISPATCHED_ORDER_FINISHED
                            , "添加派单订单记录失败"
                            , MinerBalanceTransactionType.DISPATCH_PRINCIPAL_TRANSFER_OUT
                            , "添加派单本金转出记录失败"
                            , MinerBalanceTransactionType.DISPATCH_COMMISSION_TRANSFER_OUT
                            , "添加派单佣金记录失败");
                }


                break;
            case 1:
                getService().unlockFixedAsync(unlockDTO);

                break;
            default:
                break;
        }
    }

    /**
     * 固定本金和固定收益
     *
     * @param unlockDTO
     * @return true
     * @author shenzucai
     * @time 2019.10.25 11:43
     */
    @Async("taskExecutorAutoUnlock")
    public void unlockFixedAsync(UnlockDTO unlockDTO) {
        BtBankMinerBalanceTransaction btBankMinerBalanceTransaction = getService().unlockFixed(unlockDTO);
        if (Objects.isNull(btBankMinerBalanceTransaction)) {
            return;
        }
        SpringContextUtil.getBean(MinerRebateServiceImpl.class).doMinerReward(btBankMinerBalanceTransaction);
    }

    /**
     * 处理订单解锁
     *
     * @param btBankMinerOrderTransaction1
     * @param newStatus
     * @param originalStatus
     * @param seckilledOrderFinished
     * @param orderComment
     * @param grabPrincipalTransferOut
     * @param orderAmountComment
     * @param grabCommissionTransferOut
     * @param orderRewardComment
     * @return true
     * @author shenzucai
     * @time 2019.10.25 11:42
     */
    @Async("taskExecutorAutoUnlock")
    public void unlockOrderAsync(BtBankMinerOrderTransaction btBankMinerOrderTransaction1
            , int newStatus
            , int originalStatus
            , MinerOrderTransactionType seckilledOrderFinished
            , String orderComment
            , MinerBalanceTransactionType grabPrincipalTransferOut
            , String orderAmountComment
            , MinerBalanceTransactionType grabCommissionTransferOut
            , String orderRewardComment) {
        BtBankMinerBalanceTransaction btBankMinerBalanceTransaction = getService().unlockOrder(btBankMinerOrderTransaction1
                , newStatus
                , originalStatus
                , seckilledOrderFinished
                , orderComment
                , grabPrincipalTransferOut, orderAmountComment
                , grabCommissionTransferOut
                , orderRewardComment);
        if (Objects.isNull(btBankMinerBalanceTransaction)) {
            return;
        }
        SpringContextUtil.getBean(MinerRebateServiceImpl.class).doMinerReward(btBankMinerBalanceTransaction);


    }

    /**
     * 固定本金和固定收益
     *
     * @param unlockDTO
     * @return true
     * @author shenzucai
     * @time 2019.10.25 11:43
     */
    @Transactional(rollbackFor = Exception.class)
    public BtBankMinerBalanceTransaction unlockFixed(UnlockDTO unlockDTO) {
        // 读取派单收益比例
        Object fixedScaleO = btBankConfigService.getConfig(BtBankSystemConfig.FIXED_COMMISSION_RATE);
        if (Objects.isNull(fixedScaleO)) {
            log.error("unlock fixed error -> [ err = {}, dto = {}]", "载入系统配置失败", unlockDTO);
            throw new BtBankException("载入系统配置失败");
        }

        // 固定
        BtBankMinerBalanceTransaction btBankMinerBalanceTransaction = btBankMinerBalanceTransactionService.getById(unlockDTO.getId());
        BtBankMinerBalance btBankMinerBalanceGrade = btBankMinerBalanceService.findFirstByMemberId(btBankMinerBalanceTransaction.getMemberId());
        Integer minerGrade = btBankMinerBalanceGrade.getMinerGrade();
        if (minerGrade == 1) {
            fixedScaleO = btBankConfigService.getConfig(BtBankSystemConfig.SILVER_MINER_FIXED_COMMISSION_RATE);
            if (Objects.isNull(fixedScaleO)) {
                log.error("unlock fixed error -> [ err = {}, dto = {}]", "载入系统配置失败", unlockDTO);
                throw new BtBankException("载入系统配置失败");
            }
        }
        BigDecimal fixedScale = new BigDecimal(String.valueOf(fixedScaleO));
        BigDecimal temp = BigDecimalUtil.mul2down(btBankMinerBalanceTransaction.getBalance(), fixedScale);

        BtBankMinerBalanceTransaction btBankMinerBalanceTransactionZ = new BtBankMinerBalanceTransaction();
        btBankMinerBalanceTransactionZ.setId(idWorkByTwitter.nextId());
        btBankMinerBalanceTransactionZ.setMemberId(btBankMinerBalanceTransaction.getMemberId());
        btBankMinerBalanceTransactionZ.setType(MinerBalanceTransactionType.TRANSFER_OUT.getValue());
        btBankMinerBalanceTransactionZ.setBalance(BigDecimal.ZERO);
        btBankMinerBalanceTransactionZ.setMoney(btBankMinerBalanceTransaction.getBalance());
        btBankMinerBalanceTransactionZ.setCreateTime(new Date());
        btBankMinerBalanceTransactionZ.setRefId(btBankMinerBalanceTransaction.getId());

        Boolean balanceTransactionZ = btBankMinerBalanceTransactionService.save(btBankMinerBalanceTransactionZ);

        if (!balanceTransactionZ) {
            log.error("unlock fixed error -> [ err = {}, dto = {}]", "添加本金转出记录失败", unlockDTO);
            throw new BtBankException("添加本金转出记录失败");
        }

        BtBankMinerBalanceTransaction btBankMinerBalanceTransactionF = new BtBankMinerBalanceTransaction();
        btBankMinerBalanceTransactionF.setId(idWorkByTwitter.nextId());
        btBankMinerBalanceTransactionF.setMemberId(btBankMinerBalanceTransaction.getMemberId());
        btBankMinerBalanceTransactionF.setType(MinerBalanceTransactionType.FIEXD_COMMISSION_TRANSFER_OUT.getValue());
        btBankMinerBalanceTransactionF.setBalance(BigDecimal.ZERO);
        btBankMinerBalanceTransactionF.setMoney(temp);
        btBankMinerBalanceTransactionF.setCreateTime(new Date());
        btBankMinerBalanceTransactionF.setRefId(btBankMinerBalanceTransaction.getId());

        Boolean balanceTransactionF = btBankMinerBalanceTransactionService.save(btBankMinerBalanceTransactionF);

        if (!balanceTransactionF) {
            log.error("unlock fixed error -> [ err = {}, dto = {}]", "添加本金佣金转出记录失败", unlockDTO);
            throw new BtBankException("添加本金佣金转出记录失败");
        }

        BtBankMinerBalanceTransaction btBankMinerBalanceTransactionF1 = new BtBankMinerBalanceTransaction();
        btBankMinerBalanceTransactionF1.setId(idWorkByTwitter.nextId());
        btBankMinerBalanceTransactionF1.setMemberId(btBankMinerBalanceTransaction.getMemberId());
        btBankMinerBalanceTransactionF1.setType(MinerBalanceTransactionType.FIEXD_COMMISSION_TRANSFER_IN.getValue());
        btBankMinerBalanceTransactionF1.setBalance(BigDecimal.ZERO);
        btBankMinerBalanceTransactionF1.setMoney(btBankMinerBalanceTransactionF.getMoney());
        btBankMinerBalanceTransactionF1.setCreateTime(new Date());
        btBankMinerBalanceTransactionF1.setRefId(btBankMinerBalanceTransaction.getId());

        Boolean balanceTransactionF1 = btBankMinerBalanceTransactionService.save(btBankMinerBalanceTransactionF1);

        if (!balanceTransactionF1) {
            log.error("unlock fixed error -> [ err = {}, dto = {}]", "添加本金佣金转入记录失败", unlockDTO);
            throw new BtBankException("添加本金佣金转入记录失败");
        }

        Boolean btBankMinerBalance = btBankMinerBalanceTransactionService.lambdaUpdate()
                .setSql("balance = balance - " + btBankMinerBalanceTransaction.getBalance().toPlainString())
                .ge(BtBankMinerBalanceTransaction::getBalance, btBankMinerBalanceTransaction.getBalance())
                .eq(BtBankMinerBalanceTransaction::getId, btBankMinerBalanceTransaction.getId())
                .update();

        if (!btBankMinerBalance) {
            log.error("unlock fixed error -> [ err = {}, dto = {}]", "余额记录变动失败", unlockDTO);
            throw new BtBankException("余额记录变动失败");
        }

        Boolean balance = btBankMinerBalanceService.lambdaUpdate()
                .setSql("balance_amount = balance_amount - " + btBankMinerBalanceTransaction.getBalance().toPlainString())
                .setSql("got_reward_sum = got_reward_sum + " + temp.toPlainString())
                .ge(BtBankMinerBalance::getBalanceAmount, btBankMinerBalanceTransaction.getBalance())
                .eq(BtBankMinerBalance::getMemberId, btBankMinerBalanceTransaction.getMemberId())
                .update();
        //增加挖矿积分
        memberScoreBizService.increaseScore(btBankMinerBalanceTransaction.getMemberId(), temp, MinerBalanceTransactionType.FIEXD_COMMISSION_TRANSFER_OUT.getValue());
        //解锁信用卡
        creditCardCommissionService.unLockRefund(temp,btBankMinerBalanceTransaction.getMemberId());
        if (!balance) {
            log.error("unlock fixed error -> [ err = {}, dto = {}]", "余额变动失败", unlockDTO);
            throw new BtBankException("余额变动失败");
        }


        // 远程扣减资产
        MessageRespResult reduceResult =
                memberWalletService.optionMemberWalletBalance(TransactionType.TRANSFER_ACCOUNTS, memberId, "BT", "BT", temp.negate(), 0L, "矿池佣金");
        Boolean succeedRe = (Boolean) reduceResult.getData();
        if (!succeedRe) {
            log.warn("transfer amount into miner pool failed. memberId({}) amount({})", btBankMinerBalanceTransaction.getMemberId(), temp);
            log.error("unlock fixed error -> [ err = {}, dto = {}]", "远程扣减资产失败，佣金账户余额不足", unlockDTO);
            throw new BtBankException(MessageCode.ACCOUNT_BALANCE_INSUFFICIENT);
        }
        // 远程扣减资产
        MessageRespResult respResult =
                memberWalletService.optionMemberWalletBalance(TransactionType.TRANSFER_ACCOUNTS, btBankMinerBalanceTransaction.getMemberId(), "BT", "BT", btBankMinerBalanceTransaction.getBalance().add(temp), 0L, "矿池划转到btbank");
        Boolean succeeded = (Boolean) respResult.getData();
        if (!succeeded) {
            log.warn("transfer amount into miner pool failed. memberId({}) amount({})", btBankMinerBalanceTransaction.getMemberId(), btBankMinerBalanceTransaction.getBalance().add(temp));
            log.error("unlock fixed error -> [ err = {}, dto = {}]", "远程扣减资产失败，用户账户余额不足", unlockDTO);
            throw new BtBankException(MessageCode.ACCOUNT_BALANCE_INSUFFICIENT);
        }

        try {
            MessageRespResult<Member> memberRespResult = iMemberApiService.getMember(btBankMinerBalanceTransactionF1.getMemberId());
            // 挖矿收益
            if (btBankConfigService.isNewMemberConfig(memberRespResult.getData())) {
                planAssetService.doUnlock(memberRespResult.getData(), btBankMinerBalanceTransactionF1.getMoney(), 3, btBankMinerBalanceTransactionF1.getId(), null);
            }
        } catch (Exception e) {
            // 处理红包，异常不抛出
            log.error("unlock fixed error -> [ err = {}, dto = {}]", "处理红包，异常不抛出：" + e.getMessage(), unlockDTO);
        }

        return btBankMinerBalanceTransactionF;
    }

    /**
     * 处理订单解锁
     *
     * @param btBankMinerOrderTransaction1
     * @param newStatus
     * @param originalStatus
     * @param seckilledOrderFinished
     * @param orderComment
     * @param grabPrincipalTransferOut
     * @param orderAmountComment
     * @param grabCommissionTransferOut
     * @param orderRewardComment
     * @return true
     * @author shenzucai
     * @time 2019.10.25 11:42
     */
    @Transactional(rollbackFor = Exception.class)
    public BtBankMinerBalanceTransaction unlockOrder(BtBankMinerOrderTransaction btBankMinerOrderTransaction1
            , int newStatus
            , int originalStatus
            , MinerOrderTransactionType seckilledOrderFinished
            , String orderComment
            , MinerBalanceTransactionType grabPrincipalTransferOut
            , String orderAmountComment
            , MinerBalanceTransactionType grabCommissionTransferOut
            , String orderRewardComment) {
        Boolean updateOrder;// 抢单
        updateOrder = btBankMinerOrderService.lambdaUpdate()
                .set(BtBankMinerOrder::getProcessTime, new Date())
                .set(BtBankMinerOrder::getStatus, newStatus)
                .eq(BtBankMinerOrder::getId, btBankMinerOrderTransaction1.getMinerOrderId())
                .eq(BtBankMinerOrder::getStatus, originalStatus).update();
        if (updateOrder) {
            // 更新订单流水
            BtBankMinerOrderTransaction btBankMinerOrderTransaction = new BtBankMinerOrderTransaction();

            btBankMinerOrderTransaction.setId(idWorkByTwitter.nextId());
            btBankMinerOrderTransaction.setCreateTime(new Date());
            btBankMinerOrderTransaction.setMinerOrderId(btBankMinerOrderTransaction1.getMinerOrderId());
            btBankMinerOrderTransaction.setMemberId(btBankMinerOrderTransaction1.getMemberId());
            btBankMinerOrderTransaction.setRewardAmount(btBankMinerOrderTransaction1.getRewardAmount());
            btBankMinerOrderTransaction.setMoney(btBankMinerOrderTransaction1.getMoney());
            btBankMinerOrderTransaction.setType(seckilledOrderFinished.getValue());
            btBankMinerOrderTransaction.setUnlockTime(btBankMinerOrderTransaction1.getUnlockTime());

            Boolean updateOrderTransaction = btBankMinerOrderTransactionService.save(btBankMinerOrderTransaction);

            if (!updateOrderTransaction) {
                throw new BtBankException(71007, orderComment);
            }

            BtBankMinerBalanceTransaction btBankMinerBalanceTransaction = new BtBankMinerBalanceTransaction();
            btBankMinerBalanceTransaction.setId(idWorkByTwitter.nextId());
            btBankMinerBalanceTransaction.setMemberId(btBankMinerOrderTransaction1.getMemberId());
            btBankMinerBalanceTransaction.setType(grabPrincipalTransferOut.getValue());
            btBankMinerBalanceTransaction.setBalance(BigDecimal.ZERO);
            btBankMinerBalanceTransaction.setMoney(btBankMinerOrderTransaction1.getMoney());
            btBankMinerBalanceTransaction.setCreateTime(new Date());
            btBankMinerBalanceTransaction.setOrderTransactionId(btBankMinerOrderTransaction.getId());
            btBankMinerBalanceTransaction.setRefId(btBankMinerOrderTransaction.getMinerOrderId());

            Boolean balanceTransaction = btBankMinerBalanceTransactionService.save(btBankMinerBalanceTransaction);

            if (!balanceTransaction) {
                throw new BtBankException(71008, orderAmountComment);
            }

            BtBankMinerBalanceTransaction btBankMinerBalanceTransaction1 = new BtBankMinerBalanceTransaction();
            btBankMinerBalanceTransaction1.setId(idWorkByTwitter.nextId());
            btBankMinerBalanceTransaction1.setMemberId(btBankMinerOrderTransaction1.getMemberId());
            btBankMinerBalanceTransaction1.setType(grabCommissionTransferOut.getValue());
            btBankMinerBalanceTransaction1.setBalance(BigDecimal.ZERO);
            btBankMinerBalanceTransaction1.setMoney(btBankMinerOrderTransaction1.getRewardAmount());
            btBankMinerBalanceTransaction1.setCreateTime(new Date());
            btBankMinerBalanceTransaction1.setOrderTransactionId(btBankMinerOrderTransaction.getId());
            btBankMinerBalanceTransaction.setRefId(btBankMinerOrderTransaction.getMinerOrderId());

            Boolean balanceTransaction1 = btBankMinerBalanceTransactionService.save(btBankMinerBalanceTransaction1);

            if (!balanceTransaction1) {
                throw new BtBankException(71009, orderRewardComment);
            }

            Boolean balance = btBankMinerBalanceService.lambdaUpdate()
                    .setSql("lock_amount = lock_amount - " + btBankMinerOrderTransaction1.getMoney().toPlainString())
                    .setSql("processing_reward_sum = processing_reward_sum - " + btBankMinerOrderTransaction1.getRewardAmount().toPlainString())
                    .setSql("got_reward_sum = got_reward_sum + " + btBankMinerOrderTransaction1.getRewardAmount().toPlainString())
                    .ge(BtBankMinerBalance::getLockAmount, btBankMinerBalanceTransaction1.getMoney())
                    .ge(BtBankMinerBalance::getProcessingRewardSum, btBankMinerOrderTransaction1.getRewardAmount())
                    .eq(BtBankMinerBalance::getMemberId, btBankMinerOrderTransaction1.getMemberId())
                    .update();
            //增加挖矿积分
            memberScoreBizService.increaseScore(btBankMinerOrderTransaction1.getMemberId(), btBankMinerOrderTransaction1.getRewardAmount(), grabCommissionTransferOut.getValue());
            //信用卡解锁
            creditCardCommissionService.unLockRefund(btBankMinerOrderTransaction1.getRewardAmount(),btBankMinerBalanceTransaction.getMemberId());
            if (!balance) {
                throw new BtBankException(71010, "余额变动失败");
            }

            // 远程扣减资产
            MessageRespResult reduceResult =
                    memberWalletService.optionMemberWalletBalance(TransactionType.TRANSFER_ACCOUNTS, memberId, "BT", "BT", btBankMinerOrderTransaction1.getRewardAmount().negate(), 0L, "矿池佣金");
            Boolean succeedRe = (Boolean) reduceResult.getData();
            if (!succeedRe) {
                log.warn("transfer amount into miner pool failed. memberId({}) amount({})", btBankMinerBalanceTransaction.getMemberId(), btBankMinerOrderTransaction1.getRewardAmount());
                throw new BtBankException(MessageCode.ACCOUNT_BALANCE_INSUFFICIENT);
            }

            // 远程扣减资产
            MessageRespResult respResult =
                    memberWalletService.optionMemberWalletBalance(TransactionType.TRANSFER_ACCOUNTS, btBankMinerBalanceTransaction1.getMemberId(), "BT", "BT", btBankMinerOrderTransaction1.getMoney().add(btBankMinerOrderTransaction1.getRewardAmount()), 0L, "矿池划转到btbank");
            Boolean succeeded = (Boolean) respResult.getData();
            if (!succeeded) {
                log.warn("transfer amount into miner pool failed. memberId({}) amount({})", btBankMinerBalanceTransaction1.getMemberId(), btBankMinerOrderTransaction1.getMoney().add(btBankMinerOrderTransaction1.getRewardAmount()));
                throw new BtBankException(MessageCode.ACCOUNT_BALANCE_INSUFFICIENT);
            }

            try {
                MessageRespResult<Member> memberRespResult = iMemberApiService.getMember(btBankMinerBalanceTransaction1.getMemberId());
                // 挖矿收益
                if (btBankConfigService.isNewMemberConfig(memberRespResult.getData())) {
                    planAssetService.doUnlock(memberRespResult.getData(), btBankMinerBalanceTransaction1.getMoney(), 3, btBankMinerBalanceTransaction1.getId(), null);
                }
            } catch (Exception e) {
                // 处理红包，异常不抛出
            }
            return btBankMinerBalanceTransaction1;
        }
        return null;
    }

    /**
     * 自动派单（需要在解锁资产后，且建议两者时间间隔长）
     * 作为派单定时任务，将24小时无人抢单的订单，根据规则分派一个符合条件的矿工（防止订单积压）。
     * 矿工的【矿池可用】必须大于等于【订单金额】，没有符合条件的失败，等待下次执行
     * 24小时内(派单，抢单)总金额最少的
     * 24小时内(派单，抢单)次数最少的
     *
     * @return true
     * @author shenzucai
     * @time 2019.10.24 16:51
     */
    @Override
    public Boolean autoDispatch() {
        // 读取派单开关
        Object dispatchSwitchO = btBankConfigService.getConfig(BtBankSystemConfig.DISPATCH_SWITCH);
        if (Objects.isNull(dispatchSwitchO)) {
            throw new BtBankException(BtBankMsgCode.FAILED_TO_LOAD_SYSTEM_CONFIGURATION);
        }
        Boolean dispatchSwitch = Integer.valueOf(String.valueOf(dispatchSwitchO)) == 1 ? Boolean.TRUE : Boolean.FALSE;
        if (!dispatchSwitch) {
            throw new BtBankException(BtBankMsgCode.TURN_IN_SWITCH_OFF);
        }

        // 读取派单时长
        Object dispatchTimeO = btBankConfigService.getConfig(BtBankSystemConfig.DISPATCH_TIME);
        if (Objects.isNull(dispatchTimeO)) {
            throw new BtBankException(BtBankMsgCode.FAILED_TO_LOAD_SYSTEM_CONFIGURATION);
        }
        Long dispatchTime = Long.valueOf(String.valueOf(dispatchTimeO));

        // 读取派单收益比例
        Object dispatchScaleO = btBankConfigService.getConfig(BtBankSystemConfig.DISPATCH_COMMISSION_RATE);
        if (Objects.isNull(dispatchScaleO)) {
            throw new BtBankException(BtBankMsgCode.FAILED_TO_LOAD_SYSTEM_CONFIGURATION);
        }
        BigDecimal dispatchScale = new BigDecimal(String.valueOf(dispatchScaleO));
        //银牌矿工派单收益比例
        Object dispatchScaleOSilver = btBankConfigService.getConfig(BtBankSystemConfig.SILVER_MINER_DISPATCH_COMMISSION_RATE);
        if (Objects.isNull(dispatchScaleOSilver)) {
            throw new BtBankException(BtBankMsgCode.FAILED_TO_LOAD_SYSTEM_CONFIGURATION);
        }
        BigDecimal dispatchScaleSilver = new BigDecimal(String.valueOf(dispatchScaleOSilver));

        // 读取派单解锁时间
        Object dispatchUnLockTimeO = btBankConfigService.getConfig(BtBankSystemConfig.UNLOCK_TIME);
        if (Objects.isNull(dispatchUnLockTimeO)) {
            throw new BtBankException(BtBankMsgCode.FAILED_TO_LOAD_SYSTEM_CONFIGURATION);
        }
        Integer dispatchUnLockTime = Integer.valueOf(String.valueOf(dispatchUnLockTimeO));

        // 查找符合派单的订单
        List<BtBankMinerOrder> btBankMinerOrders = btBankMinerOrderService.listDispatchOrder(dispatchTime);
        if (btBankMinerOrders == null || btBankMinerOrders.size() < 1) {
            throw new BtBankException(BtBankMsgCode.NO_ORDER_IN_LINE_WITH_THE_ORDER);
        }
        btBankMinerOrders.stream().forEach(btBankMinerOrder -> {
            // 开始匹配
            BtBankMinerBalance btBankMinerBalance = btBankMinerBalanceService.dispatchMiner(btBankMinerOrder.getMoney());
            if (Objects.isNull(btBankMinerBalance)) {
                log.info("未找到合适的矿工进行派单 {}", btBankMinerOrder);
            } else {
                List<BtBankMinerBalanceTransaction> btBankMinerBalanceTransactions = btBankMinerBalanceTransactionService.lambdaQuery()
                        .gt(BtBankMinerBalanceTransaction::getBalance, BigDecimal.ZERO)
                        .eq(BtBankMinerBalanceTransaction::getType, 1)
                        .eq(BtBankMinerBalanceTransaction::getMemberId, btBankMinerBalance.getMemberId())
                        .orderByAsc(BtBankMinerBalanceTransaction::getCreateTime).list();
                //进行匹配操作，异步处理
                Integer minerGrade = btBankMinerBalance.getMinerGrade();
                if (minerGrade == 1) {
                    getService().disPatchOrderWithMiner(btBankMinerOrder, btBankMinerBalance, dispatchScaleSilver, dispatchUnLockTime, btBankMinerBalanceTransactions);
                } else {
                    getService().disPatchOrderWithMiner(btBankMinerOrder, btBankMinerBalance, dispatchScale, dispatchUnLockTime, btBankMinerBalanceTransactions);
                }

            }
        });
        return Boolean.TRUE;
    }

    /**
     * 理财活动状态变更
     *
     * @return true
     * @author shenzucai
     * @time 2019.12.21 15:52
     */
    @Override
    public Boolean autoUpdateActivity() {
        // 具有完整的状态流转
       /* List<FinancialActivityManage> financialActivityManages = financialActivityManageService.lambdaQuery()
                .last("case `status`\n" +
                        "when 0 THEN\n" +
                        "now() BETWEEN start_time and finalize_time\n" +
                        "when 1 THEN\n" +
                        "now() BETWEEN finalize_time and release_time\n" +
                        "when 2 THEN\n" +
                        "now() > release_time\n" +
                        "else\n" +
                        "1 != 1\n" +
                        "end").list();*/

        List<ActivityDTO> financialActivityManages = financialActivityManageService.listActivities();
        for (ActivityDTO activityDTO : financialActivityManages) {
            financialActivityManageService.lambdaUpdate()
                    .set(FinancialActivityManage::getStatus, activityDTO.getShouldStatus())
                    .eq(FinancialActivityManage::getId, activityDTO.getId())
                    .ne(FinancialActivityManage::getStatus, activityDTO.getShouldStatus()).update();
        }
        return Boolean.TRUE;
    }

    /**
     * 理财活动利息和本金解锁
     *
     * @return true
     * @author shenzucai
     * @time 2019.12.21 15:52
     */
    @Override
    public Boolean autoProfitUnlock() {
        List<FinancialActivityJoinDetails> financialActivityJoinDetails = financialActivityJoinDetailsService.lambdaQuery()
                .eq(FinancialActivityJoinDetails::getType, 0)
                .isNull(FinancialActivityJoinDetails::getReleaseProfitTime)
                .last(" and release_time <= now() limit 1000").list();

        if (financialActivityJoinDetails == null || financialActivityJoinDetails.size() < 1) {
            return Boolean.TRUE;
        }
        for (FinancialActivityJoinDetails financialActivityJoinDetail : financialActivityJoinDetails) {
            try {
                getService().doUnlockFinancialActivity(financialActivityJoinDetail);
            } catch (Exception e) {
                log.error("error {}", e);
            }
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean recommendUnlock() {
        List<FinancialActivityJoinDetails> financialActivityJoinDetails = financialActivityJoinDetailsService.lambdaQuery()
                .eq(FinancialActivityJoinDetails::getType, 0)
                .eq(FinancialActivityJoinDetails::getRecommendStatus, 0)
                .gt(FinancialActivityJoinDetails::getReleaseProfitAmount, 0)
                .isNotNull(FinancialActivityJoinDetails::getReleaseProfitTime)
                .last(" and release_profit_time <= now() limit 1000").list();

        if (financialActivityJoinDetails == null || financialActivityJoinDetails.size() < 1) {
            return Boolean.TRUE;
        }
        for (FinancialActivityJoinDetails financialActivityJoinDetail : financialActivityJoinDetails) {
            try {
                getService().doRecommendUnlockFinancialActivity(financialActivityJoinDetail);
            } catch (Exception e) {
                log.error("error {}", e);
            }
        }
        return Boolean.TRUE;
    }

    @Transactional
    public Boolean doRecommendUnlockFinancialActivity(FinancialActivityJoinDetails financialActivityJoinDetail) {
        MessageRespResult<Member> memberRespResult = iMemberApiService.getMember(financialActivityJoinDetail.getMemberId());
        if (memberRespResult.isSuccess()) {
            Member member = memberRespResult.getData();
            if (member != null && member.getInviterId() != null && member.getInviterId() > 0) {
                String silverMinerRate = String.valueOf(btBankConfigService.getConfig(BtBankSystemConfig.SILVER_MINER_COMMISSION_RATE));
                Long memberId = Long.valueOf(String.valueOf(btBankConfigService.getConfig("FINANCIAL_ACTIVITY_ACCOUNT")));
                if (Objects.isNull(memberId) || StringUtils.isEmpty(silverMinerRate)) {
                    throw new BtBankException(BtBankMsgCode.FAILED_TO_LOAD_SYSTEM_CONFIGURATION);
                }
                String tranferAmount = String.valueOf(btBankConfigService.getConfig(BtBankSystemConfig.MINIMUM_TRANSFER_AMOUNT));
                if (Objects.isNull(tranferAmount)) {
                    throw new BtBankException(71005, "载入MINIMUM_TRANSFER_AMOUNT低系统配置失败");
                }
                BigDecimal tranferAmountparm = new BigDecimal(tranferAmount);
                if (!financialActivityJoinDetailsService.effectiveMiner(member.getInviterId(), tranferAmountparm)) {
                    log.info("推荐人不是有效的矿工 InviterId：{}", member.getInviterId());
                    //直接更新状态
                    Boolean isUnlock = financialActivityJoinDetailsService.lambdaUpdate()
                            .set(FinancialActivityJoinDetails::getRecommendReleaseAmount, 0)
                            .set(FinancialActivityJoinDetails::getRecommendReleaseTime, new Date())
                            .set(FinancialActivityJoinDetails::getRecommendStatus, 1)
                            .isNotNull(FinancialActivityJoinDetails::getReleaseProfitTime)
                            .eq(FinancialActivityJoinDetails::getRecommendStatus, 0)
                            .eq(FinancialActivityJoinDetails::getId, financialActivityJoinDetail.getId()).update();
                    if (!isUnlock) {
                        log.error("修改推荐奖励发放状态失败financialActivityJoinDetailId {}", financialActivityJoinDetail.getId());
                    }
                    //进入这里说明 不会存在直推奖励 但是会有金牌矿工奖励
                    getService().goldRecommoned(memberId,financialActivityJoinDetail);
                    return true;
                }
                // 4月1日 0:00后推荐注册的有效矿工，可获得其每笔挖矿收益：前30天100%，第二个30天50%，然后恢复10%的奖励（大宗挖矿的直推奖励也按这个比例）
                BigDecimal silRate = new BigDecimal(silverMinerRate);
                MemberRateDto dto = memberScoreBizService.aprilOneRate(member.getRegistrationTime());
                if (dto.getRate() != null) {
                    silRate = dto.getRate();
                }
                BigDecimal recommendAmount = financialActivityJoinDetail.getReleaseProfitAmount()
                        .multiply(silRate).setScale(8, BigDecimal.ROUND_DOWN);
                Date time = btBankConfigService.getConfig(BtBankSystemConfig.SILVER_CREATION_REGISTER_TIME_END, (v) -> DateUtils.parseDatetime(v.toString()), DateUtils.parseDatetime("2020-04-01 00:00:00"));
                Integer isReceive = 1;
                if (new Date().after(time)) {
                    isReceive = 0;
                }
                Boolean isUnlock = financialActivityJoinDetailsService.lambdaUpdate()
                        .set(FinancialActivityJoinDetails::getRecommendReleaseAmount, recommendAmount)
                        .set(FinancialActivityJoinDetails::getRecommendReleaseTime, new Date())
                        .set(FinancialActivityJoinDetails::getRecommendStatus, 1)
                        .set(FinancialActivityJoinDetails::getIsReceive, isReceive)
                        .set(FinancialActivityJoinDetails::getInviterId, member.getInviterId())
                        .isNotNull(FinancialActivityJoinDetails::getReleaseProfitTime)
                        .eq(FinancialActivityJoinDetails::getRecommendStatus, 0)
                        .eq(FinancialActivityJoinDetails::getId, financialActivityJoinDetail.getId()).update();
                if (isUnlock) {
                    getService().goldRecommoned(member.getInviterId(),financialActivityJoinDetail);
                    // 从4月1日 0:00起的直推奖励，改为手动领取。推荐收益为待领取状态，需要推荐人手动领取 不直接发放到账户
                    if (new Date().after(time)) {
                        boolean b = memberScoreBizService.addPendingRecord(member.getInviterId(), recommendAmount, member.getId(), 1, financialActivityJoinDetail.getId(), String.format("大宗挖矿直推佣金%s", dto.getComment()));
                        Assert.isTrue(b, "保存待领取记录失败");
                        return true;
                    }
                    MemberWalletService.TradePlan plan = new MemberWalletService.TradePlan();
                    //系统扣款
                    WalletChangeRecord record = memberWalletService.tryTrade(
                            TransactionType.FINANCIAL_ACTIVITY_RECOMMEND_REWARD,
                            memberId, "BT", "BT", recommendAmount.abs().negate(), financialActivityJoinDetail.getId(),
                            "大宗挖矿直推佣金");

                    if (record == null) {
                        log.error("释放大宗挖矿直推奖励失败 txId = {}, member_id = {}, amount = {}", financialActivityJoinDetail.getId(), memberId, financialActivityJoinDetail.getAmount().abs());
                        throw new BtBankException(BtBankMsgCode.ACTIVITY_PURCHASE_FREQUENCY);
                    }
                    plan.getQueue().add(record);
                    //推荐人加款
                    WalletChangeRecord recordReward = memberWalletService.tryTrade(
                            TransactionType.FINANCIAL_ACTIVITY_RECOMMEND_REWARD,
                            member.getInviterId(), "BT", "BT", recommendAmount.abs(), financialActivityJoinDetail.getId(),
                            "大宗挖矿直推佣金");

                    if (recordReward == null) {
                        log.error("释放大宗挖矿直推奖励失败 txId = {}, member_id = {}, amount = {}", financialActivityJoinDetail.getId(), member.getInviterId(), financialActivityJoinDetail.getAmount().abs());
                        throw new BtBankException(BtBankMsgCode.ACTIVITY_PURCHASE_FREQUENCY);
                    }
                    plan.getQueue().add(recordReward);
                    try {
                        boolean b = memberWalletService.confirmPlan(plan);
                        if (!b) {
                            log.error("确认奖励发放失败 plan = {}", plan);
                            throw new BtBankException(CommonMsgCode.FAILURE);
                        } else {
                            return true;
                        }
                    } catch (Exception e) {
                        if (log.isInfoEnabled() && plan.getQueue().size() > 0) {
                            log.info("释放大宗挖矿直推奖励失败，执行远程回滚. 回滚总数({}) 计划:", plan.getQueue().size());
                            plan.getQueue().forEach(y -> {
                                log.info("memberId({}) amount({}) type({})", y.getMemberId(), y.getTradeBalance(), y.getType());
                            });
                        }
                        memberWalletService.rollbackPlan(plan);
                        e.printStackTrace();
                        throw e;
                    }

                }
            } else {
                log.info("该矿工无推荐人 memberId：{}", financialActivityJoinDetail.getMemberId());
                //直接更新状态
                Boolean isUnlock = financialActivityJoinDetailsService.lambdaUpdate()
                        .set(FinancialActivityJoinDetails::getRecommendReleaseAmount, 0)
                        .set(FinancialActivityJoinDetails::getRecommendReleaseTime, new Date())
                        .set(FinancialActivityJoinDetails::getRecommendStatus, 1)
                        .isNotNull(FinancialActivityJoinDetails::getReleaseProfitTime)
                        .eq(FinancialActivityJoinDetails::getRecommendStatus, 0)
                        .eq(FinancialActivityJoinDetails::getId, financialActivityJoinDetail.getId()).update();
                if (!isUnlock) {
                    log.error("修改推荐奖励发放状态失败financialActivityJoinDetailId {}", financialActivityJoinDetail.getId());
                }
                return true;
            }
        }
        return false;
    }

    public void goldRecommoned(Long inviterId,FinancialActivityJoinDetails financialActivityJoinDetail){
        //金牌释放
        // 查询祖父级推荐人
        Member fatherMember = memberAccountService.findMemberByMemberId(inviterId);
        log.info("-------------------大宗金牌释放---开始--------------------------------");
        if (Objects.nonNull(fatherMember) && Objects.nonNull(fatherMember.getInviterId())) {
            // 递归查询释放金牌矿工奖励
            minerRebateService.processFinancialSuperiorRewards(financialActivityJoinDetail,fatherMember.getInviterId());
        }
        log.info("-------------------大宗金牌释放---结束--------------------------------");

    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void doUnlockFinancialActivity(FinancialActivityJoinDetails financialActivityJoinDetail) {
        BigDecimal profitAmount = financialActivityJoinDetail.getAmount()
                .multiply(financialActivityJoinDetail.getProfitRate())
                .divide(new BigDecimal("100")).setScale(8, BigDecimal.ROUND_DOWN);
        Boolean isUnlock = financialActivityJoinDetailsService.lambdaUpdate()
                .set(FinancialActivityJoinDetails::getReleaseProfitAmount, profitAmount)
                .set(FinancialActivityJoinDetails::getReleaseProfitTime, new Date())
                .isNull(FinancialActivityJoinDetails::getReleaseProfitTime)
                .eq(FinancialActivityJoinDetails::getId, financialActivityJoinDetail.getId()).update();

        if (isUnlock) {

            Long memberId = Long.valueOf(String.valueOf(btBankConfigService.getConfig("FINANCIAL_ACTIVITY_ACCOUNT")));
            if (Objects.isNull(memberId)) {
                throw new BtBankException(BtBankMsgCode.FAILED_TO_LOAD_SYSTEM_CONFIGURATION);
            }
            // 先发放收益
            MessageRespResult<Boolean> booleanMessageRespResult = memberWalletService.optionMemberWalletBalance(TransactionType.FINANCIAL_ACTIVITY_PROFIT
                    , memberId
                    , financialActivityJoinDetail.getUnit()
                    , financialActivityJoinDetail.getUnit()
                    , profitAmount.negate()
                    , financialActivityJoinDetail.getId()
                    , "理财活动利息");

            if (!Objects.isNull(booleanMessageRespResult)
                    && booleanMessageRespResult.isSuccess()
                    && Boolean.TRUE.equals(booleanMessageRespResult.getData())) {
                MessageRespResult<Boolean> booleanMessageRespResult1 = memberWalletService.optionMemberWalletBalance(TransactionType.FINANCIAL_ACTIVITY_PROFIT
                        , financialActivityJoinDetail.getMemberId()
                        , financialActivityJoinDetail.getUnit()
                        , financialActivityJoinDetail.getUnit()
                        , profitAmount
                        , financialActivityJoinDetail.getId()
                        , "理财活动利息");

                if (!Objects.isNull(booleanMessageRespResult1)
                        && booleanMessageRespResult1.isSuccess()
                        && Boolean.TRUE.equals(booleanMessageRespResult1.getData())) {
                    //增加挖矿积分
                    memberScoreBizService.increaseScore(financialActivityJoinDetail.getMemberId(), profitAmount, TransactionType.FINANCIAL_ACTIVITY_PROFIT.getOrdinal());
                    // 之前逻辑
                    /*memberWalletService.optionMemberWalletBalance(TransactionType.FINANCIAL_ACTIVITY_LOCK
                    ,financialActivityJoinDetail.getMemberId()
                    ,financialActivityJoinDetail.getUnit()
                    ,financialActivityJoinDetail.getUnit()
                    ,financialActivityJoinDetail.getAmount()
                    ,financialActivityJoinDetail.getAmount().negate()
                    ,financialActivityJoinDetail.getId()
                    ,"理财活动锁仓");*/
                    //大宗矿池锁仓，解锁
                    //调系统加款
                    WalletChangeRecord record = memberWalletService.tryTrade(
                            TransactionType.FINANCIAL_ACTIVITY_LOCK,
                            financialActivityJoinDetail.getMemberId(), "BT", "BT", financialActivityJoinDetail.getAmount().abs(), financialActivityJoinDetail.getId(),
                            "释放大宗矿池锁仓");
                    if (record == null) {
                        log.error("释放加大宗矿池锁仓 txId = {}, member_id = {}, amount = {}", financialActivityJoinDetail.getId(), financialActivityJoinDetail.getMemberId(), financialActivityJoinDetail.getAmount().abs());
                        throw new BtBankException(BtBankMsgCode.PRIZE_QUIZE_TRANFERIN_ERROR);
                    }
                    //大宗矿池锁仓解锁
                    try {
                        if (btBankFinancialBalanceService.realseAmountMemberId(financialActivityJoinDetail.getMemberId(), financialActivityJoinDetail.getAmount().abs())) {
                            boolean b = memberWalletService.confirmTrade(financialActivityJoinDetail.getMemberId(), record.getId());
                            if (!b) {
                                log.error("确认账户变动失败 record = {}", record);
                                throw new BtBankException(CommonMsgCode.FAILURE);
                            } else {
                                return;
                            }
                        }
                        throw new BtBankException(CommonMsgCode.FAILURE);
                    } catch (RuntimeException ex) {
                        log.error("取消大宗矿池锁仓失败 txId = {}, err = {}", record.getId(), ex.getMessage());
                        boolean b = memberWalletService.rollbackTrade(financialActivityJoinDetail.getMemberId(), record.getId());
                        log.info("回滚账户变动 result = {}, record = {}", b, record);
                        throw ex;
                    }
                }

            } else {
                throw new BtBankException(MessageCode.UNKNOW_ERROR);
            }
        }
    }


    /**
     * 异步处理订单匹配
     *
     * @param btBankMinerOrder
     * @param btBankMinerBalance
     * @param dispatchScale
     * @param dispatchUnLockTime
     * @return true
     * @author shenzucai
     * @time 2019.10.24 21:17
     */
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void disPatchOrderWithMiner(final BtBankMinerOrder btBankMinerOrder
            , final BtBankMinerBalance btBankMinerBalance
            , final BigDecimal dispatchScale
            , final Integer dispatchUnLockTime
            , List<BtBankMinerBalanceTransaction> btBankMinerBalanceTransactions) {

        // 修改订单
        Boolean updateOrder = btBankMinerOrderService.lambdaUpdate().set(BtBankMinerOrder::getStatus, 2)
                .set(BtBankMinerOrder::getProcessTime, new Date())
                .set(BtBankMinerOrder::getMemberId, btBankMinerBalance.getMemberId())
                .eq(BtBankMinerOrder::getId, btBankMinerOrder.getId())
                .eq(BtBankMinerOrder::getStatus, 0).update();
        if (!updateOrder) {
            throw new BtBankException(BtBankMsgCode.FAILED_TO_MODIFY_THE_ORDER);
        }

        BtBankMinerOrderTransaction btBankMinerOrderTransaction = new BtBankMinerOrderTransaction();

        btBankMinerOrderTransaction.setId(idWorkByTwitter.nextId());
        btBankMinerOrderTransaction.setCreateTime(new Date());
        btBankMinerOrderTransaction.setMinerOrderId(btBankMinerOrder.getId());
        btBankMinerOrderTransaction.setMemberId(btBankMinerBalance.getMemberId());
        btBankMinerOrderTransaction.setRewardAmount(BigDecimalUtil.mul2down(btBankMinerOrder.getMoney(), dispatchScale, 8));
        btBankMinerOrderTransaction.setMoney(btBankMinerOrder.getMoney());
        btBankMinerOrderTransaction.setType(MinerOrderTransactionType.DISPATCHED_ORDER.getValue());
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date zero = calendar.getTime();
        btBankMinerOrderTransaction.setUnlockTime(DateUtil.addMinToDate(zero, dispatchUnLockTime));

        Boolean updateOrderTransaction = btBankMinerOrderTransactionService.save(btBankMinerOrderTransaction);

        if (!updateOrderTransaction) {
            throw new BtBankException(BtBankMsgCode.FAILED_TO_ADD_ORDER_RECORD);
        }

        BtBankMinerBalanceTransaction btBankMinerBalanceTransaction = new BtBankMinerBalanceTransaction();
        btBankMinerBalanceTransaction.setId(idWorkByTwitter.nextId());
        btBankMinerBalanceTransaction.setMemberId(btBankMinerBalance.getMemberId());
        btBankMinerBalanceTransaction.setType(MinerBalanceTransactionType.DISPATCHED_LOCKS.getValue());
        btBankMinerBalanceTransaction.setBalance(BigDecimal.ZERO);
        btBankMinerBalanceTransaction.setMoney(btBankMinerOrder.getMoney());
        btBankMinerBalanceTransaction.setCreateTime(new Date());
        btBankMinerBalanceTransaction.setOrderTransactionId(btBankMinerOrderTransaction.getId());
        btBankMinerBalanceTransaction.setRefId(btBankMinerOrder.getId());

        Boolean balanceTransaction = btBankMinerBalanceTransactionService.save(btBankMinerBalanceTransaction);

        if (!balanceTransaction) {
            throw new BtBankException(BtBankMsgCode.FAILED_TO_ADD_BALANCE_RECORD);
        }

        BtBankMinerBalanceTransaction btBankMinerBalanceTransaction2 = new BtBankMinerBalanceTransaction();
        btBankMinerBalanceTransaction2.setId(idWorkByTwitter.nextId());
        btBankMinerBalanceTransaction2.setMemberId(btBankMinerBalance.getMemberId());
        btBankMinerBalanceTransaction2.setType(MinerBalanceTransactionType.DISPATCH_COMMISSION_TRANSFER_IN.getValue());
        btBankMinerBalanceTransaction2.setBalance(BigDecimal.ZERO);
        btBankMinerBalanceTransaction2.setMoney(btBankMinerOrderTransaction.getRewardAmount());
        btBankMinerBalanceTransaction2.setCreateTime(new Date());
        btBankMinerBalanceTransaction2.setOrderTransactionId(btBankMinerOrderTransaction.getId());
        btBankMinerBalanceTransaction2.setRefId(btBankMinerOrder.getId());

        Boolean balanceTransaction1 = btBankMinerBalanceTransactionService.save(btBankMinerBalanceTransaction2);

        if (!balanceTransaction1) {
            throw new BtBankException(BtBankMsgCode.FAILED_TO_ADD_BALANCE_RECORD);
        }

        Boolean balance = btBankMinerBalanceService.lambdaUpdate()
                .setSql("balance_amount = balance_amount - " + btBankMinerOrder.getMoney().toPlainString())
                .setSql("lock_amount = lock_amount + " + btBankMinerOrder.getMoney().toPlainString())
                .setSql("processing_reward_sum = processing_reward_sum + " + btBankMinerOrderTransaction.getRewardAmount().toPlainString())
                .ge(BtBankMinerBalance::getBalanceAmount, btBankMinerOrder.getMoney())
                .eq(BtBankMinerBalance::getMemberId, btBankMinerBalance.getMemberId()).update();

        if (!balance) {
            throw new BtBankException(BtBankMsgCode.BALANCE_CHANGE_FAILED);
        }

        if (btBankMinerBalanceTransactions == null || btBankMinerBalanceTransactions.size() < 1) {
            throw new BtBankException(MessageCode.ACCOUNT_BALANCE_INSUFFICIENT);
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (BtBankMinerBalanceTransaction btBankMinerBalanceTransaction1 : btBankMinerBalanceTransactions) {

            BigDecimal tempBalance = btBankMinerOrder.getMoney().subtract(totalAmount);
            if (tempBalance.compareTo(btBankMinerBalanceTransaction1.getBalance()) <= 0) {
                Boolean aBoolean = btBankMinerBalanceTransactionService.lambdaUpdate()
                        .setSql("balance = balance - " + tempBalance.toPlainString())
                        .ge(BtBankMinerBalanceTransaction::getBalance, tempBalance)
                        .eq(BtBankMinerBalanceTransaction::getId, btBankMinerBalanceTransaction1.getId()).update();
                if (aBoolean) {
                    totalAmount = BigDecimalUtil.add(totalAmount, tempBalance);
                    break;
                } else {
                    continue;
                }
            } else {
                Boolean aBoolean1 = btBankMinerBalanceTransactionService.lambdaUpdate()
                        .setSql("balance = balance - " + btBankMinerBalanceTransaction1.getBalance().toPlainString())
                        .eq(BtBankMinerBalanceTransaction::getId, btBankMinerBalanceTransaction1.getId())
                        .eq(BtBankMinerBalanceTransaction::getBalance, btBankMinerBalanceTransaction1.getBalance()).update();
                if (aBoolean1) {
                    totalAmount = BigDecimalUtil.add(totalAmount, btBankMinerBalanceTransaction1.getBalance());
                } else {
                    continue;
                }
            }
        }

        if (totalAmount.compareTo(btBankMinerOrder.getMoney()) < 0) {
            throw new BtBankException(BtBankMsgCode.INSUFFICIENT_BALANCE_RECORD_AVAILABLE);
        }

        minerWebSocketService.sendNewOrderStatusToAllClient(btBankMinerOrder);


    }

    @Override
    @Async
    public Boolean autoReleaseProfit() {
        // 查询3月8日后的 累计收益
        Date limitTime = btBankConfigService.getConfig(NEW_MEMBER_EXPERIENCE_REGISTER_TIME, (v) -> DateUtils.parseDatetime(v.toString()), DateUtils.parseDatetime("2020-03-09 00:00:00"));
        BigDecimal profitAmount = btBankConfigService.getConfig(NEW_MEMBER_EXPERIENCE_AMOUNT_PROFIT, (v) -> new BigDecimal(v.toString()), new BigDecimal(600));
        List<BtBankMinerBalanceTransaction> countProfitByType = btBankMinerBalanceTransactionService.countProfitByType(limitTime);
        if (countProfitByType != null || countProfitByType.size() > 0) {
            for (BtBankMinerBalanceTransaction recored : countProfitByType) {
                // 累计挖矿收益大于了600BT
                if (recored.getSumMoney().compareTo(profitAmount) >= 0) {
                    // 释放获取的600BT
                    Boolean result = memberExperienceWalletService.deductMemberExperienceWallet(recored.getMemberId(), recored.getSumMoney());
                    if (result) {
                        log.info("释放成功, 会员ID： {}", recored.getId());
                    }
                }
            }
        }
        return Boolean.TRUE;
    }



    public ScheduleServiceImpl getService() {
        return SpringContextUtil.getBean(ScheduleServiceImpl.class);
    }


}
