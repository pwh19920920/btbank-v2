package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.ImMember;
import com.spark.bitrade.repository.entity.MemberVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * (ImMember)表数据库访问层
 *
 * @author yangch
 * @since 2020-01-19 14:47:02
 */
@Mapper
public interface ImMemberMapper extends BaseMapper<ImMember> {

    public List<MemberVo> getUnRegistImMiner();
    public List<ImMember> getUnJoinGoroupUser();
    public List<ImMember> getUnJoinChatRoomUser();
    public List<MemberVo> getUnRegistImkfMiner(List<ImMember> imMemberList);

    List<MemberVo> getUnRegistImkfallMiner();
}