package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.api.dto.ActivitiesDTO;
import com.spark.bitrade.repository.entity.TurntableActivities;

/**
 * 活动表(TurntableActivities)表服务接口
 *
 * @author biu
 * @since 2020-01-08 13:56:07
 */
public interface TurntableActivitiesService extends IService<TurntableActivities> {

    TurntableActivities getInProgressOrLatestActivities();

    ActivitiesDTO findById(Integer id);

}