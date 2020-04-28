package com.spark.bitrade.api.controller;

import com.spark.bitrade.biz.OtcConfigService;
import com.spark.bitrade.biz.OtcLimitService;
import com.spark.bitrade.biz.OtcMinerService;
import com.spark.bitrade.constant.OtcConfigType;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.repository.entity.BusinessMinerOrder;
import com.spark.bitrade.repository.service.AdvertiseOperationHistoryService;
import com.spark.bitrade.repository.service.BusinessMinerOrderService;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * @author shenzucai
 * @time 2019.10.24 16:32
 */
@Api(tags = "内部接口仅限内网调用")
@RequestMapping(value = "inner/miner", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@AllArgsConstructor
public class MinerInnerController {
    @Autowired
    private OtcMinerService otcMinerService;

    @Autowired
    private OtcConfigService configService;

    @Autowired
    private AdvertiseOperationHistoryService operationHistoryService;

    @Autowired
    private BusinessMinerOrderService minerOrderService;
    @Autowired
    private OtcLimitService otcLimitService;

    /**
     * 派发销售补贴
     *
     * @return
     */

    @ApiOperation(value = "发放指定订单商家销售补贴")
    @PostMapping(value = "dispatchOtcSaleReward")


    public MessageRespResult dispatchOtcSaleReward(Long orderId) {
        return otcMinerService.dispatchOtcSaleReward(orderId);
    }


    @ApiOperation(value = "获取指定OTC配置接口", response = MessageRespResult.class)
    @PostMapping(value = "getValue")
    public MessageRespResult<String> getValue(String key) {
        return MessageRespResult.success4Data(configService.getValue(key));
    }

    /**
     * 检测商家广告休业时间
     *
     * @return
     */

    @ApiOperation(value = "检测商家广告休业时间")
    @PostMapping(value = "checkBusinessAdClosing")
    public MessageRespResult<Boolean> checkBusinessAdClosing(Long memberId) {
        return MessageRespResult.success4Data(operationHistoryService.checkBusinessAdClosing(memberId));
    }
    /**
     * OTC提现取消接口
     *
     * @return
     */

    @ApiOperation(value = "OTC提现取消接口")
    @PostMapping(value = "cancelBusinessOrder")
    public MessageRespResult<Boolean> cancelBusinessOrder(Long businessOrderId,String remark) {
        BusinessMinerOrder order = minerOrderService.getById(businessOrderId);
        return MessageRespResult.success4Data(otcMinerService.cancelBinessOrder(order,remark,null));
    }

    /**
     * 限制提出部分金额
     *
     * @return
     */

    @ApiOperation(value = "限制转出金额")
    @PostMapping(value = "forbidToWithdrawAndTransferOut")
    public MessageRespResult<String> forbidToWithdrawAndTransferOut(Long memberId) {
        return MessageRespResult.success4Data(otcLimitService.forbidToWithdrawAndTransferOut(memberId).toString());
    }
}
