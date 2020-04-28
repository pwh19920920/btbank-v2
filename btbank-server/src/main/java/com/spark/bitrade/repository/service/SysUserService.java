package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.repository.entity.ImMember;
import com.spark.bitrade.repository.entity.SysUser;

import java.util.List;

/**
 * 操作员表(SysUser)表服务接口
 *
 * @author yangch
 * @since 2020-01-19 17:56:46
 */
public interface SysUserService extends IService<SysUser> {
    List<SysUser> getUnJoinChatRoomUser();
    List<SysUser> getUnSyncUser(List<ImMember> memberList);
}