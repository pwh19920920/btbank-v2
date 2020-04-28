package com.spark.bitrade.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.repository.entity.ForeignCurrency;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 换汇币种配置(ForeignCurrency)表数据库访问层
 *
 * @author yangch
 * @since 2020-02-04 11:47:27
 */
@Mapper
public interface ForeignCurrencyMapper extends BaseMapper<ForeignCurrency> {

    public List<String> getAvailCurrency();
    public List<ForeignCurrency> getAvail();
    public List<String> getOtherAvailCurrency();
    public List<ForeignCurrency> getOtherAvail();
    ForeignCurrency getByEnName(String currency);
}