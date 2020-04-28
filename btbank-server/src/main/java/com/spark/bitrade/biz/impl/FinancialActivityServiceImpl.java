package com.spark.bitrade.biz.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.api.vo.FinancialActivityJoinDetailsVo;
import com.spark.bitrade.api.vo.FinancialActivityManageVo;
import com.spark.bitrade.biz.FinancialActivityService;
import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.WalletChangeRecord;
import com.spark.bitrade.enums.MessageCode;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.BtBankFinancialBalance;
import com.spark.bitrade.repository.entity.FinancialActivityJoinDetails;
import com.spark.bitrade.repository.entity.FinancialActivityManage;
import com.spark.bitrade.repository.service.BtBankFinancialBalanceService;
import com.spark.bitrade.repository.service.FinancialActivityJoinDetailsService;
import com.spark.bitrade.repository.service.FinancialActivityManageService;
import com.spark.bitrade.service.MemberWalletService;
import com.spark.bitrade.util.DateUtils;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author shenzucai
 * @time 2019.12.21 13:10
 */
@Slf4j
@Service("financialActivityServiceImpl")
public class FinancialActivityServiceImpl implements FinancialActivityService {

    @Autowired
    private FinancialActivityManageService financialActivityManageService;

    @Autowired
    private FinancialActivityJoinDetailsService financialActivityJoinDetailsService;

    @Autowired
    private MemberWalletService memberWalletService;

    @Autowired
    private BtBankFinancialBalanceService btBankFinancialBalanceService;

