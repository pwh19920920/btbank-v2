package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.repository.entity.ImChatRoom;

/**
 * (ImChatRoom)表服务接口
 *
 * @author yangch
 * @since 2020-01-20 14:51:20
 */
public interface ImChatRoomService extends IService<ImChatRoom> {

    ImChatRoom  getChatRoom();
}