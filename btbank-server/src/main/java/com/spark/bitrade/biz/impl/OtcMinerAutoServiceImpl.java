package com.spark.bitrade.biz.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.spark.bitrade.api.dto.MemberOrderCountDTO;
import com.spark.bitrade.biz.OtcConfigService;
import com.spark.bitrade.biz.OtcMinerAutoService;
import com.spark.bitrade.biz.OtcMinerService;
import com.spark.bitrade.constant.OtcConfigType;
import com.spark.bitrade.constant.OtcMinerOrderStatus;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.BusinessMinerOrder;
import com.spark.bitrade.repository.entity.OtcOrder;
import com.spark.bitrade.repository.mapper.OtcMinerMapper;
import com.spark.bitrade.repository.service.BusinessMinerOrderService;
import com.spark.bitrade.util.DateUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * OtcMinerAutoServiceImpl
 *
 * @author biu
 * @since 2019/12/11 16:29
 */
@Slf4j
@Service
@AllArgsConstructor
public class OtcMinerAutoServiceImpl implements OtcMinerAutoService {
    private static SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
    private BusinessMinerOrderService minerOrderService;
    private OtcMinerService otcMinerService;
    private OtcMinerMapper otcMinerMapper;
    private OtcConfigService otcConfigService;

    //处理取消订单
    public void autoProcessCancel(LocalTime min ,LocalTime max,Date date){
        log.info("开始扫描已经创建30分钟的订单,做00-08的取消工作");
        // 扫描矿池订单
        List<BusinessMinerOrder> orders = minerOrderService.list(new QueryWrapper<BusinessMinerOrder>().lambda()
                .le(BusinessMinerOrder::getCreateTime, date)
                .eq(BusinessMinerOrder::getStatus, OtcMinerOrderStatus.New.getCode())
                .orderByAsc(BusinessMinerOrder::getCreateTime)
        );
        if (orders == null || orders.size() == 0) {
            log.info("不存在超时30分钟的要被取消的订单");
            return ;
        }
        int success = 0;
        int failed = 0;
        for (BusinessMinerOrder order : orders) {
            //处理00:00-08:00提现订单30分钟后无商家抢单，自动取消订单。mahao
            LocalTime orderLocalTime =   LocalTime.parse(sdf.format(order.getCreateTime()));
            if (orderLocalTime.isAfter(min) && orderLocalTime.isBefore(max)) {
                try{
                    //取消订单
                    if(!otcMinerService.cancelBinessOrder(order,"超时取消",null)){
                        log.info("00:00-08:00取消提现订单失败{}",order);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    log.info("00:00-08:00取消提现订单失败{}",e.getMessage());
                }
                continue;
            }
        }
    }
    public String autoDispatchInnerMiner(LocalTime min ,LocalTime max ,Date date,Integer maxorders){


        // 扫描矿池订单
        List<BusinessMinerOrder> orders = minerOrderService.list(new QueryWrapper<BusinessMinerOrder>().lambda()
                //.le(BusinessMinerOrder::getCreateTime, date)
                .eq(BusinessMinerOrder::getStatus, OtcMinerOrderStatus.New.getCode())
                .eq(BusinessMinerOrder::getQueueStatus,1)
                .orderByAsc(BusinessMinerOrder::getCreateTime).last("limit " + maxorders));

        if (orders == null || orders.size() == 0) {
            return "不存在排队的订单";
        }

        // 扫描商家
        //List<MemberOrderCountDTO> members = getCertifiedMembersWithOrders();
        List<MemberOrderCountDTO> members = otcMinerMapper.innerMembers();
        if (members == null || members.size() == 0) {
            return "未找到可匹配的内部商家";
        }
        /*
         *内部商家可同时接多个提现订单，一次性吧所有订单处理完。
         */
        int success = 0;
        int failed = 0;


        // 遍历处理
        for (BusinessMinerOrder order : orders) {
            LocalTime orderLocalTime =   LocalTime.parse(sdf.format(order.getCreateTime()));
            //大于8点的订单并且是超过30分钟的订单才处理。
            if (orderLocalTime.isAfter(max)&&order.getCreateTime().getTime()<=date.getTime()) {
                //查询全部取消的购买者
                // List<Long> buyerids = otcMinerMapper.getByOrderSn( order.getRefId());
                Long lastbuyerid = otcMinerMapper.getByOrderSn( order.getRefId());
                //Map<Long, Long> map = buyerids.stream().collect(Collectors.toMap(Long::longValue, Long::longValue));
                int retry = 0;
                // 重试3次
                OtcOrder otc = null;
                while ( retry < 4) {
                    int index=(int)(Math.random()*members.size());
                    Long memberId = members.get(index).getId();
                    if(memberId.equals(lastbuyerid)){
                        continue;
                    }
                    // 匹配到自己的单
                    if (order.getSellId().equals(memberId)) {
                        retry++;
                        continue;
                    }
                    try {
                        otc = otcMinerService.mining(memberId, order.getId());
                        log.info("自动分配矿池订单成功 id = {}, order_sn = {}", order.getId(), otc.getOrderSn());
                    } catch (BtBankException ex) {
                        log.error("自动分配矿池订单出错 id = {}, code = {}, err = {}", order.getId(), ex.getCode(), ex.getMessage());
                    } catch (RuntimeException ex) {
                        log.error("自动分配矿池订单出错 id = {}, code = {}, err = {}", order.getId(), 500, ex.getMessage());
                    }
                    break;
                }
                if (otc == null) {
                    log.info("自动分配矿池订单失败 id = {}, 未匹配到商家", order.getId());
                    failed++;
                } else {
                    success++;
                }
            }
        }
        /*
        // 打算元素
        Collections.shuffle(members);
        Stack<Long> stack = new Stack<>();
        //members.stream().filter(m -> m.getOrders() == 0).map(MemberOrderCountDTO::getId).forEach(stack::push);
        //内部商家可同时接多个提现订单 去掉没有接单限制。 mh 2020.02.17
        members.stream().map(MemberOrderCountDTO::getId).forEach(stack::push);
        if (stack.size() == 0) {
            return "未找到可匹配的认证商家";
        }

        int success = 0;
        int failed = 0;
        // 获取时间限制
        String start = otcConfigService.getValue(OtcConfigType.OTC_WITHDRAW_LIMIT_MIN,
                String::new,
                "00:00");
        String end = otcConfigService.getValue(OtcConfigType.OTC_WITHDRAW_LIMIT_MAX,
                String::new,
                "00:00");
        LocalTime min = LocalTime.parse(start);
        LocalTime max = LocalTime.parse(end);

        // 遍历处理
        for (BusinessMinerOrder order : orders) {
            //处理00:00-08:00提现订单30分钟后无商家抢单，自动取消订单。mahao
            LocalTime orderLocalTime =   LocalTime.parse(sdf.format(order.getCreateTime()));
            if (orderLocalTime.isAfter(min) && orderLocalTime.isBefore(max)) {
                try{
                    //取消订单
                    if(!otcMinerService.cancelBinessOrder(order,"超时取消",null)){
                        log.info("00:00-08:00取消提现订单失败{}",order);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    log.info("00:00-08:00取消提现订单失败{}",e.getMessage());
                }
                continue;
            }
            int retry = 0;
            // 重试3次
            OtcOrder otc = null;
            while (stack.size() > 0 && retry < 3) {
                Long memberId = stack.pop();

                // 匹配到自己的单
                if (order.getSellId().equals(memberId)) {
                    retry++;
                    stack.push(memberId);
                    continue;
                }
                try {
                    otc = otcMinerService.mining(memberId, order.getId());
                    log.info("自动分配矿池订单成功 id = {}, order_sn = {}", order.getId(), otc.getOrderSn());
                } catch (BtBankException ex) {
                    log.error("自动分配矿池订单出错 id = {}, code = {}, err = {}", order.getId(), ex.getCode(), ex.getMessage());
                } catch (RuntimeException ex) {
                    log.error("自动分配矿池订单出错 id = {}, code = {}, err = {}", order.getId(), 500, ex.getMessage());
                }
                break;
            }

            if (otc == null) {
                log.info("自动分配矿池订单失败 id = {}, 未匹配到商家", order.getId());
                failed++;
            } else {
                success++;
            }
        }*/
        log.info("扫描处理结束...");
        return String.format("扫描处理结果 success = %d, failed = %d", success, failed);
    }
    @Override
    public String autoProcessWithTimout30min() {
        // 获取时间限制
        String start = otcConfigService.getValue(OtcConfigType.OTC_WITHDRAW_LIMIT_MIN,
                String::new,
                "00:00");
        String end = otcConfigService.getValue(OtcConfigType.OTC_WITHDRAW_LIMIT_MAX,
                String::new,
                "00:00");
        Integer maxorders = otcConfigService.getValue(OtcConfigType.OTC_DIG_ORDER_MAX_DISPLAY,
                Integer::new,
                5);
        LocalTime min = LocalTime.parse(start);
        LocalTime max = LocalTime.parse(end);
        Date date = DateUtils.getDateBeforeMinutes(30);
        autoProcessCancel(min,max,date);
        String s = autoDispatchInnerMiner(min, max, date,maxorders);
        otcMinerService.updateQueueStatus(maxorders);
        return  s;
    }


    public List<MemberOrderCountDTO> getCertifiedMembersWithOrders() {
        List<MemberOrderCountDTO> members = otcMinerMapper.innerMembers();

        // 进行中的订单
        List<MemberOrderCountDTO> orders = otcMinerMapper.ordersInProgress();
        Map<Long, Integer> inProgress = orders.stream()
                .collect(Collectors.toMap(MemberOrderCountDTO::getId, MemberOrderCountDTO::getOrders));

        for (MemberOrderCountDTO member : members) {
            member.setOrders(inProgress.getOrDefault(member.getId(), 0));
        }

        return members;
    }
}
