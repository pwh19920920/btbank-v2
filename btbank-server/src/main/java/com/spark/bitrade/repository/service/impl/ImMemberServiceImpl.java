package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.entity.ImMember;
import com.spark.bitrade.repository.entity.MemberVo;
import com.spark.bitrade.repository.mapper.ImMemberMapper;
import com.spark.bitrade.repository.service.ImMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * (ImMember)表服务实现类
 *
 * @author yangch
 * @since 2020-01-19 14:47:02
 */
@Service("imMemberService")
public class ImMemberServiceImpl extends ServiceImpl<ImMemberMapper, ImMember> implements ImMemberService {
    @Autowired
    private ImMemberMapper imMemberMapper;
    @Override
    public List<MemberVo> getUnRegistImMiner() {
        return imMemberMapper.getUnRegistImMiner();
    }

    @Override
    public ImMember getImMemberByMemberId(Long memberId) {
        QueryWrapper<ImMember> wrapper = new QueryWrapper<>();
        wrapper.eq("member_id", memberId);
        return  this.getOne(wrapper);
    }

    @Override
    public ImMember getImMemberByAcid(String acid) {
        QueryWrapper<ImMember> wrapper = new QueryWrapper<>();
        wrapper.eq("accid", acid);
        return  this.getOne(wrapper);
    }

    @Override
    public List<ImMember> getUnJoinChatRoomUser() {
        return imMemberMapper.getUnJoinChatRoomUser();
    }

    @Override
    public List<MemberVo> getUnRegistImKefuMiner() {
        QueryWrapper<ImMember> wrapper = new QueryWrapper<>();
        wrapper.eq("user_type", 2);
        List<ImMember> memberList = this.list(wrapper);
        if(memberList.size()>0){
            return imMemberMapper.getUnRegistImkfMiner(memberList);
        }
        return imMemberMapper.getUnRegistImkfallMiner();
    }
}