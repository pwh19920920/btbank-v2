package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.entity.EnterpriseMinerApplication;
import com.spark.bitrade.repository.mapper.EnterpriseMinerApplicationMapper;
import com.spark.bitrade.repository.service.EnterpriseMinerApplicationService;
import org.springframework.stereotype.Service;

/**
 * 企业矿工申请表(EnterpriseMinerApplication)表服务实现类
 *
 * @author biu
 * @since 2019-12-23 17:14:51
 */
@Service("enterpriseMinerApplicationService")
public class EnterpriseMinerApplicationServiceImpl extends ServiceImpl<EnterpriseMinerApplicationMapper, EnterpriseMinerApplication> implements EnterpriseMinerApplicationService {

    @Override
    public boolean hasApplication(Long memberId) {
        QueryWrapper<EnterpriseMinerApplication> query = new QueryWrapper<>();
        query.eq("member_id", memberId) .eq("status", 0);
        return count(query) > 0;
    }

    @Override
    public EnterpriseMinerApplication latestJoinApplication(Long memberId) {
        return baseMapper.latestJoinApplication(memberId);
    }

    @Override
    public EnterpriseMinerApplication latestApplication(Long memberId) {
        return baseMapper.latestApplication(memberId);
    }
}