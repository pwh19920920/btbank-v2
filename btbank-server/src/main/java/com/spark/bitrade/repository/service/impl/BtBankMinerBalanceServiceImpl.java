package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.api.vo.MinerRecommandListVO;
import com.spark.bitrade.api.vo.MinerRecommandVO;
import com.spark.bitrade.repository.entity.BtBankMinerBalance;
import com.spark.bitrade.repository.mapper.BtBankMinerBalanceMapper;
import com.spark.bitrade.repository.service.BtBankMinerBalanceService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BtBankMinerBalanceServiceImpl extends ServiceImpl<BtBankMinerBalanceMapper, BtBankMinerBalance> implements BtBankMinerBalanceService {

    @Override
    public BtBankMinerBalance findFirstByMemberId(Long memberId) {
        return baseMapper.findFirstByMemberId(memberId);
    }

    @Override
    public int updateIncBalanceAmount(Long memberId, BigDecimal addedBalanceAmount) {
        return baseMapper.updateIncBalanceAmount(memberId, addedBalanceAmount);
    }

    /**
     * 自动派单匹配矿工
     *
     * @param amount
     * @return true
     * @author shenzucai
     * @time 2019.10.24 20:16
     */
    @Override
    public BtBankMinerBalance dispatchMiner(BigDecimal amount) {
        return baseMapper.dispatchMiner(amount);
    }

    @Override
    public int grabSuccAndUpdate(Long minerBalanceId, BigDecimal money, BigDecimal reward) {
        return baseMapper.grabSuccAndUpdate(minerBalanceId, money, reward);
    }

    @Override
    public int getRecommandAndChargeSuccMemberCount(Long memberId) {
        return baseMapper.getRecommandAndChargeSuccMemberCount(memberId);
    }

    @Override
    public MinerRecommandListVO getRecommandList(Long memberId, int size, int current) {

        IPage<MinerRecommandVO> minerRecommandPage = baseMapper.getRecommandList(new Page<>(current, size), memberId);
        MinerRecommandListVO minerRecommandListVO = new MinerRecommandListVO();
        minerRecommandListVO.setContent(minerRecommandPage.getRecords());
        minerRecommandListVO.setTotalElements(minerRecommandPage.getTotal());
        minerRecommandListVO.setRecommandSuccessNum(getRecommandAndChargeSuccMemberCount(memberId));
        return minerRecommandListVO;
    }
}





