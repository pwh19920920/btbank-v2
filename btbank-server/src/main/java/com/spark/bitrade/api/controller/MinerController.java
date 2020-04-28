package com.spark.bitrade.api.controller;

import com.spark.bitrade.api.dto.MinerAssetDTO;
import com.spark.bitrade.api.dto.MinerTransferConfigDTO;
import com.spark.bitrade.api.vo.*;
import com.spark.bitrade.biz.ActivityRedpacketService;
import com.spark.bitrade.biz.MinerService;
import com.spark.bitrade.constant.ApplyGoldMinerCode;
import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.constant.BtBankSystemConfig;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.BtBankMinerBalanceTransaction;
import com.spark.bitrade.repository.entity.BtBankMinerOrder;
import com.spark.bitrade.repository.entity.BtBankMinerOrderTransaction;
import com.spark.bitrade.service.BtBankConfigService;
import com.spark.bitrade.trans.Tuple2;
import com.spark.bitrade.util.BigDecimalUtil;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 矿工资产服务控制器
 *
 * @author davi
 */

@Slf4j
@Api(tags = {"矿工资产控制器 , 主动推送websocket 地址 : /api/v2/miner/webSocket"})
@RequestMapping(path = "api/v2/miner", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@AllArgsConstructor
public class MinerController {

    private MinerService minerService;
    private BtBankConfigService configService;
    private ActivityRedpacketService activityRedpacketService;


    @ApiOperation(value = "查询资产", response = MinerAssetDTO.class)
    @GetMapping(value = "queryAsset")
    public MessageRespResult<MinerAssetDTO> queryAsset(@MemberAccount Member member) {
        MinerAssetDTO dto = minerService.queryMinerAsset(member.getId());
        return MessageRespResult.success4Data(dto);
    }

    @ApiOperation(value = "划转资产到矿池")
    @PostMapping(value = "transferAsset")
    public MessageRespResult transferAsset(@RequestParam BigDecimal amount, @MemberAccount Member member) {
        minerService.transferAsset(amount, member.getId());
        return MessageRespResult.success();
    }


    @ApiOperation(value = "获取用户推荐列表", response = MinerRecommandListVO.class)
    @PostMapping(value = "getRecommandList")
    public MessageRespResult getRecommandList(@MemberAccount Member member, @RequestParam(defaultValue = "20", name = "size")
            int size, @RequestParam(defaultValue = "1", name = "current") int cuurent) {
        MinerRecommandListVO list = minerService.getRecommandList(member.getId(), size, cuurent);
        return MessageRespResult.success4Data(list);
    }


    @ApiOperation(value = "获取上次申请金牌矿工结果", response = MinerRewardListVO.class)
    @PostMapping(value = "getLastApplyResult")
    public MessageRespResult getLastApplyResult(@MemberAccount Member member) {
        Tuple2<ApplyGoldMinerCode, String> lastApplyStatus = minerService.getLastApplyStatus(member.getId());
        HashMap<String, Object> map = new HashMap<>();
        map.put("status", lastApplyStatus.getFirst().getCode());
        map.put("remark", lastApplyStatus.getSecond());
        return MessageRespResult.success4Data(map);
    }


    @ApiOperation(value = "获取用户推荐收益列表", response = MinerRewardListVO.class)
    @PostMapping(value = "getRewardList")
    public MessageRespResult getRewardList(@MemberAccount Member member, @RequestParam(defaultValue = "20", name = "size")
            int size, @RequestParam(defaultValue = "1", name = "current") int cuurent) {
        MinerRewardListVO list = minerService.getRewardList(member.getId(), size, cuurent);
        return MessageRespResult.success4Data(list);
    }

    @ApiOperation(value = "获取用户奖励列表", response = MinerRewardListVO.class)
    @PostMapping(value = "getMyRewardList")
    public MessageRespResult<MyRewardListVO> getMyRewardList(@MemberAccount Member member, @RequestParam(defaultValue = "20", name = "size")
            long size, @RequestParam(defaultValue = "1", name = "current") long cuurent) {
        MyRewardListVO list = minerService.getMyRewards(member.getId(), cuurent, size);
        return MessageRespResult.success4Data(list);
    }


    @ApiOperation(value = "用户申请金牌矿工", response = BtBankMsgCode.class)
    @PostMapping(value = "applyUpgradeToGold")
    public MessageRespResult applyUpgradeToGold(@MemberAccount Member member) {
        minerService.applyUpgradeToGold(member.getId());
        return MessageRespResult.success();
    }

    @ApiOperation(value = "用户查看是否满足申请金牌矿工")
    @PostMapping(value = "tryApplyUpgradeToGold")
    public MessageRespResult tryApplyUpgradeToGold(@MemberAccount Member member) {
        Tuple2<ApplyGoldMinerCode, String> result = minerService.tryApplyUpgradeToGold(member.getId());
        HashMap<String, Object> map = new HashMap<>();
        map.put("status", result.getFirst().getCode());
        map.put("remark", result.getSecond());
        return MessageRespResult.success4Data(map);
    }

    @ApiOperation(value = "查询矿池资金明细", response = BtBankMinerBalanceTransaction.class, responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "当前页", name = "current", dataType = "int", required = true),
            @ApiImplicitParam(value = "每页显示记录数", name = "size", dataType = "int", required = true),
            @ApiImplicitParam(value = "每页显示记录数", name = "types", dataTypeClass = String.class),
            @ApiImplicitParam(value = "日期范围 格式: 2019-12-12 ~ 2019-12-13", name = "range", dataTypeClass = String.class),
    })
    @PostMapping(value = "minerBalanceTransaction")
    public MessageRespResult getMinerBalanceTransaction(@MemberAccount Member member, @RequestParam(defaultValue = "20", name = "size") int size, @RequestParam(defaultValue = "1", name = "current") int current,
                                                        @RequestParam(required = false) String types, @RequestParam(value = "range", required = false) String range) {

        List<Integer> typeList = new ArrayList<>();
        if (types != null && !StringUtils.isEmpty(types)) {
            for (String s : types.trim().split(",")) {
                try {
                    typeList.add(Integer.valueOf(s));
                } catch (Exception e) {
                    log.info("types 转换出错");
                }
            }
        }

        MinerBalanceTransactionsVO transactions = minerService.getMinerBalanceTransactionsByMemberId(member.getId(), typeList, current, size, range);

        return MessageRespResult.success("success", transactions);
    }


    @ApiOperation(value = "查询我的订单列表", response = BtBankMinerOrder.class, responseContainer = "List")
    @PostMapping(value = "minerOrders")
    public MessageRespResult getMinerOrdersByMemberId(@MemberAccount Member member, @RequestParam(defaultValue = "20", name = "size") int size, @RequestParam(defaultValue = "1", name = "current") int current, @RequestParam(required = false) String types) {

        List<Integer> typeList = new ArrayList<>();
        if (types != null && !StringUtils.isEmpty(types)) {
            for (String s : types.trim().split(",")) {
                try {
                    typeList.add(Integer.valueOf(s));
                } catch (Exception e) {
                    log.info("types 转换出错");
                }
            }
        }

        MinerOrdersVO minerOrders = minerService.getMyMinerOrdersByMemberId(member.getId(), typeList, current, size);
        //MinerOrdersVO minerOrders = minerService.getMinerOrdersByMemberId(member.getId(), typeList, current, size);
        return MessageRespResult.success("success", minerOrders);
    }


    @ApiOperation(value = "抢单")
    @PostMapping(value = "grabMineOrder")
    public MessageRespResult grabMineOrderByMemberId(@MemberAccount Member member, Long orderId) {


        if (null == orderId) {
            return MessageRespResult.error("orderId 不能为空");
        } else {
            BtBankMinerOrderTransaction orderTransaction = minerService.grabMineOrder(member.getId(), orderId);
            if (orderTransaction != null) {
                //处理
                try {
                    activityRedpacketService.processGrabOrderRedPack(member.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return MessageRespResult.success("抢单成功", orderTransaction);
            }
        }

        return MessageRespResult.error("抢单失败");

    }


    @ApiOperation(value = "查询我的帐户信息", response = MinerBalanceVO.class)
    @PostMapping(value = "minerBalance")
    public MessageRespResult getMinerBalance(@MemberAccount Member member) {

        MinerBalanceVO minerBalance = minerService.getMinerBalance(member.getId());

        minerBalance.setTotalRewardSum(BigDecimalUtil.add(minerBalance.getProcessingRewardSum(), minerBalance.getGotRewardSum()));

        return MessageRespResult.success("success", minerBalance);

    }

    @ApiOperation(value = "查询矿池列表", response = BtBankMinerOrder.class, responseContainer = "List")
    @PostMapping(value = "orders")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "当前页", name = "current", dataType = "int", required = true),
            @ApiImplicitParam(value = "每页显示记录数", name = "size", dataType = "int", required = true),
            @ApiImplicitParam(value = "范围为（type 1（0-500】，2（500-2000】，3（2000-5000】，4 (5000 以上)）", name = "type", dataTypeClass = Integer.class, required = true),
    })
    public MessageRespResult getOrders(@MemberAccount Member member, @RequestParam( name = "type") Integer type, @RequestParam(defaultValue = "20", name = "size") int size, @RequestParam(defaultValue = "1", name = "current") int current) {

        MinerOrdersVO minerOrders = minerOrders = minerService.getMinerOrders(current, size,type);
        return MessageRespResult.success("success", minerOrders);

    }
    @ApiOperation(value = "查询订单记录", response = BtBankMinerOrderTransaction.class, responseContainer = "List")
    @PostMapping(value = "orderTransactions")
    public MessageRespResult getOrderTransactions(@MemberAccount Member member, @RequestParam(defaultValue = "1") String types, @RequestParam(defaultValue = "20", name = "size") int size, @RequestParam(defaultValue = "1", name = "current") int current) {

        List<Integer> typeList = new ArrayList<>();
        if (types != null && !StringUtils.isEmpty(types)) {
            for (String s : types.trim().split(",")) {
                try {
                    typeList.add(Integer.valueOf(s));
                } catch (Exception e) {
                    log.info("types 转换出错");
                }
            }
        }
        MinerOrderTransactionsVO minerOrderTransactions = minerService.getMinerOrderTransactionsByMemberId(member.getId(), typeList, current, size);
        return MessageRespResult.success("success", minerOrderTransactions);

    }

    @ApiOperation(value = "查询最低划转资金")
    @GetMapping(value = "config")
    public MessageRespResult<MinerTransferConfigDTO> getConfig() {
        String config = (String) configService.getConfig(BtBankSystemConfig.MINIMUM_TRANSFER_AMOUNT);
        String secKillCommissionRate = (String) configService.getConfig(BtBankSystemConfig.SILVER_MINER_GRAB_COMMISSION_RATE);
        String dispatchCommissionRate = (String) configService.getConfig(BtBankSystemConfig.SILVER_MINER_DISPATCH_COMMISSION_RATE);
        String fixedCommissionRate = (String) configService.getConfig(BtBankSystemConfig.SILVER_MINER_FIXED_COMMISSION_RATE);
        String autoRefreshRate = (String) configService.getConfig(BtBankSystemConfig.AUTO_REFRESH_RATE);

        MinerTransferConfigDTO dto = new MinerTransferConfigDTO();
        dto.setMinimum(BigDecimal.ZERO);
        dto.setSecKillCommissionRate(BigDecimal.ZERO);
        dto.setSecKillCommissionRate(BigDecimal.ZERO);
        dto.setFixedCommissionRate(BigDecimal.ZERO);

        if (config != null) {
            dto.setMinimum(new BigDecimal(config));
        }

        if (secKillCommissionRate != null) {
            dto.setSecKillCommissionRate(new BigDecimal(secKillCommissionRate));
        }

        if (dispatchCommissionRate != null) {
            dto.setDispatchCommissionRate(new BigDecimal(dispatchCommissionRate));
        }

        if (fixedCommissionRate != null) {
            dto.setFixedCommissionRate(new BigDecimal(fixedCommissionRate));
        }

        if (autoRefreshRate != null) {
            dto.setAutoRefreshRate(Long.valueOf(autoRefreshRate));
        }


        //写入金牌矿工比例
        String goldMinerRate = configService.getConfig(BtBankSystemConfig.GOLD_MINER_COMMISSION_RATE).toString();
        dto.setGoldMinerRewardRate(new BigDecimal(goldMinerRate));

        String silverMinerRate = configService.getConfig(BtBankSystemConfig.SILVER_MINER_COMMISSION_RATE).toString();
        dto.setSilverMinerRewardRate(new BigDecimal(silverMinerRate));


        return MessageRespResult.success4Data(dto);
    }


    /**
     * 根据用户id获取用户推荐且成功充值的矿工数量
     */
    @ApiOperation(value = "根据用户id获取用户推荐且成功充值的矿工数量")
    @PostMapping(value = {"/getRecommandAndChargeSuccMemberCount"})
    public MessageRespResult listMembersByIds(@MemberAccount Member member) {
        int count = minerService.getRecommandAndChargeSuccMemberCount(member.getId());
        return MessageRespResult.success(count + "");
    }

    /**
     * 根据用户id获取用户推荐且成功充值的矿工数量
     */
    @ApiOperation(value = "获取token")
    @PostMapping(value = {"/getLSYXToken"})
    public MessageRespResult getLSYXToken(@MemberAccount Member member){
        String mobilePhone = member.getMobilePhone();
        if (StringUtils.isEmpty(mobilePhone)){
            throw new BtBankException(BtBankMsgCode.PLEASE_BING_MOBILE);
        }
        RestTemplate restTemplate=new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("mobile",mobilePhone);

        String lsyx = configService.getConfig(BtBankSystemConfig.CREDIT_PARTNER_NO, v -> v.toString(), "LSYX");

        String url = configService.getConfig(BtBankSystemConfig.CREDIT_GET_TOKEN_URL, v -> v.toString(), "https://code.lianshangyouxuan.com/lsyx/api/user/getToken");

        map.add("partnerNo",lsyx);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        ResponseEntity<MessageRespResult> res = restTemplate.postForEntity(url, request, MessageRespResult.class);
        if (res.getStatusCode()==HttpStatus.OK&&res.getBody().getCode()==200){
            Object data = res.getBody().getData();
            return MessageRespResult.success4Data(data);
        }

        throw new BtBankException(CommonMsgCode.SERVICE_UNAVAILABLE);
    }



}
