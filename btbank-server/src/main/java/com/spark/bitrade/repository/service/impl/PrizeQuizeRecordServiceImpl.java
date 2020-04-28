package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.mapper.PrizeQuizeRecordMapper;
import com.spark.bitrade.repository.entity.PrizeQuizeRecord;
import com.spark.bitrade.repository.service.PrizeQuizeRecordService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 往期竞猜记录(PrizeQuizeRecord)表服务实现类
 *
 * @author daring5920
 * @since 2020-01-02 09:58:28
 */
@Service("prizeQuizeRecordService")
public class PrizeQuizeRecordServiceImpl extends ServiceImpl<PrizeQuizeRecordMapper, PrizeQuizeRecord> implements PrizeQuizeRecordService {

    @Override
    public boolean updateMaxReward(PrizeQuizeRecord prizeQuizeRecord) {
        UpdateWrapper<PrizeQuizeRecord> update = new UpdateWrapper<>();
        Date now = new Date();
        // 更新最大金额
        update.eq("id", prizeQuizeRecord.getId())
                .set("max_reward", prizeQuizeRecord.getMaxReward())
                .set("max_reward_member_id",prizeQuizeRecord.getMaxRewardMemberId())
                .set("update_time", now);
        return update(update);
    }

    @Override
    public boolean updateRewardRealseTime(PrizeQuizeRecord prizeQuizeRecord) {
        UpdateWrapper<PrizeQuizeRecord> update = new UpdateWrapper<>();
        Date now = new Date();
        // 更新最大金额
        update.eq("id", prizeQuizeRecord.getId())
                .set("reward_release_time", now);
        return update(update);
    }
}