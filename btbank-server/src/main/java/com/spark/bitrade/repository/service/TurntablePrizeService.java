package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.repository.entity.TurntablePrize;

import java.util.List;

/**
 * 奖品表(TurntablePrize)表服务接口
 *
 * @author biu
 * @since 2020-01-08 13:56:37
 */
public interface TurntablePrizeService extends IService<TurntablePrize> {

    List<TurntablePrize> getPrizes(Integer actId);

    boolean decrement(Integer prizeId);
}