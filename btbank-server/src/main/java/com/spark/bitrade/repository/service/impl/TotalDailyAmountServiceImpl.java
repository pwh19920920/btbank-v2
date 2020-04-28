package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.entity.TotalDailyAmount;
import com.spark.bitrade.repository.mapper.TotalDailyAmountMapper;
import com.spark.bitrade.repository.service.TotalDailyAmountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * total_daily_amount 表服务接口
 *
 * @author qiuyuanjie
 * @since 2020-03-09 16:48:01
 */
@Service("totalDailyAmountService")
public class TotalDailyAmountServiceImpl extends ServiceImpl<TotalDailyAmountMapper,TotalDailyAmount> implements TotalDailyAmountService {

    @Autowired
    private TotalDailyAmountMapper totalDailyAmountMapper;

    @Override
    public Boolean statTotalDailyAmount() {
        boolean flag = false;
        //用户钱包的资产统计
        List<TotalDailyAmount> totalDailyAmounts = totalDailyAmountMapper.memberWalletStat();
        //活期宝币数
        TotalDailyAmount hqb = totalDailyAmountMapper.hqbStatTotal();
        //大宗挖矿
        TotalDailyAmount bulk = totalDailyAmountMapper.bulkMiningTotal();
        //矿池可用 + 矿池锁仓
        TotalDailyAmount minerBalance = totalDailyAmountMapper.minerBalanceAmountStat();
        //企业矿池
        TotalDailyAmount enterprise = totalDailyAmountMapper.enterpriseMinerStat();
        //红包锁仓
        TotalDailyAmount redLock = totalDailyAmountMapper.redLockAmountStat();
        //将所有数据汇总
        for (TotalDailyAmount t :
                totalDailyAmounts) {
            if (t.getCoinId().equals("BT")) {
                //总额相加
                t.setMemberTotal(t.getMemberTotal().add(hqb.getHqbAmout()).add(bulk.getBulkMining()).add(minerBalance.getMinerBalanceAmount()).add(enterprise.getEnterPriseOrepool()).add(minerBalance.getMinerLockAmount()));
                t.setHqbAmout(hqb.getHqbAmout());
                t.setBulkMining(bulk.getBulkMining());
                t.setMinerBalanceAmount(minerBalance.getMinerBalanceAmount());
                t.setMinerLockAmount(minerBalance.getMinerLockAmount());
                t.setEnterPriseOrepool(enterprise.getEnterPriseOrepool());
                t.setRedLockAmount(redLock.getRedLockAmount());
            }
            totalDailyAmountMapper.insert(t);
            flag = true;
        }
        return flag;
    }
}
