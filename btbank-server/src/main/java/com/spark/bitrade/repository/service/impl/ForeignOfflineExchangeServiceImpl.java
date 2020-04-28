package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.repository.entity.FinancialActivityJoinDetails;
import com.spark.bitrade.repository.entity.MinerPrizeQuizeTransaction;
import com.spark.bitrade.repository.mapper.ForeignOfflineExchangeMapper;
import com.spark.bitrade.repository.entity.ForeignOfflineExchange;
import com.spark.bitrade.repository.service.ForeignOfflineExchangeService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 换汇线下订单(ForeignOfflineExchange)表服务实现类
 *
 * @author mahao
 * @since 2020-02-04 11:48:53
 */
@Service("foreignOfflineExchangeService")
public class ForeignOfflineExchangeServiceImpl extends ServiceImpl<ForeignOfflineExchangeMapper, ForeignOfflineExchange> implements ForeignOfflineExchangeService {

    @Override
    public IPage<ForeignOfflineExchange> orderlist(Member member, Integer current, Integer size) {
        IPage<ForeignOfflineExchange> page = new Page<>(current, size);
        QueryWrapper<ForeignOfflineExchange> query = new QueryWrapper<>();
        query.lambda().eq(ForeignOfflineExchange::getMemberId,member.getId()).orderByDesc(ForeignOfflineExchange::getCreateTime);

        return this.page(page,query);
    }

    @Override
    public boolean refound(ForeignOfflineExchange foreignOfflineExchange) {
        UpdateWrapper<ForeignOfflineExchange> update = new UpdateWrapper<>();
        Date now = new Date();
        update.lambda().eq(ForeignOfflineExchange::getId, foreignOfflineExchange.getId())
                .set(ForeignOfflineExchange::getOrderStatus, 3).set(ForeignOfflineExchange::getUpdateTime, now);

        return update(update);
    }

    @Override
    public List<ForeignOfflineExchange> orderListByCompleteStatus() {
        QueryWrapper<ForeignOfflineExchange> query = new QueryWrapper<>();
        query.lambda().eq(ForeignOfflineExchange::getCompleteStatus,1)
                .eq(ForeignOfflineExchange::getOrderStatus,1);
        return this.list(query);
    }

    @Override
    public List<ForeignOfflineExchange> orderListByCancle() {
        QueryWrapper<ForeignOfflineExchange> query = new QueryWrapper<>();
        query.lambda().eq(ForeignOfflineExchange::getCompleteStatus,3);
        return this.list(query);
    }

}