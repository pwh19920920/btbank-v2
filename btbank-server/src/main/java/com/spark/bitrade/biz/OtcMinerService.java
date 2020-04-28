package com.spark.bitrade.biz;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.api.vo.OtcWithdrawVO;
import com.spark.bitrade.constant.OtcMinerOrderStatus;
import com.spark.bitrade.constant.PayMode;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.repository.entity.BusinessMinerOrder;
import com.spark.bitrade.repository.entity.OtcOrder;
import com.spark.bitrade.util.MessageRespResult;

import java.math.BigDecimal;
import java.util.List;

/**
 * OtcMinerService
 *
 * @author biu
 * @since 2019/11/28 13:40
 */
public interface OtcMinerService {

    /**
     * 一键提现订单
     *
     * @param memberId 会员ID
     * @param amount   数量
     * @param payMode  支付方式
     * @return vo
     */
    OtcWithdrawVO withdraw(Long memberId, BigDecimal amount, PayMode payMode);

    /**
     * 分页查询未匹配的矿池订单
     *
     * @param page
     * @param query
     * @return
     */
    IPage<BusinessMinerOrder> page(IPage<BusinessMinerOrder> page, QueryWrapper<BusinessMinerOrder> query);

    /**
     * 商家抢单,挖矿
     *
     * @param memberId
     * @param id
     * @return
     */
    OtcOrder mining(Long memberId, Long id);

    /**
     * 更改订单状态
     *
     * @param refId  otc order id
     * @param source 源状态
     * @param dest   目标状态
     * @return bool
     */
    boolean updateOrderStatus(String refId, OtcMinerOrderStatus source, OtcMinerOrderStatus dest);

    /**
     * 发起申诉
     *
     * @param orderSn sn
     */
    void appeal(String orderSn);

    /**
     * 所有已付款的订单
     *
     * @return orders
     */
    List<OtcWithdrawVO> findPaidOrders();

    /**
     * 申诉处理完成
     *
     * @param appealId 申诉id
     */
    void appealCompleted(Long appealId);

    /**
     * 是否是内部商家
     *
     * @param memberId id
     * @return bool
     */
    boolean isInnerMemberAccount(Long memberId);

    /**
     * 对指定的订单派发额外的收益
     */
    MessageRespResult dispatchOtcSaleReward(Long orderId);

    /**
     * 修改kfka问题后造成订单状态异常数据
     */
    void updateOtcOrderStaus();

    /**
     * 查询是否内部商家
     */
    boolean chechInnerMember(Long memberId);

    boolean cancelBinessOrder(BusinessMinerOrder order,String remark,Integer minerOrderType );

    /**
     * 标记提现订单
     * @param member 商家用户
     * @param otcSn otc订单
     */
    MessageRespResult markOtcOrder(Member member,Long otcSn);

    void cancelOtcOrder(Member member,OtcOrder otcOrder);

    void withdrawLimitValidate(BigDecimal amount);

    /**
     * 修改排队状态
     */
    void updateQueueStatus(Integer size);
}
