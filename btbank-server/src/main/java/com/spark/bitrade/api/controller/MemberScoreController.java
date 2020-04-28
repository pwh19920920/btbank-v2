package com.spark.bitrade.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.api.vo.MemberScoreListVo;
import com.spark.bitrade.biz.MemberScoreBizService;
import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.BtBankMemberPendingWard;
import com.spark.bitrade.repository.entity.BtBankMemberScoreWallet;
import com.spark.bitrade.repository.service.BtBankMemberPendingWardService;
import com.spark.bitrade.repository.service.BtBankMemberScoreWalletService;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = {"用户挖矿积分控制器"})
@RequestMapping(path = "api/v2/memberScore", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@AllArgsConstructor
public class MemberScoreController {

    private BtBankMemberPendingWardService btBankMemberPendingWardService;

    private BtBankMemberScoreWalletService memberScoreWalletService;

    private MemberScoreBizService memberScoreBizService;
    /**
     * 查询待领取积分列表
     */
    @ApiOperation(value = "查询待领取积分列表", tags = "用户挖矿积分接口")
    @RequestMapping(value = "findPendingList",method = {RequestMethod.POST,RequestMethod.GET})
    public MessageRespResult<IPage<MemberScoreListVo>> findPendingList(@MemberAccount Member member,
                                             @RequestParam(defaultValue = "10") Integer size,
                                             @RequestParam(defaultValue = "1") Integer current){

        IPage<MemberScoreListVo> page = btBankMemberPendingWardService.pageList(current,size,member.getId());
        return MessageRespResult.success4Data(page);
    }


    /**
     * 查询待领取积分列表
     */
    @ApiOperation(value = "查询会员积分钱包", tags = "用户挖矿积分接口")
    @RequestMapping(value = "findMemberScore",method = {RequestMethod.POST,RequestMethod.GET})
    public MessageRespResult findMemberScore(@MemberAccount Member member){

        BtBankMemberScoreWallet wallet =
                memberScoreWalletService.findOne(member.getId());
        return MessageRespResult.success4Data(wallet.getBalance());
    }


    /**
     * 查询待领取积分列表
     */
    @ApiOperation(value = "领取挖矿积分奖励", tags = "用户挖矿积分接口")
    @RequestMapping(value = "receiveScoreWard",method = {RequestMethod.POST})
    public MessageRespResult receiveScoreWard(@MemberAccount Member member,
                                              @RequestParam Long receiveId){


        BtBankMemberPendingWard ward = btBankMemberPendingWardService.getById(receiveId);
        if (ward==null){
            throw new BtBankException(BtBankMsgCode.PENDING_WARD_NOT_FIND);
        }
        Long memberId = ward.getMemberId();
        if (!member.getId().equals(memberId)){
            throw new BtBankException(BtBankMsgCode.THIS_RECORD_IS_NOT_YOU);
        }
        //领取奖励
        memberScoreBizService.doReceive(memberId,ward);

        return MessageRespResult.success();
    }
}
