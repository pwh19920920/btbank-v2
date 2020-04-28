package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.repository.entity.PrizeQuizeRecord;

/**
 * 往期竞猜记录(PrizeQuizeRecord)表服务接口
 *
 * @author daring5920
 * @since 2020-01-02 09:58:28
 */
public interface PrizeQuizeRecordService extends IService<PrizeQuizeRecord> {

    boolean updateMaxReward(PrizeQuizeRecord prizeQuizeRecord);

    boolean updateRewardRealseTime(PrizeQuizeRecord prizeQuizeRecord);
}