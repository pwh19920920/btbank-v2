package com.spark.bitrade.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.api.vo.WelfareActivityVo;
import com.spark.bitrade.api.vo.WelfareInvolvementVo;
import com.spark.bitrade.api.vo.WelfareLockedVo;
import com.spark.bitrade.biz.WelfareService;
import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.constants.AcctMsgCode;
import com.spark.bitrade.controller.ApiController;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.repository.entity.WelfareActivity;
import com.spark.bitrade.repository.entity.WelfareInvolvement;
import com.spark.bitrade.repository.service.WelfareActivityService;
import com.spark.bitrade.repository.service.WelfareInvolvementService;
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

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * WelfareController
 *
 * @author biu
 * @since 2020/4/8 14:26
 */
@Slf4j
@Api(tags = {"福利包控制器"})
@RequestMapping(path = "api/v2/welfare", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@AllArgsConstructor
public class WelfareController extends ApiController {

    private final WelfareService welfareService;
    private final WelfareActivityService welfareActivityService;
    private final WelfareInvolvementService welfareInvolvementService;

    @ApiOperation(value = "获取福利包活动", notes = "根据ID获取福利包活动详情")
    @ApiImplicitParam(value = "ID", name = "id", dataType = "int", paramType = "path")
    @GetMapping(value = {"/{id}", "/no-auth/{id}"})
    public MessageRespResult<WelfareActivityVo> index(@PathVariable("id") Integer id) {
        return success(WelfareActivityVo.of(welfareActivityService.getById(id)));
    }

    @ApiOperation(value = "获取最新的福利包活动", notes = "根据类型获取最新的记录")
    @ApiImplicitParam(value = "类型； 0：新人福利包 1：增值福利包", name = "type", dataType = "int")
    @GetMapping(value = {"/latest", "/no-auth/latest"})
    public MessageRespResult<WelfareActivityVo> latest(@RequestParam("type") Integer type) {
        return success(WelfareActivityVo.of(welfareActivityService.findTheLatest(type)));
    }

    @ApiOperation(value = "获取全部福利包产品", notes = "根据类型获取全部列表，按创建时间倒序排列")
    @ApiImplicitParam(value = "类型； 0：新人福利包 1：增值福利包", name = "type", dataType = "int")
    @GetMapping(value = {"/list", "/no-auth/list"})
    public MessageRespResult<List<WelfareActivityVo>> list(@RequestParam("type") Integer type) {
        return success(welfareActivityService
                .findAllByType(type).stream().map(WelfareActivityVo::of)
                .collect(Collectors.toList()));
    }

    @ApiOperation(value = "剩余次数", notes = "根据类型获取剩余参与次数")
    @ApiImplicitParam(value = "类型； 0：新人福利包 1：增值福利包", name = "type", dataType = "int")
    @PostMapping("/chances")
    public MessageRespResult<Integer> chances(@MemberAccount Member member, @RequestParam("type") Integer type) {
        return success(welfareActivityService.chances(type, member));
    }

    @ApiOperation(value = "购买福利包", notes = "根据ID购买指定福利包活动")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "ID", name = "id", dataType = "int", paramType = "path"),
            @ApiImplicitParam(value = "购买份数", name = "number", dataType = "int", paramType = "query")
    })
    @PostMapping("/buy/{id}")
    public MessageRespResult<List<WelfareInvolvement>> buy(@MemberAccount Member member, @PathVariable("id") Integer id,
                                                           @RequestParam(value = "number", defaultValue = "1") Integer number) {
        // 判断是否是有效矿工
        if (!welfareService.isAvailableMiner(member.getId())) {
            throw BtBankMsgCode.NOT_EFECT_MINER.asException();
        }
        // 预处理判断钱包余额是否足够
        if (!welfareService.checkWalletBalance(member.getId(), BigDecimal.valueOf(10000).multiply(BigDecimal.valueOf(number)))) {
            throw new MessageCodeException(AcctMsgCode.ACCOUNT_BALANCE_INSUFFICIENT);
        }

        return success(welfareActivityService.buy(id, number, member));
    }

    @ApiOperation(value = "撤回福利包", notes = "根据ID撤回指定福利包活动")
    @ApiImplicitParam(value = "ID", name = "id", dataType = "long", paramType = "path")
    @PostMapping("/refund/{id}")
    public MessageRespResult<WelfareInvolvement> refund(@MemberAccount Member member, @PathVariable("id") Long id) {
        // return success(welfareActivityService.refund(id, member));
        // 2020-04-10 项目方要求福利包活动不许撤回
        return success();
    }

    @ApiOperation(value = "获取参与明细", notes = "根据类型，状态和周期查询参与明细")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "类型； 0：新人福利包 1：增值福利包", name = "type"),
            @ApiImplicitParam(value = "状态; 0: 持仓 1：释放 2：撤回", name = "active"),
            @ApiImplicitParam(value = "周期； 0：本周 1：本月 2：半年 3：全部", name = "period"),
            @ApiImplicitParam(value = "页码", name = "page", defaultValue = "1"),
            @ApiImplicitParam(value = "每页显示记录", name = "size", defaultValue = "15"),
    })
    @RequestMapping(value = "/records", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<Map<String, Object>> records(
            @MemberAccount Member member,
            @RequestParam("type") Integer type,
            @RequestParam("active") Integer active,
            @RequestParam("period") Integer period,
            @RequestParam(value = "page", defaultValue = "1") Integer current,
            @RequestParam(value = "size", defaultValue = "15") Integer size) {

        // 恒定条件
        LambdaQueryWrapper<WelfareInvolvement> query = new LambdaQueryWrapper<>();
        query.eq(WelfareInvolvement::getMemberId, member.getId()).eq(WelfareInvolvement::getActType, type);

        // 动态条件
        Date now = Calendar.getInstance().getTime();
        // 状态
        if (0 == active) {
            // 持仓，未释放； 释放时间 > 当前时间
            query.eq(WelfareInvolvement::getStatus, 0).gt(WelfareInvolvement::getReleaseTime, now);
        }
        if (1 == active) {
            // 持仓，已释放； 释放时间 < 当前时间
            query.eq(WelfareInvolvement::getStatus, 0).lt(WelfareInvolvement::getReleaseTime, now)
                    .isNotNull(WelfareInvolvement::getEarningReleaseTime);
        }
        if (2 == active) {
            // 撤回
            query.eq(WelfareInvolvement::getStatus, 1);
        }
        // 周期
        if (0 == period) {
            // 7天内
            query.gt(WelfareInvolvement::getCreateTime, getDateBeforeDays(now, 7));
        }
        if (1 == period) {
            // 30天内
            query.gt(WelfareInvolvement::getCreateTime, getDateBeforeDays(now, 30));
        }
        if (2 == period) {
            // 180天内
            query.gt(WelfareInvolvement::getCreateTime, getDateBeforeDays(now, 180));
        }

        // 排序
        query.orderByDesc(WelfareInvolvement::getCreateTime);

        Page<WelfareInvolvement> page = new Page<>(current, size);

        Map<String, Object> map = new HashMap<>();
        IPage<WelfareInvolvement> result = welfareInvolvementService.page(page, query);
        map.put("page", result);
        if (active == 1) {
            // profit
            BigDecimal profit = BigDecimal.ZERO;
            for (WelfareInvolvement record : result.getRecords()) {
                if (record.getEarningReleaseAmount() != null) {
                    profit = profit.add(record.getEarningReleaseAmount());
                }
            }
            map.put("profit", profit);
        }
        return success(map);
    }

    @ApiOperation(value = "获取参与明细", notes = "根据类型，状态和周期查询参与明细")
    @ApiImplicitParam(value = "ID；明细ID", name = "id")
    @RequestMapping(value = "/records/{id}", method = {RequestMethod.GET, RequestMethod.POST})
    public MessageRespResult<WelfareInvolvementVo> records(@MemberAccount Member member, @PathVariable("id") Long id) {
        WelfareInvolvement involvement = welfareInvolvementService.getById(id);
        if (involvement == null || !involvement.getMemberId().equals(member.getId())) {
            throw BtBankMsgCode.WELFARE_INVOLVEMENT_NOT_FOUND.asException();
        }
        WelfareInvolvementVo vo = WelfareInvolvementVo.of(involvement);
        WelfareActivity activity = welfareActivityService.getById(involvement.getActId());
        return success(vo.fill(activity));
    }

    @ApiOperation(value = "查询理财活动收益")
    @ApiImplicitParam(value = "类型； 0：新人福利包 1：增值福利包", name = "type")
    @GetMapping(value = "/involvement/profit", params = "type")
    public MessageRespResult<Map<String, Object>> getTotalProfit(@MemberAccount Member member, @RequestParam("type") Integer type) {
        BigDecimal profit = welfareInvolvementService.getTotalProfit(member, type);
        BigDecimal lock = welfareInvolvementService.getTotalLock(member, type);
        Map<String, Object> map = new HashMap<>();
        map.put("profit", profit);
        map.put("lock", lock);
        return MessageRespResult.success4Data(map);
    }

    @ApiOperation(value = "查看福利挖矿锁仓")
    @GetMapping(value = "/locked/balance")
    public MessageRespResult<WelfareLockedVo> locked(@MemberAccount Member member) {
        return success(welfareInvolvementService.getLockedBalance(member));
    }

    private Date getDateBeforeDays(Date date, int before) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -Math.abs(before));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }
}
