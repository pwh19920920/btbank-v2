package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.api.vo.MinerPrizeQuizeVo;
import com.spark.bitrade.repository.entity.EnterpriseMinerTransaction;
import com.spark.bitrade.repository.mapper.MinerPrizeQuizeTransactionMapper;
import com.spark.bitrade.repository.entity.MinerPrizeQuizeTransaction;
import com.spark.bitrade.repository.service.MinerPrizeQuizeTransactionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 矿工参与竞猜记录(MinerPrizeQuizeTransaction)表服务实现类
 *
 * @author daring5920
 * @since 2020-01-02 09:39:36
 */
@Service("minePrizeQuizeTransactionService")
public class MinerPrizeQuizeTransactionServiceImpl extends ServiceImpl<MinerPrizeQuizeTransactionMapper, MinerPrizeQuizeTransaction> implements MinerPrizeQuizeTransactionService {

    @Override
    public MinerPrizeQuizeVo queryTotal(Long prieQuizeId) {
        return this.baseMapper.queryTotal(prieQuizeId);
    }

    @Override
    public MinerPrizeQuizeVo queryUpTotal(Long prieQuizeId) {
        return this.baseMapper.queryUpTotal(prieQuizeId);
    }

    @Override
    public MinerPrizeQuizeVo queryDownTotal(Long prieQuizeId) {
        return this.baseMapper.queryDownTotal(prieQuizeId);
    }

    @Override
    public List<MinerPrizeQuizeVo> getMinerTransaction(Long memberId, IPage<MinerPrizeQuizeTransaction> page) {
        return this.baseMapper.getMinerTransaction(memberId,page);
    }

    @Override
    public boolean collect(MinerPrizeQuizeTransaction minerPrizeQuizeTransaction, Long recordId) {
        UpdateWrapper<MinerPrizeQuizeTransaction> update = new UpdateWrapper<>();
        Date now = new Date();
        // 更新本次投注扣款状态
        update.eq("id", minerPrizeQuizeTransaction.getId()).eq("guess_status", 2).eq("release_status",0)
                .set("release_status", 1)
                .set("release_ref_id", recordId + "")
                .set("update_time", now);

        return update(update);
    }

    @Override
    public boolean release(MinerPrizeQuizeTransaction minerPrizeQuizeTransaction, Long recordId) {
        UpdateWrapper<MinerPrizeQuizeTransaction> update = new UpdateWrapper<>();
        Date now = new Date();
        // 未压中并且未扣款
        update.eq("id", minerPrizeQuizeTransaction.getId()).eq("guess_status", 1).eq("release_status",0)
                .set("release_status", 1)
                .set("release_ref_id", recordId + "")
                .set("update_time", now);

        return update(update);
    }

    @Override
    public boolean reward(MinerPrizeQuizeTransaction minerPrizeQuizeTransaction, Long recordId, BigDecimal amount) {
        UpdateWrapper<MinerPrizeQuizeTransaction> update = new UpdateWrapper<>();
        Date now = new Date();
        // 未压中并且未扣款
        update.eq("id", minerPrizeQuizeTransaction.getId()).eq("guess_status", 1).eq("reward_status",0)
                .set("reward_status", 1)
                .set("reward_ref_id", recordId + "")
                .set("reward_release_time", now)
                .set("reward", amount)
                .set("update_time", now);
        return update(update);
    }

    @Override
    public MinerPrizeQuizeTransaction queryMaxAmount(Long prieQuizeId) {
        return  this.baseMapper.queryMaxAmount(prieQuizeId);
    }


}