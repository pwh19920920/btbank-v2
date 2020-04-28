package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.api.dto.RebateRecordDTO;
import com.spark.bitrade.api.vo.BtBankRebateRecordVO;
import com.spark.bitrade.api.vo.MyRewardListVO;
import com.spark.bitrade.api.vo.MyRewardVO;
import com.spark.bitrade.repository.entity.BtBankRebateRecord;
import com.spark.bitrade.repository.mapper.BtBankRebateRecordMapper;
import com.spark.bitrade.repository.service.BtBankRebateRecordService;
import com.spark.bitrade.util.ListPageUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BtBankRebateRecordServiceImpl extends ServiceImpl<BtBankRebateRecordMapper, BtBankRebateRecord> implements BtBankRebateRecordService {

    @Override
    public IPage<BtBankRebateRecordVO> getRebateRecordAndNamePage(Page<BtBankRebateRecordVO> page, Long memberId) {
        return baseMapper.getRebateRecordAndNamePage(page, memberId);
    }

    @Override
    public MyRewardListVO getMyRewards(Long cuurent, Long size, Long memberId) {

        List<MyRewardVO> list = new ArrayList<>();
        //已获得奖励
        List<MyRewardVO> gotRewards = new ArrayList<>();
        gotRewards.addAll(baseMapper.getMyReward1(memberId));
        gotRewards.addAll(baseMapper.getMyReward6(memberId));
        gotRewards.addAll(baseMapper.getMyReward7(memberId));
        gotRewards.addAll(baseMapper.getMyReward8(memberId));
        gotRewards.addAll(baseMapper.getMyReward9(memberId));
        BigDecimal gotRewardTotal = gotRewards.stream().map(MyRewardVO::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        //推荐挖矿佣金奖励
        List<MyRewardVO> recommends = new ArrayList<>();
        recommends.addAll(baseMapper.getMyReward2(memberId));
        recommends.addAll(baseMapper.getMyReward3(memberId));
        recommends.addAll(baseMapper.getMyReward4(memberId));
        recommends.addAll(baseMapper.getMyReward5(memberId));
        // 直推佣金（每推荐一个100BT）
        recommends.addAll(baseMapper.getMyReward10(memberId));
        recommends.addAll(baseMapper.getMyReward11(memberId));
        BigDecimal recommendTotal = recommends.stream().map(MyRewardVO::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        list.addAll(gotRewards);
        list.addAll(recommends);


        // 按发放时间降序排列
        List<MyRewardVO> collect = list.stream().sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime())).collect(Collectors.toList());

        IPage<MyRewardVO> iPage = ListPageUtil.getPage(collect, cuurent, size);

        MyRewardListVO myRewardListVO = new MyRewardListVO();
        myRewardListVO.setGotRewardTotal(gotRewardTotal);
        myRewardListVO.setRecommendTotal(recommendTotal);
        myRewardListVO.setContent(iPage.getRecords());
        myRewardListVO.setTotalElements(iPage.getTotal());

        return myRewardListVO;
    }

    @Override
    public List<RebateRecordDTO> getRecordsCreatedAfter(Date date) {
        return baseMapper.getRecordsCreatedAfter(date);
    }
}








