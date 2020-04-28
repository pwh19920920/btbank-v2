package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.api.vo.MemberScoreListVo;
import com.spark.bitrade.repository.entity.BtBankMemberPendingWard;

/**
 * <p>
 * 用户直推待领取奖励记录 服务类
 * </p>
 *
 * @author qhliao
 * @since 2020-03-18
 */
public interface BtBankMemberPendingWardService extends IService<BtBankMemberPendingWard> {

    IPage<MemberScoreListVo> pageList(int current,int size,Long memberId);
}
