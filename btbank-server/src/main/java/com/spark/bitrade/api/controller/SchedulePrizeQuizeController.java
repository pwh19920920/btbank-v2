package com.spark.bitrade.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.utils.StringUtils;
import com.spark.bitrade.api.vo.CoinThumb;
import com.spark.bitrade.biz.PrizeQuizeService;
import com.spark.bitrade.constant.BtBankPrizeQuizConfig;
import com.spark.bitrade.repository.entity.PrizeQuizeRecord;
import com.spark.bitrade.repository.service.PrizeQuizeRecordService;
import com.spark.bitrade.service.ExchangeFastApiService;
import com.spark.bitrade.util.DateUtil;
import com.spark.bitrade.util.MessageRespResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by mahao on 2020/1/2.
 */
@Slf4j
@Api(tags = {"竞猜活动定时任务"})
@RequestMapping(path = "inner/schedulePrizQuize", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@AllArgsConstructor
public class SchedulePrizeQuizeController {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private PrizeQuizeService prizeQuizeService;
    private PrizeQuizeRecordService prizeQuizeRecordService;
    private ExchangeFastApiService  exchangeFastApiService;
    @ApiOperation(value = "自动生成活动的记录")
    @PostMapping(value = "generatePrizeQuizeRecord")
    public MessageRespResult<String> generatePrizeQuizeRecord() {
        MessageRespResult<String> result = new MessageRespResult();
        String prizeQuizSwitch = (String)prizeQuizeService.getConfig(BtBankPrizeQuizConfig.PRIZE_QUIZ_SWITCH);
        Integer  prizeQuizStartTime = Integer.parseInt((String)prizeQuizeService.getConfig(BtBankPrizeQuizConfig.PRIZE_QUIZ_TIME));
        if(StringUtils.isEmpty(prizeQuizSwitch)){
            throw new IllegalArgumentException("未找到竞猜活动开始的PRIZE_QUIZ_SWITCH 配置");
        }
        if(prizeQuizStartTime==null){
            throw new IllegalArgumentException("未找到竞猜活动开始时间的PRIZE_QUIZ_TIME 配置");
        }
        Date current = new Date();
        PrizeQuizeRecord prizeQuizeRecord  = new PrizeQuizeRecord();
        prizeQuizeRecord.setCoinUnit("BTC");
        Calendar calendar =  Calendar.getInstance();
        //一天的开始时间 yyyy:MM:dd 00:00:00
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        calendar.add(Calendar.SECOND,prizeQuizStartTime );
        // 开始时间
        prizeQuizeRecord.setStartTime(calendar.getTime());

        //查询昨天活动并修改结束标志
        PrizeQuizeRecord prizeQuizeRecordOld =prizeQuizeService.queryRecordByStartTime("BTC",DateUtils.addDays(calendar.getTime(),-1));
        if(prizeQuizeRecordOld!=null){
            prizeQuizeRecordOld.setType(2);
            prizeQuizeRecordService.updateById(prizeQuizeRecordOld);
        }



        //查询当天活动是否生成如果已经生成就退出生成活动
        Boolean existRecord = prizeQuizeService.existRecord("BTC",calendar.getTime());
        if(!existRecord){
            // 结束时间
            calendar.set(Calendar.HOUR_OF_DAY,22);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);
            prizeQuizeRecord.setFinalizeTime(calendar.getTime());
            prizeQuizeRecord.setCreateTime(current );
            prizeQuizeRecord.setUpdateTime(current);
            prizeQuizeRecord.setType(1);

            int  count = prizeQuizeService.getCntByCoinUnit("BTC")+1;


            //开奖时间
            calendar.set(Calendar.HOUR_OF_DAY,12);
            calendar.set(Calendar.MINUTE,0);
            calendar.set(Calendar.SECOND,0);
            Date rewardTime = DateUtils.addDays(calendar.getTime(),1);
            prizeQuizeRecord.setRewardResultTime(rewardTime);

            prizeQuizeRecord.setPrizeQuizCount(count);
            prizeQuizeRecord.setDownNum(0);
            prizeQuizeRecord.setTotalNum(0);
            prizeQuizeRecord.setUpNum(0);
            prizeQuizeRecord.setTotalAmount(BigDecimal.ZERO);
            prizeQuizeRecord.setUpAmount(BigDecimal.ZERO);
            prizeQuizeRecord.setDownAmount(BigDecimal.ZERO);
            prizeQuizeRecord.setRewardAmount(BigDecimal.ZERO);
            prizeQuizeRecord.setMaxReward(BigDecimal.ZERO);
            prizeQuizeRecord.setPlatformAmount(BigDecimal.ZERO);
            prizeQuizeRecord.setTotalAmount(BigDecimal.ZERO);
            prizeQuizeRecord.setCurrentAmout(BigDecimal.ZERO);
            if("0".equals(prizeQuizSwitch)){
                prizeQuizeRecord.setType(3);
            }
            prizeQuizeRecordService.save(prizeQuizeRecord);
            result.setCode(0);
            result.setMessage("定时生成竞猜活动记录成功");
            log.info("定时生成竞猜活动记录成功");
            return result;
        }else{
            log.info("竞猜活动已经存在");
        }

        result.setCode(1);
        result.setMessage("定时生成竞猜活动记录失败");
        return result;
    }
    @ApiOperation(value = "更新BTC竞猜价格")
    @PostMapping(value = "updateAmount")
    public MessageRespResult<String> updateAmount(String btcPrice) {
        MessageRespResult<String> result = new MessageRespResult();
        String prizeQuizSwitch = (String)prizeQuizeService.getConfig(BtBankPrizeQuizConfig.PRIZE_QUIZ_SWITCH);
        Integer  prizeQuizStartTime = Integer.parseInt((String)prizeQuizeService.getConfig(BtBankPrizeQuizConfig.PRIZE_QUIZ_TIME));
        if(StringUtils.isEmpty(prizeQuizSwitch)){
            throw new IllegalArgumentException("未找到竞猜活动开始的PRIZE_QUIZ_SWITCH 配置");
        }
        if(prizeQuizStartTime==null){
            throw new IllegalArgumentException("未找到竞猜活动开始时间的PRIZE_QUIZ_TIME 配置");
        }
        //定时获取
        MessageRespResult<String> qoinThumbret =  exchangeFastApiService.thumb("BTC");
        // MessageRespResult<String> qoinThumbret = new MessageRespResult<String>();
        //qoinThumbret.setCode(0);
        // qoinThumbret.setData(btcPrice);
        if(qoinThumbret.isSuccess()&&!StringUtils.isEmpty(qoinThumbret.getData())){
            CoinThumb coinThumb = JSON.parseObject(qoinThumbret.getData(),CoinThumb.class);
            if(coinThumb!=null&&coinThumb.getClose()!=null){
                //更新今天结束价格和明天开始价格
                Calendar calendar =  Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY,0);
                calendar.set(Calendar.MINUTE,0);
                calendar.set(Calendar.SECOND,0);
                calendar.set(Calendar.MILLISECOND,0);
                calendar.add(Calendar.SECOND,prizeQuizStartTime );
                // 开始时间
                Date startTime = calendar.getTime();
                PrizeQuizeRecord prizeQuizeRecordCurrent =prizeQuizeService.queryRecordByStartTime("BTC",startTime);
                if(prizeQuizeRecordCurrent!=null){
                    prizeQuizeRecordCurrent.setCurrentAmout(coinThumb.getClose());
                    prizeQuizeRecordService.updateById(prizeQuizeRecordCurrent);
                }else{
                    log.debug("今天活动记录未生成，获取价格失败！");
                }
                // 昨天开始时间
                Date yestodayTime = DateUtil.addDay(startTime,-1);
                PrizeQuizeRecord prizeQuizeRecordOld =prizeQuizeService.queryRecordByStartTime("BTC",yestodayTime);
                if(prizeQuizeRecordOld!=null){
                    prizeQuizeRecordOld.setTomorrowAmount(coinThumb.getClose());
                    // 涨
                    if(prizeQuizeRecordOld.getTomorrowAmount().compareTo(prizeQuizeRecordOld.getCurrentAmout())==1){
                        prizeQuizeRecordOld.setPriQuizeResult(1);
                    }else{
                        //跌
                        prizeQuizeRecordOld.setPriQuizeResult(0);
                    }
                    prizeQuizeRecordOld.setType(2);
                    prizeQuizeRecordService.updateById(prizeQuizeRecordOld);
                    result.setCode(0);
                    result.setMessage("");
                    return result;
                }else{
                    log.debug("昨天开奖的记录未生成，不计算开奖结果！");
                }

            }
            log.info("解析BTC价格失败");
        }else{
            log.info("获取BTC价格失败");
        }
        /*if("0".equals(prizeQuizSwitch)){
        }*/
        result.setCode(1);
        result.setMessage("");
        return result;
    }
    @ApiOperation(value = "更新用户竞猜结果和统计收益")
    @PostMapping(value = "updateMinerResult")
    public MessageRespResult<String> updateMinerResult() {
        MessageRespResult<String> result = new MessageRespResult();
        PrizeQuizeRecord prizeQuizeRecord = prizeQuizeService.getPrizeQuizeRecord();
        if(prizeQuizeRecord!=null){
            //更新记录
            prizeQuizeService.updateMinerResult(prizeQuizeRecord);
            result.setCode(0);
            result.setMessage("");
            return result;
        }else{
            log.info("获取活动记录失败");
        }

        result.setCode(1);
        result.setMessage("");
        return result;
    }
    @ApiOperation(value = "统一扣款")
    @PostMapping(value = "drawAmount")
    public MessageRespResult<String> drawAmount() {
        MessageRespResult<String> result = new MessageRespResult();
        PrizeQuizeRecord prizeQuizeRecord = prizeQuizeService.getPrizeQuizeRecord();
        if(prizeQuizeRecord!=null){
            //扣用户锁仓金额
            prizeQuizeService.collect(prizeQuizeRecord);
            result.setCode(0);
            result.setMessage("");
            return result;
        }else{
            log.info("获取活动记录失败");
        }
        result.setCode(1);
        result.setMessage("");
        return result;
    }
    @ApiOperation(value = "统一释放")
    @PostMapping(value = "realseAmount")
    public MessageRespResult<String> realseAmount() {
        MessageRespResult<String> result = new MessageRespResult();
        PrizeQuizeRecord prizeQuizeRecord = prizeQuizeService.getPrizeQuizeRecord();
        if(prizeQuizeRecord!=null){
            //扣用户锁仓金额
            prizeQuizeService.release(prizeQuizeRecord);
            result.setCode(0);
            result.setMessage("释放成功");
            return result;
        }else{
            log.info("获取活动记录失败");
        }
        result.setCode(1);
        result.setMessage("");
        return result;
    }

}
