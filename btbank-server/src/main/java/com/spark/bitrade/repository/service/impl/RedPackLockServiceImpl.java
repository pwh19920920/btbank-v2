package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.mapper.RedPackLockMapper;
import com.spark.bitrade.repository.entity.RedPackLock;
import com.spark.bitrade.repository.service.RedPackLockService;
import org.springframework.stereotype.Service;

/**
 * 红包锁仓表(RedPackLock)表服务实现类
 *
 * @author daring5920
 * @since 2019-12-08 10:44:37
 */
@Service("redPackLockService")
public class RedPackLockServiceImpl extends ServiceImpl<RedPackLockMapper, RedPackLock> implements RedPackLockService {

}