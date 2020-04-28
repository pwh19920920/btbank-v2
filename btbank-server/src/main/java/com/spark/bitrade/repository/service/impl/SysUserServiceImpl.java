package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.entity.ImMember;
import com.spark.bitrade.repository.entity.TurntableWinning;
import com.spark.bitrade.repository.mapper.SysUserMapper;
import com.spark.bitrade.repository.entity.SysUser;
import com.spark.bitrade.repository.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;

/**
 * 操作员表(SysUser)表服务实现类
 *
 * @author yangch
 * @since 2020-01-19 17:56:46
 */
@Service("sysUserService")
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;
    @Override
    public List<SysUser> getUnJoinChatRoomUser() {
        return sysUserMapper.getUnJoinChatRoomUser();
    }

    @Override
    public List<SysUser> getUnSyncUser(List<ImMember> memberList) {
        return sysUserMapper.getUnSyncUser(memberList);
    }
}