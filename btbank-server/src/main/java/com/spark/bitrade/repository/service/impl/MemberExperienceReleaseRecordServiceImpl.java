package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.entity.MemberExperienceReleaseRecord;
import com.spark.bitrade.repository.mapper.MemberExperienceReleaseRecordMapper;
import com.spark.bitrade.repository.service.MemberExperienceReleaseRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 3月8号体验金释放记录 服务实现类
 * </p>
 *
 * @author qhliao
 * @since 2020-03-06
 */
@Service
public class MemberExperienceReleaseRecordServiceImpl extends ServiceImpl<MemberExperienceReleaseRecordMapper, MemberExperienceReleaseRecord> implements MemberExperienceReleaseRecordService {

    @Override
    public List<Long> findOldMemberHasReturn() {
        return baseMapper.findOldMemberHasReturn();
    }
}
