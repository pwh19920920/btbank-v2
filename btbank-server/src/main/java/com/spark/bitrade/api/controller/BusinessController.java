package com.spark.bitrade.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.MemberTransaction;
import com.spark.bitrade.service.IMemberTransactionApiService;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.vo.ProfitVo;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhouhf
 * @time 2019.11.29 09:16
 */
@Slf4j
@Api(tags = {"商家挖矿收益"})
@RequestMapping(path = "api/v2/business", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@AllArgsConstructor
public class BusinessController {

    @Autowired
    IMemberTransactionApiService memberTransactionApiService;

    @ApiOperation(value = "昨日收益与累计收益", response = ProfitVo.class)
    @PostMapping(value = "ProfitCount")
    public MessageRespResult<ProfitVo> ProfitCount(@MemberAccount Member member) {
        return MessageRespResult.success4Data(memberTransactionApiService.ProfitCount(member.getId()).getData());
    }

    @ApiOperation(value = "累计收益详情列表", response = MemberTransaction.class)
    @ApiImplicitParams({
            @ApiImplicitParam(value = "当前页", name = "page", dataType = "int", required = true),
            @ApiImplicitParam(value = "每页显示记录数", name = "size", dataType = "int", required = true),
    })
    @PostMapping(value = "ProfitList")
    public MessageRespResult ProfitList(@MemberAccount Member member,int page,int size) {
        return MessageRespResult.success4Data(memberTransactionApiService.ProfitList(member.getId(),page,size).getData());
    }
}
