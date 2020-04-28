package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.repository.entity.ForeignOfflineExchange;
import com.spark.bitrade.repository.mapper.ForeignOnlineExchangeMapper;
import com.spark.bitrade.repository.entity.ForeignOnlineExchange;
import com.spark.bitrade.repository.service.ForeignOnlineExchangeService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 换汇线上订单表(ForeignOnlineExchange)表服务实现类
 *
 * @author yangch
 * @since 2020-02-04 11:49:34
 */
@Service("foreignOnlineExchangeService")
public class ForeignOnlineExchangeServiceImpl extends ServiceImpl<ForeignOnlineExchangeMapper, ForeignOnlineExchange> implements ForeignOnlineExchangeService {

    @Override
    public IPage<ForeignOnlineExchange> orderlist(Member member, Integer current, Integer size) {
        IPage<ForeignOnlineExchange> page = new Page<>(current, size);
        QueryWrapper<ForeignOnlineExchange> query = new QueryWrapper<>();
        query.lambda().eq(ForeignOnlineExchange::getMemberId,member.getId()).orderByDesc(ForeignOnlineExchange::getCreateTime);
        return this.page(page,query);
    }

    @Override
    public List<ForeignOnlineExchange> orderListByCompleteStatus() {
        QueryWrapper<ForeignOnlineExchange> query = new QueryWrapper<>();
        query.lambda().eq(ForeignOnlineExchange::getCompleteStatus,1);
        return this.list(query);
    }


}