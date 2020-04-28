package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.api.vo.MinerBalanceTransactionsVO;
import com.spark.bitrade.repository.entity.BtBankMinerBalanceTransaction;
import com.spark.bitrade.repository.mapper.BtBankMinerBalanceTransactionMapper;
import com.spark.bitrade.repository.service.BtBankMinerBalanceTransactionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class BtBankMinerBalanceTransactionServiceImpl extends ServiceImpl<BtBankMinerBalanceTransactionMapper, BtBankMinerBalanceTransaction> implements BtBankMinerBalanceTransactionService {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public MinerBalanceTransactionsVO getMinerBalanceTransactionsByMemberId(Long memberId, List<Integer> types, int page, int size, String range) {


        QueryWrapper queryWrapper = new QueryWrapper<BtBankMinerBalanceTransaction>()
                .eq("member_id", memberId).orderByDesc("create_time");


        if (types != null && types.size() > 0) {
            queryWrapper.in("type", types);
        }

        Page<BtBankMinerBalanceTransaction> orderPage = new Page<>(page, size);
        if (StringUtils.isNoneBlank(range)) {
            // 开始时间 结束时间 2019-12-29 匹配企业挖矿搜索字段
            Date start = null;
            Date end = null;
            String[] strings = range.split("~");
            if (strings.length == 2) {
                try {
                    start = sdf.parse(strings[0].trim());
                    end = sdf.parse(strings[1].trim());

                } catch (ParseException e) {
                    e.printStackTrace();
                    throw new IllegalArgumentException("日期范围参数无效");
                }
                if (start != null && end != null) {
                    Calendar startcal = Calendar.getInstance();
                    startcal.setTime(start);
                    startcal.set(Calendar.HOUR_OF_DAY, 0);
                    startcal.set(Calendar.MINUTE, 0);
                    startcal.set(Calendar.SECOND, 0);
                    startcal.set(Calendar.MILLISECOND, 0);

                    Calendar endcal = Calendar.getInstance();
                    endcal.setTime(end);
                    endcal.set(Calendar.HOUR_OF_DAY, 23);
                    endcal.set(Calendar.MINUTE, 59);
                    endcal.set(Calendar.SECOND, 59);
                    endcal.set(Calendar.MILLISECOND, 999);
                    queryWrapper.ge("create_time", startcal.getTime());
                    queryWrapper.le("create_time", endcal.getTime());
                }
            }
        }
        IPage<BtBankMinerBalanceTransaction> minerBalancePage = this.baseMapper.selectPage(orderPage, queryWrapper);
        MinerBalanceTransactionsVO minerBalanceTransactionsVO = new MinerBalanceTransactionsVO();


        minerBalanceTransactionsVO.setContent(minerBalancePage.getRecords());
        minerBalanceTransactionsVO.setTotalElements(minerBalancePage.getTotal());

        return minerBalanceTransactionsVO;
    }

    @Override
    public int spendBalanceWithIdAndBalance(Long id, BigDecimal payDecimal) {

        return baseMapper.spendBalanceWithIdAndBalance(id, payDecimal);
    }

    @Override
    public BigDecimal getYestodayMinerBalanceTransactionsSumByMemberId(Long memberId, List types) {
        return baseMapper.getYestodayMinerBalanceTransactionsSumByMemberId(memberId, types);
    }

    @Override
    public List<BtBankMinerBalanceTransaction> listNeedRebate() {
        return baseMapper.listNeedRebate();
    }

    @Override
    public boolean markRebateProcessedById(Long id) {
        return baseMapper.markRebateProcessedById(id);
    }

    @Override
    public List<BtBankMinerBalanceTransaction> countProfitByType(Date limitTime) {
        return this.baseMapper.countProfitByType(limitTime);
    }

    @Override
    public BigDecimal sum38AfterTransfer(Long memberId,Date date) {
        return this.baseMapper.sum38AfterTransfer(memberId,date);
    }

    @Override
    public BtBankMinerBalanceTransaction findByTypeAndMemberId(Long memberId) {
        QueryWrapper<BtBankMinerBalanceTransaction> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(BtBankMinerBalanceTransaction::getMemberId, memberId).eq(BtBankMinerBalanceTransaction::getType, 1);
        return this.getOne(queryWrapper);
    }
    @Override
    public Long[] getValidMiners() {
        return baseMapper.getValidMiners();
    }
}


