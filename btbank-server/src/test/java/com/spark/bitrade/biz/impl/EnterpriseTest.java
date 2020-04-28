package com.spark.bitrade.biz.impl;

import com.spark.bitrade.biz.EnterpriseService;
import com.spark.bitrade.constant.ForeignConst;
import com.spark.bitrade.repository.entity.EnterpriseMiner;
import com.spark.bitrade.util.ValidateUtil;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

/**
 * Created by mahao on 2019/12/29.
 */
public class EnterpriseTest {

    @Test
    public void TestUrl(){

        System.out.println(ForeignConst.getForeignexchange("USD",new BigDecimal(100)));
        //System.out.println(ForeignConst.getForeignexchangePer("USD"));
        System.out.println("ENHUSD".substring(3,"ENHUSD".length()));
    }

    @Test
    public void TestBalance(){
        String email = null;

        if(!com.aliyuncs.utils.StringUtils.isEmpty(email)&&!ValidateUtil.isEmail(email)){
            System.out.println("NNNNNN");
        }
        /*EnterpriseMiner miner = new  EnterpriseMiner();
        miner.setBalance(new BigDecimal(1000));
        if(miner.getBalance().compareTo(BigDecimal.ZERO)==1){
            System.out.println("");
        }*/
        /* if(new BigDecimal(-20).compareTo(BigDecimal.ZERO)==-1){
            System.out.println("NNNNNN");
        }
       if(new BigDecimal(0.0000247).compareTo(new BigDecimal(0.001))==1 ) {
            System.out.println("ffffff");
        }else{
           System.out.println("tttt");
       } */
    }

    @Test
    public void redPacket(){
        BigDecimal min = new BigDecimal(1.5);
        BigDecimal max = new BigDecimal(5);
        BigDecimal surplusAmount = new BigDecimal(10);
        BigDecimal surplusAmount2 = new BigDecimal(4);
        System.out.println(genRedPacket(min,max,surplusAmount));
        System.out.println(genRedPacket(min,max,surplusAmount2));
    }
    public BigDecimal genRedPacket(BigDecimal min, BigDecimal max, BigDecimal surplusAmount) {
        if(max.compareTo(surplusAmount)==1){
            return surplusAmount ;
        }else{
            //最小金额和最大金额之间生成一个随机数
            BigDecimal redpacket = new BigDecimal(Math.random()).setScale(2,BigDecimal.ROUND_DOWN);
            redpacket = redpacket.multiply(max.add(min.negate())).setScale(2,BigDecimal.ROUND_DOWN);
            redpacket = redpacket.add(min.abs());
            return redpacket;
        }
    }

}
