package com.spark.bitrade.biz.impl;

import com.alibaba.fastjson.JSON;
import com.spark.bitrade.api.vo.*;
import com.spark.bitrade.biz.EnterpriseService;
import com.spark.bitrade.biz.MinerService;
import com.spark.bitrade.constant.BtBankSystemConfig;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.EnterpriseMinerTransaction;
import com.spark.bitrade.service.BtBankConfigService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mahao on 2019/12/29.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Slf4j
public class MinerTrasctionTest {
    @Autowired
    private EnterpriseService enterpriseService;
    @Autowired
    private MinerService minerService;
    @Autowired
    private BtBankConfigService btBankConfigService;
    @Test
    public void TestTransaction(){
        String types = "1,2,3,4,7,9";
        List<Integer> typeList = new ArrayList<>();
        if (types != null && !StringUtils.isEmpty(types)) {
            for (String s : types.trim().split(",")) {
                try {
                    typeList.add(Integer.valueOf(s));
                } catch (Exception e) {
                    log.info("types 转换出错");
                }
            }
        }

        MinerBalanceTransactionsVO transactions = minerService.getMinerBalanceTransactionsByMemberId(360211L, typeList, 1, 100,"2019-10-01~2019-12-29");
        System.out.print(JSON.toJSONString(transactions));
    }

    @Test
    public void testConfig(){
        /*String  minTransferAmount = (String)btBankConfigService.getConfig(BtBankSystemConfig.ENTERPRISE_MINIMUM_TRANSFER_AMOUNT);
        if (com.aliyuncs.utils.StringUtils.isEmpty(minTransferAmount)) {
            throw new IllegalArgumentException("未找到企业挖矿最低划转金额 ENTERPRISE_MINIMUM_TRANSFER_AMOUNT 配置");
        }
        BigDecimal amount = new BigDecimal(90);
        if(amount == null || amount.compareTo(new BigDecimal(minTransferAmount))< 1){
            throw new BtBankException(4001, "划转数量不能少于"+minTransferAmount+"BT");
        }*/
        /**/ApplicationVO app = new  ApplicationVO ();
        app.setType(2);
        app.setDescription("##############");
        enterpriseService.apply(360606L, app);

        /* QueryVo queryVo = QueryVo.builder().current(1).size(20).build();
        queryVo.parseRange("yyyy-MM-dd", "~", "2019-12-01~2019-12-30");
        EnterpriseMinerTransactionsVO enterpriseMinerTransactionsVO = enterpriseService.query(360606L, Arrays.asList(3, 4), queryVo);
        List<EnterpriseMinerTransaction> contentList  =  enterpriseMinerTransactionsVO.getContent();
        for (int i =0;i<contentList.size();i++){
            if(contentList.get(i).getAmount().compareTo(BigDecimal.ZERO)==-1){
                contentList.get(i).setAmount(contentList.get(i).getAmount().negate());
                System.out.println(JSON.toJSONString(contentList.get(i)));
            }
        }
        System.out.println(JSON.toJSONString(enterpriseMinerTransactionsVO)); */

    }
}
