package com.spark.bitrade.biz.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.spark.bitrade.biz.ActivityRedpacketService;
import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.constant.BtBankSystemConfig;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.SilkDataDist;
import com.spark.bitrade.entity.WalletChangeRecord;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.ActivityRedPackManage;
import com.spark.bitrade.repository.entity.ActivityRedPackReceiveRecord;
import com.spark.bitrade.repository.entity.BtBankMinerOrder;
import com.spark.bitrade.repository.service.ActivityRedPackManageService;
import com.spark.bitrade.repository.service.ActivityRedPackReceiveRecordService;
import com.spark.bitrade.service.MemberWalletService;
import com.spark.bitrade.service.SilkDataDistService;
import com.spark.bitrade.util.SpringContextUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
@Slf4j
@AllArgsConstructor
@Service
public class ActivityRedpacketServiceImpl  implements ActivityRedpacketService {
    private ActivityRedPackManageService activityRedPackManageService;
    private ActivityRedPackReceiveRecordService activityRedPackReceiveRecordService;
    private SilkDataDistService silkDataDistService;
    private MemberWalletService memberWalletService;
    private RedisTemplate redisTemplate;
    @Override
    public Boolean processGrabOrderRedPack(Long  memberId) {
        log.info("处理抢单挖矿红包逻辑 memberId {}",memberId);
        return getService().processRedPack(memberId,(short)0,null);
    }

