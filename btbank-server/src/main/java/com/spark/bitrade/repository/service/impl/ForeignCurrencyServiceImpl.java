package com.spark.bitrade.repository.service.impl;

import com.aliyuncs.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.repository.entity.ForeignMemberBankinfo;
import com.spark.bitrade.repository.mapper.ForeignCurrencyMapper;
import com.spark.bitrade.repository.entity.ForeignCurrency;
import com.spark.bitrade.repository.service.ForeignCurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 换汇币种配置(ForeignCurrency)表服务实现类
 *
 * @author mahao
 * @since 2020-02-04 11:47:27
 */
@Service("foreignCurrencyService")
public class ForeignCurrencyServiceImpl extends ServiceImpl<ForeignCurrencyMapper, ForeignCurrency> implements ForeignCurrencyService {

    @Autowired
    private ForeignCurrencyMapper foreignCurrencyMapper;
    @Override
    public List<String> getAvailCurrency() {
        return foreignCurrencyMapper.getAvailCurrency();
    }

    @Override
    public List<ForeignCurrency> getAvail() {
        return foreignCurrencyMapper.getAvail();
    }

    @Override
    public List<String> getOtherAvailCurrency() {
        return foreignCurrencyMapper.getOtherAvailCurrency();
    }

    @Override
    public List<ForeignCurrency> getOtherAvail() {
        return foreignCurrencyMapper.getOtherAvail();
    }

    @Override
    public ForeignCurrency getByEnName(String enname) {

        return foreignCurrencyMapper.getByEnName(enname);
    }

    @Override
    public Map<String, ForeignCurrency> getAvilMap() {
        List<ForeignCurrency> foreignCurrencyList =  foreignCurrencyMapper.getAvail();
        Map<String,ForeignCurrency> foreignCurrencymap = new HashMap<String,ForeignCurrency>();
        for (ForeignCurrency foreignCurrency :foreignCurrencyList){
            if(foreignCurrency!=null&&!StringUtils.isEmpty(foreignCurrency.getCurrency())){
                foreignCurrencymap.put(foreignCurrency.getCurrency(),foreignCurrency);
            }
        }
        return foreignCurrencymap;
    }
}