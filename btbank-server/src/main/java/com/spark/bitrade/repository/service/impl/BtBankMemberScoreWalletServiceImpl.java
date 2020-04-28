package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.entity.BtBankMemberScoreWallet;
import com.spark.bitrade.repository.mapper.BtBankMemberScoreWalletMapper;
import com.spark.bitrade.repository.service.BtBankMemberScoreWalletService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * <p>
 * 用户挖矿积分钱包表 服务实现类
 * </p>
 *
 * @author qhliao
 * @since 2020-03-18
 */
@Service
public class BtBankMemberScoreWalletServiceImpl extends ServiceImpl<BtBankMemberScoreWalletMapper, BtBankMemberScoreWallet> implements BtBankMemberScoreWalletService {


    @Override
    public BtBankMemberScoreWallet findOne(Long memberId) {
        BtBankMemberScoreWallet wallet =this.lambdaQuery()
                .eq(BtBankMemberScoreWallet::getMemberId, memberId).last("limit 1 ").one();
        if (wallet == null) {
            wallet = new BtBankMemberScoreWallet();
            wallet.setMemberId(memberId);
            wallet.setBalance(BigDecimal.ZERO);
            wallet.setFrozenBalance(BigDecimal.ZERO);
            this.save(wallet);
        }
        return wallet;
    }

    @Override
    public boolean decreaseScore(Long memberId, BigDecimal score) {
        BtBankMemberScoreWallet wallet = this.findOne(memberId);
        int i = baseMapper.decreaseScore(wallet.getId(), score);
        return i>0;
    }
}
