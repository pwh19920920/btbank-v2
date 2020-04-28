package com.spark.bitrade.biz;

import com.spark.bitrade.repository.entity.ActivityRedPackManage;
import com.spark.bitrade.repository.entity.ActivityRedPackReceiveRecord;
import com.spark.bitrade.entity.Member;
import java.math.BigDecimal;
import java.util.List;

public interface ActivityRedpacketService {
    /**
     * 处理抢单发放红包逻辑
     * @param memberId
     */
    public Boolean processGrabOrderRedPack(Long memberId);

    /**
     * 红包处理
     * @param memberId
     * @param triggerEvent
     * @param inviteeId
     */
    public Boolean processRedPack(Long memberId, Short triggerEvent,Long inviteeId);

    /**
     * 查询当前可以参与的活动列表
     */
    public List<ActivityRedPackManage> getActivityRedPackManage(Short triggerEvent);
    /**
     * 生成红包金额，如果最后一个红包小于max，最后一个红包数量剩余金额
     *  @param min
     *  @param max
     *  @param surplusAmount
     */
    public BigDecimal genRedPacket(BigDecimal min,BigDecimal max, BigDecimal surplusAmount);

    /**
     * 查询是否已经领取奖励
     *  @param memberId
     *  @param redPackId
     */
    public Boolean alreadyGotRedpack(Long memberId,Long redPackId);

    /**
     * 查询是否已经领取奖励
     *  @param memberId
     *  @param activityRedPackManage
     *  @param redPackAmount
     */
    public Boolean mineReward(Long memberId,ActivityRedPackManage activityRedPackManage,BigDecimal redPackAmount,Long inviteeId);
    /**
     * 根据类型获得活动
     *  @param triggerEvent
     */
    public ActivityRedPackManage getOneActivityRedPackManage(Short triggerEvent) ;
    /**
     * 处理推荐人红包
     *  @param member
     */
    public Boolean  processRecommendRedPack(Member member);
    /**
     * 红包具体处理逻辑
     *  @param member
     *  @param triggerEvent
     */
    ActivityRedPackReceiveRecord getRedPack(Member member, int triggerEvent);
    /**
     * 确认红包
     *  @param recordId
     */
    Boolean ackRedPack(Long recordId);

    /**
     * 查询已解锁，还有剩余金额的活动将剩余金额释放到可用余额
     */
    public List<ActivityRedPackManage> getRealseLockAmountActivity();

    Boolean realseLockAmount(ActivityRedPackManage activityRedPackManage);
}
