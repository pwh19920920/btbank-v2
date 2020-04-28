package com.spark.bitrade.api.controller;

import com.spark.bitrade.api.vo.CreditCardCommissionRefundVO;
import com.spark.bitrade.api.vo.OrderReceiverVO;
import com.spark.bitrade.biz.CreditCardCommissionService;
import com.spark.bitrade.biz.MinePoolService;
import com.spark.bitrade.util.MessageRespResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * @author davi
 */
@Api(tags = "开放接口控制器")
@RequestMapping(path = "api/v2/no-auth/dmz", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@AllArgsConstructor
public class OpenController {
    private MinePoolService minePoolService;
    private CreditCardCommissionService creditCardCommissionService;
    @ApiOperation(value = "公开接口-接单")
    @PostMapping("orderReceiver")
    public MessageRespResult orderReceiver(@RequestBody OrderReceiverVO vo) {
        minePoolService.receiveOrder(vo);
        return MessageRespResult.success();
    }



    @ApiOperation(value = "公开接口-信用卡手续费返佣")
    @PostMapping("creditCardCommissionRefund")
    public MessageRespResult creditCardCommissionRefund(@RequestBody CreditCardCommissionRefundVO vo){
        String mobilePhone = vo.getMobilePhone();
        if (StringUtils.isEmpty(mobilePhone)||vo.getCommissionAmount()==null||
                vo.getTimestamp()==null||StringUtils.isEmpty(vo.getHashCode())){
            return MessageRespResult.error("参数必须全部填写");
        }
        long l = System.currentTimeMillis();
        long sub = l - vo.getTimestamp();
        if (new BigDecimal(sub/1000).abs().intValue()>60){
            return MessageRespResult.error(5173999,"非法请求");
        }
        creditCardCommissionService.refund(vo);

        return MessageRespResult.success();
    }
}
