package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.mapper.ForeignCashLocationMapper;
import com.spark.bitrade.repository.entity.ForeignCashLocation;
import com.spark.bitrade.repository.service.ForeignCashLocationService;
import org.springframework.stereotype.Service;

/**
 * 外汇线下换汇地址(ForeignCashLocation)表服务实现类
 *
 * @author yangch
 * @since 2020-02-04 11:35:35
 */
@Service("foreignCashLocationService")
public class ForeignCashLocationServiceImpl extends ServiceImpl<ForeignCashLocationMapper, ForeignCashLocation> implements ForeignCashLocationService {

}