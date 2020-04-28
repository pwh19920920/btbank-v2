package com.spark.bitrade.biz.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.api.vo.AdvertiseHistoryVo;
import com.spark.bitrade.api.vo.AliExchangeResult;
import com.spark.bitrade.api.vo.AliExchangeTransResult;
import com.spark.bitrade.biz.AdvertiseService;
import com.spark.bitrade.biz.ForeignConfigService;
import com.spark.bitrade.constant.ForeignConst;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.repository.entity.ForeignCashLocation;
import com.spark.bitrade.repository.entity.ForeignCurrency;
import com.spark.bitrade.repository.entity.ForeignOfflineExchange;
import com.spark.bitrade.repository.entity.ForeignOnlineExchange;
import com.spark.bitrade.repository.service.ForeignCashLocationService;
import com.spark.bitrade.repository.service.ForeignCurrencyService;
import com.spark.bitrade.repository.service.ForeignOfflineExchangeService;
import com.spark.bitrade.repository.service.ForeignOnlineExchangeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Slf4j
public class ForeignConfigTest {
    @Autowired
    private ForeignConfigService foreignConfigService;
    @Autowired
    private ForeignCashLocationService foreignCashLocationService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ForeignCurrencyService foreignCurrencyService;
    @Autowired
    private ForeignOfflineExchangeService foreignOfflineExchangeService;
    @Autowired
    private ForeignOnlineExchangeService foreignOnlineExchangeService;

    @Autowired
    private AdvertiseService advertiseService;
    @Test
    public void Test(){

        System.out.println(JSON.toJSONString(foreignCurrencyService.getByEnName("USD")));

    }
    @Test
    public void Testorder(){
        Member member = new Member();
        member.setId(36106L);
        IPage<ForeignOfflineExchange> page1 = foreignOfflineExchangeService.orderlist( member,  1, 20);
        IPage<ForeignOnlineExchange> page = foreignOnlineExchangeService.orderlist( member,  1, 20);
        System.out.println(JSON.toJSONString(page1));
        System.out.println(JSON.toJSONString(page));
    }
    @Test
    public void TestCurrencyService(){
        //String result = foreignConfigService.getValue("OTC_MINER_COMMISSION_RATE");
        //System.out.println(JSON.toJSONString(foreignCurrencyService.getAvail()));
        List<String> pairs = new ArrayList<>();
        pairs.add("USD");
        pairs.add("CNH");
        List<String>  currencyList = foreignCurrencyService.getAvailCurrency();
        StringBuilder sb = new StringBuilder(ForeignConst.FOREIGNEXCHANGEPAIRS);
        sb.append(StringUtils.join(currencyList ,",")).append("&api_key=").append(ForeignConst.FOREIGNKEY);

        System.out.println(JSON.toJSONString(currencyList));
        System.out.println(JSON.toJSONString(sb.toString()));
    }
    @Test
    public void TestExchange(){
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(ForeignConst.getForeignexchangePer("USD"), String.class);
        System.out.println(responseEntity.getBody());
    }
    @Test
    public void TestCashLocation(){
        QueryWrapper<ForeignCashLocation> query = new QueryWrapper<>();
        query.lambda().eq(ForeignCashLocation::getStatus,1);
        System.out.println(JSON.toJSONString(foreignCashLocationService.list(query)));
    }
    @Test
    public void TestAdvHis(){
        Member member = new Member();
        member.setId(360660L);
//        IPage<AdvertiseHistoryVo>  ret=  advertiseService.getAdvertiseHistory(member, 1, 20);
//        System.out.println(JSON.toJSONString(ret));
    }

    @Test
    public void TestTHB(){
        String url = "https://ali-waihui.showapi.com/waihui-list";
        String appcode = "b505fa4f5ea9474aa0f481159c640cbb";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "APPCODE " + ForeignConst.APPCODE);
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
        ResponseEntity<String> resEntity = restTemplate.exchange(ForeignConst.OTHERFOREIGLIST, HttpMethod.GET, requestEntity, String.class);
        if(resEntity.getStatusCode()== HttpStatus.OK){
            System.out.println(resEntity.getBody());
            if(StringUtils.isNotBlank(resEntity.getBody())){
                AliExchangeResult result = JSON.parseObject(resEntity.getBody(),AliExchangeResult.class);
                System.out.println(result.getShowapi_res_body().getList());
            }

        }

    }

    @Test
    public void TestCNY(){
        String url = "https://ali-waihui.showapi.com/waihui-transform?fromCode=CNY&money=100&toCode=THB";
        String appcode = "b505fa4f5ea9474aa0f481159c640cbb";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "APPCODE " + ForeignConst.APPCODE);
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
        ResponseEntity<String> resEntity = restTemplate.exchange(ForeignConst.getOtherForeignexchange("THB",new BigDecimal(200)), HttpMethod.GET, requestEntity, String.class);
        if(resEntity.getStatusCode()== HttpStatus.OK){
            System.out.println(resEntity.getBody());
            if(StringUtils.isNotBlank(resEntity.getBody())){
                AliExchangeTransResult result = JSON.parseObject(resEntity.getBody(),AliExchangeTransResult.class);
                System.out.println(JSON.toJSONString(result.getShowapi_res_body()));
            }

        }

    }

}
