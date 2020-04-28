package com.spark.bitrade.service;

import com.spark.bitrade.constant.FeignServiceConstant;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.vo.ProfitVo;
import com.spark.bitrade.vo.UnConfirmVo;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 提供账务流水 API服务
 *
 * @author zhouhf
 * @time 2019.11.29 11:35
 */
@FeignClient(FeignServiceConstant.ACCOUNT_SERVER)
public interface IMemberTransactionApiService {


    @PostMapping(value = "acct/api/v2/memberTransaction/ProfitCount")
    MessageRespResult<ProfitVo> ProfitCount(@RequestParam("memberId") Long memberId);

    @PostMapping(value = "acct/api/v2/memberTransaction/ProfitList")
    MessageRespResult ProfitList(@RequestParam("memberId") Long memberId,
                                 @RequestParam("page") int page,
                                 @RequestParam("size") int size);

    @GetMapping("acct/v2/walletChangeRecord/unConfirm")
    MessageRespResult<List<UnConfirmVo>> unConfirmWalletChangeRecords();
}
