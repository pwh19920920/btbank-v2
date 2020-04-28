package com.spark.bitrade.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.api.vo.MinerPrizeQuizeVo;
import com.spark.bitrade.api.vo.PrizeQuizeRecordVO;
import com.spark.bitrade.api.vo.PrizeQuizeVo;
import com.spark.bitrade.api.vo.QueryVo;
import com.spark.bitrade.biz.PrizeQuizeService;
import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.MinerPrizeQuizeTransaction;
import com.spark.bitrade.repository.entity.PrizeQuizeRecord;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * 竞猜活动控制器
 * @author qiuyuanjie
 * @time 2020.01.02.10:24
 */
@Slf4j
@Api(tags = {"竞猜活动控制器"})
@RestController
@RequestMapping(value = "api/v2/prize" , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@AllArgsConstructor
public class PrizeQuizeController {

    private PrizeQuizeService prizeQuizeService;

    private RedisTemplate redisTemplate;

    @ApiOperation(value = "竞猜活动结果记录", notes = "查询所有的竞猜活动结果的往期记录",response = PrizeQuizeRecordVO.class, responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "当前页", name = "current", dataType = "Integer"),
            @ApiImplicitParam(value = "每页显示记录数", name = "size", dataType = "Integer"),
    })
    @PostMapping("/no-auth/prizeResult")
    public MessageRespResult<IPage<PrizeQuizeRecordVO>> prizeResult(@RequestParam(defaultValue = "20", name = "size") Integer size,
                                                                    @RequestParam(defaultValue = "1", name = "current") Integer current) {
        QueryVo query = QueryVo.builder().current(current).size(size).build();
        return prizeQuizeService.getPrizeResult(query);
    }

    @ApiOperation(value = "当日竞猜活动",notes = "当天的竞猜活动详情，人数、金额等信息",response = PrizeQuizeVo.class)
    @PostMapping("currentPrize")
    public MessageRespResult<PrizeQuizeVo> getCurrentPrize(@MemberAccount Member member){
        return prizeQuizeService.getCurrentPrize(member.getId());
    }

    @ApiOperation(value = "当日竞猜活动",notes = "当天的竞猜活动详情，人数、金额等信息",response = PrizeQuizeVo.class)
    @PostMapping("/no-auth/currentPrizeWithoutUser")
    public MessageRespResult<PrizeQuizeVo> currentPrizeWithoutUser(){
        return prizeQuizeService.getCurrentPrize(null);
    }

    @ApiOperation(value = "用户竞猜记录",notes = "用户自己参与竞猜活动的记录",response = MinerPrizeQuizeVo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(value = "当前页", name = "current", dataType = "Integer"),
            @ApiImplicitParam(value = "每页显示记录数", name = "size", dataType = "Integer"),
    })
    @PostMapping("myPrizeQuize")
    public MessageRespResult myPrizeQuize(@MemberAccount Member member,
                                          @RequestParam(defaultValue = "20", name = "size", required = false) Integer size,
                                          @RequestParam(defaultValue = "1", name = "current", required = false) Integer current){

        QueryVo vo = QueryVo.builder().current(current).size(size).build();
        IPage<MinerPrizeQuizeVo> page = prizeQuizeService.minerPrizeRecord(member.getId(), vo);
        return MessageRespResult.success4Data(page);
    }

    @ApiOperation(value = "用户投注功能", notes = "用户点击投注按钮开始投注")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "当前活动的ID",name = "activityId" ,required = true),
            @ApiImplicitParam(value = "当前活动的投注金额" , name = "amount" , required = true),
            @ApiImplicitParam(value = "投注类型0-跌 1-涨" , name = "prizeQuizeType" , required = true)
    })
    @PostMapping("betting")
    public MessageRespResult betting(@MemberAccount Member member,
                                     @RequestParam(value = "activityId") Long activityId,
                                     @RequestParam(value = "amount")BigDecimal amount,
                                     @RequestParam(value = "prizeQuizeType") Integer prizeQuizeType){
        //防止重复提交
        if(redisTemplate.hasKey("PRIZE:ACTIVITI:ACTIVITYID:"+activityId+":memberId"+member.getId())){
            throw new BtBankException(BtBankMsgCode.ACTIVITY_PURCHASE_FREQUENCY);
        }else{
            this.redisTemplate.opsForValue().set("PRIZE:ACTIVITI:ACTIVITYID:"+activityId+":memberId"+member.getId(), 1, 3,  TimeUnit.SECONDS);
        }
        PrizeQuizeRecord record = prizeQuizeService.getPrizeActivityManage(activityId);
        if (Objects.isNull(record)) {
            throw new BtBankException(BtBankMsgCode.FIND_ACTIVITY_FAILED);
        }

        //防止接口请求重复投注
        //1获取当天记录
        //2判断用户是否参与了活动 返回true参与了活动  返回false没参与活动
        boolean isActivity = prizeQuizeService.minerIsActivity(record,member.getId());
        if (!isActivity){
            throw new BtBankException(BtBankMsgCode.ALREADY_ATTEND_ACTIVITY);
        }
        //判断用户是否可以投注
        prizeQuizeService.checkPrize(record,member);
        //校验用户金额  非负数  非浮点数  最小值
        Long minAmount = prizeQuizeService.getMinAmount();
        amount = new  BigDecimal(amount.setScale(0, BigDecimal.ROUND_DOWN).longValue());
        int i = amount.compareTo(BigDecimal.valueOf(minAmount));
        if (i < 0) {
            throw new BtBankException(BtBankMsgCode.PRIZE_MIN_AMOUNT.getCode(),"投注最小金额:" + minAmount);
        }
        Long maxAmount = prizeQuizeService.getMaxAmount();
        i = amount.compareTo(BigDecimal.valueOf(maxAmount));
        if (i > 0) {
            throw new BtBankException(BtBankMsgCode.PRIZE_MAX_AMOUNT.getCode(),"投注最大金额:" + maxAmount);
        }
        //用户开始投注
        MinerPrizeQuizeTransaction transaction = new MinerPrizeQuizeTransaction();
        transaction.setAmount(amount);
        transaction.setPrieQuizeId(activityId);
        transaction.setPrizeQuizeType(prizeQuizeType);
        Boolean aBoolean = prizeQuizeService.minerBetting(member,record,transaction);
        return aBoolean ? MessageRespResult.success("参加活动成功"):MessageRespResult.error("参加活动失败");
    }

}