    /**
     * 参加理财活动
     *
     * @param member
     * @param amount     总金额
     * @param activityId
     * @return true
     * @author shenzucai
     * @time 2019.12.21 13:01
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinancialActivityJoinDetails joinLock(Member member, BigDecimal amount, Long activityId, Integer purchaseNums) {
        if(purchaseNums <= 0 || BigDecimal.ZERO.compareTo(amount) == 1){
            throw new BtBankException(MessageCode.INVALID_PARAMETER);
        }
        // 0 查询活动
        FinancialActivityManage financialActivityManage = financialActivityManageService.lambdaQuery().eq(FinancialActivityManage::getId, activityId).one();
        if (Objects.isNull(financialActivityManage)) {
            throw new BtBankException(BtBankMsgCode.FIND_ACTIVITY_FAILED);
        }
        // 1 更新活动
        Boolean reduceActivity = financialActivityManageService.lambdaUpdate()
                .setSql("remain_purchase_nums = remain_purchase_nums -" + purchaseNums)
                .eq(FinancialActivityManage::getId, activityId)
                .eq(FinancialActivityManage::getStatus, 1)
                .ge(FinancialActivityManage::getRemainPurchaseNums, purchaseNums).update();
        if (!reduceActivity) {
            log.error("reduce remain_purchase_nums fail:activityId {},purchaseNums {}", activityId, purchaseNums);
            throw new BtBankException(BtBankMsgCode.UPDATE_ACTIVITY_FAILED);
        }

        // 2 添加活动记录
        FinancialActivityJoinDetails financialActivityJoinDetails = new FinancialActivityJoinDetails();
        financialActivityJoinDetails.setId(IdWorker.getId());
        financialActivityJoinDetails.setActivityId(activityId);
        financialActivityJoinDetails.setAmount(amount);
        financialActivityJoinDetails.setCreateTime(new Date());
        financialActivityJoinDetails.setUpdateTime(new Date());
        financialActivityJoinDetails.setFinalizeTime(financialActivityManage.getFinalizeTime());
        financialActivityJoinDetails.setMemberId(member.getId());
        financialActivityJoinDetails.setMemberName(StringUtils.isEmpty(member.getUsername()) ? member.getRealName() : member.getUsername());
        financialActivityJoinDetails.setName(financialActivityManage.getName());
        financialActivityJoinDetails.setPurchaseNums(purchaseNums);
        financialActivityJoinDetails.setReleaseTime(financialActivityManage.getReleaseTime());
        financialActivityJoinDetails.setStartTime(financialActivityManage.getStartTime());
        financialActivityJoinDetails.setType(0);
        financialActivityJoinDetails.setProfitRate(financialActivityManage.getProfitRate());
        financialActivityJoinDetails.setUnit(financialActivityManage.getUnit());
        if (!financialActivityJoinDetailsService.save(financialActivityJoinDetails)) {
            log.error("insert financialActivityJoinDetailsService fail:{}", financialActivityJoinDetailsService);
            throw new BtBankException(MessageCode.UNKNOW_ERROR);
        }

        // 3 调用account-server锁币
        MessageRespResult<Boolean> messageRespResult = memberWalletService.optionMemberWalletBalance(TransactionType.FINANCIAL_ACTIVITY_LOCK
                , member.getId()
                , financialActivityManage.getUnit()
                , financialActivityManage.getUnit()
                , amount.negate()
                , amount
                , financialActivityJoinDetails.getId()
                , "理财活动锁仓");

        if(Objects.isNull(messageRespResult)){
            throw new BtBankException(MessageCode.UNKNOW_ERROR);
        }

        if(!messageRespResult.isSuccess() || Objects.isNull(messageRespResult.getData()) || !messageRespResult.getData()){
            throw new BtBankException(MessageCode.ACCOUNT_FROZEN_BALANCE_INSUFFICIENT);
        }
        return financialActivityJoinDetails;
    }
    /**
     * 参加理财活动锁仓
     *
     * @param member
     * @param amount
     * @param activityId
     * @return true
     * @author shenzucai
     * @time 2019.12.21 13:01
     */
    @Override
    @Transactional
    public FinancialActivityJoinDetails joinActivitiesLock(Member member, BigDecimal amount, Long activityId, Integer purchaseNums) {
        // 0 查询活动
        FinancialActivityManage financialActivityManage = financialActivityManageService.lambdaQuery().eq(FinancialActivityManage::getId, activityId).one();
        if (Objects.isNull(financialActivityManage)) {
            throw new BtBankException(BtBankMsgCode.FIND_ACTIVITY_FAILED);
        }
        // 1 更新活动
        Boolean reduceActivity = financialActivityManageService.lambdaUpdate()
                .setSql("remain_purchase_nums = remain_purchase_nums -" + purchaseNums)
                .eq(FinancialActivityManage::getId, activityId)
                .eq(FinancialActivityManage::getStatus, 1)
                .ge(FinancialActivityManage::getRemainPurchaseNums, purchaseNums).update();
        if (!reduceActivity) {
            log.error("reduce remain_purchase_nums fail:activityId {},purchaseNums {}", activityId, purchaseNums);
            throw new BtBankException(BtBankMsgCode.UPDATE_ACTIVITY_FAILED);
        }

        // 2 添加活动记录
        FinancialActivityJoinDetails financialActivityJoinDetails = new FinancialActivityJoinDetails();
        financialActivityJoinDetails.setId(IdWorker.getId());
        financialActivityJoinDetails.setActivityId(activityId);
        financialActivityJoinDetails.setAmount(amount);
        financialActivityJoinDetails.setCreateTime(new Date());
        financialActivityJoinDetails.setUpdateTime(new Date());
        financialActivityJoinDetails.setFinalizeTime(financialActivityManage.getFinalizeTime());
        financialActivityJoinDetails.setMemberId(member.getId());
        financialActivityJoinDetails.setMemberName(StringUtils.isEmpty(member.getUsername()) ? member.getRealName() : member.getUsername());
        financialActivityJoinDetails.setName(financialActivityManage.getName());
        financialActivityJoinDetails.setPurchaseNums(purchaseNums);
        financialActivityJoinDetails.setReleaseTime(financialActivityManage.getReleaseTime());
        financialActivityJoinDetails.setStartTime(financialActivityManage.getStartTime());
        financialActivityJoinDetails.setType(0);
        financialActivityJoinDetails.setProfitRate(financialActivityManage.getProfitRate());
        financialActivityJoinDetails.setUnit(financialActivityManage.getUnit());
        financialActivityJoinDetails.setIsReceive(0);
        if (!financialActivityJoinDetailsService.save(financialActivityJoinDetails)) {
            log.error("insert financialActivityJoinDetailsService fail:{}", financialActivityJoinDetailsService);
            throw new BtBankException(MessageCode.UNKNOW_ERROR);
        }

        // 查询是否有账户，如果没有账户创建账户
        BtBankFinancialBalance btBankFinancialBalance =  btBankFinancialBalanceService.findFirstByMemberId(member.getId());
        if(btBankFinancialBalance==null){
            if(!getService().createBtBankFinancialBalance(member.getId())){
                throw new BtBankException(BtBankMsgCode.FIND_ACTIVITY_FAILED);
            }
        }
        //添加流水记录，更改金额
        WalletChangeRecord record = memberWalletService.tryTrade(
                TransactionType.FINANCIAL_ACTIVITY_LOCK,
                member.getId(), "BT", "BT", amount.abs().negate(), financialActivityJoinDetails.getId(),
                "参加大宗矿池锁仓");
        if (record == null) {
            log.error("参加大宗矿池锁仓 txId = {}, member_id = {}, amount = {}",financialActivityJoinDetails.getId(), member.getId(),amount.abs());
            throw new BtBankException(BtBankMsgCode.PRIZE_QUIZE_TRANFERIN_ERROR);
        }
        //修改账户金额
        try {
            if( btBankFinancialBalanceService.lockAmountByMemberId(member.getId(),amount)){
                boolean b = memberWalletService.confirmTrade(member.getId(), record.getId());
                if (!b) {
                    log.error("确认账户变动失败 record = {}", record);
                    throw new BtBankException(CommonMsgCode.FAILURE);
                } else {
                    return financialActivityJoinDetails;
                }
            }
            throw new BtBankException(CommonMsgCode.FAILURE);
        } catch (RuntimeException ex) {
            log.error("参加大宗矿池锁仓失败 txId = {}, err = {}", record.getId(), ex.getMessage());
            boolean b = memberWalletService.rollbackTrade(member.getId(), record.getId());
            log.info("回滚账户变动 result = {}, record = {}", b, record);
            throw ex;
        }
    }

