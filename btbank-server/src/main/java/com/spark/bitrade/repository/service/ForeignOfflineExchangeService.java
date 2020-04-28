package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.repository.entity.FinancialActivityJoinDetails;
import com.spark.bitrade.repository.entity.ForeignOfflineExchange;

import java.util.List;

/**
 * 换汇线下订单(ForeignOfflineExchange)表服务接口
 *
 * @author mahao
 * @since 2020-02-04 11:48:53
 */
public interface ForeignOfflineExchangeService extends IService<ForeignOfflineExchange> {

    IPage<ForeignOfflineExchange> orderlist(Member member, Integer current, Integer size);

    boolean refound(ForeignOfflineExchange foreignOfflineExchange);

    /**
     * 查找所有未与归集账户完成的订单
     * @return
     */
    List<ForeignOfflineExchange> orderListByCompleteStatus();

    /**
     * 查找所有用户取消的线下订单
     * @return
     */
    List<ForeignOfflineExchange> orderListByCancle();
}