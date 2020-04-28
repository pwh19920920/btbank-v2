package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.entity.BtBankMinerPerRank;
import com.spark.bitrade.repository.mapper.BtBankMinerPerRankMapper;
import com.spark.bitrade.repository.service.BtBankMinerPerRankService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 有效矿工业绩排名统计(BtBankMinerPerRank)表服务实现类
 *
 * @author daring5920
 * @since 2020-03-18 15:58:04
 */
@Service("btBankMinerPerRankService")
public class BtBankMinerPerRankServiceImpl extends ServiceImpl<BtBankMinerPerRankMapper, BtBankMinerPerRank> implements BtBankMinerPerRankService {

    @Override
    public BtBankMinerPerRank getSub(Long minerId) {
        return getBaseMapper().getSub(minerId);
    }

    @Override
    public BigDecimal getPer(Long memberId) {
        List<Long> members = new ArrayList<>();
        members.add(memberId);
        //下级的用户id
        List<Long> subs = getBaseMapper().getSubId(memberId);
        //统计本人和下级的绩效
        members.addAll(subs);
        BigDecimal money = getBaseMapper().getOnesPer(members);

        return money;
    }
}