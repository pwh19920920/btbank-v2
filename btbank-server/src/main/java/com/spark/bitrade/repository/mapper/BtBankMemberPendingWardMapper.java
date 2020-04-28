package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.api.vo.MemberScoreListVo;
import com.spark.bitrade.repository.entity.BtBankMemberPendingWard;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户直推待领取奖励记录 Mapper 接口
 * </p>
 *
 * @author qhliao
 * @since 2020-03-18
 */
public interface BtBankMemberPendingWardMapper extends BaseMapper<BtBankMemberPendingWard> {

    List<MemberScoreListVo> pageList(@Param("page") IPage<MemberScoreListVo> page, @Param("memberId") Long memberId);

}
