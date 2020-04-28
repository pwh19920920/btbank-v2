package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.mapper.ActivityRedPackReceiveRecordMapper;
import com.spark.bitrade.repository.entity.ActivityRedPackReceiveRecord;
import com.spark.bitrade.repository.service.ActivityRedPackReceiveRecordService;
import org.springframework.stereotype.Service;

/**
 * (ActivityRedPackReceiveRecord)表服务实现类
 *
 * @author yangch
 * @since 2020-01-13 10:44:03
 */
@Service("activityRedPackReceiveRecordService")
public class ActivityRedPackReceiveRecordServiceImpl extends ServiceImpl<ActivityRedPackReceiveRecordMapper, ActivityRedPackReceiveRecord> implements ActivityRedPackReceiveRecordService {

}