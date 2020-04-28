package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.repository.entity.ImMember;
import com.spark.bitrade.repository.entity.MemberVo;

import java.util.List;

/**
 * (ImMember)表服务接口
 *
 * @author yangch
 * @since 2020-01-19 14:47:02
 */
public interface ImMemberService extends IService<ImMember> {

    public List<MemberVo> getUnRegistImMiner();

    ImMember getImMemberByMemberId(Long memberId);

    ImMember getImMemberByAcid(String acid);
    List<ImMember> getUnJoinChatRoomUser();

    List<MemberVo> getUnRegistImKefuMiner();
}