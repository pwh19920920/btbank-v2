package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.constant.BtBankPrizeQuizConfig;
import com.spark.bitrade.constant.BtBankSystemConfig;
import com.spark.bitrade.repository.entity.BtBankDataDict;
import com.spark.bitrade.repository.mapper.GuessingConfigDataDictMapper;
import com.spark.bitrade.repository.entity.GuessingConfigDataDict;
import com.spark.bitrade.repository.service.GuessingConfigDataDictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 竞猜活动规则配置(GuessingConfigDataDict)表服务实现类
 *
 * @author daring5920
 * @since 2020-01-02 10:30:50
 */
@Service("guessingConfigDataDictService")
@Slf4j
public class GuessingConfigDataDictServiceImpl extends ServiceImpl<GuessingConfigDataDictMapper, GuessingConfigDataDict> implements GuessingConfigDataDictService {



    @Override
    public GuessingConfigDataDict findFirstByDictIdAndDictKey(String dictId, String dictKey) {
        return baseMapper.findFirstByDictIdAndDictKey(dictId, dictKey);
    }
}