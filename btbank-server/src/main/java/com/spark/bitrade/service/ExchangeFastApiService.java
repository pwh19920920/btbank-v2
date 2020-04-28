package com.spark.bitrade.service;

import com.spark.bitrade.util.MessageRespResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by mahao on 2020/1/3.
 */

@Service
public class ExchangeFastApiService {
    @Autowired
    private IExchangeFastApiService exchangeApiService;

    public MessageRespResult<String> thumb(String unit) {
        return exchangeApiService.thumb(unit);
    }

}
