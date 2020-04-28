package com.spark.bitrade.biz.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.utils.StringUtils;
import com.spark.bitrade.api.vo.CoinThumb;
import com.spark.bitrade.biz.PrizeQuizeService;
import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.constant.BtBankPrizeQuizConfig;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.BtBankFinancialBalance;
import com.spark.bitrade.repository.entity.MinerPrizeQuizeTransaction;
import com.spark.bitrade.repository.entity.PrizeQuizeRecord;
import com.spark.bitrade.repository.service.BtBankFinancialBalanceService;
import com.spark.bitrade.repository.service.FinancialActivityJoinDetailsService;
import com.spark.bitrade.repository.service.PrizeQuizeRecordService;
import com.spark.bitrade.service.IBtbankServerService;
import com.spark.bitrade.util.DateUtil;
import com.spark.bitrade.util.MessageRespResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2020/1/2.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Slf4j
public class PrizeQuizeTest {
    @Autowired
    private IBtbankServerService btbankServerService;
    @Autowired
    private PrizeQuizeService prizeQuizeService;
    @Autowired
    private PrizeQuizeRecordService prizeQuizeRecordService;
    @Autowired
    private BtBankFinancialBalanceService btBankFinancialBalanceService;
    @Autowired
    private FinancialActivityServiceImpl financialActivityService;

    @Autowired
    private FinancialActivityJoinDetailsService financialActivityJoinDetailsService;
    @Test
    public void getStartTime(){
        Long  minTransferAmount = Long.parseLong((String)prizeQuizeService.getConfig(BtBankPrizeQuizConfig.PRIZE_QUIZ_TIME));
        System.out.print(minTransferAmount);
    }
    @Test
    public void testgetCntByCoinUnit(){
        int  count = prizeQuizeService.getCntByCoinUnit("BTC");
        System.out.print("活动场次 " +count);
    }


    @Test
    public void createRecord(){
        btbankServerService.generatePrizeQuizeRecord();
    }

