package com.spark.bitrade.repository.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.repository.entity.ForeignCurrency;

import java.util.List;
import java.util.Map;

/**
 * 换汇币种配置(ForeignCurrency)表服务接口
 *
 * @author mahao
 * @since 2020-02-04 11:47:27
 */
public interface ForeignCurrencyService extends IService<ForeignCurrency> {
    public List<String> getAvailCurrency();
    public List<ForeignCurrency> getAvail();
    public List<String> getOtherAvailCurrency();
    public List<ForeignCurrency> getOtherAvail();
    public ForeignCurrency getByEnName(String enname);
    public Map<String,ForeignCurrency> getAvilMap();
}