package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.api.vo.MemberScoreListVo;
import com.spark.bitrade.repository.entity.BtBankMemberPendingWard;
import com.spark.bitrade.repository.mapper.BtBankMemberPendingWardMapper;
import com.spark.bitrade.repository.service.BtBankMemberPendingWardService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 用户直推待领取奖励记录 服务实现类
 * </p>
 *
 * @author qhliao
 * @since 2020-03-18
 */
@Service
public class BtBankMemberPendingWardServiceImpl extends ServiceImpl<BtBankMemberPendingWardMapper, BtBankMemberPendingWard> implements BtBankMemberPendingWardService {

    @Override
    public IPage<MemberScoreListVo> pageList(int current, int size,Long memberId) {
        IPage<MemberScoreListVo> page=new Page<>(current,size);
        List<MemberScoreListVo> list=baseMapper.pageList(page,memberId);
        page.setRecords(list);
        return page;
    }
}
