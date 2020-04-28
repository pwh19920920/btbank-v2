package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.entity.TurntableInvolvedRecord;
import com.spark.bitrade.repository.mapper.TurntableInvolvedRecordMapper;
import com.spark.bitrade.repository.service.TurntableInvolvedRecordService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 参与记录详情表(TurntableInvolvedRecord)表服务实现类
 *
 * @author biu
 * @since 2020-01-08 17:25:48
 */
@Service("turntableInvolvedRecordService")
public class TurntableInvolvedRecordServiceImpl extends ServiceImpl<TurntableInvolvedRecordMapper, TurntableInvolvedRecord> implements TurntableInvolvedRecordService {

    @Override
    public boolean overTopLimit(Long memberId, Integer prizeId, Integer limit) {
        QueryWrapper<TurntableInvolvedRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("member_id", memberId).eq("prize_id", prizeId);
        return count(wrapper) >= limit;
    }

    @Override
    public boolean record(Integer actId, Long memberId, Integer prizeId) {
        TurntableInvolvedRecord record = new TurntableInvolvedRecord();
        record.setActId(actId);
        record.setMemberId(memberId);
        record.setPrizeId(prizeId);
        record.setCreateTime(new Date());
        return save(record);
    }
}