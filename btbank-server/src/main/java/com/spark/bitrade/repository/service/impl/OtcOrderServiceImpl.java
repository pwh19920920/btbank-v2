package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.api.dto.FixOrderDto;
import com.spark.bitrade.repository.entity.OtcOrder;
import com.spark.bitrade.repository.mapper.OtcOrderMapper;
import com.spark.bitrade.repository.service.OtcOrderService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * (OtcOrder)表服务实现类
 *
 * @author daring5920
 * @since 2019-11-28 17:43:20
 */
@Service("otcOrderService")
public class OtcOrderServiceImpl extends ServiceImpl<OtcOrderMapper, OtcOrder> implements OtcOrderService {

    @Override
    public List<OtcOrder> getNeedDispatchOrdersOrder() {
        return baseMapper.getNeedDispatchOrdersOrder();
    }

    @Override
    public List<Date> getNeedDispatchDateList() {
        return this.baseMapper.getNeedDispatchDateList();
    }

    @Override
    public List<Long> getNeedDispatchMemberIds() {
        return baseMapper.getNeedDispatchMemberIds();
    }

    @Override
    public BigDecimal getTotalSaleReward(Date beginTime, Date endTime, Long memberId) {
        return baseMapper.getTotalSaleReward(beginTime, endTime, memberId);
    }

    @Override
    public int updateOrderDispatchSaleRewardStatus(OtcOrder order) {
        return baseMapper.updateOrderDispatchSaleRewardStatus(order.getId(), order.getSaleRewardStatus());
    }

    @Override
    public Date getEarliestDate() {
        return baseMapper.getEarliestDate();
    }

    @Override
    public Optional<OtcOrder> findOneByOrderSn(Long orderSn) {

        QueryWrapper<OtcOrder> query = new QueryWrapper<>();
        query.eq("order_sn", orderSn);

        return Optional.ofNullable(baseMapper.selectOne(query));
    }

    @Override
    public int countOrders(Long memberId, boolean isBuy) {
        QueryWrapper<OtcOrder> counter = new QueryWrapper<>();

        // advertise_id = 0 挖矿订单

        // 购买订单
        if (isBuy) {
            /*
             * case 1: advertise_type = 1 出售广告 customer_id 买家id
             * case 2: advertise_type = 0 购买广告 member_id 买家id
             * status -> 1=未付款/2=已付款
             */
            counter.eq("advertise_type", 1)
                    .eq("customer_id", memberId).in("status", 1, 2);

            int case1 = count(counter);

            counter = new QueryWrapper<>();
            counter.eq("advertise_type", 0)
                    .eq("member_id", memberId).in("status", 1, 2);

            int case2 = count(counter);

            return case1 + case2;
        }
        // 出售订单
        else {
            /*
             * case 1: advertise_type = 1 出售广告 member_id 卖家id
             * case 2: advertise_type = 0 购买广告 customer_id 卖家id
             * status -> 1=未付款/2=已付款
             */
            counter.eq("advertise_type", 1)
                    .eq("member_id", memberId).in("status", 1, 2);

            int case1 = count(counter);

            counter = new QueryWrapper<>();
            counter.eq("advertise_type", 0)
                    .eq("customer_id", memberId).in("status", 1, 2);

            int case2 = count(counter);

            return case1 + case2;
        }
    }
    /**
     * 查询oct订单状态不一致数量
     */
    @Override
    public int queryOtcorderCnt() {
        return baseMapper.queryOtcorderCnt();
    }

    @Override
    public List<FixOrderDto> queryFixOrders() {
        return baseMapper.queryFixOrders();
    }

    /**
     * 修改kfka问题后造成订单状态异常数据
     */
    @Override
    public int updateOtcOrderStaus() {
        return baseMapper.updateOtcOrderStaus();
    }

    @Override
    public Boolean updateFixOtcOrderStaus(FixOrderDto fixOrderDto) {
        return baseMapper.updateFixOtcOrderStaus(fixOrderDto);
    }
}