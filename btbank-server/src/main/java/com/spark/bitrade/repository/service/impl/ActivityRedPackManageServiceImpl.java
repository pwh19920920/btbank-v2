package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.mapper.ActivityRedPackManageMapper;
import com.spark.bitrade.repository.entity.ActivityRedPackManage;
import com.spark.bitrade.repository.service.ActivityRedPackManageService;
import org.springframework.stereotype.Service;

/**
 * (ActivityRedPackManage)表服务实现类
 *
 * @author yangch
 * @since 2020-01-13 10:39:07
 */
@Service("activityRedPackManageService")
public class ActivityRedPackManageServiceImpl extends ServiceImpl<ActivityRedPackManageMapper, ActivityRedPackManage> implements ActivityRedPackManageService {

}