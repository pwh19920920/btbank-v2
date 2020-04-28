package com.spark.bitrade.api.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.api.vo.*;
import com.spark.bitrade.biz.ForeignConfigService;
import com.spark.bitrade.biz.ForeignService;
import com.spark.bitrade.config.AliyunConfig;
import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.constant.BtBankSystemConfig;
import com.spark.bitrade.constant.ForeignConst;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.WalletChangeRecord;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.*;
import com.spark.bitrade.repository.service.ForeignCurrencyService;
import com.spark.bitrade.repository.service.ForeignOfflineExchangeService;
import com.spark.bitrade.repository.service.ForeignOnlineExchangeService;
import com.spark.bitrade.repository.service.PictureAccessoryService;
import com.spark.bitrade.service.MemberWalletService;
import com.spark.bitrade.util.AliyunUtil;
import com.spark.bitrade.util.BigDecimalUtils;
import com.spark.bitrade.util.IdWorkByTwitter;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Api(tags = {"换汇订单"})
@RequestMapping(path = "api/v2/foreignorder", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@AllArgsConstructor
public class ForeignOrderController {
    private RestTemplate restTemplate;
    private ForeignCurrencyService foreignCurrencyService;
    private ForeignConfigService foreignConfigService;
    private RedisTemplate redisTemplate;
    private ForeignService foreignService;
    private AliyunConfig aliyunConfig;
    private ForeignOfflineExchangeService foreignOfflineExchangeService;
    @ApiOperation(value = "获取手换汇配置", response = ForeignExchangeConfigVo.class)
    @PostMapping(value = "exchangeconfig")
    public MessageRespResult<ForeignExchangeConfigVo > exchangeconfig() {
        String  limitAmount = (String)foreignConfigService.getValue(BtBankSystemConfig.EXCHANGE_LIMIT);
        String  onlineRate = (String)foreignConfigService.getValue(BtBankSystemConfig.EXCHANGE_ONLINE_RATE);
        String  offineRate = (String)foreignConfigService.getValue(BtBankSystemConfig.EXCHANGE_OFFLINE_RATE);
        String  offineSwitch = (String)foreignConfigService.getValue(BtBankSystemConfig.EXCHANGE_OFFLINE_SWITCH);
        String  onineSwitch = (String)foreignConfigService.getValue(BtBankSystemConfig.EXCHANGE_ONLINE_SWITCH);
        String limitOnLineAmount = (String)foreignConfigService.getValue(BtBankSystemConfig.EXCHANGE_LIMITONLINE);
        if (!StringUtils.isEmpty(limitAmount)&&!StringUtils.isEmpty(onlineRate)&&!StringUtils.isEmpty(offineRate)
            &&!StringUtils.isEmpty(offineSwitch)&&!StringUtils.isEmpty(onineSwitch)&&!StringUtils.isEmpty(limitOnLineAmount)) {
            ForeignExchangeConfigVo foreignExchangeConfigVo =new ForeignExchangeConfigVo();
            foreignExchangeConfigVo.setLimitAmount(new BigDecimal(limitAmount));
            foreignExchangeConfigVo.setLimitOnLineAmount(new BigDecimal(limitOnLineAmount));
            foreignExchangeConfigVo.setOffineRate(new BigDecimal(offineRate));
            foreignExchangeConfigVo.setOnlineRate(new BigDecimal(onlineRate));
            foreignExchangeConfigVo.setOnineSwitch("1".equals(onineSwitch)==true?true:false);
            foreignExchangeConfigVo.setOffineSwitch("1".equals(offineSwitch)==true?true:false);
            return MessageRespResult.success4Data(foreignExchangeConfigVo);
        }
        throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_CONFIG);
    }
    @ApiOperation(value = "获取汇率", response = ForeignExchange.class)
    @PostMapping(value = "getexchange")
    public MessageRespResult< List<ForeignExchange> > getexchange() {
        String rediskey = "entity:foreignexchange:rate";
        List<ForeignExchange> foreignExchangelist = (List<ForeignExchange>)redisTemplate.opsForValue().get(rediskey);
        if(foreignExchangelist!=null&&foreignExchangelist.size()>0){
            return MessageRespResult.success4Data(foreignExchangelist);
        }
        List<ForeignCurrency> foreignCurrencyList =  foreignCurrencyService.list();
        /*try {
            for(ForeignCurrency foreignCurrency:foreignCurrencyList){
                String uri = AliyunUtil.getPrivateUrl(aliyunConfig, foreignCurrency.getImage());
                foreignCurrency.setImageUrl(uri);
            }
            if(foreignCurrencyList.size()>0){
                foreignCurrencyService.updateBatchById(foreignCurrencyList);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }*/
        Map<String, ForeignCurrency> foreignCurrencymap = foreignCurrencyList.stream().collect(
                Collectors.toMap(ForeignCurrency::getCurrency, Function.identity(), (key1, key2) -> key2));
        // API1查询汇率
        List<String>  currencyList = foreignCurrencyService.getAvailCurrency();
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
        /*List<String>  currencyList = foreignCurrencyService.getAvailCurrency();
        Map<String,ForeignCurrency> foreignCurrencymap = foreignCurrencyService.getAvilMap();
        if(currencyList.size()>0){
            String url = ForeignConst.getForeignexchange(currencyList);
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
            if(responseEntity.getStatusCode()== HttpStatus.OK){
                String result = responseEntity.getBody();
                if(!StringUtils.isEmpty(result)){
                    foreignExchangelist = JSON.parseArray(result,ForeignExchange.class);
                    if(foreignExchangelist.size()>0){
                        for(ForeignExchange foreignExchange :foreignExchangelist){
                            ForeignCurrency foreignCurrency = foreignCurrencymap.get(foreignExchange.getSymbol().substring(3,foreignExchange.getSymbol().length()));
                            if(foreignCurrency!=null){
                                foreignExchange.setCurrency(foreignCurrency.getCurrency());
                                foreignExchange.setId(foreignCurrency.getId());
                                foreignExchange.setName(foreignCurrency.getCnmane());
                                foreignExchange.setLocation(foreignCurrency.getLocation());
                                try {
                                    // foreignCurrency.getImage()
                                    String uri = AliyunUtil.getPrivateUrl(aliyunConfig, foreignCurrency.getImage());
                                    foreignExchange.setImage(foreignCurrency.getImageUrl());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    log.error("获取图片失败", e);
                                }
                            }
                        }
                        redisTemplate.opsForValue().set(rediskey, foreignExchangelist, 15,  TimeUnit.MINUTES);
                        return MessageRespResult.success4Data(foreignExchangelist);
                    }
                }
            }
        }*/
        return MessageRespResult.success4Data(foreignExchangelist);
    }


    @ApiOperation(value = "线下订单")
    @PostMapping(value = "offlineorder")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "换汇BT数量 ", name = "buyCount", dataTypeClass = BigDecimal.class, required = true),
            @ApiImplicitParam(value = "手续费", name = "serviceCharge", dataTypeClass = BigDecimal.class, required = false),
            @ApiImplicitParam(value = "汇率", name = "exchangeRate", dataTypeClass = BigDecimal.class, required = false),
            @ApiImplicitParam(value = "地址ID", name = "addressId",  dataTypeClass = Long.class, required = true),
            @ApiImplicitParam(value = "地址", name = "address",  dataTypeClass = String.class, required = true),
            @ApiImplicitParam(value = "换汇币种", name = "exchangeSwapCurrency",  dataTypeClass = String.class, required = true),
    })

    public MessageRespResult< List<ForeignExchange> > offlineorder(@MemberAccount Member member,BigDecimal buyCount,BigDecimal serviceCharge,
             BigDecimal exchangeRate,  String exchangeSwapCurrency , Long addressId  ,  String address   ) {
        String  limitAmount = (String)foreignConfigService.getValue(BtBankSystemConfig.EXCHANGE_LIMIT);
        String  offineRate = (String)foreignConfigService.getValue(BtBankSystemConfig.EXCHANGE_OFFLINE_RATE);
        Integer offilenswitch = foreignConfigService.getConfig(BtBankSystemConfig.EXCHANGE_OFFLINE_SWITCH, v -> Integer.parseInt(v.toString()), 0);
        if(offilenswitch==null){
            throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_CONFIG);
        }
        if(offilenswitch==0){
            log.info("线上购汇关闭");
            throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_SWITCH);
        }
        if(StringUtils.isEmpty(limitAmount)||StringUtils.isEmpty(offineRate)){
            throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_CONFIG);
        }
        if(StringUtils.isEmpty(exchangeSwapCurrency)){
            throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_SYMBOL);
        }
        if(StringUtils.isEmpty(address) ||addressId ==null && addressId <=0){
            throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_ADDRESS);
        }
        if(buyCount==null || buyCount.compareTo(new BigDecimal(limitAmount))==-1 ){
            throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_LIMLMIT);
        }
        //防止重复提交
        if(redisTemplate.hasKey("FOREIGN:OFFLINEEXCHINGE:memberId"+member.getId())){
            throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_FREQUENCY);
        }else{
            redisTemplate.opsForValue().set("FOREIGN:OFFLINEEXCHINGE:memberId"+member.getId(), 1, 3,  TimeUnit.SECONDS);
        }
        if(foreignService.offlineorder(member,address,addressId,buyCount,exchangeSwapCurrency,offineRate)){
            return  MessageRespResult.success();

        }

        throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_ERROR);
    }
    @ApiOperation(value = "线上订单")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "换汇BT数量 ", name = "buyCount", dataTypeClass = BigDecimal.class, required = true),
            @ApiImplicitParam(value = "手续费", name = "serviceCharge", dataTypeClass = BigDecimal.class, required = false),
            @ApiImplicitParam(value = "汇率", name = "exchangeRate", dataTypeClass = BigDecimal.class, required = false),
            @ApiImplicitParam(value = "银行ID", name = "bankId",  dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "换汇币种", name = "exchangeSwapCurrency",  dataTypeClass = String.class, required = true),
    })
    @PostMapping(value = "onlineorder")

    public MessageRespResult onlineorder(@MemberAccount Member member,BigDecimal buyCount,BigDecimal serviceCharge,
            BigDecimal exchangeRate,  String exchangeSwapCurrency , Long bankId    ) {
        String  limitAmount = (String)foreignConfigService.getValue(BtBankSystemConfig.EXCHANGE_LIMITONLINE);
        String onineRate = (String)foreignConfigService.getValue(BtBankSystemConfig.EXCHANGE_ONLINE_RATE);
        Integer onilenswitch = foreignConfigService.getConfig(BtBankSystemConfig.EXCHANGE_ONLINE_SWITCH, v -> Integer.parseInt(v.toString()), 0);
        if(onilenswitch==null){
            throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_CONFIG);
        }
        if(onilenswitch==0){
            log.info("线上购汇关闭");
            throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_SWITCH);
        }
        if(StringUtils.isEmpty(limitAmount)||StringUtils.isEmpty(onineRate)){
            throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_CONFIG);
        }
        if(StringUtils.isEmpty(exchangeSwapCurrency)){
            throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_SYMBOL);
        }
        if(bankId ==null|| bankId <=0){
            throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_BANK);
        }
        if(buyCount==null ||buyCount.compareTo(new BigDecimal(limitAmount))==-1 ){
            throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_LIMLMIT);
        }
        //防止重复提交
        if(redisTemplate.hasKey("FOREIGN:ONLINEEXCHINGE:memberId"+member.getId())){
            throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_FREQUENCY);
        }else{
            this.redisTemplate.opsForValue().set("FOREIGN:ONLINEEXCHINGE:memberId"+member.getId(), 1, 3,  TimeUnit.SECONDS);
        }
        if(foreignService.onlineorder(member,bankId,buyCount,exchangeSwapCurrency,onineRate)){
            return  MessageRespResult.success();
        }
        throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_ERROR);
    }

    @PostMapping(value = "/orderlist")
    @ApiOperation(value = "订单列表", response = IPage.class)
    @ApiImplicitParams({
            @ApiImplicitParam(value = "订单类型 1(线下订单)， 2(线上订单)", name = "type", dataTypeClass = Integer.class),
            @ApiImplicitParam(value = "当前页", name = "current", dataTypeClass = Integer.class),
            @ApiImplicitParam(value = "每页条数", name = "size", dataTypeClass = Integer.class)
    })
    public MessageRespResult<IPage> orderlist(@MemberAccount Member member,Integer type,Integer current,Integer size) {
        if(current==null){
            current = 1;
        }
        if(size==null){
            size = 10;
        }
        // 线下订单
        if(type ==1 ){
            IPage<ForeignOfflineExchange> page = foreignService.offlineorderlist(member,  current, size);
            return MessageRespResult.success4Data(page);
        }else{
            //线上订单
            IPage<ForeignOnlineExchange> page =  foreignService.onlineorderlist( member,  current, size);
            return MessageRespResult.success4Data(page);
        }

    }
    //根据订单ID
    @PostMapping(value = "/cancelorder")
    @ApiOperation(value = "取消订单", response = MessageRespResult.class)
    @ApiImplicitParams({
            @ApiImplicitParam(value = "订单ID ", name = "orderId", dataTypeClass = Long.class)
    })

    public MessageRespResult cancelorder(@MemberAccount Member member,Long orderId) {
        if(orderId==null || orderId<=0){
            throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_ORDER_NOTEIST);
        }
        //防止重复提交
        if(redisTemplate.hasKey("FOREIGN:OFFLINECANCEL:memberId"+member.getId()+":ORDERID"+orderId)){
            throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_FREQUENCY);
        }else{
            this.redisTemplate.opsForValue().set("FOREIGN:OFFLINECANCEL:memberId"+member.getId()+":ORDERID"+orderId, 1, 3,  TimeUnit.SECONDS);
        }
        ForeignOfflineExchange exchange =  foreignOfflineExchangeService.getById(orderId);
        if(exchange==null) {
            throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_ORDER_NOTEIST);
        }
        if(foreignService.refound(member,exchange)){
            //处理扣除手续费
            try{
                if(foreignService.handleDrawServiceFee(exchange)){
                    log.info("扣除手续费失败{}",exchange);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return  MessageRespResult.success();
        }else{
            throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_ORDER_FOBIDCANCEL);
        }
    }
}
