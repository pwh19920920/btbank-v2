package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.repository.entity.MemberExperienceWallet;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 新用户3月8之后体验金账户 服务类
 * </p>
 *
 * @author qhliao
 * @since 2020-03-06
 */
public interface MemberExperienceWalletService extends IService<MemberExperienceWallet> {

    MemberExperienceWallet findByMemberIdAndCoinId(Long memberId,String coinId);

    /**
     * 用户累计收益达到600BT 释放
     * @param memberId  会员ID
     * @param sumProfit 累计收益
     */
    Boolean deductMemberExperienceWallet(Long memberId, BigDecimal sumProfit);


    int increaseLockBalance(Long walletId, BigDecimal amount);

    int decreaseLockBalance(Long walletId,BigDecimal amount);

    List<Member> findByRegisterTime(Date date);

    List<Member> findByRegisterTimeAndInviter(Date date);
}
