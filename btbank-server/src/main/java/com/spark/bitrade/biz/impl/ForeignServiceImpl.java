package com.spark.bitrade.biz.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.spark.bitrade.api.vo.AliExchangeResult;
import com.spark.bitrade.api.vo.AliExchangeTransResult;
import com.spark.bitrade.api.vo.AliExchangeVo;
import com.spark.bitrade.api.vo.ExchangeVo;
import com.spark.bitrade.biz.ForeignConfigService;
import com.spark.bitrade.biz.ForeignService;
import com.spark.bitrade.config.AliyunConfig;
import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.constant.ForeignConst;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.WalletChangeRecord;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.*;
import com.spark.bitrade.repository.service.*;
import com.spark.bitrade.service.MemberWalletService;
import com.spark.bitrade.util.IdWorkByTwitter;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ForeignServiceImpl implements ForeignService {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ForeignCurrencyService foreignCurrencyService;
    @Autowired
    private MemberWalletService memberWalletService;
    @Autowired
    private ForeignOfflineExchangeService foreignOfflineExchangeService;
    @Autowired
    private ForeignOnlineExchangeService foreignOnlineExchangeService;
    @Autowired
    private IdWorkByTwitter idWorkByTwitter;
    @Autowired
    ForeignCashLocationService foreignCashLocationService;
    @Autowired
    private ForeignMemberBankinfoService foreignMemberBankinfoService;

    /**
     * 系统细腿扣除手续费
     *
     * @param foreignOfflineExchange
     */
    @Override
    @Transactional(rollbackFor =  Exception.class)
    public boolean handleSystemRefound(ForeignOfflineExchange foreignOfflineExchange,Long account){

        WalletChangeRecord refoundrecord =   memberWalletService.tryTrade(TransactionType.FOREIGN_EXCHANGE_SERVICE_CHARGE,account,"BT", "BT",
                foreignOfflineExchange.getBuyCount().negate(), foreignOfflineExchange.getId(),foreignOfflineExchange.getMemberId()+"取消换汇扣除手续费退款");
        if (refoundrecord == null) {
            log.error("取消购汇系统退款失败 orderId = {}, account = {}, amount = {}", foreignOfflineExchange.getId(),account,  foreignOfflineExchange.getServiceCharge().subtract(foreignOfflineExchange.getServiceCharge()).negate());
            return false;
        }
        try {
            UpdateWrapper<ForeignOfflineExchange> update = new UpdateWrapper<>();
            Date now = new Date();
            update.lambda()
                    .eq(ForeignOfflineExchange::getId, foreignOfflineExchange.getId())
                    .eq(ForeignOfflineExchange::getOrderStatus, 3)
                    .eq(ForeignOfflineExchange::getRefoundStatus, 2)
                    .set(ForeignOfflineExchange::getRefoundStatus, 3)
                    .set(ForeignOfflineExchange::getActualBtAmount, foreignOfflineExchange.getServiceCharge())
                    .set(ForeignOfflineExchange::getUpdateTime, now);
            if(foreignOfflineExchangeService.update(update)){
                boolean b = memberWalletService.confirmTrade(refoundrecord.getMemberId(), refoundrecord.getId());
                if (!b) {
                    log.error("取消购汇系统退款失败 record = {}", refoundrecord);
                    throw new BtBankException(CommonMsgCode.FAILURE);
                } else {
                    return true;
                }
            }
            throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_FAIL);
        } catch (RuntimeException ex) {
            log.error("取消购汇系统退款失败 txId = {}, err = {}", refoundrecord.getId(), ex.getMessage());
            boolean b = memberWalletService.rollbackTrade(refoundrecord.getMemberId(), refoundrecord.getId());
            log.info("取消购汇系统退款失败 result = {}, record = {}", b, refoundrecord);
            throw ex;
        }
    }
    /**
     * 保存银行卡
     *
     * @param bankInfo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean savebank(ForeignMemberBankinfo bankInfo) {
        return foreignMemberBankinfoService.save(bankInfo);
    }
    /**
     * 修改银行卡
     *
     * @param bankInfo
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatebank(ForeignMemberBankinfo bankInfo) {
        return foreignMemberBankinfoService.updateById(bankInfo);
    }

    /**
     * 扣手续费，失败后定时任务会重复执行
     *
     * @param foreignOfflineExchange
     */
    @Override
    @Transactional(rollbackFor =  Exception.class)
    public boolean handleDrawServiceFee(ForeignOfflineExchange  foreignOfflineExchange){
        //扣手续费失败
        //扣手续费从处理逻辑，如果失败定时任务会单独处理。
        WalletChangeRecord refoundrecord =   memberWalletService.tryTrade(TransactionType.FOREIGN_EXCHANGE_SERVICE_CHARGE,foreignOfflineExchange.getMemberId(),"BT", "BT",
                foreignOfflineExchange.getServiceCharge().negate(), foreignOfflineExchange.getId(),"取消换汇手续费");
        if (refoundrecord == null) {
            log.error("线下退款扣手续费失败 orderId = {}, member_id = {}, amount = {}", foreignOfflineExchange.getId(), foreignOfflineExchange.getMemberId(), foreignOfflineExchange.getActualBtAmount().negate());
            return false;
        }
        try {
            UpdateWrapper<ForeignOfflineExchange> update = new UpdateWrapper<>();
            Date now = new Date();
            update.lambda()
                    .eq(ForeignOfflineExchange::getId, foreignOfflineExchange.getId())
                    .eq(ForeignOfflineExchange::getOrderStatus, 3)
                    .eq(ForeignOfflineExchange::getRefoundStatus, 1)
                    .set(ForeignOfflineExchange::getRefoundStatus, 2)
                    .set(ForeignOfflineExchange::getUpdateTime, now);
            if(foreignOfflineExchangeService.update(update)){
                boolean b = memberWalletService.confirmTrade(refoundrecord.getMemberId(), refoundrecord.getId());
                if (!b) {
                    log.error("线下退款扣手续费失败 record = {}", refoundrecord);
                    throw new BtBankException(CommonMsgCode.FAILURE);
                } else {
                    return true;
                }
            }
            throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_FAIL);
        } catch (RuntimeException ex) {
            log.error("线下退款扣手续费失败 txId = {}, err = {}", refoundrecord.getId(), ex.getMessage());
            boolean b = memberWalletService.rollbackTrade(refoundrecord.getMemberId(), refoundrecord.getId());
            log.info("线下退款扣手续费失败 result = {}, record = {}", b, refoundrecord);
            throw ex;
        }
    }
    @Override
    @Transactional(rollbackFor =  Exception.class)
    public boolean refound(Member member, ForeignOfflineExchange foreignOfflineExchange) {

        foreignOfflineExchange.setCompleteStatus(3);
        if(foreignOfflineExchange==null){
            throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_ORDER_NOTEIST);
        }
        if(foreignOfflineExchange.getOrderStatus() == 1&&foreignOfflineExchange.getMemberId().equals(member.getId()) ){

            WalletChangeRecord record =   memberWalletService.tryTrade(TransactionType.FOREIGN_EXCHANGE_CANCEL,member.getId(),"BT", "BT",
                    foreignOfflineExchange.getActualBtAmount(), foreignOfflineExchange.getId(),"线下购汇扣除服务费退款");
            if (record == null) {
                log.error("线下退款失败 orderId = {}, member_id = {}, amount = {}", foreignOfflineExchange.getId(), member.getId(), foreignOfflineExchange.getActualBtAmount().negate());
                throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_FAIL);
            }
            try {
                UpdateWrapper<ForeignOfflineExchange> update = new UpdateWrapper<>();
                Date now = new Date();
                update.lambda()
                        .eq(ForeignOfflineExchange::getId, foreignOfflineExchange.getId())
                        .eq(ForeignOfflineExchange::getOrderStatus, 1)
                        .eq(ForeignOfflineExchange::getRefoundStatus, 0)
                        .set(ForeignOfflineExchange::getOrderStatus, 3)
                        .set(ForeignOfflineExchange::getRefoundStatus, 1)
                        .set(ForeignOfflineExchange::getUpdateTime, now);
                if(foreignOfflineExchangeService.update(update)){
                    boolean b = memberWalletService.confirmTrade(record.getMemberId(), record.getId());
                    if (!b) {
                        log.error("线下退款失败 record = {}", record);
                        throw new BtBankException(CommonMsgCode.FAILURE);
                    } else {

                      return true;
                    }
                }
                throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_FAIL);
            } catch (RuntimeException ex) {
                log.error("线下退款失败 txId = {}, err = {}", record.getId(), ex.getMessage());
                boolean b = memberWalletService.rollbackTrade(record.getMemberId(), record.getId());
                log.info("线下退款失败 result = {}, record = {}", b, record);
                throw ex;
            }
        }else if(foreignOfflineExchange.getOrderStatus() == 2){
            throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_ORDER_FOBIDCANCEL);
        }else if(foreignOfflineExchange.getOrderStatus() == 3){
            throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_ORDER_CANCELED);
        }
        throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_ORDER_FOBIDCANCEL);
    }



    @Override
    public IPage<ForeignOfflineExchange> offlineorderlist(Member member, Integer current, Integer size) {
        return  foreignOfflineExchangeService.orderlist( member,  current, size);
    }

    @Override
    public IPage<ForeignOnlineExchange> onlineorderlist(Member member, Integer current, Integer size) {
        return  foreignOnlineExchangeService.orderlist( member,  current, size);
    }

    @Override
    @Transactional(rollbackFor =  Exception.class)
    public boolean onlineorder(Member member, Long bankId, BigDecimal buyCount, String exchangeSwapCurrency,String onineRate) {
        ForeignOnlineExchange foreignOnlineExchange = new  ForeignOnlineExchange();
        foreignOnlineExchange.setBankId(bankId);
        foreignOnlineExchange.setBuyCount(buyCount);
        foreignOnlineExchange.setExchangeSwapCurrency(exchangeSwapCurrency);
        ForeignMemberBankinfo foreignMemberBankinfo= foreignMemberBankinfoService.getById(bankId);
        if(foreignMemberBankinfo==null||foreignMemberBankinfo.getMemberId()==null||!foreignMemberBankinfo.getMemberId().equals(member.getId())){
            throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_BANK_NOTEXIST);
        }
        foreignOnlineExchange.setMemberId(member.getId());
        foreignOnlineExchange.setCreateTime(new Date());
        foreignOnlineExchange.setPhoneNumber(member.getMobilePhone());
        foreignOnlineExchange.setRealName(member.getRealName());
        foreignOnlineExchange.setCompleteStatus(0);
        foreignOnlineExchange.setCollectStatus(0);
        foreignOnlineExchange.setOrderStatus(1);
        BigDecimal serviceFee = foreignOnlineExchange.getBuyCount().multiply(new BigDecimal(onineRate)).setScale(8,BigDecimal.ROUND_DOWN);
        BigDecimal total = serviceFee.add(foreignOnlineExchange.getBuyCount()).setScale(8,BigDecimal.ROUND_DOWN);
        foreignOnlineExchange.setServiceCharge(serviceFee);
        foreignOnlineExchange.setActualBtAmount(total);
        if(StringUtils.isEmpty(ForeignConst.otherCurrency.get(exchangeSwapCurrency))){
            String url = ForeignConst.getForeignexchange(foreignOnlineExchange.getExchangeSwapCurrency(),foreignOnlineExchange.getBuyCount());
            ResponseEntity<String> responseEntity = null;
            try{
                responseEntity = restTemplate.getForEntity(url, String.class);
            }catch (Exception e){
                e.printStackTrace();
                throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_FAIL);
            }
            if(responseEntity.getStatusCode()== HttpStatus.OK){
                if(!StringUtils.isEmpty(responseEntity.getBody())){
                    ExchangeVo exchangeVo = JSON.parseObject(responseEntity.getBody(),ExchangeVo.class);
                    if(exchangeVo!=null&&exchangeVo.getValue()!=null){
                        foreignOnlineExchange.setExchangeSwapCount(exchangeVo.getValue());
                        if(foreignOnlineExchange.getBuyCount()!=null){
                            foreignOnlineExchange.setExchangeRate(exchangeVo.getValue().divide(foreignOnlineExchange.getBuyCount(),8,BigDecimal.ROUND_DOWN));
                        }
                    }
                }else{
                    throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_FAIL);
                }
            }else{
                throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_FAIL);
            }
        }else{
            //第二种API获取汇率
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "APPCODE " + ForeignConst.APPCODE);
            HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
            ResponseEntity<String> resEntity = restTemplate.exchange(ForeignConst.getOtherForeignexchange(exchangeSwapCurrency,foreignOnlineExchange.getBuyCount()), HttpMethod.GET, requestEntity, String.class);
            if(resEntity.getStatusCode()== HttpStatus.OK){
                if(StringUtils.isNotBlank(resEntity.getBody())) {
                    AliExchangeTransResult result = JSON.parseObject(resEntity.getBody(), AliExchangeTransResult.class);
                    if(result!=null&&result.getShowapi_res_code()!=null&&result.getShowapi_res_code()==0){
                        if(result.getShowapi_res_body().getRet_code()!=null&&result.getShowapi_res_body().getRet_code()==0&&result.getShowapi_res_body().getMoney()!=null){
                            foreignOnlineExchange.setExchangeSwapCount(new BigDecimal(result.getShowapi_res_body().getMoney()));
                            if(foreignOnlineExchange.getBuyCount()!=null){
                                foreignOnlineExchange.setExchangeRate(new BigDecimal(result.getShowapi_res_body().getMoney()).divide(foreignOnlineExchange.getBuyCount(),8,BigDecimal.ROUND_DOWN));
                            }
                        }
                    }
                }
            }
        }
        if(foreignOnlineExchange.getExchangeRate()==null||foreignOnlineExchange.getExchangeRate().compareTo(BigDecimal.ZERO)<=0){
            throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_FAIL);
        }
        ForeignCurrency foreignCurrency =  foreignCurrencyService.getByEnName(exchangeSwapCurrency);
        if(foreignCurrency!=null&&!StringUtils.isEmpty(foreignCurrency.getCnmane())){
            foreignOnlineExchange.setExchangeCurrencyName(foreignCurrency.getCnmane());
        }
        foreignOnlineExchange.setId(idWorkByTwitter.nextId());
        WalletChangeRecord record =   memberWalletService.tryTrade(TransactionType.FOREIGN_EXCHANGE_ONLINE,member.getId(),"BT", "BT",
                foreignOnlineExchange.getActualBtAmount().negate(), foreignOnlineExchange.getId(),"线上购汇");
        if (record == null) {
            log.error("线上换汇扣款失败 txId = {}, member_id = {}, amount = {}", foreignOnlineExchange.getId(), member.getId(), foreignOnlineExchange.getActualBtAmount().negate());
            throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_FAIL);
        }
        foreignOnlineExchange.setRefId(record.getId());
        try {
            if(foreignOnlineExchangeService.save(foreignOnlineExchange)){
                boolean b = memberWalletService.confirmTrade(record.getMemberId(), record.getId());
                if (!b) {
                    log.error("线上换汇扣款失败 record = {}", record);
                    throw new BtBankException(CommonMsgCode.FAILURE);
                } else {
                    return true;
                }
            }
            throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_FAIL);
        } catch (RuntimeException ex) {
            log.error("线上换汇扣款失败 txId = {}, err = {}", record.getId(), ex.getMessage());
            boolean b = memberWalletService.rollbackTrade(record.getMemberId(), record.getId());
            log.info("线上换汇扣款失败 result = {}, record = {}", b, record);
            throw ex;
        }
    }

    @Override
    @Transactional(rollbackFor =  Exception.class)
    public boolean offlineorder(Member member, String address, Long addressId, BigDecimal buyCount, String exchangeSwapCurrency, String offineRate) {
        ForeignOfflineExchange foreignOfflineExchange = new ForeignOfflineExchange();
        foreignOfflineExchange.setBuyCount(buyCount);
        foreignOfflineExchange.setAddressId(addressId);
        foreignOfflineExchange.setMemberId(member.getId());
        foreignOfflineExchange.setCreateTime(new Date());
        foreignOfflineExchange.setPhoneNumber(member.getMobilePhone());
        foreignOfflineExchange.setRealName(member.getRealName());
        foreignOfflineExchange.setRefoundStatus(0);
        foreignOfflineExchange.setCompleteStatus(0);
        foreignOfflineExchange.setCollectStatus(0);
        foreignOfflineExchange.setExchangeSwapCurrency(exchangeSwapCurrency);
        foreignOfflineExchange.setOrderStatus(1);
        foreignOfflineExchange.setId(idWorkByTwitter.nextId());
        ForeignCashLocation foreignCashLocation = foreignCashLocationService.getById(addressId);
        if(foreignCashLocation==null){
            throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_ADDRESS_NOTEXIST);
        }
        foreignOfflineExchange.setAddress(foreignCashLocation.getLocation());
        ForeignCurrency foreignCurrency =  foreignCurrencyService.getByEnName(exchangeSwapCurrency);
        if(foreignCurrency!=null&&!StringUtils.isEmpty(foreignCurrency.getCnmane())){
            foreignOfflineExchange.setExchangeCurrencyName(foreignCurrency.getCnmane());
        }
        BigDecimal rate = foreignOfflineExchange.getBuyCount().multiply(new BigDecimal(offineRate)).setScale(8,BigDecimal.ROUND_DOWN);
        BigDecimal total = rate.add(foreignOfflineExchange.getBuyCount()).setScale(8,BigDecimal.ROUND_DOWN);
        foreignOfflineExchange.setServiceCharge(rate);
        foreignOfflineExchange.setActualBtAmount(total);
        if(StringUtils.isEmpty(ForeignConst.otherCurrency.get(exchangeSwapCurrency))){
            String url = ForeignConst.getForeignexchange(foreignOfflineExchange.getExchangeSwapCurrency(),foreignOfflineExchange.getBuyCount());
            ResponseEntity<String> responseEntity = null;
            try{
                responseEntity = restTemplate.getForEntity(url, String.class);
            }catch (Exception e){
                e.printStackTrace();
                throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_FAIL);
            }
            if(responseEntity!=null&&responseEntity.getStatusCode()== HttpStatus.OK){
                if(!StringUtils.isEmpty(responseEntity.getBody())){
                    ExchangeVo exchangeVo = JSON.parseObject(responseEntity.getBody(),ExchangeVo.class);
                    if(exchangeVo!=null&&exchangeVo.getValue()!=null){
                        foreignOfflineExchange.setExchangeSwapCount(exchangeVo.getValue());
                        if(foreignOfflineExchange.getBuyCount()!=null){
                            foreignOfflineExchange.setExchangeRate( exchangeVo.getValue().divide(foreignOfflineExchange.getBuyCount(),8,BigDecimal.ROUND_DOWN));
                        }
                    }
                }
            }
        }else{
            //第二种换汇的方式
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "APPCODE " + ForeignConst.APPCODE);
            HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
            ResponseEntity<String> resEntity = restTemplate.exchange(ForeignConst.getOtherForeignexchange(exchangeSwapCurrency,foreignOfflineExchange.getBuyCount()), HttpMethod.GET, requestEntity, String.class);
            if(resEntity.getStatusCode()== HttpStatus.OK){
                if(StringUtils.isNotBlank(resEntity.getBody())) {
                    AliExchangeTransResult result = JSON.parseObject(resEntity.getBody(), AliExchangeTransResult.class);
                    if(result!=null&&result.getShowapi_res_code()!=null&&result.getShowapi_res_code()==0){
                        if(result.getShowapi_res_body().getRet_code()!=null&&result.getShowapi_res_body().getRet_code()==0&&result.getShowapi_res_body().getMoney()!=null){
                            foreignOfflineExchange.setExchangeSwapCount(new BigDecimal(result.getShowapi_res_body().getMoney()));
                            if(foreignOfflineExchange.getBuyCount()!=null){
                                foreignOfflineExchange.setExchangeRate(new BigDecimal(result.getShowapi_res_body().getMoney()).divide(foreignOfflineExchange.getBuyCount(),8,BigDecimal.ROUND_DOWN));
                            }
                        }
                    }
                }
            }
        }
        if(foreignOfflineExchange.getExchangeRate()==null||foreignOfflineExchange.getExchangeRate().compareTo(BigDecimal.ZERO)<=0){
            throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_FAIL);
        }
        WalletChangeRecord record =   memberWalletService.tryTrade(TransactionType.FOREIGN_EXCHANGE_OFFLINE,member.getId(),"BT", "BT",
                foreignOfflineExchange.getActualBtAmount().negate(), foreignOfflineExchange.getId(),"线下取现");
        if (record == null) {
            log.error("线下换汇扣款失败 txId = {}, member_id = {}, amount = {}", foreignOfflineExchange.getId(),member.getId(), foreignOfflineExchange.getActualBtAmount().negate());
            throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_FAIL);
        }
        foreignOfflineExchange.setRefId(record.getId());
        try {
            if(foreignOfflineExchangeService.save(foreignOfflineExchange)){
                boolean b = memberWalletService.confirmTrade(record.getMemberId(), record.getId());
                if (!b) {
                    log.error("线下换汇扣款失败 record = {}", record);
                    throw new BtBankException(CommonMsgCode.FAILURE);
                } else {
                    return true;
                }
            }
            throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_FAIL);
        } catch (RuntimeException ex) {
            log.error("线下换汇扣款失败 txId = {}, err = {}", record.getId(), ex.getMessage());
            boolean b = memberWalletService.rollbackTrade(record.getMemberId(), record.getId());
            log.info("线下换汇扣款失败 result = {}, record = {}", b, record);
            throw ex;
        }
    }

    @Override
    @Transactional(rollbackFor =  Exception.class)
    public boolean collectoffline(ForeignOfflineExchange exchange, Long account) {
        WalletChangeRecord record = memberWalletService.tryTrade(TransactionType.FOREIGN_EXCHANGE_COLLECT, account,
                "BT", "BT", exchange.getActualBtAmount(), exchange.getId(), exchange.getMemberId()+"线下取现归集");
        if (record == null) {
            log.error("线下购汇归集失败 exchange.id = {}, member_id = {}, amount = {}", exchange.getId(), record.getMemberId(), exchange.getActualBtAmount());
            return false;
        }
        try{
            // 1 更新活动
            Boolean collectRecord = foreignOfflineExchangeService.lambdaUpdate()
                    .eq(ForeignOfflineExchange::getCollectStatus,0)
                    .eq(ForeignOfflineExchange::getId, exchange.getId())
                    .set(ForeignOfflineExchange::getCollectStatus,1).update();
            if(collectRecord){
                boolean b = memberWalletService.confirmTrade(record.getMemberId(), record.getId());
                if (!b){
                    log.error("线下购汇归集失败 record = {}", record);
                    throw new BtBankException(CommonMsgCode.FAILURE);
                }
                return true;
            }else{
                throw new BtBankException(CommonMsgCode.FAILURE);
            }

        }catch (RuntimeException ex){
            log.error("线下购汇归集失败 txId = {}, err = {}", record.getId(), ex.getMessage());
            boolean b = memberWalletService.rollbackTrade(record.getMemberId(), record.getId());
            log.info("线下购汇归集失败 result = {}, record = {}", b, record);
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor =  Exception.class)
    public boolean collectonline(ForeignOnlineExchange exchange, Long account) {
        WalletChangeRecord record = memberWalletService.tryTrade(TransactionType.FOREIGN_EXCHANGE_COLLECT, account,
                "BT", "BT", exchange.getActualBtAmount(), exchange.getId(), exchange.getMemberId()+"线上购汇归集");
        if (record == null) {
            log.error("线上购汇归集失败 exchange.id = {}, member_id = {}, amount = {}", exchange.getId(), record.getMemberId(), exchange.getActualBtAmount());
            return false;
        }
        try{
            // 1 更新活动
            Boolean collectRecord = foreignOnlineExchangeService.lambdaUpdate()
                    .eq(ForeignOnlineExchange::getCollectStatus,0)
                    .eq(ForeignOnlineExchange::getId, exchange.getId())
                    .set(ForeignOnlineExchange::getCollectStatus,1).update();
            if(collectRecord){
                boolean b = memberWalletService.confirmTrade(record.getMemberId(), record.getId());
                if (!b){
                    log.error("线上购汇归集失败 record = {}", record);
                    throw new BtBankException(CommonMsgCode.FAILURE);
                }
                return true;
            }else{
                throw new BtBankException(CommonMsgCode.FAILURE);
            }

        }catch (RuntimeException ex){
            log.error("线上购汇归集失败 txId = {}, err = {}", record.getId(), ex.getMessage());
            boolean b = memberWalletService.rollbackTrade(record.getMemberId(), record.getId());
            log.info("线上购汇归集失败 result = {}, record = {}", b, record);
        }
        return false;
    }

    @Override
    public List<ForeignOfflineExchange> offlineCollectOrderList(){
        QueryWrapper<ForeignOfflineExchange> query = new QueryWrapper<>();
        query.lambda().eq(ForeignOfflineExchange::getCollectStatus,0);
        return foreignOfflineExchangeService.list(query);
    }
    @Override
    public List<ForeignOnlineExchange> onlineCollectOrderList(){
        QueryWrapper<ForeignOnlineExchange> query = new QueryWrapper<>();
        query.lambda().eq(ForeignOnlineExchange::getCollectStatus,0);
        return foreignOnlineExchangeService.list(query);
    }
    @Override
    public List<ForeignOfflineExchange> offlinedrawServiceFeeList(){
        QueryWrapper<ForeignOfflineExchange> query = new QueryWrapper<>();
        query.lambda().eq(ForeignOfflineExchange::getOrderStatus,3)
                .eq(ForeignOfflineExchange::getRefoundStatus,1);
        return foreignOfflineExchangeService.list(query);
    }
    @Override
    public List<ForeignOfflineExchange> offlineSystemRefoundList(){
        QueryWrapper<ForeignOfflineExchange> query = new QueryWrapper<>();
        query.lambda().eq(ForeignOfflineExchange::getOrderStatus,3)
                .eq(ForeignOfflineExchange::getRefoundStatus,2);
        return foreignOfflineExchangeService.list(query);
    }
    public ForeignServiceImpl getService() {
        return SpringContextUtil.getBean(ForeignServiceImpl.class);
    }
}
