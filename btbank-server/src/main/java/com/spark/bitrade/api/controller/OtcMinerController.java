package com.spark.bitrade.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.api.dto.OtcWithdrawDTO;
import com.spark.bitrade.api.vo.OtcWithdrawVO;
import com.spark.bitrade.biz.OtcConfigService;
import com.spark.bitrade.biz.OtcMinerService;
import com.spark.bitrade.biz.PictureService;
import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.constant.CertifiedBusinessStatus;
import com.spark.bitrade.constant.MemberLevelEnum;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.enums.MessageCode;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.face.core.interceptor.annotation.FaceAuthentication;
import com.spark.bitrade.repository.entity.BusinessMinerOrder;
import com.spark.bitrade.repository.entity.OtcOrder;
import com.spark.bitrade.repository.entity.PictureAccessory;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.web.bind.annotation.MemberAccount;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

/**
 * OTC矿工控制器
 *
 * @author biu
 * @since 2019/11/28 10:28
 */
@Slf4j
@Api(tags = {"OTC矿工资产控制器"})
@RequestMapping(path = "api/v2/otc4miner", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RestController
@AllArgsConstructor
public class OtcMinerController {

    private OtcMinerService otcMinerService;
    private PictureService pictureService;
    private OtcConfigService otcConfigService;


    @ApiOperation(value = "pre一键提现接口", notes = "根据操作时间决定是否友情提示")
    @PostMapping("pre")
    public MessageRespResult<String> pre(@MemberAccount Member member) {

        if (member == null) {
            return MessageRespResult.error(MessageCode.MISSING_ACCOUNT);
        }
        LocalTime localTime = LocalTime.now();
        LocalTime min = LocalTime.parse("23:30:00");
        LocalTime max = LocalTime.parse("08:00:00");
        if (localTime.isAfter(min) || localTime.isBefore(max)) {
            return MessageRespResult.success4Data("【友情提示】提示内容：夜深了，您的提现订单可能无法及时处理，请耐心等待商家正常上班时间处理");
        } else {
            return MessageRespResult.success4Data(null);
        }
    }

    @ApiOperation(value = "一键提现接口", notes = "创建挖矿订单，放入矿池")
    @PostMapping("withdraw")
    @FaceAuthentication
    public MessageRespResult<OtcWithdrawVO> withdraw(@MemberAccount Member member, OtcWithdrawDTO dto) {

        // 数据检查
        dto.check();
        //add by qhliao 验证限额
        otcMinerService.withdrawLimitValidate(dto.getAmount());
        //验证手机号
        String mobilePhone = member.getMobilePhone();
        if(StringUtils.isEmpty(mobilePhone)){
            log.error("{}未绑定手机号",member.getId());
            throw new BtBankException(BtBankMsgCode.MOBILE_NO_NOT_FIND);
        }
        if (member.getMemberLevel() != MemberLevelEnum.REALNAME && member.getMemberLevel() != MemberLevelEnum.IDENTIFICATION) {
            log.error("非认证用户 member_id = {}", member.getId());
            throw new BtBankException(BtBankMsgCode.NON_CERTIFIED_MEMBER);
        }

        if (dto.getAmount().compareTo(new BigDecimal("100")) < 0) {
            throw new BtBankException(BtBankMsgCode.MIN_WITHDRAW_AMOUNT);
        }

        // 必须设置银行卡收款方式
        if (!StringUtils.hasText(member.getBank()) || !StringUtils.hasText(member.getCardNo())) {
            log.error("未设置银行卡收款方式 member_id= {}", member.getId());
            throw new BtBankException(BtBankMsgCode.BANK_ACCOUNT_NOT_EXIST);
        }

        // 创建订单放入矿池
        OtcWithdrawVO withdraw = otcMinerService.withdraw(member.getId(), dto.getAmount(), dto.getPayMode());
        Integer size = otcConfigService.getValue("OTC_DIG_ORDER_MAX_DISPLAY", (v) -> Integer.parseInt(v), 5);
        otcMinerService.updateQueueStatus(size);
        return MessageRespResult.success4Data(withdraw);
    }

    @ApiOperation(value = "矿池订单列表", notes = "矿池订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "当前页", name = "current", dataType = "int", required = true),
            @ApiImplicitParam(value = "每页显示记录数", name = "size", dataType = "int", required = true),
            @ApiImplicitParam(value = "订单状态 [0:新订单,1:未付款,2:已付款,3:已完成,4:申诉中,5:已关闭,6：取消]", name = "status", dataTypeClass = Integer.class, required = true),
            @ApiImplicitParam(value = "是否只显示抢到的订单 1：是 0：否", name = "owned", dataTypeClass = Integer.class),
            @ApiImplicitParam(value = "是否只显示自己的订单 1：是 0：否", name = "self", dataTypeClass = Integer.class),
    })
    @PostMapping("list")
    public MessageRespResult<IPage<OtcWithdrawVO>> getList(@MemberAccount Member member,
                                                           @RequestParam(value = "owned", required = false) Integer owned,
                                                           @RequestParam(value = "self", required = false) Integer self,
                                                           @RequestParam(value = "status", required = false) Integer status,
                                                           Integer current,
                                                           Integer size) {

        // 查看自己的提现订单不需要认证 查看已抢到的不需要认证
        if (!new Integer(1).equals(self) && !new Integer(1).equals(owned) && isNotCertifiedMerchant(member)) {
            log.error("非认证商家 member_id = {}", member.getId());
            throw new BtBankException(BtBankMsgCode.NON_CERTIFIED_MERCHANT);
        }
        Integer display=otcConfigService.getValue("OTC_DIG_ORDER_MAX_DISPLAY", (v) -> Integer.parseInt(v), 5);
        if (status!=null&&status==0){
             size = display;
        }
        IPage<BusinessMinerOrder> page = new Page<>(current, size);
        QueryWrapper<BusinessMinerOrder> query = new QueryWrapper<>();

        if (owned != null && owned == 1) {
            query.eq("buy_id", member.getId());
        }
        if (status != null) {
            query.eq("status", status);
        }
        if (self != null && self == 1) {
            query.eq("sell_id", member.getId());
        }
        if(status!=null&&status==0){
            query.orderByDesc("queue_status").orderByAsc("create_time");
        }else {
            query.orderByAsc("create_time");
        }

        //更新排队状态
        otcMinerService.updateQueueStatus(display);
        IPage<OtcWithdrawVO> result = otcMinerService.page(page, query).convert(OtcWithdrawVO::of);
        return MessageRespResult.success4Data(result);
    }

    @ApiOperation(value = "提现订单列表", notes = "提现订单列表", response = OtcWithdrawVO.class)
    @ApiImplicitParams({
            @ApiImplicitParam(value = "当前页", name = "current", dataType = "int", required = true),
            @ApiImplicitParam(value = "每页显示记录数", name = "size", dataType = "int", required = true),
            @ApiImplicitParam(value = "订单状态 [0:新订单,1:未付款,2:已付款,3:已完成,4:申诉中,5:已关闭，6:提现取消]", name = "status", dataTypeClass = Integer.class),
    })
    @PostMapping("drawlist")
    public MessageRespResult<IPage<OtcWithdrawVO>> getwithDrawList(@MemberAccount Member member,
                                                                   @RequestParam(value = "status", required = true) Integer status,
                                                                   Integer current,
                                                                   Integer size) {
        IPage<BusinessMinerOrder> page = new Page<>(current, size);
        QueryWrapper<BusinessMinerOrder> query = new QueryWrapper<>();
        if (status != null) {
            query.eq("status", status);
        }
        query.eq("sell_id", member.getId());
        query.orderByAsc("create_time");

        IPage<OtcWithdrawVO> result = otcMinerService.page(page, query).convert(OtcWithdrawVO::of);
        return MessageRespResult.success4Data(result);
    }

    @ApiOperation(value = "商家挖矿抢单", notes = "用户一键提现，商家挖矿抢单")
    @PostMapping("mining")
    public MessageRespResult mining(@MemberAccount Member member, @ApiParam(name = "id", value = "提现订单ID") Long id) {
        if (id == null) {
            throw new BtBankException(CommonMsgCode.INVALID_PARAMETER);
        }

        if (isNotCertifiedMerchant(member)) {
            log.error("非认证商家 member_id = {}", member.getId());
            throw new BtBankException(BtBankMsgCode.NON_CERTIFIED_MERCHANT);
        }
        // 挖矿
        OtcOrder otcOrder = otcMinerService.mining(member.getId(), id);
        //修改排队状态
        Integer size = otcConfigService.getValue("OTC_DIG_ORDER_MAX_DISPLAY", (v) -> Integer.parseInt(v), 5);
        otcMinerService.updateQueueStatus(size);
        return MessageRespResult.success4Data(otcOrder);

    }

    @ApiOperation(value = "付款凭证上传", notes = "otc商家挖矿付款凭证上传")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "订单编号", name = "id", dataType = "long", required = true),
            @ApiImplicitParam(value = "付款回执", name = "paymentReceiptUrl", dataType = "string", required = true),
            @ApiImplicitParam(value = "转账信息", name = "transferUrl", dataType = "string", required = true),
    })
    @PostMapping("savePaymentPicture")
    public MessageRespResult savePaymentPicture(Long id, String paymentReceiptUrl, String transferUrl) {
        if (id == null || "".equals(paymentReceiptUrl) || "".equals(transferUrl)) {
            throw new BtBankException(CommonMsgCode.INVALID_PARAMETER);
        }
        pictureService.saveMinerPayUrl(id, paymentReceiptUrl, transferUrl);
        return MessageRespResult.success();
    }

    @ApiOperation(value = "付款凭证上传", notes = "otc商家挖矿付款凭证上传")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "订单编号", name = "id", dataType = "long", required = true),
            @ApiImplicitParam(value = "凭证类型 [0:付款回执,1:转账信息]", name = "type", dataType = "int", required = true),
            @ApiImplicitParam(value = "图片地址", name = "url", dataType = "string", required = true),
    })
    @PostMapping("savePaymentPicture2")
    public MessageRespResult savePaymentPicture(Long id, Integer type, String url) {
        if (id == null || type == null || (type != 0 && type != 1) || "".equals(url)) {
            throw new BtBankException(CommonMsgCode.INVALID_PARAMETER);
        }
        pictureService.saveMinerPayUrl(id, type, url);
        return MessageRespResult.success();
    }

    @ApiOperation(value = "付款凭证查询", notes = "otc商家挖矿付款凭证查询")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "otc_order_id", name = "id", dataType = "long", required = true),
    })
    @PostMapping("getPaymentPicture")
    public MessageRespResult<List<PictureAccessory>> getPaymentPicture(Long id) {
        if (id == null) {
            throw new BtBankException(CommonMsgCode.INVALID_PARAMETER);
        }
        List<PictureAccessory> list = pictureService.getMinerPayUrl(id);
        return MessageRespResult.success4Data(list);

    }

    private boolean isNotCertifiedMerchant(Member member) {
        // 非认证用户
        if (member.getMemberLevel() != MemberLevelEnum.IDENTIFICATION) {
            return true;
        }
        CertifiedBusinessStatus status = member.getCertifiedBusinessStatus();

        return CertifiedBusinessStatus.VERIFIED != status
                && CertifiedBusinessStatus.CANCEL_AUTH != status
                && CertifiedBusinessStatus.RETURN_FAILED != status;
    }


}
