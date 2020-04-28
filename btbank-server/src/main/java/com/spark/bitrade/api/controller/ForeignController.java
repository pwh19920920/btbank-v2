package com.spark.bitrade.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.spark.bitrade.api.vo.QueryVo;
import com.spark.bitrade.biz.ForeignService;
import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.ForeignMemberBankinfo;
import com.spark.bitrade.repository.service.ForeignMemberBankinfoService;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Api(tags = {"外汇银行卡添加"})
@RequestMapping(path = "api/v2/foreign", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@AllArgsConstructor
public class ForeignController {

    private ForeignMemberBankinfoService foreignMemberBankinfoService;
    private RedisTemplate redisTemplate;
    private ForeignService foreignService;
    @ApiOperation(value = "添加银行卡",notes = "添加新的银行卡信息")
    @PostMapping("saveBankInfo")
    public MessageRespResult saveBankInfo(@MemberAccount Member member, ForeignMemberBankinfo bankInfo){
        checkInfo(bankInfo);
        //防止重复提交
        if(redisTemplate.hasKey("FOREIGN:SAVINGBANKINFO:memberId"+member.getId())){
            throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_FREQUENCY);
        }else{
            this.redisTemplate.opsForValue().set("FOREIGN:SAVINGBANKINFO:memberId"+member.getId(), 1, 3,  TimeUnit.SECONDS);
        }
        bankInfo.setMemberId(member.getId());
        bankInfo.setId(IdWorker.getId());
        if(foreignService.savebank(bankInfo)){
            return MessageRespResult.success();
        }
        throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_BANKSAVE_ERROR);



    }

    @ApiOperation(value = "更新银行卡",notes = "更新银行卡操作")
    @PostMapping("updateBankInfo")
    public MessageRespResult updateBankInfo(@MemberAccount Member member,ForeignMemberBankinfo bankInfo){
        checkInfo(bankInfo);
        if(redisTemplate.hasKey("FOREIGN:UPDATINGBANKINFO:memberId"+member.getId())){
            throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_FREQUENCY);
        }else{
            this.redisTemplate.opsForValue().set("FOREIGN:UPDATINGBANKINFO:memberId"+member.getId(), 1, 3,  TimeUnit.SECONDS);
        }
        bankInfo.setMemberId(member.getId());
        if(foreignService.updatebank(bankInfo)){
            return MessageRespResult.success();
        }
        throw new BtBankException(BtBankMsgCode.FOREIGN_EXCHANGE_BANKUPDATE_ERROR);

    }

    @ApiOperation(value = "用户银行卡",notes = "展示用户所有的银行卡",response = ForeignMemberBankinfo.class)
    @PostMapping("selectAllBankInfo")
    public MessageRespResult<IPage<ForeignMemberBankinfo>> selectAllBankInfo(@MemberAccount Member member,
                                               @RequestParam(defaultValue = "20", name = "size") Integer size,
                                               @RequestParam(defaultValue = "1", name = "current") Integer current){
        QueryVo query = QueryVo.builder().current(current).size(size).build();
        ForeignMemberBankinfo bankInfo = new ForeignMemberBankinfo();
        bankInfo.setMemberId(member.getId());
        IPage<ForeignMemberBankinfo> foreignMemberBankinfos = foreignMemberBankinfoService.queryAll(bankInfo,query);
        return MessageRespResult.success4Data(foreignMemberBankinfos);
    }

    /**
     * 校验用户的表单信息
     * @param info
     */
    private void checkInfo(ForeignMemberBankinfo info){
        if (StringUtils.isEmpty(info.getSwiftCode()) || StringUtils.isEmpty(info.getAccountName()) || StringUtils.isEmpty(info.getAccountNumber())||
                StringUtils.isEmpty(info.getBankAddress()) || StringUtils.isEmpty(info.getBankName())){
            throw new BtBankException(BtBankMsgCode.FOREIGN_BANN_INFO);
        }
        if (info.getSwiftCode().length() > 50 || info.getAccountName().length() > 50 || info.getAccountNumber().length() > 50 ||
                info.getBankAddress().length() > 100 || info.getBankName().length() > 100){
            throw new BtBankException(BtBankMsgCode.FOREIGN_OVER_LENGTH);
        }
    }
}
