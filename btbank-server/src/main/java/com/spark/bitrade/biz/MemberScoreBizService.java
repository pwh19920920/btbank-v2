package com.spark.bitrade.biz;

import com.spark.bitrade.api.dto.MemberRateDto;
import com.spark.bitrade.repository.entity.BtBankMemberPendingWard;

import java.math.BigDecimal;
import java.util.Date;

public interface MemberScoreBizService {

    /**
     * 增加积分 并产生积分流水
     * @param memberId
     * @param score
     * @param type
     * @return
     */
    void increaseScore(Long memberId, BigDecimal score,int type);

    boolean addPendingRecord(Long memberId,BigDecimal amount,Long childId,int type,Long txId,String comment);

    /**
     * 获取返佣比例4月1日 0:00后推荐注册的有效矿工，可获得其每笔挖矿收益：前30天100%，第二个30天50%，然后恢复10%的奖励（大宗挖矿的直推奖励也按这个比例）
     * @param registerTime
     * @return
     */
    MemberRateDto aprilOneRate(Date registerTime);
    /**
     * 领取积分
     */
    void doReceive(Long memberId, BtBankMemberPendingWard ward);
}
