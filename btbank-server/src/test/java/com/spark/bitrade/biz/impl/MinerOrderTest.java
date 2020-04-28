package com.spark.bitrade.biz.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.spark.bitrade.constant.OtcMinerOrderStatus;
import com.spark.bitrade.repository.entity.BusinessMinerOrder;
import com.spark.bitrade.repository.service.BtBankMinerOrderService;
import com.spark.bitrade.repository.service.BusinessMinerOrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@Slf4j
public class MinerOrderTest {

    @Autowired
    private BtBankMinerOrderService btBankMinerOrderService;
    @Autowired
    private BusinessMinerOrderService minerOrderService;
    @Test
    public void testOrder(){
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4)   ;
        System.out.println(JSON.toJSONString(btBankMinerOrderService.getMinerOrders(new ArrayList<>(), 20,20)));
    }
    @Test
    public void InnOrder(){
        List<BusinessMinerOrder> orders = minerOrderService.list(new QueryWrapper<BusinessMinerOrder>().lambda()
                //.le(BusinessMinerOrder::getCreateTime, date)
                .eq(BusinessMinerOrder::getStatus, OtcMinerOrderStatus.New.getCode())
                .orderByAsc(BusinessMinerOrder::getCreateTime).last("limit " + 5));
        System.out.println(JSON.toJSONString(orders));
    }


}
