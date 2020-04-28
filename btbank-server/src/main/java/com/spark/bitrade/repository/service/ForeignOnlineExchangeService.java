package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.repository.entity.ForeignOnlineExchange;

import java.util.List;

/**
 * 换汇线上订单表(ForeignOnlineExchange)表服务接口
 *
 * @author yangch
 * @since 2020-02-04 11:49:34
 */
public interface ForeignOnlineExchangeService extends IService<ForeignOnlineExchange> {

    IPage<ForeignOnlineExchange> orderlist(Member member, Integer current, Integer size);

    /**
     * 查找所有未与归集账户完成的订单
     * @return
     */
    List<ForeignOnlineExchange> orderListByCompleteStatus();
}