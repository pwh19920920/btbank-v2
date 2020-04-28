package com.spark.bitrade.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.api.vo.FinancialActivityJoinDetailsVo;
import com.spark.bitrade.api.vo.FinancialActivityManageVo;
import com.spark.bitrade.biz.FinancialActivityService;
import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.constant.BtBankSystemConfig;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.FinancialActivityJoinDetails;
import com.spark.bitrade.repository.entity.FinancialActivityManage;
import com.spark.bitrade.service.BtBankConfigService;
import com.spark.bitrade.util.DateUtils;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by mahao on 2019/12/21.
 */
@Slf4j
@Api(tags = {"理财活动"})
@RequestMapping(path = "api/v2/financialActivity", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@AllArgsConstructor
public class FinancialActivityController {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private FinancialActivityService financialActivityService;
    @Autowired
    private BtBankConfigService configService;
    @ApiOperation(value = "非登录用户查询可参加的活动", response = FinancialActivityManage.class)
    @ApiImplicitParams({
            @ApiImplicitParam(value = "理财产品分类 0：全部，1：1个月，2：1-6个月，3：6个月以上", name = "type", dataTypeClass = Integer.class)
    })
    @PostMapping(value = "/no-auth/getAllActivities")
    public MessageRespResult<List<FinancialActivityManage>> getAvailableActivities(@RequestParam(defaultValue = "0", name = "type") int type) {
        FinancialActivityManageVo financialActivityManageVo = new FinancialActivityManageVo();
        financialActivityManageVo.setType(type);
        return MessageRespResult.success4Data(financialActivityService.getAvailableActivities(financialActivityManageVo));
    }
    @ApiOperation(value = "登录用户查询可参加的活动,type 0（全部），1（1个月），2（1-6个月），3（6个月以上）", response = FinancialActivityManage.class)
    @ApiImplicitParams({
            @ApiImplicitParam(value = "理财产品分类 0：全部，1：1个月，2：1-6个月，3：6个月以上", name = "type", dataTypeClass = Integer.class)
    })
    @PostMapping(value = "/getAuthAvailableActivities")
    public MessageRespResult<List<FinancialActivityManage>> getAuthAvailableActivities(@MemberAccount Member member,@RequestParam(defaultValue = "0", name = "type") int type) {
        FinancialActivityManageVo financialActivityManageVo = new FinancialActivityManageVo();
        financialActivityManageVo.setType(type);
        financialActivityManageVo.setMemberId(member.getId());
        List<Long> josinActivityIds = new ArrayList<>();
        List<FinancialActivityJoinDetails> josinActivities =financialActivityService.getJoinActivities(member);
        for (FinancialActivityJoinDetails financialActivityJoinDetails:josinActivities){
            josinActivityIds.add(financialActivityJoinDetails.getActivityId());
        }
        if(josinActivityIds.size()>0){
            financialActivityManageVo.setJoinids(josinActivityIds);
        }
        return MessageRespResult.success4Data(financialActivityService.getAvailableActivities(financialActivityManageVo));
    }
    @ApiOperation(value = "根据查询", response = FinancialActivityManage.class)
    @PostMapping(value = "/no-auth/getActivitiesDetail")
    public MessageRespResult<FinancialActivityManage> getActivitiesDetail(Long activityId) {
        return MessageRespResult.success4Data(financialActivityService.getFinancialActivityManage(activityId));
    }
    @ApiOperation(value = "查询我参加的活动", response = FinancialActivityJoinDetails.class)
    @ApiImplicitParams({
        @ApiImplicitParam(value = "当前页", name = "current", dataType = "int", required = true),
        @ApiImplicitParam(value = "每页显示记录数", name = "size", dataType = "int", required = true),
        @ApiImplicitParam(value = "参与活动状态 0：持仓明细，2：撤销明细， 1：释放明细", name = "type", dataTypeClass = Integer.class),
        @ApiImplicitParam(value = "明细下面周期类型 0(本周)， 1(本月)，2（半年），3（全部）", name = "timeType", dataTypeClass = Integer.class),
    })
    @PostMapping(value = "/getJoinActivities")
    public MessageRespResult<Map> getJoinActivities(@MemberAccount Member member,int type,int timeType,Integer current,Integer size) {
        Map map  = new HashMap();
        IPage<FinancialActivityJoinDetails> page = financialActivityService.getJoinActivitiesPage( member, type, timeType, current, size);
        map.put("page",page);
        //释放的时候处理
        if(type ==1 ){
            BigDecimal  profit = financialActivityService.getJoinActivitiesProfit( member, timeType);
            map.put("profit",profit);
        }
        return MessageRespResult.success4Data(map);
    }
    @ApiOperation(value = "查询理财活动收益", response = BigDecimal.class)
    @ApiImplicitParams({
            @ApiImplicitParam(value = "理财活动收益类型 0(本周)， 1(本月)，2（半年），3（全部）", name = "type", dataTypeClass = Integer.class)
    })
    @PostMapping(value = "/getJoinActivitiesProfit")
    public MessageRespResult<BigDecimal> getJoinActivitiesProfit(@MemberAccount Member member,int type) {

        return   MessageRespResult.success4Data(financialActivityService.getJoinActivitiesProfit( member, type));
    }
    @PostMapping(value = "/getTotalProfit")
    public MessageRespResult<Map> getTotalProfit(@MemberAccount Member member) {
        BigDecimal totalProfite = financialActivityService.getJoinActivitiesProfit( member, 3);
        BigDecimal totalLock = financialActivityService.getTotalLock(member);
        Map map = new HashMap();
        map.put("totalProfite",totalProfite);
        map.put("totalLock",totalLock);
        return   MessageRespResult.success4Data(map);
    }

    @ApiOperation(value = "参加活动", response = FinancialActivityJoinDetails.class)
    @ApiImplicitParams({
            @ApiImplicitParam(value = "金额", name = "amount", dataType = "BigDecimal", required = true),
            @ApiImplicitParam(value = "活动ID", name = "activityId", dataType = "Long", required = true),
            @ApiImplicitParam(value = "购买数目", name = "purchaseNums", dataTypeClass = Integer.class)
    })
    @PostMapping(value = "/joinActivities")
    public MessageRespResult<FinancialActivityJoinDetails> joinActivities(@MemberAccount Member member, BigDecimal amount, Long activityId, Integer purchaseNums) {
        Date time=configService.getConfig(BtBankSystemConfig.SILVER_CREATION_REGISTER_TIME_END, (v) -> DateUtils.parseDatetime(v.toString()), DateUtils.parseDatetime("2020-04-01 00:00:00"));
        Date registrationTime = member.getRegistrationTime();
        if (registrationTime.after(time)){
            throw new BtBankException(BtBankMsgCode.YOU_CANT_JOIN_ACTIVITIES);
        }
        if(activityId ==null || activityId==0){
            throw new BtBankException(BtBankMsgCode.FIND_ACTIVITY_FAILED);
        }
        if(purchaseNums==null|| purchaseNums<=0){
            throw new BtBankException(BtBankMsgCode.ACTIVITY_PURCHASE_NUM_ERROR);
        }
        //防止重复提交
        if(redisTemplate.hasKey("ACTIVITI:ACTIVITYID:"+activityId+":memberId"+member.getId())){
            throw new BtBankException(BtBankMsgCode.ACTIVITY_PURCHASE_FREQUENCY);
        }else{
            this.redisTemplate.opsForValue().set("ACTIVITI:ACTIVITYID:"+activityId+":memberId"+member.getId(), 1, 3,  TimeUnit.SECONDS);
        }
        FinancialActivityManage financialActivityManage = financialActivityService.getFinancialActivityManage(activityId);
        if (Objects.isNull(financialActivityManage)) {
            throw new BtBankException(BtBankMsgCode.FIND_ACTIVITY_FAILED);
        }
        if(purchaseNums>financialActivityManage.getUpSinglePurchase()){
            throw new BtBankException(BtBankMsgCode.ACTIVITY_OVER_MAX_LIMIT);
        }

        if(purchaseNums>financialActivityManage.getRemainPurchaseNums()){
            throw new BtBankException(BtBankMsgCode.ACTIVITY_OVER_MAX);
        }
        //查询是否已经购买
        if(financialActivityService.alreadyJoinActivity(member,activityId,purchaseNums,financialActivityManage)){
            throw new BtBankException(BtBankMsgCode.ALREADY_ATTEND_ACTIVITY);
        }
        Date current = new Date();
        if(financialActivityManage.getCreateTime().after(current)){
            throw new BtBankException(BtBankMsgCode.ACTIVITY_NOT_START);
        }
        if(financialActivityManage.getFinalizeTime().before(current)){
            throw new BtBankException(BtBankMsgCode.ACTIVITY_END);
        }
        FinancialActivityJoinDetails financialActivityJoinDetails = financialActivityService.joinActivitiesLock(member,financialActivityManage.getPerAmount().multiply(new BigDecimal(purchaseNums)),activityId,purchaseNums);
        if(financialActivityJoinDetails!=null){
            return MessageRespResult.success4Data(financialActivityJoinDetails);
        }
        return MessageRespResult.error("购买失败！");
    }


    @ApiOperation(value = "取消活动", response = String.class)
    @ApiImplicitParams({
            @ApiImplicitParam(value = "参加活动ID", name = "lockDetailId", dataType = "BigDecimal", required = true),
    })
    @PostMapping(value = "/cancelActivities")
    public MessageRespResult<String> cancelActivities(@MemberAccount Member member, Long lockDetailId) {
        if(lockDetailId ==null &&lockDetailId.equals(0)){
            throw new BtBankException(BtBankMsgCode.FIND_ACTIVITY_FAILED);
        }
        if(financialActivityService.cancelActivityLock(member,lockDetailId)){

            return MessageRespResult.success();
        }
        return MessageRespResult.error("撤销活动失败");
    }

    @PostMapping(value = "/getActivityDetails")
    @ApiOperation(value = "参加活动详情", response = FinancialActivityJoinDetailsVo.class)
    @ApiImplicitParams({
            @ApiImplicitParam(value = "参加活动ID", name = "lockDetailId", dataType = "BigDecimal", required = true),
    })
    public MessageRespResult<FinancialActivityJoinDetailsVo> getActivityDetails(@MemberAccount Member member, Long lockDetailId) {
        if(lockDetailId ==null &&lockDetailId.equals(0)){
            throw new BtBankException(BtBankMsgCode.FIND_ACTIVITY_FAILED);
        }
        return MessageRespResult.success4Data(financialActivityService.getActivityDetails(lockDetailId));
    }
}
