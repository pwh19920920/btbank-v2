package com.spark.bitrade.api.controller;

import com.alibaba.fastjson.JSON;
import com.spark.bitrade.api.vo.AliExchangeResult;
import com.spark.bitrade.api.vo.AliExchangeVo;
import com.spark.bitrade.api.vo.ForeignExchange;
import com.spark.bitrade.biz.ForeignConfigService;
import com.spark.bitrade.biz.ForeignService;
import com.spark.bitrade.config.AliyunConfig;
import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.constant.BtBankSystemConfig;
import com.spark.bitrade.constant.ForeignConst;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.MemberTransaction;
import com.spark.bitrade.entity.WalletChangeRecord;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.ForeignCurrency;
import com.spark.bitrade.repository.entity.ForeignOfflineExchange;
import com.spark.bitrade.repository.entity.ForeignOnlineExchange;
import com.spark.bitrade.repository.service.ForeignCurrencyService;
import com.spark.bitrade.repository.service.ForeignOfflineExchangeService;
import com.spark.bitrade.repository.service.ForeignOnlineExchangeService;
import com.spark.bitrade.service.MemberWalletService;
import com.spark.bitrade.util.AliyunUtil;
import com.spark.bitrade.util.BigDecimalUtils;
import com.spark.bitrade.util.MessageRespResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by qiuyuanjie on 2020/2/6.
 */