    @Test
    public void updateAmount(){
        Long prieQuizeId = 3L;
        MinerPrizeQuizeTransaction minerPrizeQuizeTransaction = new MinerPrizeQuizeTransaction();
        minerPrizeQuizeTransaction.setAmount(new BigDecimal(500));
        minerPrizeQuizeTransaction.setPrizeQuizeType(0);

        PrizeQuizeRecord prizeQuizeRecord = prizeQuizeRecordService.getById(prieQuizeId);
        if(prizeQuizeRecord!=null){
            Boolean addprizeQuizeRecordStatic = false;
            // 跌
            if(minerPrizeQuizeTransaction.getPrizeQuizeType()==0){
                addprizeQuizeRecordStatic = prizeQuizeRecordService.lambdaUpdate()
                        .setSql("total_num = total_num +" + 1 )
                        .setSql("down_num = down_num +" + 1 )
                        .setSql("total_num = total_num +" + 1 )
                        .setSql("total_amount = total_amount +" +  minerPrizeQuizeTransaction.getAmount() )
                        .setSql("down_amount = down_amount +" +  minerPrizeQuizeTransaction.getAmount() )
                        .eq(PrizeQuizeRecord::getId, prieQuizeId).update();
                //涨
            }else if(minerPrizeQuizeTransaction.getPrizeQuizeType()==1){
                addprizeQuizeRecordStatic = prizeQuizeRecordService.lambdaUpdate()
                        .setSql("up_num = up_num +" + 1 )
                        .setSql("down_num = down_num +" + 1 )
                        .setSql("total_num = total_num +" + 1 )
                        .setSql("total_amount = total_amount +" +  minerPrizeQuizeTransaction.getAmount() )
                        .setSql("up_amount = up_amount +" +  minerPrizeQuizeTransaction.getAmount() )
                        .eq(PrizeQuizeRecord::getId, prieQuizeId).update();
            }
            System.out.print("更新结果 addprizeQuizeRecordStatic = "+addprizeQuizeRecordStatic);
        }
    }
    @Test
    public void updateQuizResult(){
        MessageRespResult<String> result = new MessageRespResult();
        String prizeQuizSwitch = (String)prizeQuizeService.getConfig(BtBankPrizeQuizConfig.PRIZE_QUIZ_SWITCH);
        Integer  prizeQuizStartTime = Integer.parseInt((String)prizeQuizeService.getConfig(BtBankPrizeQuizConfig.PRIZE_QUIZ_TIME));
        if(StringUtils.isEmpty(prizeQuizSwitch)){
            throw new IllegalArgumentException("未找到竞猜活动开始的PRIZE_QUIZ_SWITCH 配置");
        }
        if(prizeQuizStartTime==null){
            throw new IllegalArgumentException("未找到竞猜活动开始时间的PRIZE_QUIZ_TIME 配置");
        }
        if("1".equals(prizeQuizSwitch)){
            //定时获取
            // MessageRespResult<String> qoinThumbret =  exchangeFastApiService.thumb("BTC/BT");
            CoinThumb coinThumb = new CoinThumb();
            coinThumb.setSymbol("BTC/BT");
            coinThumb.setClose(new BigDecimal(2.17));
            MessageRespResult<String> qoinThumbret = new MessageRespResult();
            qoinThumbret.setCode(0);
            qoinThumbret.setData(JSON.toJSONString(coinThumb));

            if(qoinThumbret.isSuccess()&&!StringUtils.isEmpty(qoinThumbret.getData())){
                CoinThumb coinThumbret = JSON.parseObject(qoinThumbret.getData(),CoinThumb.class);
                if(coinThumbret!=null&&coinThumbret.getClose()!=null){

                    //更新今天结束价格和明天开始价格
                    Calendar calendar =  Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY,0);
                    calendar.set(Calendar.MINUTE,0);
                    calendar.set(Calendar.SECOND,0);
                    calendar.set(Calendar.MILLISECOND,0);
                    calendar.add(Calendar.SECOND,prizeQuizStartTime );
                    // 开始时间
                    Date startTime = calendar.getTime();
                    PrizeQuizeRecord prizeQuizeRecordCurrent =prizeQuizeService.queryRecordByStartTime("BT",startTime);
                    if(prizeQuizeRecordCurrent!=null){
                        prizeQuizeRecordCurrent.setCurrentAmout(coinThumbret.getClose());
                        prizeQuizeRecordService.updateById(prizeQuizeRecordCurrent);
                    }else{
                        log.debug("今天活动记录未生成，获取价格失败！");
                    }
                    // 昨天开始时间
                    Date yestodayTime = DateUtil.addDay(startTime,-1);
                    PrizeQuizeRecord prizeQuizeRecordOld =prizeQuizeService.queryRecordByStartTime("BT",yestodayTime);
                    if(prizeQuizeRecordOld!=null){
                        prizeQuizeRecordOld.setTomorrowAmount(coinThumbret.getClose());
                        // 涨
                        if(prizeQuizeRecordOld.getCurrentAmout().compareTo(prizeQuizeRecordOld.getTomorrowAmount())==1){
                            prizeQuizeRecordOld.setPriQuizeResult(1);
                        }else{
                            //跌
                            prizeQuizeRecordOld.setPriQuizeResult(0);
                        }
                        prizeQuizeRecordOld.setType(2);
                        prizeQuizeRecordService.updateById(prizeQuizeRecordOld);
                    }else{
                        log.debug("昨天开奖的记录未生成，不计算开奖结果！");
                    }

                }
                log.info("解析BTC价格失败");
            }else{
                log.info("获取BTC价格失败");
            }

        }

    }
    @Test
    public void TestUpdatePrizeQuizeResult(){
        PrizeQuizeRecord prizeQuizeRecord = prizeQuizeService.getPrizeQuizeRecord();
        if(prizeQuizeRecord!=null){
            //更新记录
            prizeQuizeService.updateMinerResult(prizeQuizeRecord);
        }else{
            log.info("获取活动记录失败");
        }
    }

    @Test
    public void TestDraw(){
        PrizeQuizeRecord prizeQuizeRecord = prizeQuizeService.getPrizeQuizeRecord();
        if(prizeQuizeRecord!=null){
            //更新记录
            //扣用户锁仓金额
            prizeQuizeService.collect(prizeQuizeRecord);
        }else{
            log.info("获取活动记录失败");
        }

    }
    @Test
    public void TestRealse(){
        PrizeQuizeRecord prizeQuizeRecord = prizeQuizeService.getPrizeQuizeRecord();
        if(prizeQuizeRecord!=null){
            //更新记录
            //扣用户锁仓金额
            prizeQuizeService.release(prizeQuizeRecord);
        }else{
            log.info("获取活动记录失败");
        }

    }
    @Test
    public void TestCnt(){

        int  count = prizeQuizeService.getCntByCoinUnit("BTC")+1;
        System.out.print("count = "+count);
    }
    @Test
    public void TestMemberID(){
        if(!financialActivityService.createBtBankFinancialBalance(123456L)){
            throw new BtBankException(BtBankMsgCode.FIND_ACTIVITY_FAILED);
        }
        BtBankFinancialBalance btBankFinancialBalance2 =  btBankFinancialBalanceService.findFirstByMemberId(123456L);

        System.out.print("btBankFinancialBalance2 = "+JSON.toJSONString(btBankFinancialBalance2));
    }
    @Test
    public void TestFinallRealse(){
//        BigDecimal amount = new BigDecimal(400);
//        btBankFinancialBalanceService.realseAmountMemberId(349442L,amount);
        boolean result = financialActivityJoinDetailsService.effectiveMiner(360092L,BigDecimal.ZERO);
        System.out.println(result);
    }



}
