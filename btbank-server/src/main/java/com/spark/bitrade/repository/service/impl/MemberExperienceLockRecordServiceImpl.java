package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.entity.MemberExperienceLockRecord;
import com.spark.bitrade.repository.mapper.MemberExperienceLockRecordMapper;
import com.spark.bitrade.repository.service.MemberExperienceLockRecordService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * <p>
 * 3月8号体验金锁仓记录 服务实现类
 * </p>
 *
 * @author qhliao
 * @since 2020-03-06
 */
@Service
public class MemberExperienceLockRecordServiceImpl extends ServiceImpl<MemberExperienceLockRecordMapper, MemberExperienceLockRecord> implements MemberExperienceLockRecordService {

    @Override
    public boolean lockRecordExistByMemberId(Long memberId) {
        return baseMapper.findByMemberId(memberId).isPresent();
    }

    @Override
    public MemberExperienceLockRecord findByMemberId(Long memberId) {
        Optional<MemberExperienceLockRecord> optional = this.baseMapper.findByMemberId(memberId);
        if(optional.isPresent()){
            return optional.get();
        }
        return null;
    }

}
