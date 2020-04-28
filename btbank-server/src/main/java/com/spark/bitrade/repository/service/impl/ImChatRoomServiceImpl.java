package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.mapper.ImChatRoomMapper;
import com.spark.bitrade.repository.entity.ImChatRoom;
import com.spark.bitrade.repository.service.ImChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * (ImChatRoom)表服务实现类
 *
 * @author yangch
 * @since 2020-01-20 14:51:20
 */
@Service("imChatRoomService")
public class ImChatRoomServiceImpl extends ServiceImpl<ImChatRoomMapper, ImChatRoom> implements ImChatRoomService {
    @Autowired
    private ImChatRoomMapper imChatRoomMapper;
    @Override
    public ImChatRoom getChatRoom() {
        return imChatRoomMapper.getChatRoom();
    }
}