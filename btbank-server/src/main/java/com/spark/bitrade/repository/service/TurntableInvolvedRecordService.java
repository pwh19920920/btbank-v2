package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.repository.entity.TurntableInvolvedRecord;

/**
 * 参与记录详情表(TurntableInvolvedRecord)表服务接口
 *
 * @author biu
 * @since 2020-01-08 17:25:48
 */
public interface TurntableInvolvedRecordService extends IService<TurntableInvolvedRecord> {

    boolean overTopLimit(Long memberId, Integer prizeId, Integer limit);

    boolean record(Integer actId, Long memberId, Integer prizeId);
}