    /**
     * 撤销参加理财活动
     *
     * @param member
     * @param lockDetailId
     * @return true
     * @author shenzucai
     * @time 2019.12.21 13:01
     */
    @Override
    public Boolean cancelLock(Member member, Long lockDetailId) {
        // 0 查询活动
        FinancialActivityJoinDetails financialActivityJoinDetails = financialActivityJoinDetailsService.lambdaQuery()
                .eq(FinancialActivityJoinDetails::getId, lockDetailId)
                .eq(FinancialActivityJoinDetails::getMemberId,member.getId())
                .isNull(FinancialActivityJoinDetails::getReleaseProfitTime)
                .eq(FinancialActivityJoinDetails::getType,0)
                .one();
        if (Objects.isNull(financialActivityJoinDetails)) {
            throw new BtBankException(BtBankMsgCode.FIND_ACTIVITY_FAILED);
        }
        // 1 更新活动
        Boolean reduceActivity = financialActivityManageService.lambdaUpdate()
                .setSql("remain_purchase_nums = remain_purchase_nums +" + financialActivityJoinDetails.getPurchaseNums())
                .eq(FinancialActivityManage::getId, financialActivityJoinDetails.getActivityId())
                .eq(FinancialActivityManage::getStatus, 1)
                .update();
        if (!reduceActivity) {
            log.error("add remain_purchase_nums fail: {}", financialActivityJoinDetails);
            throw new BtBankException(BtBankMsgCode.UPDATE_ACTIVITY_FAILED);
        }

        // 2 修改活动记录
        financialActivityJoinDetails.setUpdateTime(new Date());
        financialActivityJoinDetails.setType(1);

        Boolean cancelUpdate = financialActivityJoinDetailsService.lambdaUpdate()
                .set(FinancialActivityJoinDetails::getUpdateTime,new Date())
                .set(FinancialActivityJoinDetails::getType,1)
                .eq(FinancialActivityJoinDetails::getId,lockDetailId)
                .isNull(FinancialActivityJoinDetails::getReleaseProfitTime).update();
        if (!cancelUpdate) {
            log.error("update financialActivityJoinDetailsService fail:{}", financialActivityJoinDetailsService);
            throw new BtBankException(MessageCode.UNKNOW_ERROR);
        }

        // 3 调用account-server锁币
        MessageRespResult<Boolean> messageRespResult = memberWalletService.optionMemberWalletBalance(TransactionType.FINANCIAL_ACTIVITY_LOCK
                , member.getId()
                , financialActivityJoinDetails.getUnit()
                , financialActivityJoinDetails.getUnit()
                , financialActivityJoinDetails.getAmount()
                , financialActivityJoinDetails.getAmount().negate()
                , financialActivityJoinDetails.getId()
                , "理财活动锁仓撤回");

        if(Objects.isNull(messageRespResult)){
            throw new BtBankException(MessageCode.UNKNOW_ERROR);
        }

        if(!messageRespResult.isSuccess() || Objects.isNull(messageRespResult.getData()) || !messageRespResult.getData()){
            throw new BtBankException(MessageCode.ACCOUNT_FROZEN_BALANCE_INSUFFICIENT);
        }
        return Boolean.TRUE;
    }
    /**
     * 撤销参加理财活动修改
     *
     * @param member
     * @param lockDetailId
     * @return true
     * @author shenzucai
     * @time 2019.12.21 13:01
     */
    @Override
    @Transactional
    public Boolean cancelActivityLock(Member member, Long lockDetailId) {
        FinancialActivityJoinDetails financialActivityJoinDetails = financialActivityJoinDetailsService.lambdaQuery()
                .eq(FinancialActivityJoinDetails::getId, lockDetailId)
                .eq(FinancialActivityJoinDetails::getMemberId,member.getId())
                .isNull(FinancialActivityJoinDetails::getReleaseProfitTime)
                .eq(FinancialActivityJoinDetails::getType,0)
                .one();
        if (Objects.isNull(financialActivityJoinDetails)) {
            throw new BtBankException(BtBankMsgCode.FIND_ACTIVITY_FAILED);
        }
        // 1 更新活动
        Boolean reduceActivity = financialActivityManageService.lambdaUpdate()
                .setSql("remain_purchase_nums = remain_purchase_nums +" + financialActivityJoinDetails.getPurchaseNums())
                .eq(FinancialActivityManage::getId, financialActivityJoinDetails.getActivityId())
                .eq(FinancialActivityManage::getStatus, 1)
                .update();
        if (!reduceActivity) {
            log.error("add remain_purchase_nums fail: {}", financialActivityJoinDetails);
            throw new BtBankException(BtBankMsgCode.UPDATE_ACTIVITY_FAILED);
        }

        // 2 修改活动记录
        financialActivityJoinDetails.setUpdateTime(new Date());
        financialActivityJoinDetails.setType(1);

        Boolean cancelUpdate = financialActivityJoinDetailsService.lambdaUpdate()
                .set(FinancialActivityJoinDetails::getUpdateTime,new Date())
                .set(FinancialActivityJoinDetails::getType,1)
                .eq(FinancialActivityJoinDetails::getId,lockDetailId)
                .isNull(FinancialActivityJoinDetails::getReleaseProfitTime).update();
        if (!cancelUpdate) {
            log.error("update financialActivityJoinDetailsService fail:{}", financialActivityJoinDetailsService);
            throw new BtBankException(MessageCode.UNKNOW_ERROR);
        }
        //调系统加款
        WalletChangeRecord record = memberWalletService.tryTrade(
                TransactionType.FINANCIAL_ACTIVITY_LOCK,
                member.getId(), "BT", "BT", financialActivityJoinDetails.getAmount().abs().negate(), financialActivityJoinDetails.getId(),
                "取消参加大宗矿池锁仓");
        if (record == null) {
            log.error("取消参加大宗矿池锁仓 txId = {}, member_id = {}, amount = {}",financialActivityJoinDetails.getId(), member.getId(),financialActivityJoinDetails.getAmount().abs());
            throw new BtBankException(BtBankMsgCode.PRIZE_QUIZE_TRANFERIN_ERROR);
        }
        //扣除大宗矿池
        try {
            if( btBankFinancialBalanceService.realseAmountMemberId(member.getId(),financialActivityJoinDetails.getAmount().abs())){
                boolean b = memberWalletService.confirmTrade(member.getId(), record.getId());
                if (!b) {
                    log.error("确认账户变动失败 record = {}", record);
                    throw new BtBankException(CommonMsgCode.FAILURE);
                } else {
                    return Boolean.TRUE;
                }
            }
            throw new BtBankException(CommonMsgCode.FAILURE);
        } catch (RuntimeException ex) {
            log.error("取消大宗矿池锁仓失败 txId = {}, err = {}", record.getId(), ex.getMessage());
            boolean b = memberWalletService.rollbackTrade(member.getId(), record.getId());
            log.info("回滚账户变动 result = {}, record = {}", b, record);
            throw ex;
        }
    }
    @Override
    public List<FinancialActivityManage> getAvailableActivities(FinancialActivityManageVo financialActivityManageVo) {
        return financialActivityManageService.getAvailableActivities(financialActivityManageVo);
    }

