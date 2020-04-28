package com.spark.bitrade.service.adapter;

import com.alibaba.fastjson.JSONArray;
import com.spark.bitrade.config.AutoRefreshTask;
import com.spark.bitrade.controller.vo.CoinThumb;
import com.spark.bitrade.dto.DetailBean;
import com.spark.bitrade.service.ICoinExchange;
import com.spark.bitrade.util.MessageRespResult;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

/**
 * ICoinExchangeAdapter
 *
 * @author biu
 * @since 2019/12/18 17:20
 */
@Slf4j
@Component
@AllArgsConstructor
public class ICoinExchangeAdapter implements ICoinExchange {


    @Override
    public MessageRespResult<BigDecimal> getUsdExchangeRate(String coin) {
        throw new UnsupportedOperationException("该操作不支持");
    }

    @Override
    public MessageRespResult<BigDecimal> getCnyExchangeRate(String coin) {
        throw new UnsupportedOperationException("该操作不支持");
    }

    @Override
    public MessageRespResult<BigDecimal> getCnytExchangeRate(String coin) {

        if(StringUtils.equalsIgnoreCase("BT",StringUtils.upperCase(coin))){
            return MessageRespResult.success4Data(new BigDecimal("1"));
        }
        List<DetailBean> details = AutoRefreshTask.otcPrice.getData().getDetail();
        BigDecimal usdt = BigDecimal.ZERO;
        for(DetailBean detailBean:details){
            if(StringUtils.equalsIgnoreCase(detailBean.getCoinName(),StringUtils.upperCase(coin))){
                return MessageRespResult.success4Data(new BigDecimal(detailBean.getBuy()));
            }
            if(StringUtils.equalsIgnoreCase("USDT",StringUtils.upperCase(coin))){
                usdt = new BigDecimal(detailBean.getBuy());
            }
        }

        if(AutoRefreshTask.thumbs.containsKey(StringUtils.upperCase(coin))){
            CoinThumb coinThumb = AutoRefreshTask.thumbs.get(StringUtils.upperCase(coin));
            return MessageRespResult.success4Data(coinThumb.getUsdRate().multiply(usdt).setScale(8));
        }

        return new MessageRespResult<>(500, "远程汇率接口调用出错");
    }

    @Override
    public MessageRespResult<BigDecimal> getUsdCnyRate() {
        throw new UnsupportedOperationException("该操作不支持");
    }

    @Override
    public JSONArray findKHistory(String symbol, long from, long to, String resolution) {
        throw new UnsupportedOperationException("该操作不支持");
    }
}
