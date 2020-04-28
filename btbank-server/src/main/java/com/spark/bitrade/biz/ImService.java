package com.spark.bitrade.biz;


import com.spark.bitrade.api.vo.ImResult;

import com.spark.bitrade.repository.entity.ImChatRoom;
import com.spark.bitrade.repository.entity.ImGroup;
import com.spark.bitrade.repository.entity.ImMember;
import com.spark.bitrade.repository.entity.MemberVo;

import java.util.List;

/**
 * @author mahao
 * @time 2020.1.19 12:59
 */

public interface ImService {

    public List<MemberVo> getUnRegistImMiner();
    public ImMember getImMemberByMemberId(Long memberId);
    public ImMember getImMemberByAcid(String acid);
    public ImMember syncImMember(MemberVo memberVo);
    public ImResult createGroup(ImGroup imGroup);
    public List<ImGroup> getAvailableGroup();
    public boolean addchatroom(ImChatRoom imChatRoom);
    public boolean addsyschatroom(ImChatRoom imChatRoom);

    MemberVo findInfo(Long id);

    List<MemberVo> getUnRegistImKefuMiner();
}
