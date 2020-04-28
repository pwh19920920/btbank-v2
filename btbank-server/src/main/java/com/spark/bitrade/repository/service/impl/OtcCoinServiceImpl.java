package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.mapper.OtcCoinMapper;
import com.spark.bitrade.repository.entity.OtcCoin;
import com.spark.bitrade.repository.service.OtcCoinService;
import org.springframework.stereotype.Service;

/**
 * (OtcCoin)表服务实现类
 *
 * @author daring5920
 * @since 2019-11-29 15:55:58
 */
@Service("otcCoinService")
public class OtcCoinServiceImpl extends ServiceImpl<OtcCoinMapper, OtcCoin> implements OtcCoinService {

}