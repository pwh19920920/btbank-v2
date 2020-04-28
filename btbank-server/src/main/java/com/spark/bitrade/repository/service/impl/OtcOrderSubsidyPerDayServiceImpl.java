package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.mapper.OtcOrderSubsidyPerDayMapper;
import com.spark.bitrade.repository.entity.OtcOrderSubsidyPerDay;
import com.spark.bitrade.repository.service.OtcOrderSubsidyPerDayService;
import org.springframework.stereotype.Service;

/**
 * 用户补贴记录表(OtcOrderSubsidyPerDay)表服务实现类
 *
 * @author daring5920
 * @since 2019-12-02 11:58:36
 */
@Service("otcOrderSubsidyPerDayService")
public class OtcOrderSubsidyPerDayServiceImpl extends ServiceImpl<OtcOrderSubsidyPerDayMapper, OtcOrderSubsidyPerDay> implements OtcOrderSubsidyPerDayService {

}