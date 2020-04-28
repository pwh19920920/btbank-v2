package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.repository.entity.EnterpriseMiner;

import java.math.BigDecimal;

/**
 * 企业矿工表(EnterpriseMiner)表服务接口
 *
 * @author biu
 * @since 2019-12-23 17:15:02
 */
public interface EnterpriseMinerService extends IService<EnterpriseMiner> {

    EnterpriseMiner findByMemberId(Long memberId);

    boolean transfer(Long memberId, BigDecimal amount);

    boolean mining(Integer minerId, BigDecimal amount);

    boolean reward(Integer minerId, BigDecimal amount);
}