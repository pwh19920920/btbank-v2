package com.spark.bitrade.config;

import com.alibaba.fastjson.JSON;
import com.spark.bitrade.controller.vo.CoinThumb;
import com.spark.bitrade.dto.*;
import com.spark.bitrade.entity.ExchangeFastCoin;
import com.spark.bitrade.service.ExchangeFastCoinService;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author shenzucai
 * @time 2019.12.27 13:44
 */
@Component
@Slf4j
public class AutoRefreshTask {

    @Resource(name = "huoBiExchangeRestTemplate")
    private RestTemplate huoBiExchangeRestTemplate;

    @Resource(name = "huoBiOtcRestTemplate")
    private RestTemplate huoBiOtcRestTemplate;

    @Autowired
    private ExchangeFastCoinService exchangeFastCoinService;

    public static Map<String, CoinThumb> thumbs = new HashMap<>();

    public static OtcPrice otcPrice = new OtcPrice();

    @Autowired
    private ApplicationContext context;

    private boolean notProdEnvironment() {

        for (String s : context.getEnvironment().getActiveProfiles()) {
            if ("prod".equals(s)) {
                return false;
            }
        }
        return true;
    }

    @Scheduled(fixedDelay = 6000)
    public void run() {
        getOtcPrices();
        getTickers();
    }

    private void getTickers() {
        List<ExchangeFastCoin> exchangeFastCoins = exchangeFastCoinService.list4CoinSymbol(notProdEnvironment() ? "24098437" : "24984705", "BT");
        if (exchangeFastCoins == null || exchangeFastCoins.size() < 1) {
            return;
        }


        if (Objects.isNull(otcPrice.getData())) {
            return;
        }
        if (Objects.isNull(otcPrice.getData().getDetail()) || otcPrice.getData().getDetail().size() < 1) {
            return;
        }
        BigDecimal usdt = BigDecimal.ZERO;
        List<DetailBean> detailBeans = otcPrice.getData().getDetail();
        for(DetailBean detailBean : detailBeans){
            if (StringUtils.equalsIgnoreCase(detailBean.getCoinName(), "USDT")) {
                usdt = new BigDecimal(detailBean.getBuy());
            }
        }

        if(BigDecimal.ZERO.compareTo(usdt) != -1){
            return;
        }

        for (ExchangeFastCoin exchangeFastCoin : exchangeFastCoins) {
            updateCoinThumb(usdt, exchangeFastCoin);
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateCoinThumb(BigDecimal usdt, ExchangeFastCoin exchangeFastCoin) {
        if(StringUtils.equalsIgnoreCase(exchangeFastCoin.getCoinSymbol(),"USDT")){
            CoinThumb coinThumb = new CoinThumb();
            coinThumb.setSymbol(exchangeFastCoin.getCoinSymbol() + "/BT");
            coinThumb.setOpen(usdt);
            coinThumb.setHigh(usdt);
            coinThumb.setLow(usdt);
            coinThumb.setClose(usdt);
            coinThumb.setChange(usdt.setScale(2));
            coinThumb.setChg(usdt.setScale(2));
            coinThumb.setVolume(new BigDecimal("47386477.66"));
            coinThumb.setTurnover(usdt);
            coinThumb.setLastDayClose(coinThumb.getOpen());
            coinThumb.setUsdRate(new BigDecimal("1"));
            coinThumb.setBaseUsdRate(new BigDecimal("1"));
            thumbs.put(StringUtils.upperCase(exchangeFastCoin.getCoinSymbol()), coinThumb);
            try{
                ResponseEntity<String> resp = huoBiExchangeRestTemplate.exchange("/market/history/kline?symbol=" + StringUtils.lowerCase(exchangeFastCoin.getCoinSymbol()) + "husd&period=1day&size=1&bttest="+System.currentTimeMillis(),
                        HttpMethod.GET, null, String.class);
                String respBody = resp.getBody();
                KlineHistory exchangeMarketTicker = JSON.parseObject(respBody,KlineHistory.class);
                coinThumb.setVolume(exchangeMarketTicker.getData().get(0).getVol());
                coinThumb.setTurnover(exchangeMarketTicker.getData().get(0).getAmount());
                thumbs.put(StringUtils.upperCase(exchangeFastCoin.getCoinSymbol()), coinThumb);
            }catch (Exception e){

            }
            return;
        }

        ResponseEntity<String> resp = huoBiExchangeRestTemplate.exchange("/market/history/kline?symbol=" + StringUtils.lowerCase(exchangeFastCoin.getCoinSymbol()) + "usdt&period=1day&size=1&bttest="+System.currentTimeMillis(),
                HttpMethod.GET, null, String.class);
        String respBody = resp.getBody();
        KlineHistory exchangeMarketTicker = JSON.parseObject(respBody,KlineHistory.class);
        if (Objects.isNull(exchangeMarketTicker)) {
            return;
        }
        List<DataBeanX> dataBeanXES = exchangeMarketTicker.getData();
        if(dataBeanXES == null || dataBeanXES.size()<1){
            return;
        }
        DataBeanX dataBeanX = dataBeanXES.get(0);
        // log.info("exchangeMarketTicker {}",exchangeMarketTicker);
        if (!Objects.isNull(dataBeanX)) {
            CoinThumb coinThumb = new CoinThumb();
            coinThumb.setSymbol(exchangeFastCoin.getCoinSymbol() + "/BT");
            coinThumb.setOpen(dataBeanX.getOpen().multiply(usdt));
            coinThumb.setHigh(dataBeanX.getHigh().multiply(usdt));
            coinThumb.setLow(dataBeanX.getLow().multiply(usdt));
            coinThumb.setClose(dataBeanX.getClose().multiply(usdt));
            coinThumb.setChange(coinThumb.getClose().subtract(coinThumb.getOpen()).setScale(2,BigDecimal.ROUND_DOWN));
            coinThumb.setChg(coinThumb.getChange().divide(coinThumb.getOpen(), 8, BigDecimal.ROUND_DOWN).setScale(4,BigDecimal.ROUND_DOWN));
            coinThumb.setVolume(dataBeanX.getVol());
            coinThumb.setTurnover(dataBeanX.getAmount());
            coinThumb.setLastDayClose(coinThumb.getOpen().multiply(usdt));
            coinThumb.setUsdRate(dataBeanX.getClose());
            coinThumb.setBaseUsdRate(new BigDecimal("1").divide(usdt, 8, BigDecimal.ROUND_DOWN).setScale(8,BigDecimal.ROUND_DOWN));
            thumbs.put(StringUtils.upperCase(exchangeFastCoin.getCoinSymbol()), coinThumb);
        }
    }

    private void getOtcPrices() {

        ParameterizedTypeReference<OtcPrice> pr = new ParameterizedTypeReference<OtcPrice>() {
        };

        ResponseEntity<OtcPrice> resp = huoBiOtcRestTemplate.exchange("/v1/data/market/detail?bttest="+System.currentTimeMillis(),
                HttpMethod.GET, null, pr);
        OtcPrice tempOtcPrice = resp.getBody();

        if (!Objects.isNull(tempOtcPrice)) {
            otcPrice = tempOtcPrice;
        }
    }

}
