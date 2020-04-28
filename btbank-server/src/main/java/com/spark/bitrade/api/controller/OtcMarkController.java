package com.spark.bitrade.api.controller;

import com.spark.bitrade.biz.OtcMinerService;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.repository.entity.BusinessMinerOrder;
import com.spark.bitrade.repository.entity.OtcOrder;
import com.spark.bitrade.repository.service.BusinessMinerOrderService;
import com.spark.bitrade.service.MemberAccountService;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author qiuyuanjie
 * @since 2020/02/24 09:28
 */
@Slf4j
@Api(tags = {"提现订单银行账户与姓名不符"})
@RequestMapping(path = "api/v2/otcCheckName", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@AllArgsConstructor
public class OtcMarkController {

    private OtcMinerService otcMinerService;

    private BusinessMinerOrderService minerOrderService;

    private MemberAccountService memberAccountService;


    @ApiOperation(value = "银行账户与姓名不符接口", notes = "如果提现方的银行账户与姓名，使用这个接口进行标记")
    @PostMapping("checkRealName")
    public MessageRespResult<OtcOrder> checkRealName(Long memberId, @RequestParam("otcSn")Long otcSn){
        Member member = memberAccountService.findMemberByMemberId(memberId);
        MessageRespResult messageRespResult = otcMinerService.markOtcOrder(member, otcSn);
        return messageRespResult;
    }

    @ApiOperation(value = "OTC提现标记取消接口")
    @PostMapping(value = "cancelBusinessOrder")
    public MessageRespResult<Boolean> cancelBusinessOrder(Long businessOrderId,String remark) {
        BusinessMinerOrder order = minerOrderService.getById(businessOrderId);
        return MessageRespResult.success4Data(otcMinerService.cancelBinessOrder(order,remark,null));
    }
}
