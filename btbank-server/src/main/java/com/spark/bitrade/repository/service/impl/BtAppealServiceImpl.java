package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.entity.BtAppeal;
import com.spark.bitrade.repository.mapper.BtAppealMapper;
import com.spark.bitrade.repository.service.BtAppealService;
import org.springframework.stereotype.Service;

/**
 * BtAppealServiceImpl
 *
 * @author biu
 * @since 2019/12/2 9:48
 */
@Service
public class BtAppealServiceImpl extends ServiceImpl<BtAppealMapper, BtAppeal> implements BtAppealService {
}
