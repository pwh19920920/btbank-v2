package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.ImMember;
import com.spark.bitrade.repository.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 操作员表(SysUser)表数据库访问层
 *
 * @author yangch
 * @since 2020-01-19 17:56:46
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    public List<SysUser> getUnJoinChatRoomUser();

    public List<SysUser> getUnSyncUser(List<ImMember> memberList);
}