package com.spark.bitrade.api.controller;

import com.aliyuncs.utils.StringUtils;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.api.vo.*;
import com.spark.bitrade.biz.EnterpriseService;
import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.constant.BtBankSystemConfig;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.MemberWallet;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.EnterpriseMiner;
import com.spark.bitrade.repository.entity.EnterpriseMinerTransaction;
import com.spark.bitrade.repository.service.EnterpriseMinerService;
import com.spark.bitrade.service.BtBankConfigService;
import com.spark.bitrade.service.MemberWalletService;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * EnterpriseMinerController
 *
 * @author biu
 * @since 2019/12/23 17:20
 */
@Slf4j
@Api(tags = {"企业矿工控制器"})
@RequestMapping(path = "api/v2/enterprise", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@AllArgsConstructor
public class EnterpriseMinerController {
    private EnterpriseMinerService enterpriseMinerService;
    private EnterpriseService enterpriseService;
    private BtBankConfigService btBankConfigService;
    private MemberWalletService memberWalletService;

    @ApiOperation(value = "获取矿工信息", notes = "返回基本的矿工信息", response = EnterpriseMinerVO.class)
    @GetMapping("/miner/{memberId}")
    public MessageRespResult<EnterpriseMinerVO> miner(@PathVariable("memberId") Long memberId) {
        return enterpriseService.findByMemberId(memberId)
                .map(MessageRespResult::success4Data)
                .orElse(this.error("未找到企业矿工"));
    }

    @ApiOperation(value = "企业矿工申请查看", notes = "查看申请结果", response = ApplicationResultVO.class)
    @GetMapping("/miner/apply")
    public MessageRespResult<ApplicationResultVO> apply(@MemberAccount Member member) {
        return MessageRespResult.success4Data(enterpriseService.findApplication(member.getId()));
    }

    @ApiOperation(value = "企业矿工申请", notes = "申请成为企业矿工 或 申请退出企业矿工", response = ApplicationResultVO.class)
    @PostMapping("/miner/apply")
    public MessageRespResult<ApplicationResultVO> apply(@MemberAccount Member member,
                                                        ApplicationVO app) {
        return MessageRespResult.success4Data(enterpriseService.apply(member.getId(), app));
    }

    @ApiOperation(value = "划转", notes = "余额和企业矿池余额之间的划转")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "方向 0: 矿池转出到余额 1：余额转入矿池", name = "direction", dataType = "int", required = true),
            @ApiImplicitParam(value = "数量 不能少于100BT", name = "amount", required = true),
    })
    @PostMapping("/transfer")
    public MessageRespResult<Boolean> transfer(@MemberAccount Member member,
                                               @RequestParam(value = "direction") Integer direction,
                                               @RequestParam(value = "amount") BigDecimal amount) {
        // 校验最低转入金额
        // 奖励发放账户
        String minTransferAmount = (String) btBankConfigService.getConfig(BtBankSystemConfig.ENTERPRISE_MINIMUM_TRANSFER_AMOUNT);
        if (StringUtils.isEmpty(minTransferAmount)) {
            throw new IllegalArgumentException("未找到企业挖矿最低划转金额 ENTERPRISE_MINIMUM_TRANSFER_AMOUNT 配置");
        }
        TransferVo transferVo = new TransferVo(member.getId(), direction, amount);
        if (amount == null) {


            throw new BtBankException(BtBankMsgCode.ENTERPRISE_MINER_TRANSFER_MINLIMIT);
            // throw new BtBankException(4001, "划转数量不能少于"+minTransferAmount+"BT");
        }

        if (transferVo.isIn()) {
            if (amount.compareTo(new BigDecimal(minTransferAmount)) < 0) {
                // throw new BtBankException(4001, "划转数量不能少于"+minTransferAmount+"BT");
                throw new BtBankException(BtBankMsgCode.ENTERPRISE_MINER_TRANSFER_MINLIMIT);

            }
            // 1：余额转入矿池"限制余额
            MessageRespResult<MemberWallet> messageRespResultmemberWallet = memberWalletService.getWalletByUnit(member.getId(), "BT");

            if (messageRespResultmemberWallet == null || messageRespResultmemberWallet.getData() == null) {
                throw new BtBankException(BtBankMsgCode.ENTERPRISE_MINER_BALANCE_NOTENOUGH);
                // throw new BtBankException(4001, "可用余额不足");
            }
            MemberWallet memberWallet = messageRespResultmemberWallet.getData();
            if (memberWallet == null || memberWallet.getBalance() == null || memberWallet.getBalance().compareTo(amount) < 0) {
                // throw new BtBankException(4001, "可用余额不足");
                throw new BtBankException(BtBankMsgCode.ENTERPRISE_MINER_BALANCE_NOTENOUGH);
            }
        } else if (transferVo.isOut()) {
            //余额
            EnterpriseMiner miner = enterpriseMinerService.findByMemberId(member.getId());
            if (miner.getBalance() == null || miner.getBalance().compareTo(amount) < 0) {
                // throw new BtBankException(4001, "矿池余额不足");
                throw new BtBankException(BtBankMsgCode.ENTERPRISE_MINER_ENTERPRIZE_BALANCE_NOTENOUGH);
            }
        }


        transferVo.doCheck();

        if (!enterpriseService.isAvailableEnterpriseMiner(member.getId())) {
            throw new BtBankException(BtBankMsgCode.ENTERPRISE_MINER_NOT_FOUND);
            // return this.error("不是有效的企业矿工");
        }

        return MessageRespResult.success4Data(enterpriseService.transfer(transferVo));
    }

    @ApiOperation(value = "查询矿池资金明细", notes = "查询矿池资金明细，适配BT矿工矿池流水", response = EnterpriseMinerTransactionsVO.class, responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "当前页", name = "current", dataType = "int", required = true),
            @ApiImplicitParam(value = "每页显示记录数", name = "size", dataType = "int", required = true),
            @ApiImplicitParam(value = "日期范围 格式: 2019-12-12 ~ 2019-12-13", name = "range", dataTypeClass = String.class),
    })
    @PostMapping("/minerBalanceTransaction")
    public MessageRespResult<EnterpriseMinerTransactionsVO> minerBalanceTransaction(
            @MemberAccount Member member,
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam(value = "size", defaultValue = "20") Integer size,
            @RequestParam(value = "range", required = false) String range) {

        QueryVo queryVo = QueryVo.builder().current(current).size(size).build();
        queryVo.parseRange("yyyy-MM-dd", "~", range);
        EnterpriseMinerTransactionsVO enterpriseMinerTransactionsVO = enterpriseService.query(member.getId(), Arrays.asList(3, 4), queryVo);
        List<EnterpriseMinerTransaction> contentList = enterpriseMinerTransactionsVO.getContent();
        for (int i = 0; i < contentList.size(); i++) {
            if (contentList.get(i).getAmount().compareTo(BigDecimal.ZERO) == -1) {
                contentList.get(i).setAmount(contentList.get(i).getAmount().negate());
            }
        }
        return MessageRespResult.success4Data(enterpriseMinerTransactionsVO);
    }

    @ApiOperation(value = "查询矿池资金明细", notes = "注意：当type=4时,amount=订单数量,reward=实际收益数量", response = EnterpriseMinerTransaction.class, responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "当前页", name = "current", dataType = "int", required = true),
            @ApiImplicitParam(value = "每页显示记录数", name = "size", dataType = "int", required = true),
            @ApiImplicitParam(value = "类型 1：转入 2：转出 3：抢单挖矿 4：挖矿收益；参数格式(逗号分隔) eg. 1,2", name = "type", dataTypeClass = String.class),
            @ApiImplicitParam(value = "日期范围 格式: 2019-12-12 ~ 2019-12-13", name = "range", dataTypeClass = String.class),
    })
    @PostMapping("/transactions")
    public MessageRespResult<IPage<EnterpriseMinerTransaction>> transactions(
            @MemberAccount Member member,
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam(value = "type") String type,
            @RequestParam(value = "size", defaultValue = "20") Integer size,
            @RequestParam(value = "range", required = false) String range) {

        List<Integer> types = new ArrayList<>();
        for (String s : type.split(",")) {
            try {
                types.add(Integer.parseInt(s));
            } catch (NumberFormatException ex) {
                // ignore
                log.error("类型转换出错 s = {}, err = {}", s, ex.getMessage());
            }
        }

        QueryVo queryVo = QueryVo.builder().current(current).size(size).build();
        queryVo.parseRange("yyyy-MM-dd", "~", range);
        IPage<EnterpriseMinerTransaction> pageresult = enterpriseService.page(member.getId(), types, queryVo);
        for (int i = 0; i < pageresult.getRecords().size(); i++) {
            if (pageresult.getRecords().get(i).getAmount().compareTo(BigDecimal.ZERO) == -1) {
                pageresult.getRecords().get(i).setAmount(pageresult.getRecords().get(i).getAmount().negate());
            }
        }
        return MessageRespResult.success4Data(pageresult);
    }


    private <T> MessageRespResult<T> error(String message) {
        return new MessageRespResult<>(1, message, null);
    }
}