    @Override
    public  Boolean processRedPack(Long memberId, Short triggerEvent,Long inviteeId) {
        //查询活动
        //List<ActivityRedPackManage> activityRedPackManagelst = getService().getActivityRedPackManage(triggerEvent);
        // for (ActivityRedPackManage activityRedPackManage :activityRedPackManagelst){
        ActivityRedPackManage activityRedPackManage  = getService().getOneActivityRedPackManage(triggerEvent);

        if (activityRedPackManage.getSurplusAmount().compareTo(BigDecimal.ZERO)<=0) {
            return Boolean.TRUE;
        }
        //查询该用户是否已经存在该领奖记录
        //if(!getService().alreadyGotRedpack(memberId,activityRedPackManage.getId())){}
        //检查当前触发次数是否满足发送条件
        String rediskey = "entity:redpacket:"+activityRedPackManage.getId();
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        operations.increment(rediskey, 1);
        Object o =  redisTemplate.opsForValue().get(rediskey);
        int currentCnt = (int)o;
        //redisTemplate.getConnectionFactory().getConnection().incrBy(redisTemplate.getKeySerializer().serialize(rediskey), 1);
        if((currentCnt%activityRedPackManage.getTriggerEventCount()) ==0){
            BigDecimal redpacket = getService().genRedPacket(activityRedPackManage.getMinAmount(),activityRedPackManage.getMaxAmount(),activityRedPackManage.getSurplusAmount());
            if (redpacket.compareTo(BigDecimal.ZERO)<=0) {
                return Boolean.TRUE;
            }
            try{
                log.info("当前次数{}",currentCnt);
                return getService().mineReward(memberId,activityRedPackManage,redpacket,inviteeId);
            }catch (Exception e){
                e.printStackTrace();
                log.error("mine reward red pack Activities error redPacketId：{}，redPacketName：{} ,memberId:{} ",activityRedPackManage.getId(),activityRedPackManage.getRedpackName(),memberId);
            }
            return Boolean.FALSE;
        }else{
            //累计当前参与次数
            //更新活动
            Boolean reduceActivity = activityRedPackManageService.lambdaUpdate()
                    .setSql("trigger_event_current = trigger_event_current +" + 1)
                    .eq(ActivityRedPackManage::getId, activityRedPackManage.getId())
                    .eq(ActivityRedPackManage::getDeleteFlag, 0).update();
            if(reduceActivity){
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }


    }

    /**
     * 查询当前可以参与的活动列表,只查询随机金额,并且剩余金额大于零
     */
    @Override
    public List<ActivityRedPackManage> getActivityRedPackManage(Short triggerEvent) {
        Date now = new Date();
        QueryWrapper<ActivityRedPackManage> query = new QueryWrapper<>();
        query.lambda().eq(ActivityRedPackManage::getTriggerEvent,triggerEvent).eq(ActivityRedPackManage::getDeleteFlag,0)
                .eq(ActivityRedPackManage::getUnit,"BT").eq(ActivityRedPackManage::getReceiveType,1)
                .lt(ActivityRedPackManage::getStartTime,now).ge(ActivityRedPackManage::getEndTime,now)
                .ge(ActivityRedPackManage::getSurplusAmount,0);
        return activityRedPackManageService.list(query);
    }
    /**
     * 查询当前可以参与的活动列表,只查询随机金额,并且剩余金额大于零
     */
    @Override
    public ActivityRedPackManage getOneActivityRedPackManage(Short triggerEvent) {
        Date now = new Date();
        QueryWrapper<ActivityRedPackManage> query = new QueryWrapper<>();
        query.lambda().eq(ActivityRedPackManage::getTriggerEvent,triggerEvent).eq(ActivityRedPackManage::getDeleteFlag,0)
                .eq(ActivityRedPackManage::getUnit,"BT").eq(ActivityRedPackManage::getReceiveType,1)
                .lt(ActivityRedPackManage::getStartTime,now).ge(ActivityRedPackManage::getEndTime,now)
                .ge(ActivityRedPackManage::getSurplusAmount,0).orderByDesc(ActivityRedPackManage::getCreateTime);
        List<ActivityRedPackManage> list = activityRedPackManageService.list(query);
        if(list!=null&&list.size()>0){
            return list.get(0);
        }
        return null;
    }

    @Override
    public Boolean processRecommendRedPack(Member member) {
        log.info("处理推荐有效矿工逻辑 memberId {},inviterId{}",member.getId(),member.getInviterId());
        //被推荐人成为有效矿工，邀请者发放红包
        return getService().processRedPack(member.getInviterId(),(short)1,member.getId());
    }

    @Override
    public ActivityRedPackReceiveRecord getRedPack(Member member, int triggerEvent) {
        QueryWrapper<ActivityRedPackReceiveRecord> query = new QueryWrapper<ActivityRedPackReceiveRecord>();
        query.lambda().eq(ActivityRedPackReceiveRecord::getMemberId,member.getId()).eq(ActivityRedPackReceiveRecord::getTriggerEvent,triggerEvent).eq(ActivityRedPackReceiveRecord::getReceiveStatus,1);
        List<ActivityRedPackReceiveRecord> list = activityRedPackReceiveRecordService.list(query);
        if(list!=null&&list.size()>0){
            return list.get(0);
        }
        return null;
    }

    @Override
    public Boolean ackRedPack(Long recordId) {
        Boolean updateReCord = activityRedPackReceiveRecordService.lambdaUpdate()
                .setSql("receive_status = " + 3)
                .eq(ActivityRedPackReceiveRecord::getId, recordId).update();
        if(updateReCord){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public List<ActivityRedPackManage> getRealseLockAmountActivity() {
        Date now = new Date();
        QueryWrapper<ActivityRedPackManage> query = new QueryWrapper<>();
        query.lambda().eq(ActivityRedPackManage::getDeleteFlag,0)
                .le(ActivityRedPackManage::getEndTime,now)
                .ge(ActivityRedPackManage::getSurplusAmount,0).orderByDesc(ActivityRedPackManage::getCreateTime);
        return  activityRedPackManageService.list(query);
    }

    @Override
    public Boolean realseLockAmount(ActivityRedPackManage activityRedPackManage) {
        //查询系统字典
        SilkDataDist silkDataDist = silkDataDistService.findOne("RED_PACK_CONFIG","TOTAL_ACCOUNT_ID");
        if(silkDataDist==null){
            log.error("红包支付账户未配置,请联系管理员");
            return Boolean.FALSE;
        }
        Long totalAccountId = Long.valueOf(silkDataDist.getDictVal());
        if(activityRedPackManage.getSurplusAmount().compareTo(BigDecimal.ZERO)==1){
            //系统账户从冻结到可用
            WalletChangeRecord record = memberWalletService.realseFreeze(
                    TransactionType.ACTIVITY_AWARD,
                    totalAccountId, "BT", "BT", activityRedPackManage.getSurplusAmount(),  activityRedPackManage.getId(),
                    "系统红包账户释放剩余锁仓");
            if (record == null) {
                log.error("系统红包账户释放剩余锁仓 txId = {}, member_id = {}, amount = {}", activityRedPackManage.getId(), totalAccountId,activityRedPackManage.getSurplusAmount());
                throw new BtBankException(BtBankMsgCode.PRIZE_QUIZE_TRANFERIN_ERROR);
            }
            try{
                Boolean updateAmount = activityRedPackManageService.lambdaUpdate()
                        .setSql("surplus_amount = " + 0)
                        .eq(ActivityRedPackManage::getId, activityRedPackManage.getId())
                        .eq(ActivityRedPackManage::getDeleteFlag, 0).ge(ActivityRedPackManage::getSurplusAmount,0).update();
                if (!updateAmount) {
                    log.error("reduce surplus_amount fail:redPacketId {},redpacketAmount {}", activityRedPackManage.getId(), activityRedPackManage.getSurplusAmount());
                    throw new BtBankException(BtBankMsgCode.UPDATE_ACTIVITY_FAILED);
                }
                boolean b =  memberWalletService.confirmTrade(totalAccountId,record.getId());
                if (!b) {
                    log.error("确认账户变动失败 record = {}", record);
                    throw new BtBankException(CommonMsgCode.FAILURE);
                } else {
                    return Boolean.TRUE;
                }
            }catch (Exception e){
                e.printStackTrace();
                memberWalletService.confirmTrade(totalAccountId,record.getId());
                log.error( "系统红包账户释放剩余锁仓失败 txId = {}, member_id = {}, amount = {}", activityRedPackManage.getId(), totalAccountId,activityRedPackManage.getSurplusAmount());
                throw e;
            }
        }
        return Boolean.FALSE;
    }

    @Override
    public BigDecimal genRedPacket(BigDecimal min, BigDecimal max, BigDecimal surplusAmount) {
        if(max.compareTo(surplusAmount)==1){
            return surplusAmount ;
        }else if(min.compareTo(max)==0){
            return max ;
        }else{
            //最小金额和最大金额之间生成一个随机数
            BigDecimal redpacket = new BigDecimal(Math.random()).setScale(2,BigDecimal.ROUND_DOWN);
            redpacket = redpacket.multiply(max.add(min.negate())).setScale(2,BigDecimal.ROUND_DOWN);
            redpacket = redpacket.add(min.abs());
            return redpacket;
        }
    }

    @Override
    public Boolean alreadyGotRedpack(Long memberId, Long redPackId) {
        QueryWrapper<ActivityRedPackReceiveRecord> query = new QueryWrapper<ActivityRedPackReceiveRecord>();
        query.lambda().eq(ActivityRedPackReceiveRecord::getMemberId,memberId).eq(ActivityRedPackReceiveRecord::getRedpackId,redPackId);
        return activityRedPackReceiveRecordService.count(query)>0?true:false;
    }


    @Override
    @Transactional
    public Boolean mineReward(Long memberId, ActivityRedPackManage activityRedPackManage,BigDecimal redpacket,Long inviteeId) {
        //查询系统字典
        SilkDataDist silkDataDist = silkDataDistService.findOne("RED_PACK_CONFIG","TOTAL_ACCOUNT_ID");
        if(silkDataDist==null){
            log.error("红包支付账户未配置,请联系管理员");
            return Boolean.FALSE;
        }
        //更新活动
        Boolean reduceActivity = activityRedPackManageService.lambdaUpdate()
                .setSql("surplus_amount = surplus_amount -" + redpacket)
                .setSql("trigger_event_current = trigger_event_current +" + 1)
                .eq(ActivityRedPackManage::getId, activityRedPackManage.getId())
                .eq(ActivityRedPackManage::getDeleteFlag, 0).ge(ActivityRedPackManage::getSurplusAmount,0).update();
        if (!reduceActivity) {
            log.error("reduce surplus_amount fail:redPacketId {},redpacketAmount {}", activityRedPackManage.getId(), redpacket);
            throw new BtBankException(BtBankMsgCode.UPDATE_ACTIVITY_FAILED);
        }
        Long totalAccountId = Long.valueOf(silkDataDist.getDictVal());
        MemberWalletService.TradePlan plan = new MemberWalletService.TradePlan();
        Date now = new Date();
        ActivityRedPackReceiveRecord activityRedPackReceiveRecord = new ActivityRedPackReceiveRecord();
        activityRedPackReceiveRecord.setUpdateTime(now);
        activityRedPackReceiveRecord.setRedpackId( activityRedPackManage.getId());
        activityRedPackReceiveRecord.setMemberId(memberId);
        activityRedPackReceiveRecord.setCreateTime(now);
        activityRedPackReceiveRecord.setReceiveUnit(activityRedPackManage.getUnit());
        activityRedPackReceiveRecord.setReceiveAmount(redpacket);
        activityRedPackReceiveRecord.setReceiveStatus((short)1);
        activityRedPackReceiveRecord.setId(IdWorker.getId());
        activityRedPackReceiveRecord.setTriggerEvent(activityRedPackManage.getTriggerEvent());
        activityRedPackReceiveRecord.setRedpackName(activityRedPackManage.getRedpackName());
        activityRedPackReceiveRecord.setReceiveTime(now);
        if(inviteeId !=null ){
            activityRedPackReceiveRecord.setSubMemberId(inviteeId);
        }
        //总账户扣款
        try{
            String comment = null;
            if(activityRedPackManage.getTriggerEvent()==0){
                comment = "抢单挖矿红包";
            }else{
                comment ="推荐有效矿工红包";
            }
            //先释放
            WalletChangeRecord realseRecord = memberWalletService.realseFreeze(
                    TransactionType.ACTIVITY_AWARD,
                    totalAccountId, "BT", "BT", redpacket,  activityRedPackReceiveRecord.getId(),
                    comment+"系统账户释放");
            if (realseRecord == null) {
                log.error(comment + "系统账户释放 txId = {}, member_id = {}, amount = {}", activityRedPackReceiveRecord.getId(), totalAccountId,redpacket);
                throw new BtBankException(BtBankMsgCode.PRIZE_QUIZE_TRANFERIN_ERROR);
            }
            Boolean realseRecordConfirm = memberWalletService.confirmTrade(totalAccountId,realseRecord.getId());
            if(!realseRecordConfirm){
                log.error(comment + "系统账户释放确认失败 txId = {}, member_id = {}, amount = {}", activityRedPackReceiveRecord.getId(), totalAccountId,redpacket);
                throw new BtBankException(BtBankMsgCode.PRIZE_QUIZE_TRANFERIN_ERROR);
            }
            WalletChangeRecord record = memberWalletService.tryTrade(
                    TransactionType.ACTIVITY_AWARD,
                    totalAccountId, "BT", "BT", redpacket.abs().negate(),  activityRedPackReceiveRecord.getId(),
                    comment+"系统账户扣款");

            if (record == null) {
                log.error(comment + "系统账户扣款 txId = {}, member_id = {}, amount = {}", activityRedPackReceiveRecord.getId(), totalAccountId,redpacket);
                throw new BtBankException(BtBankMsgCode.PRIZE_QUIZE_TRANFERIN_ERROR);
            }
            plan.getQueue().add(record);
            WalletChangeRecord rewardRecord = null;
            //发放奖励
            if(activityRedPackManage.getReleaseType() ==1){
                //直接发放
                rewardRecord = memberWalletService.tryTrade(
                        TransactionType.ACTIVITY_AWARD,
                        memberId, "BT", "BT", redpacket.abs(),   activityRedPackReceiveRecord.getId(),
                        comment);
                if (rewardRecord == null) {
                    log.error(comment + "用户加款 txId = {}, member_id = {}, amount = {}", activityRedPackReceiveRecord.getId(), memberId,redpacket);
                    throw new BtBankException(BtBankMsgCode.PRIZE_QUIZE_TRANFERIN_ERROR);
                }
                plan.getQueue().add(rewardRecord);
            }else{
                //锁仓发放
                rewardRecord = memberWalletService.rewardFreeze(TransactionType.ACTIVITY_AWARD,
                        memberId, "BT", "BT", redpacket.abs(),   activityRedPackReceiveRecord.getId(),
                        comment);
                if (rewardRecord == null) {
                    log.error(comment + "用户锁仓 txId = {}, member_id = {}, amount = {}", activityRedPackReceiveRecord.getId(), memberId,redpacket);
                    throw new BtBankException(BtBankMsgCode.PRIZE_QUIZE_TRANFERIN_ERROR);
                }
                plan.getQueue().add(rewardRecord);
            }

            if(activityRedPackReceiveRecordService.save(activityRedPackReceiveRecord)){
                boolean b =  memberWalletService.confirmPlan(plan);
                if (!b) {
                    log.error("确认发放挖矿奖励红包失败  plan = {}", plan);
                    throw new BtBankException(CommonMsgCode.FAILURE);
                } else {
                    log.info("发送红包成功  memberId = {},redpacket {}", memberId,redpacket);
                    return true;
                }
            }else{
                log.error("保存领取红包记录失败 member_id = {}, amount = {}", memberId,redpacket);
                throw new BtBankException(BtBankMsgCode.PRIZE_QUIZE_TRANFERIN_ERROR);
            }
        }catch(Exception e){
            e.printStackTrace();
            if (log.isInfoEnabled() && plan.getQueue().size() > 0) {
                log.info("红包发送失败，执行远程回滚. 回滚总数({}) 计划:", plan.getQueue().size());
                plan.getQueue().forEach(y -> {
                    log.info("memberId({}) amount({}) type({})", y.getMemberId(), y.getTradeBalance(), y.getType());
                });
            }
            e.printStackTrace();

            memberWalletService.rollbackPlan(plan);
        }
        throw new BtBankException(BtBankMsgCode.PRIZE_QUIZE_TRANFERIN_ERROR);
    }
    public  ActivityRedpacketServiceImpl getService() {
        return SpringContextUtil.getBean(ActivityRedpacketServiceImpl.class);
    }
}