    @Override
    public FinancialActivityManage getFinancialActivityManage(Long activityId) {
        return financialActivityManageService.getById(activityId);
    }



    @Override
    public IPage<FinancialActivityJoinDetails> getJoinActivitiesPage( Member member, int type, int timeType, Integer current, Integer size ) {
        IPage<FinancialActivityJoinDetails> page = new Page<>(current, size);
        QueryWrapper<FinancialActivityJoinDetails> query = new QueryWrapper<>();
        Date date = new Date();
        String clumn = "create_time";
        int queryType = 0;
        switch (type){
            // 持仓明细
            case  0 :
                clumn = "create_time";
                queryType = 0;
                query.lambda().isNull(FinancialActivityJoinDetails::getReleaseProfitTime);
                break;
            //释放
            case 1:
                clumn = "release_time";
                queryType = 0;
                query.lambda().isNotNull(FinancialActivityJoinDetails::getReleaseProfitTime);
                break;
            //撤回
            case 2:
                clumn = "update_time";
                queryType = 1;
                break;
        }
        if(timeType == 0 ){
            query.ge(clumn,DateUtils.subDate(date,6));
        }else if(timeType == 1){
            query.ge(clumn,DateUtils.subDate(date,29));
        }else if(timeType == 2){
            query.ge(clumn,DateUtils.subDate(date,179));
        }
        query.le(clumn,date).lambda().eq(FinancialActivityJoinDetails::getMemberId,member.getId());
        query.eq("type",queryType).orderByDesc(clumn);
        return financialActivityJoinDetailsService.page(page,query);

    }

