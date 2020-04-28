package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.ImChatRoomMember;
import org.apache.ibatis.annotations.Mapper;

/**
 * (ImChatRoomMember)表数据库访问层
 *
 * @author yangch
 * @since 2020-01-20 14:51:20
 */
@Mapper
public interface ImChatRoomMemberMapper extends BaseMapper<ImChatRoomMember> {

}