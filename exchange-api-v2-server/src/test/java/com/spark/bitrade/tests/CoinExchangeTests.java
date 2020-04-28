package com.spark.bitrade.tests;

import com.alibaba.fastjson.JSON;
import com.spark.bitrade.controller.vo.CoinThumb;
import com.spark.bitrade.service.ICoinExchange;
import com.spark.bitrade.util.MessageRespResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

/**
 * CoinExchangeTests
 *
 * @author biu
 * @since 2019/12/18 17:34
 */
@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class CoinExchangeTests {

    @Autowired
    private ICoinExchange exchange;

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void testGetRate() {
        MessageRespResult<BigDecimal> btc = exchange.getCnytExchangeRate("BTC");
        System.out.println(JSON.toJSONString(btc));
    }

    @Test
    public void testRestTemplate() {
        ParameterizedTypeReference<MessageRespResult<BigDecimal>> pr = new ParameterizedTypeReference<MessageRespResult<BigDecimal>>() {
        };
        ResponseEntity<MessageRespResult<BigDecimal>> forEntity = restTemplate.exchange("/market/exchange-rate/cnyt/BTC", HttpMethod.GET, null, pr);
        System.out.println(JSON.toJSONString(forEntity));
    }

    @Test
    public void testGetCoinThumb() {
        ParameterizedTypeReference<List<CoinThumb>> pr = new ParameterizedTypeReference<List<CoinThumb>>() {
        };
        ResponseEntity<List<CoinThumb>> forEntity = restTemplate.exchange("/market/symbol-thumb-v2?showAll=0", HttpMethod.GET, null, pr);
        System.out.println(JSON.toJSONString(forEntity));
    }
}