    @Override
    public Boolean alreadyJoinActivity(Member member, Long activityId,int purchaseNums,FinancialActivityManage financialActivityManage) {
        // 查询参加活动份数限制。
        FinancialActivityJoinDetailsVo financialActivityJoinDetailsVo = new FinancialActivityJoinDetailsVo();
        financialActivityJoinDetailsVo.setMemberId(member.getId());
        financialActivityJoinDetailsVo.setActivityId(activityId);

        Integer alreadyPurchaseNums = financialActivityJoinDetailsService.getAlreadyJoinNum(financialActivityJoinDetailsVo);
        if(alreadyPurchaseNums ==null ){
            if(financialActivityManage.getUpSinglePurchase()<(purchaseNums)){
                throw new BtBankException(BtBankMsgCode.ACTIVITY_OVER_MAX_LIMIT);
            }
        }else{
            if(financialActivityManage.getUpSinglePurchase()<(alreadyPurchaseNums+purchaseNums)){
                throw new BtBankException(BtBankMsgCode.ACTIVITY_OVER_MAX_LIMIT);
            }
        }
        return false;
    }

    @Override
    public List<FinancialActivityJoinDetails> getJoinActivities(Member member) {
        return financialActivityJoinDetailsService.lambdaQuery().eq(FinancialActivityJoinDetails::getMemberId,member.getId())
                .eq(FinancialActivityJoinDetails::getType,0).eq(FinancialActivityJoinDetails::getUnit,"BT").isNull(FinancialActivityJoinDetails::getReleaseProfitAmount).list();
    }

    @Override
    public BigDecimal getJoinActivitiesProfit(Member member, int timeType) {
        Date date = new Date();
        Date startTime = null;
        if(timeType == 0 ){
            startTime = DateUtils.subDate(date,6);
        }else if(timeType == 1){
            startTime = DateUtils.subDate(date,29);
        }else if(timeType == 2){
            startTime = DateUtils.subDate(date,179);
        }
        FinancialActivityJoinDetailsVo financialActivityJoinDetailsVo = new FinancialActivityJoinDetailsVo();
        financialActivityJoinDetailsVo.setMemberId(member.getId());
        financialActivityJoinDetailsVo.setStartTime(startTime);
        financialActivityJoinDetailsVo.setEndTime(date);
        return financialActivityJoinDetailsService.getJoinActivitiesProfit(financialActivityJoinDetailsVo);
    }

    @Override
    public BigDecimal getTotalLock(Member member) {
        FinancialActivityJoinDetailsVo financialActivityJoinDetailsVo = new FinancialActivityJoinDetailsVo();
        financialActivityJoinDetailsVo.setMemberId(member.getId());
        return financialActivityJoinDetailsService.getTotalLock(financialActivityJoinDetailsVo);
    }

    @Override
    public FinancialActivityJoinDetailsVo getActivityDetails(Long lockDetailId) {
        FinancialActivityJoinDetailsVo financialActivityJoinDetailsVo = new FinancialActivityJoinDetailsVo();
        financialActivityJoinDetailsVo.setId(lockDetailId);
        return financialActivityJoinDetailsService.getJoinActivitiesDetail(financialActivityJoinDetailsVo);
    }

    public  boolean createBtBankFinancialBalance(Long memberId){
        Date now = new Date();
        BtBankFinancialBalance btBankFinancialBalance = new BtBankFinancialBalance();
        btBankFinancialBalance.setMemberId(memberId);
        btBankFinancialBalance.setUpdateTime(now);
        btBankFinancialBalance.setBalanceAmount(BigDecimal.ZERO);
        btBankFinancialBalance.setCreateTime(now);
        return btBankFinancialBalanceService.save(btBankFinancialBalance);
    }

    private FinancialActivityService getService() {
        return SpringContextUtil.getBean(FinancialActivityServiceImpl.class);
    }
}
