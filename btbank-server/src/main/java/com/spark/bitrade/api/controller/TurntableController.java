package com.spark.bitrade.api.controller;

import com.spark.bitrade.api.vo.*;
import com.spark.bitrade.biz.TurntableService;
import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TurntableController
 *
 * @author biu
 * @since 2020/1/7 16:53
 */
@Slf4j
@Api(tags = {"转盘抽奖控制器"})
@RequestMapping(path = "api/v2/turntable", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@AllArgsConstructor
public class TurntableController {

    private TurntableService turntableService;

    @ApiOperation(value = "获取活动信息", notes = "返回进行中的活动信息", response = ActivitiesVO.class)
    @RequestMapping(value = "/activities", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<ActivitiesVO> activities() {
        return MessageRespResult.success4Data(turntableService.getActivities());
    }

    @ApiOperation(value = "活动抽奖", notes = "参与转盘活动抽奖", response = ActivitiesDrawVO.class)
    @ApiImplicitParam(value = "活动ID", name = "actId", required = true)
    @PostMapping(value = "/activities", params = "actId")
    public MessageRespResult<ActivitiesDrawVO> activities(@MemberAccount Member member, @RequestParam("actId") Integer actId) {

        if (!turntableService.activityIsOpen()) {
            throw BtBankMsgCode.TURNTABLE_ACTIVITY_PAUSED.asException();
        }

        return MessageRespResult.success4Data(turntableService.draw(actId, member));
    }

    @ApiOperation(value = "获取活动中奖信息", notes = "返回活动中奖信息，最新50条", response = ActivitiesCarouselVO.class)
    @ApiImplicitParam(value = "活动ID, 不指定或为0时返回最新的50条中奖信息", name = "actId", required = false)
    @RequestMapping(value = "/activities/winnings", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<List<ActivitiesCarouselVO>> activitiesWinnings(@RequestParam(value = "actId", required = false) Integer actId) {
        return MessageRespResult.success4Data(turntableService.carousel(actId));
    }


    @ApiOperation(value = "剩余次数", notes = "返回当前用户当前活动的剩余抽奖次数", response = ActivitiesChanceVO.class)
    @ApiImplicitParam(value = "活动ID", name = "actId", required = true)
    @RequestMapping(value = "/chances", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<ActivitiesChanceVO> chances(@MemberAccount Member member, @RequestParam("actId") Integer actId) {
        Integer chances = turntableService.getChances(member.getId());

        ActivitiesChanceVO vo = new ActivitiesChanceVO();
        vo.setActId(actId);
        vo.setNumber(chances);

        return MessageRespResult.success4Data(vo);
    }


    @ApiOperation(value = "兑换奖品", notes = "设置当前中奖奖品的领取人信息")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "中奖记录ID", name = "id", required = true),
            @ApiImplicitParam(value = "收件人姓名", name = "username", required = true),
            @ApiImplicitParam(value = "收件人电话", name = "mobile", required = true)
    })
    @PostMapping("/exchange")
    public MessageRespResult<Boolean> exchange(@MemberAccount Member member,
                                               @RequestParam("id") Long id,
                                               @RequestParam("username") String username,
                                               @RequestParam("mobile") String mobile) {
        return MessageRespResult.success4Data(turntableService.exchangePrize(id, member.getId(), username, mobile));
    }

    @ApiOperation(value = "中奖记录", notes = "返回当前用户活动的中奖记录", response = ActivitiesWinningVO.class, responseContainer = "List")
    @ApiImplicitParam(value = "活动ID，指定活动将返回指定活动得中奖记录", name = "actId", required = false)
    @RequestMapping(value = "/winnings", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<List<ActivitiesWinningVO>> winnings(@MemberAccount Member member, @RequestParam(value = "actId", required = false) Integer actId) {
        return MessageRespResult.success4Data(turntableService.getWinnings(actId, member.getId()));
    }

    @ApiOperation(value = "确认收货", notes = "更新中奖记录的状态")
    @ApiImplicitParam(value = "记录ID", name = "id", required = true)
    @PostMapping(value = "/winnings", params = "id")
    public MessageRespResult<Boolean> winnings1(@MemberAccount Member member, @RequestParam("id") Long id) {
        return MessageRespResult.success4Data(turntableService.confirmReceived(id, member.getId()));
    }
}
