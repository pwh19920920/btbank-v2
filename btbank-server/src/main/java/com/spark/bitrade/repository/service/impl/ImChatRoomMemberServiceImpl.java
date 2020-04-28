package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.mapper.ImChatRoomMemberMapper;
import com.spark.bitrade.repository.entity.ImChatRoomMember;
import com.spark.bitrade.repository.service.ImChatRoomMemberService;
import org.springframework.stereotype.Service;

/**
 * (ImChatRoomMember)表服务实现类
 *
 * @author yangch
 * @since 2020-01-20 14:51:20
 */
@Service("imChatRoomMemberService")
public class ImChatRoomMemberServiceImpl extends ServiceImpl<ImChatRoomMemberMapper, ImChatRoomMember> implements ImChatRoomMemberService {

}