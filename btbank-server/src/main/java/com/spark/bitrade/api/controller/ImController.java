package com.spark.bitrade.api.controller;

import com.spark.bitrade.api.vo.ApplicationResultVO;
import com.spark.bitrade.api.vo.MinerImVo;
import com.spark.bitrade.biz.ImService;
import com.spark.bitrade.biz.MinerService;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.repository.entity.MemberVo;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = {"聊天"})
@RequestMapping(path = "api/v2/im", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@AllArgsConstructor
public class ImController {
    private ImService imService;

    @Autowired
    private MinerService minerService;

    @ApiOperation(value = "获取IM token", notes = "获取IM token", response = MemberVo.class)
    @PostMapping("/getImToken")
    public MessageRespResult<MemberVo> getImToken(@MemberAccount Member member) {

        return MessageRespResult.success4Data(imService.findInfo(member.getId()));
    }

    @ApiOperation(value = "进入群聊接口(校验该用户是否为有效矿工)")
    @PostMapping(value = "/cheak/miner")
    public MessageRespResult<MinerImVo> validMiner(@MemberAccount Member member) {
        MinerImVo minerImVo = minerService.validMiner(member);
        return MessageRespResult.success4Data(minerImVo);
    }

    @ApiOperation(value = "根据手机号校验用户是否是有效旷工")
    @PostMapping(value = "/cheak/phone")
    public MessageRespResult<MinerImVo> cheakMiner(@MemberAccount Member member, String phone) {
        MinerImVo minerImVo = minerService.cheakMiner(phone);
        return MessageRespResult.success4Data(minerImVo);
    }

}