@Slf4j
@Api(tags = {"外汇订单"})
@RequestMapping(path = "inner/scheduleForeignLineController", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@AllArgsConstructor
public class ScheduleForeignLineController {


    private ForeignConfigService foreignConfigService;
    private ForeignService foreignService;
    private ForeignCurrencyService foreignCurrencyService;
    private AliyunConfig aliyunConfig;
    private RedisTemplate redisTemplate;
    private RestTemplate restTemplate;
    @ApiOperation(value = "线下取现系统账号归集")
    @PostMapping("auto/foreigncollect")
    public MessageRespResult foreigncollect() {
        //归集账户
        Long account = foreignConfigService.getConfig(BtBankSystemConfig.EXCHANGE_COLLECT_ACCOUNT, v -> Long.valueOf(v.toString()), 0l);
        if (account.equals(0l) || account == null) {
            log.error("归集账户配置问题 account = {}", account);
            throw new BtBankException(CommonMsgCode.FAILURE);
        }

        //线下购汇归集
        List<ForeignOfflineExchange> foreignOfflineExchanges = foreignService.offlineCollectOrderList();
        log.info("开始线下归集，需要归集的共{}条记录", foreignOfflineExchanges.size());
        for (ForeignOfflineExchange exchange : foreignOfflineExchanges) {
            try {
                if (!foreignService.collectoffline(exchange, account)) {
                    log.error("线下换汇归集失败失败 exchange = {}, account = {}", exchange, account);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //线上购汇归集
        List<ForeignOnlineExchange> foreignOnlineExchanges = foreignService.onlineCollectOrderList();
        log.info("开始线上归集，需要归集的共{}条记录", foreignOfflineExchanges.size());
        for (ForeignOnlineExchange exchange : foreignOnlineExchanges) {
            try {
                if (!foreignService.collectonline(exchange, account)) {
                    log.error("线下换汇归集失败失败 exchange = {}, account = {}", exchange, account);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return MessageRespResult.success();
    }

    @ApiOperation(value = "线下退款")
    @PostMapping("auto/foreignRefound")
    public MessageRespResult foreignRefound() {
        //归集账户
        Long account = foreignConfigService.getConfig(BtBankSystemConfig.EXCHANGE_COLLECT_ACCOUNT, v -> Long.valueOf(v.toString()), 0l);
        if (account.equals(0l) || account == null) {
            log.error("归集账户配置问题 account = {}", account);
            throw new BtBankException(CommonMsgCode.FAILURE);
        }
        //扣手续费
        List<ForeignOfflineExchange> foreignOfflineExchanges = foreignService.offlinedrawServiceFeeList();
        for (ForeignOfflineExchange foreignOfflineExchange : foreignOfflineExchanges) {
            try {
                if (!foreignService.handleDrawServiceFee(foreignOfflineExchange)) {
                    log.error("线下退款失败 exchange = {}, account = {}", foreignOfflineExchange, account);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        List<ForeignOfflineExchange> foreignOfflineExchangeList = foreignService.offlineSystemRefoundList();
        for (ForeignOfflineExchange foreignOfflineExchange : foreignOfflineExchangeList) {
            try {
                if (!foreignService.handleSystemRefound(foreignOfflineExchange, account)) {
                    log.error("系统退款失败 exchange = {}, account = {}", foreignOfflineExchange, account);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //系统账号退款
        return MessageRespResult.success();
    }
    @ApiOperation(value = "更新汇率图片地址")
    @PostMapping("auto/foreignupdateimage")
    public MessageRespResult foreignupdateimage() {
        //更新图片地址
        List<ForeignCurrency> foreignCurrencyList =  foreignCurrencyService.list();
        try {
            for(ForeignCurrency foreignCurrency:foreignCurrencyList){
                String uri = AliyunUtil.getPrivateUrl(aliyunConfig, foreignCurrency.getImage());
                foreignCurrency.setImageUrl(uri);

            }
            if(foreignCurrencyList.size()>0){
                foreignCurrencyService.updateBatchById(foreignCurrencyList);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        //更新汇率
        String rediskey = "entity:foreignexchange:rate";
        Map<String, ForeignCurrency> foreignCurrencymap = foreignCurrencyList.stream().collect(
                Collectors.toMap(ForeignCurrency::getCurrency, Function.identity(), (key1, key2) -> key2));
        // API1查询汇率
        List<String>  currencyList = foreignCurrencyService.getAvailCurrency();
        List<ForeignExchange> foreignExchangelist = null;
        if(currencyList.size()>0) {
            String url = ForeignConst.getForeignexchange(currencyList);
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                String result = responseEntity.getBody();
                if(!StringUtils.isEmpty(result)){
                    foreignExchangelist = JSON.parseArray(result, ForeignExchange.class);
                    if(foreignExchangelist.size()>0){
                        for(ForeignExchange foreignExchange :foreignExchangelist){
                            ForeignCurrency foreignCurrency = foreignCurrencymap.get(foreignExchange.getSymbol().substring(3,foreignExchange.getSymbol().length()));
                            if(foreignCurrency!=null){
                                foreignExchange.setCurrency(foreignCurrency.getCurrency());
                                foreignExchange.setId(foreignCurrency.getId());
                                foreignExchange.setName(foreignCurrency.getCnmane());
                                foreignExchange.setLocation(foreignCurrency.getLocation());
                                foreignExchange.setImage(foreignCurrency.getImageUrl());
                            }
                        }

                    }
                }
            }
        }
        // AP2 查询汇率
        List<ForeignCurrency> otherForeignCurrencyList = foreignCurrencyService.getOtherAvail();
        if(otherForeignCurrencyList!=null&&otherForeignCurrencyList.size()>0){
            //Map<String, ForeignCurrency> otherForeignCurrencymap = foreignCurrencyList.stream().collect(
            //       Collectors.toMap(ForeignCurrency::getCurrency, Function.identity(), (key1, key2) -> key2));
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "APPCODE " + ForeignConst.APPCODE);
            HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
            ResponseEntity<String> resEntity = restTemplate.exchange(ForeignConst.OTHERFOREIGLIST, HttpMethod.GET, requestEntity, String.class);
            if(resEntity.getStatusCode()== HttpStatus.OK){
                if(StringUtils.isNotBlank(resEntity.getBody())){
                    AliExchangeResult result = JSON.parseObject(resEntity.getBody(),AliExchangeResult.class);
                    if(result.getShowapi_res_code()==0&&result.getShowapi_res_body().getRet_code()==0){
                        List<AliExchangeVo>  aiExchangeVolst = result.getShowapi_res_body().getList();
                        if(aiExchangeVolst!=null&&aiExchangeVolst.size()>0){
                            Map<String, AliExchangeVo> aliCurrencymap = aiExchangeVolst.stream().collect(
                                    Collectors.toMap(AliExchangeVo::getCode, Function.identity(), (key1, key2) -> key2));
                            //查询从阿里获取配置的数据
                            List<ForeignCurrency> aliforeignCurrencyList = foreignCurrencyService.getOtherAvail();
                            if(foreignExchangelist==null){
                                foreignExchangelist = new ArrayList<>();
                            }
                            for ( ForeignCurrency foreignCurrency :aliforeignCurrencyList){
                                AliExchangeVo aliExchangeVo = aliCurrencymap.get(foreignCurrency.getCurrency());
                                if(aliExchangeVo!=null){
                                    ForeignExchange foreignExchange = new ForeignExchange();
                                    foreignExchange.setId(foreignCurrency.getId());
                                    foreignExchange.setImage(foreignCurrency.getImageUrl());
                                    foreignExchange.setLocation(foreignCurrency.getLocation());
                                    foreignExchange.setCurrency(foreignCurrency.getCurrency());
                                    foreignExchange.setName(foreignCurrency.getCnmane());
                                    foreignExchange.setSymbol(foreignCurrency.getCurrency());
                                    if(aliExchangeVo.getChao_in()!=null){
                                        foreignExchange.setPrice(BigDecimalUtils.div(new BigDecimal(100),new BigDecimal(aliExchangeVo.getChao_in())));
                                    }else if(aliExchangeVo.getHui_in()!=null){
                                        foreignExchange.setPrice(BigDecimalUtils.div(new BigDecimal(100),new BigDecimal(aliExchangeVo.getHui_in())));
                                    }
                                    foreignExchange.setBid(foreignExchange.getPrice());
                                    foreignExchange.setAsk(foreignExchange.getPrice());
                                    foreignExchangelist.add(foreignExchange);
                                }
                            }
                        }
                    }
                }

            }
        }
        if(foreignExchangelist!=null&&foreignExchangelist.size()>0){
            redisTemplate.opsForValue().set(rediskey, foreignExchangelist, 15,  TimeUnit.MINUTES);
        }

        return MessageRespResult.success();
    }
}
