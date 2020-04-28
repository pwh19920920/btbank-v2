package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.constants.AcctMsgCode;
import com.spark.bitrade.entity.Coin;
import com.spark.bitrade.entity.MemberWallet;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.mapper.MemberWalletMapper;
import com.spark.bitrade.service.ICoinService;
import com.spark.bitrade.service.IDistributedIdService;
import com.spark.bitrade.service.IMemberWalletService;
import com.spark.bitrade.util.ExceptionUitl;
import com.spark.bitrade.util.SpringContextUtil;
import com.spark.bitrade.vo.MemberWalletVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 会员钱包表服务实现类
 *
 * @author yangch
 * @since 2019-06-15 16:14:18
 */
@Slf4j
@Service("memberWalletServiceImpl")
public class MemberWalletServiceImpl extends ServiceImpl<MemberWalletMapper, MemberWallet> implements IMemberWalletService {
    @Autowired
    private IDistributedIdService idService;

    @Autowired
    private ICoinService coinService;

    @Override
    public MemberWallet findByCoinAndMemberId(String coinId, Long memberId) {
        MemberWallet memberWallet = this.baseMapper.selectOne(new QueryWrapper<MemberWallet>()
                .eq("member_id", memberId)
                .eq("coin_id", coinId));
        //钱包账户 不存在时，创建钱包账户
        if (StringUtils.isEmpty(memberWallet)) {
            memberWallet = this.getService().createMemberWallet(memberId, coinId);
        }

        return memberWallet;
    }

    /**
     * 根据币种和用户ID获取钱包
     *
     * @param unit     币种
     * @param memberId 用户ID
     * @return
     */
    @Override
    public MemberWallet findByUnitAndMemberId(String unit, Long memberId) {
        Coin coin = coinService.findByUnit(org.apache.commons.lang3.StringUtils.upperCase(unit));
        MemberWallet memberWallet = this.baseMapper.selectOne(new QueryWrapper<MemberWallet>()
                .eq("member_id", memberId)
                .eq("coin_id", coin.getName()));
        //钱包账户 不存在时，创建钱包账户
        if (StringUtils.isEmpty(memberWallet)) {
            memberWallet = this.getService().createMemberWallet(memberId, coin.getName());
        }

        return memberWallet;
    }

    @Override
    public Boolean trade(Long walletId, BigDecimal tradeBalance, BigDecimal tradeFrozenBalance, BigDecimal tradeLockBalance) throws MessageCodeException {
        if (this.baseMapper.trade(walletId, tradeBalance, tradeFrozenBalance, tradeLockBalance) <= 0) {
            ExceptionUitl.throwsMessageCodeException(AcctMsgCode.ACCOUNT_BALANCE_TRADE_FAILED);
        }

        return true;
    }

    @Override
    public Boolean tradeAllowNegative(Long walletId, BigDecimal tradeBalance, BigDecimal tradeFrozenBalance, BigDecimal tradeLockBalance) throws MessageCodeException {
        if (this.baseMapper.tradeAllowNegative(walletId, tradeBalance, tradeFrozenBalance, tradeLockBalance) <= 0) {
            ExceptionUitl.throwsMessageCodeException(AcctMsgCode.ACCOUNT_BALANCE_TRADE_FAILED);
        }

        return true;
    }

    @Override
    public Boolean trade(Long walletId, BigDecimal tradeBalance, BigDecimal tradeFrozenBalance, BigDecimal tradeLockBalance, BigDecimal minimum) throws MessageCodeException {
        if (this.baseMapper.trade2(walletId, tradeBalance, tradeFrozenBalance, tradeLockBalance, minimum) <= 0) {
            ExceptionUitl.throwsMessageCodeException(AcctMsgCode.ACCOUNT_BALANCE_TRADE_FAILED);
        }

        return true;
    }


    /**
     *  创建钱包账户
     *
     * @param memberId 会员ID
     * @param coinId   币种Id
     *                  
     */
    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public MemberWallet createMemberWallet(long memberId, String coinId) {
        MemberWallet memberWallet = new MemberWallet();
        memberWallet.setId(idService.generateId());
        //memberWallet.setAddress("");
        memberWallet.setMemberId(memberId);
        memberWallet.setCoinId(coinId);
        memberWallet.setBalance(new BigDecimal("0"));
        memberWallet.setFrozenBalance(new BigDecimal("0"));
        memberWallet.setLockBalance(new BigDecimal("0"));
        memberWallet.setIsLock(BooleanEnum.IS_FALSE);
        memberWallet.setEnabledIn(BooleanEnum.IS_TRUE);
        memberWallet.setEnabledOut(BooleanEnum.IS_TRUE);
        memberWallet.setVersion(0);

        if (SqlHelper.retBool(this.baseMapper.insert(memberWallet))) {
            return memberWallet;
        } else {
            log.warn("创建钱包账户失败！memberId={}, coinId={}", memberId, coinId);
            return null;
        }
    }

    public MemberWalletServiceImpl getService() {
        return SpringContextUtil.getBean(MemberWalletServiceImpl.class);
    }


    /**
     *  会员余额接口
     * huyu
     *
     * @param memberId 会员ID
     *                  
     */
    @Override
    public List<MemberWalletVo> findAllBalanceByMemberId(Long memberId) {
        List<MemberWalletVo> list = this.baseMapper.findSilkPayCoinWalletByMemberId(memberId);
        List<MemberWalletVo> voList = list.stream().map(wallet -> new MemberWalletVo(wallet.getBalance(), coinService.findOne(wallet.getUnit()).getUnit(), 0)).collect(Collectors.toList());
        return voList;
    }
}