package com.spark.bitrade.biz.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.spark.bitrade.api.dto.BankInfo;
import com.spark.bitrade.api.dto.FixOrderDto;
import com.spark.bitrade.api.dto.MemberOrderCountDTO;
import com.spark.bitrade.api.vo.OtcWithdrawVO;
import com.spark.bitrade.biz.MinerOtcOrderService;
import com.spark.bitrade.biz.OtcConfigService;
import com.spark.bitrade.biz.OtcLimitService;
import com.spark.bitrade.biz.OtcMinerService;
import com.spark.bitrade.constant.*;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.WalletChangeRecord;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.*;
import com.spark.bitrade.repository.mapper.OtcMinerMapper;
import com.spark.bitrade.repository.service.*;
import com.spark.bitrade.service.BtV1CollectActionKafkaPusher;
import com.spark.bitrade.service.IOtcExchange;
import com.spark.bitrade.service.MemberAccountService;
import com.spark.bitrade.service.MemberWalletService;
import com.spark.bitrade.sms.KafkaSMSProvider;
import com.spark.bitrade.util.BigDecimalUtils;
import com.spark.bitrade.util.GeneratorUtil;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * OtcMinerServiceImpl
 *
 * @author biu
 * @since 2019/11/28 13:43
 */
@Slf4j
@Service
public class OtcMinerServiceImpl implements OtcMinerService {
    private static SimpleDateFormat sdf=new SimpleDateFormat("HH:mm:ss");
    private MemberWalletService walletService;
    private BtV1CollectActionKafkaPusher kafkaPusher;

    private BusinessMinerOrderService minerOrderService;
    private OtcLimitService otcLimitService;
    private OtcCoinService otcCoinService;
    private MemberAccountService memberAccountService;
    private OtcOrderService otcOrderService;

    private MinerOtcOrderService minerOtcOrderService;
    private OtcConfigService otcConfigService;
    private BtAppealService btAppealService;
    private MemberPaymentAccountService memberPaymentAccountService;

    private StringRedisTemplate redisTemplate;
    private OtcMinerMapper otcMinerMapper;

    private IOtcExchange iOtcExchange;
    private AdvertisesService advertisesService;

    private KafkaSMSProvider smsProvider;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OtcWithdrawVO withdraw(Long memberId, BigDecimal amount, PayMode payMode) {

        // 防止重放攻击
        final String key = "replay:attack:withdraw:" + memberId;
        ValueOperations<String, String> operations = redisTemplate.opsForValue();

        Long increment = operations.increment(key, 1);
        if (new Long(1).equals(increment)) {
            redisTemplate.expire(key, 5, TimeUnit.SECONDS);
        } else {
            redisTemplate.expire(key, 5, TimeUnit.SECONDS);
            // throw new BtBankException(BtBankMsgCode.WITHDRAWAL_IN_PROGRESS);
            throw new BtBankException(4010, "FORBID_RESUBMIT");
        }

        // 同一时间只能提现一笔
        if (minerOrderService.countInProgress(memberId) > 0) {
            throw new BtBankException(BtBankMsgCode.WITHDRAWAL_IN_PROGRESS);
        }

        // 法币购买的BT，次日才可一键体现和转出
        // 静止提现的数量
        BigDecimal forbid = otcLimitService.forbidToWithdrawAndTransferOut(memberId);
        //  尝试冻结余额 balance - forbid >= amount
        BigDecimal minimum = forbid.add(amount);
        // 提现余额检查， 尝试冻结余额 balance - forbid >= amount
        if (!otcLimitService.balanceIsEnough(memberId, minimum)) {
            throw new BtBankException(BtBankMsgCode.INSUFFICIENT_WITHDRAWAL_BALANCE);
        }

        // 获取提现服务费费率
        BigDecimal rate = otcConfigService.getValue(OtcConfigType.OTC_WITHDRAW_SERVICE_RATE,
                BigDecimal::new,
                new BigDecimal("0.005"));
        // 获取时间限制
        String start = otcConfigService.getValue(OtcConfigType.OTC_WITHDRAW_LIMIT_MIN,
                String::new,
                "00:00");
        String end = otcConfigService.getValue(OtcConfigType.OTC_WITHDRAW_LIMIT_MAX,
                String::new,
                "00:00");
        if (otcLimitService.isInPunishment(memberId)) {
            rate = otcConfigService.getValue(OtcConfigType.OTC_WITHDRAW_PUNISHMENT_SERVICE_RATE,
                    BigDecimal::new,
                    new BigDecimal("0.01"));
            log.info("处在风控规则处罚中, 使用处罚提现费率 rate = {}", rate);
        }
        //考虑到深夜商家休息，系统凌晨0：00分—早上8：00，提现手续费翻倍，已经被处罚的用户手续费2%，普通用户手续费1%；  2020-02-17 mahao
        LocalTime localTime = LocalTime.now();
        LocalTime min = LocalTime.parse(start);
        LocalTime max = LocalTime.parse(end);
        if (localTime.isAfter(min) && localTime.isBefore(max)) {
            BigDecimal rateTimes = otcConfigService.getValue(OtcConfigType.UPOTC_WITHDRAW_SERVICE_RATE,
                    BigDecimal::new,
                    new BigDecimal(2));
            rate = rate.multiply(rateTimes);

        }
        // 创建订单
        BusinessMinerOrder order = new BusinessMinerOrder();
        order.setId(IdWorker.getId());
        order.setSellId(memberId);

        BigDecimal fee = amount.multiply(rate).setScale(8, BigDecimal.ROUND_DOWN);
        // amount = amount.subtract(fee);

        order.setAmount(amount);
        order.setFee(fee);
        order.setRewardAmount(fee);
        order.setPayMode(payMode.name());

        order.setStatus(OtcMinerOrderStatus.New.getCode());
        Date date = new Date();
        order.setCreateTime(date);
        date.setTime(System.currentTimeMillis()+1000);
        order.setUpdateTime(date);
        if (minerOrderService.save(order)) {
            // 尝试冻结
            MessageRespResult<Boolean> freeze = walletService.freeze(TransactionType.OTC_WITHDRAW_FROZEN, memberId,
                    "BT", "BT", amount, minimum, order.getId(), "一键提现，最低余额=" + minimum);
            if (freeze.isSuccess() && Boolean.TRUE.equals(freeze.getData())) {
                log.info("一键提现成功，加入矿池 order = {}", order);
                return OtcWithdrawVO.of(order);
            } else {
                log.error("一键提现失败 code = {}, data = {}, err = {}", freeze.getCode(), freeze.getData(), freeze.getMessage());
                throw new BtBankException(BtBankMsgCode.of(freeze.getCode(), freeze.getMessage()));
            }
        }

        throw new BtBankException(BtBankMsgCode.FAILED_TO_ADD_ORDER_RECORD);
    }

    @Override
    public IPage<BusinessMinerOrder> page(IPage<BusinessMinerOrder> page, QueryWrapper<BusinessMinerOrder> query) {
        return minerOrderService.page(page, query);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public OtcOrder mining(Long memberId, Long id) {

        long orderSn = IdWorker.getId();

        // 订单必须存在且状态为 0 = New
        BusinessMinerOrder minerOrder = minerOrderService.getById(id);
        if (minerOrder == null || !new Integer(0).equals(minerOrder.getStatus())) {
            throw new BtBankException(BtBankMsgCode.ORDER_NOT_EXIST);
        }
        // 不能抢自己的单
        if (minerOrder.getSellId().equals(memberId)) {
            log.error("不能抢自己的订单");
            throw new BtBankException(BtBankMsgCode.FORBID_GRAB_OWN);
        }
        //add by qhliao 验证限额
        getService().withdrawLimitValidate(minerOrder.getAmount());

        // 内部商家不受订单数目限制
        if(otcMinerMapper.chechInnerMember(memberId)!=1){
            // 不能有OTC买单
            if (otcOrderService.countOrders(memberId, true) > 0) {
                log.error("存在其他进行中的OTC买单，无法抢单");
                throw new BtBankException(BtBankMsgCode.HAVE_AN_UNFINISHED_ORDER);
            }
        }
        // 矿池订单改变状态
        boolean result = minerOrderService.lambdaUpdate()
                .set(BusinessMinerOrder::getStatus, OtcMinerOrderStatus.Unpaid.getCode())
                .set(BusinessMinerOrder::getBuyId, memberId)
                .set(BusinessMinerOrder::getRefId, orderSn + "")
                .set(BusinessMinerOrder::getQueueStatus,0)
                .set(BusinessMinerOrder::getUpdateTime,new Date())
                .eq(BusinessMinerOrder::getId, id).eq(BusinessMinerOrder::getStatus, OtcMinerOrderStatus.New.getCode())
                .update();

        if (!result) {
            log.info("otc矿池订单状态修改失败-> businessMinerOrderId-{}", id);
            throw new BtBankException(BtBankMsgCode.FAILED_TO_MODIFY_THE_ORDER);
        }

        // 创建OtcOrder记录
        OtcCoin otcCoin = otcCoinService.lambdaQuery().eq(OtcCoin::getUnit, "BT").one();
        Member member = memberAccountService.findMemberByMemberId(memberId);
        Member customer = memberAccountService.findMemberByMemberId(minerOrder.getSellId());
        BankInfo bankInfo = BankInfo.builder()
                .bank(customer.getBank())
                .branch(customer.getBranch())
                .cardNo(customer.getCardNo())
                .build();

        String payMethodInfo = handlePayModeInfo(customer, PayMode.BANK, customer.getRealName(), bankInfo);

        String serviceRate = otcConfigService.getValue(OtcConfigType.OTC_MINER_COMMISSION_RATE);

        if (otcLimitService.isInPunishment(minerOrder.getSellId())) {
            serviceRate = otcConfigService.getValue(OtcConfigType.OTC_WITHDRAW_PUNISHMENT_SERVICE_RATE);
            log.info("卖家处在风控规则处罚中，服务费率 rate = {}", serviceRate);
        }
        // 获取时间限制
        String start = otcConfigService.getValue(OtcConfigType.OTC_WITHDRAW_LIMIT_MIN,
                String::new,
                "00:00");
        String end = otcConfigService.getValue(OtcConfigType.OTC_WITHDRAW_LIMIT_MAX,
                String::new,
                "00:00");
        //0-8点手续费加倍
        LocalTime min = LocalTime.parse(start);
        LocalTime max = LocalTime.parse(end);
        LocalTime minerOrderLocalTime =   LocalTime.parse(sdf.format(minerOrder.getCreateTime()));
        BigDecimal rateTimes = new BigDecimal(1);
        if (minerOrderLocalTime.isAfter(min) && minerOrderLocalTime.isBefore(max)) {
            rateTimes = otcConfigService.getValue(OtcConfigType.UPOTC_WITHDRAW_SERVICE_RATE,
                    BigDecimal::new,
                    new BigDecimal(2));

        }
        BigDecimal rate = new BigDecimal(serviceRate).multiply(rateTimes);
        BigDecimal serviceMoney = minerOrder.getAmount().multiply(rate);
        String randomCode = String.valueOf(GeneratorUtil.getRandomNumber(1000, 9999));

        OtcOrder otcOrder = new OtcOrder();

        // 商家挖矿0, 不适用需求10 -> 法币一键购买的BT，次日才可一键提现和转出
        otcOrder.setAdvertiseType(0);
        // 特殊标识
        otcOrder.setAdvertiseId(0L);
        otcOrder.setCoinId(otcCoin.getId());
        otcOrder.setCountry("中国");
        otcOrder.setCommission(BigDecimal.ZERO);
        otcOrder.setCreateTime(new Date());
        otcOrder.setCustomerId(customer.getId());
        otcOrder.setCustomerName(customer.getUsername());
        otcOrder.setCustomerRealName(customer.getRealName());
        otcOrder.setMemberId(memberId);
        otcOrder.setMemberName(member.getUsername());
        otcOrder.setMemberRealName(member.getRealName());
        // 服务费让利给商家
        otcOrder.setMoney(minerOrder.getAmount().subtract(serviceMoney));
        otcOrder.setNumber(minerOrder.getAmount());
        otcOrder.setPrice(BigDecimal.ONE);
        otcOrder.setOrderMoney(minerOrder.getAmount());
        otcOrder.setOrderSn(orderSn);
        //修改申诉时间15分钟
        otcOrder.setTimeLimit(15);
        otcOrder.setBranch(customer.getBranch());
        otcOrder.setBank(customer.getBank());
        otcOrder.setCardNo(customer.getCardNo());
        otcOrder.setPayMethodInfo(payMethodInfo);
        otcOrder.setServiceMoney(serviceMoney);
        otcOrder.setServiceRate(rate);
        otcOrder.setPayMode(PayMode.BANK.getCnName());
        otcOrder.setPayCode(randomCode);
        otcOrder.setStatus(1);
        otcOrder.setOrderSourceType(24984705);
        // 特殊标识，不可删除或修改
        otcOrder.setRemark("用户一键提币，商家挖矿,businessMinerOrderId-" + id);

        boolean res = otcOrderService.save(otcOrder);
        if (!res) {
            log.info("otc商家挖矿订单创建失败-> orderSn-{}", orderSn);
            throw new BtBankException(BtBankMsgCode.FAILED_TO_ADD_ORDER_RECORD);
        }

        // Kafka 事件触发
        kafkaPusher.pushOtcOrderCreated(otcOrder.getMemberId(), otcOrder.getOrderSn());

        return otcOrder;
    }

    @Override
    public boolean updateOrderStatus(String refId, OtcMinerOrderStatus source, OtcMinerOrderStatus dest) {

        // 判断订单是否存在
        QueryWrapper<BusinessMinerOrder> query = new QueryWrapper<>();
        query.eq("ref_id", refId);

        int count = minerOrderService.count(query);
        // 无匹配数据
        if (count == 0) {
            return false;
        }
        // 异常数据
        if (count > 1) {
            throw new RuntimeException("数据异常, refId = " + refId + ", count = " + count);
        }
        BusinessMinerOrder order = minerOrderService.getOne(query);
        UpdateWrapper<BusinessMinerOrder> update = new UpdateWrapper<>();
        update.eq("ref_id", refId).eq("status", source)
                .set("status", dest).set("update_time", new Date());

        // 撤单,胜诉
        if (dest == OtcMinerOrderStatus.New) {
            // 清空信息
            update.set("buy_id", null).set("ref_id", null);
        }

        boolean ret = minerOrderService.update(update);

        log.info("更新订单状态处理结果 ref_id = {}, source = {}, dest = {}, ret = {}", refId, source, dest, ret);
        if(order!=null){
            checkAndDispath(order);
        }
        return ret;

    }
    @Transactional(rollbackFor = Exception.class)
    public void checkAndDispath(BusinessMinerOrder order){
        if(order.getBuyId()!=null){
            //购买方是内部商家直接分配其他商家
            if(otcMinerMapper.chechInnerMember(order.getBuyId())==1){
                List<MemberOrderCountDTO> members = otcMinerMapper.innerMembers();
                if (members == null || members.size() == 0) {
                    log.info("未找到可匹配的内部商家 orderid = {}", order.getId());
                    return ;
                }
                Long lastbuyerid = otcMinerMapper.getByOrderSn( order.getRefId());
                int retry = 0;
                // 重试3次
                OtcOrder otc = null;
                while ( retry < 4) {
                    int index=(int)(Math.random()*members.size());
                    Long memberId = members.get(index).getId();
                    if(memberId.equals(order.getBuyId())){
                        continue;
                    }
                    // 匹配到自己的单
                    if (order.getSellId().equals(memberId)) {
                        retry++;
                        continue;
                    }
                    try {
                        otc = getService().mining(memberId, order.getId());
                        log.info("自动分配矿池订单成功 id = {}, order_sn = {},new_memberId={},old_memberId={}", order.getId(), otc.getOrderSn(),memberId,order.getBuyId());

                    } catch (BtBankException ex) {
                        log.error("自动分配矿池订单出错 id = {}, code = {}, err = {}", order.getId(), ex.getCode(), ex.getMessage());
                    } catch (RuntimeException ex) {
                        log.error("自动分配矿池订单出错 id = {}, code = {}, err = {}", order.getId(), 500, ex.getMessage());
                    }
                    break;
                }
            }
        }
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void appeal(String orderSn) {
        Optional<OtcOrder> optional = otcOrderService.findOneByOrderSn(NumberUtils.toLong(orderSn, 0L));

        if (optional.isPresent()) {
            OtcOrder order = optional.get();
            Integer status = order.getStatus();

            // 必须是已付款 status = 2
            if (!new Integer(2).equals(status)) {
                log.error("订单状态不符合自动申诉条件 order_sn = {}, status = {}", orderSn, status);
                return;
            }

            // 创建申诉
            BtAppeal appeal = new BtAppeal();
            appeal.setInitiatorId(order.getMemberId());
            appeal.setAssociateId(order.getCustomerId());
            appeal.setOrderId(order.getId());
            appeal.setAppealType(3);
            appeal.setRemark("请求放币，超过15分钟未放币自动申诉");
            appeal.setStatus(0);
            appeal.setCreateTime(new Date());

            boolean save = btAppealService.save(appeal);

            // 更新OTC订单状态
            boolean step1 = updateOrderStatus(orderSn, OtcMinerOrderStatus.Paid, OtcMinerOrderStatus.Appeal);

            UpdateWrapper<OtcOrder> update = new UpdateWrapper<>();
            update.eq("id", order.getId()).eq("status", OtcMinerOrderStatus.Paid.getCode())
                    .set("status", OtcMinerOrderStatus.Appeal.getCode());
            boolean step2 = otcOrderService.update(update);

            // Kafka 事件触发
            if (step2) {
                kafkaPusher.pushOtcOrderAppealCreated(order.getMemberId(), order.getOrderSn());
            }

            if (!save || !step1 || !step2) {
                log.error("自动申诉业务处理失败 save_appeal ={}, update_miner_order = {}, update_otc_order = {}", save, step1, step2);
                throw new BtBankException(500, "自动申诉业务处理失败");
            }
            log.info("自动申诉业务处理完成 order_sn = {}", orderSn);
        }

    }

    @Override
    public List<OtcWithdrawVO> findPaidOrders() {
        QueryWrapper<BusinessMinerOrder> query = new QueryWrapper<>();
        query.eq("status", OtcMinerOrderStatus.Paid.getCode());
        return minerOrderService.list(query).stream().map(OtcWithdrawVO::of).collect(Collectors.toList());
    }

    @Override
    public void appealCompleted(Long appealId) {
        BtAppeal appeal = btAppealService.getById(appealId);
        if (appeal == null) {
            log.error("未找到申诉记录 id = {}", appealId);
            return;
        }

        OtcOrder order = otcOrderService.getById(appeal.getOrderId());
        if (order == null) {
            log.error("申诉对应订单未找到 appeal_id = {}, order_id = {}", appealId, appeal.getOrderId());
            return;
        }

        Long orderSn = order.getOrderSn();
        QueryWrapper<BusinessMinerOrder> query = new QueryWrapper<>();
        query.eq("ref_id", orderSn + "");
        BusinessMinerOrder mo = minerOrderService.getOne(query);

        if (mo == null) {
            log.warn("非挖矿订单，不处理 appeal_id = {}, order_sn = {}", appealId, orderSn);
            return;
        }

        // 申诉放币
        if (new Integer(1).equals(appeal.getIsSuccess())) {
            updateOrderStatus(orderSn + "", OtcMinerOrderStatus.Appeal, OtcMinerOrderStatus.Close);
        }
        // 败诉重回矿池
        else {
            updateOrderStatus(orderSn + "", OtcMinerOrderStatus.Appeal, OtcMinerOrderStatus.New);
        }
    }

    @Override
    public boolean isInnerMemberAccount(Long memberId) {
        return otcLimitService.isInnerMerchant(memberId);
    }

    @Override
    public MessageRespResult dispatchOtcSaleReward(Long orderId) {

        //按用户发放 按日期分组
        OtcOrder order = otcOrderService.lambdaQuery().eq(OtcOrder::getId, orderId).one();//.getById(orderId);

        if (order == null) {
            throw new BtBankException(BtBankMsgCode.OTC_ORDER_NOT_EXSITS);
        }
        if (order.getReleaseTime() == null) {
            throw new BtBankException(BtBankMsgCode.OTC_ORDER_NEED_RELEASE);
        }

        if (order.getSaleRewardStatus() == 1) {
            return MessageRespResult.error("已发放补贴的订单不再发放");
        }
        if (order.getAdvertiseType() == 0) {
            return MessageRespResult.error("购买广告的订单不发放补贴");
        }
        minerOtcOrderService.dispatchSaleOrderReward(order);
        return MessageRespResult.success();
    }

    private String handlePayModeInfo(Member member, PayMode payMode, String realName, BankInfo bankInfo) {
        Map<String, Object> map = new HashMap<>();
        if (payMode == PayMode.BANK) {
            map.put("payInfo", bankInfo);
        } else {
            map.put("payInfo", "unknown");
        }
        map.put("realName", realName);
        return JSON.toJSONString(map);
    }

    @Autowired
    public void setWalletService(MemberWalletService walletService) {
        this.walletService = walletService;
    }

    @Autowired
    public void setKafkaPusher(BtV1CollectActionKafkaPusher kafkaPusher) {
        this.kafkaPusher = kafkaPusher;
    }

    @Autowired
    public void setMinerOrderService(BusinessMinerOrderService minerOrderService) {
        this.minerOrderService = minerOrderService;
    }

    @Autowired
    public void setOtcLimitService(OtcLimitService otcLimitService) {
        this.otcLimitService = otcLimitService;
    }

    @Autowired
    public void setOtcCoinService(OtcCoinService otcCoinService) {
        this.otcCoinService = otcCoinService;
    }

    @Autowired
    public void setMemberAccountService(MemberAccountService memberAccountService) {
        this.memberAccountService = memberAccountService;
    }

    @Autowired
    public void setOtcOrderService(OtcOrderService otcOrderService) {
        this.otcOrderService = otcOrderService;
    }


    @Autowired
    public void setBtAppealService(BtAppealService btAppealService) {
        this.btAppealService = btAppealService;
    }

    @Autowired
    public void setMinerOtcOrderService(MinerOtcOrderService minerOtcOrderService) {
        this.minerOtcOrderService = minerOtcOrderService;
    }

    @Autowired
    public void setOtcConfigService(OtcConfigService otcConfigService) {
        this.otcConfigService = otcConfigService;
    }

    @Autowired
    public void setMemberPaymentAccountService(MemberPaymentAccountService memberPaymentAccountService) {
        this.memberPaymentAccountService = memberPaymentAccountService;
    }

    @Autowired
    public void setRedisConnectionFactory(RedisConnectionFactory redisConnectionFactory) {
        this.redisTemplate = new StringRedisTemplate(redisConnectionFactory);
    }
    @Autowired
    public void setOtcMinerMapper(OtcMinerMapper otcMinerMapper) {
        this.otcMinerMapper = otcMinerMapper;
    }

    @Autowired
    public void setiOtcExchange(IOtcExchange iOtcExchange) {
        this.iOtcExchange = iOtcExchange;
    }
    @Autowired
    public void setAdvertisesService(AdvertisesService advertisesService){
        this.advertisesService = advertisesService;
    }

    /**
     * 修改kfka问题后造成订单状态异常数据
     */
    @Override
    public void updateOtcOrderStaus() {
        List<FixOrderDto> fixOrderDtos = otcOrderService.queryFixOrders();
        if(fixOrderDtos != null && fixOrderDtos.size() > 0){
            for(FixOrderDto fixOrderDto:fixOrderDtos){
                if(fixOrderDto.getPassStatus() == 0){
                    BusinessMinerOrder businessMinerOrder = minerOrderService.getById(fixOrderDto.getId());
                    if(!Objects.isNull(businessMinerOrder)) {
                        updateOrderStatus(businessMinerOrder.getRefId(), OtcMinerOrderStatus.Unpaid, OtcMinerOrderStatus.New);
                    }
                }
                otcOrderService.updateFixOtcOrderStaus(fixOrderDto);
            }
        }
    }

    @Override
    public boolean chechInnerMember(Long memberId) {
        return otcMinerMapper.chechInnerMember(memberId)==1?true:false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelBinessOrder(BusinessMinerOrder order,String remark,Integer minerOrderType) {
        WalletChangeRecord record = walletService.realseFreeze(TransactionType.OTC_WITHDRAW_FROZEN_CANCEL,order.getSellId(),"BT","BT",
                order.getAmount().abs(),order.getId(),"OTC提现系统取消");
        if (record == null) {
            log.error("系统取消otc取现订单失败 orderId = {}, member_id = {}, amount = {}", order.getId(), order.getSellId(), order.getAmount());
            throw new BtBankException(BtBankMsgCode.CANCEL_OTC_ORDER_ERROR);
        }
        try {
            UpdateWrapper<BusinessMinerOrder> update = new UpdateWrapper<>();
            update.lambda()
                    .eq(BusinessMinerOrder::getId, order.getId())
                    .eq(BusinessMinerOrder::getStatus, minerOrderType == null ? OtcMinerOrderStatus.New.getCode():minerOrderType)
                    .set(BusinessMinerOrder::getStatus, OtcMinerOrderStatus.Cancel.getCode())
                    .set(BusinessMinerOrder::getRemark, remark)
                    .set(BusinessMinerOrder::getQueueStatus,0)
                    .set(BusinessMinerOrder::getRefId,null)
                    .set(BusinessMinerOrder::getBuyId,null)
                    .set(BusinessMinerOrder::getUpdateTime, new Date());
            if(minerOrderService.update(update)){
                boolean b = walletService.confirmTrade(record.getMemberId(), record.getId());
                if (!b) {
                    log.error("系统取消otc取现订单失败 record = {}", record);
                    throw new BtBankException(CommonMsgCode.FAILURE);
                } else {

                    return true;
                }
            }
            throw new BtBankException(CommonMsgCode.FAILURE);
        } catch (RuntimeException ex) {
            log.error("系统取消otc取现订单失败 txId = {}, err = {}", record.getId(), ex.getMessage());
            boolean b = walletService.rollbackTrade(record.getMemberId(), record.getId());
            log.info("系统取消otc取现订单失败 result = {}, record = {}", b, record);
            throw ex;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageRespResult markOtcOrder(Member member, Long otcSn) {
        MessageRespResult<OtcOrder> result = new MessageRespResult<>();
        try {
            QueryWrapper<OtcOrder> otcOrderQueryWrapper = new QueryWrapper<>();
            otcOrderQueryWrapper.lambda()
                    .eq(OtcOrder::getOrderSn, otcSn)
                    .eq(OtcOrder::getStatus, 1);
            OtcOrder otcOrder = otcOrderService.getOne(otcOrderQueryWrapper);
            if (null == otcOrder) {
                result.setMessage("标记失败");
                result.setData(otcOrder);
                throw new BtBankException(BtBankMsgCode.ORDER_NOT_EXIST);
            }
            //查询对应得矿池订单
            QueryWrapper<BusinessMinerOrder> query = new QueryWrapper<>();
            query.eq("ref_id", otcSn);
            BusinessMinerOrder businessMinerOrder = minerOrderService.getBaseMapper().selectOne(query);
            if (null == businessMinerOrder){
                result.setMessage("标记失败");
                throw new BtBankException(BtBankMsgCode.ORDER_NOT_EXIST);
            }
            otcOrder.setMarked(1);
            otcOrderService.updateById(otcOrder);
            //是否是已经标记过此订单
            Integer markCount = businessMinerOrder.getMarkCount();
            String markMember = businessMinerOrder.getMarkMember();
            if (markCount != 0 || markMember != null) {
                //被标记过
                //1.是否是同一个商家标记过
                boolean contains = markMember.contains(member.getId() + "");
                if (contains) {
                    //是同一个商家不能再标记此订单
                    result.setData(otcOrder);
                    return result;
                }
                //不是同一个商家
                markCount = markCount + 1;
                businessMinerOrder.setMarkCount(markCount);
                businessMinerOrder.setMarkMember(markMember + member.getId() + ",");
                minerOrderService.updateById(businessMinerOrder);
            } else {
                //没有被标记过
                businessMinerOrder.setMarkCount(markCount + 1);
                businessMinerOrder.setMarkMember(member.getId() + ",");
                minerOrderService.updateById(businessMinerOrder);
            }
        }catch (RuntimeException e){
            log.error("商家标记OTC订单银行账户与实名不符失败 orderSn = {}, err = {}", otcSn, e.getMessage());
            e.printStackTrace();
            return result;
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOtcOrder(Member member, OtcOrder otcOrder) {
        //校验订单
        int ret = checkOtcOrder(otcOrder,member);
        WalletChangeRecord record = null;
        try {
            //修改订单
            UpdateWrapper<OtcOrder> update = new UpdateWrapper<>();
            update.lambda()
                    .eq(OtcOrder::getStatus, OtcMinerOrderStatus.New.getCode())
                    .or()
                    .eq(OtcOrder::getStatus, OtcMinerOrderStatus.Paid.getCode())
                    .or()
                    .eq(OtcOrder::getStatus, OtcMinerOrderStatus.Appeal.getCode())
                    .eq(OtcOrder::getOrderSn, otcOrder.getOrderSn())
                    .set(OtcOrder::getCancelTime, new Date())
                    .set(OtcOrder::getStatus, 0)
                    .set(OtcOrder::getIsManualCancel, 1)
                    .set(OtcOrder::getCancelMemberId, member.getId());
            //这个订单没有更新，直接抛出异常
            if(otcOrderService.update(update)){

                //代表该会员是广告发布者，购买类型的广告，并且是付款者
                if (ret == 1){

                    if (!new Long(0).equals(otcOrder.getAdvertiseId())){

                        QueryWrapper<Advertise> queryWrapper = new QueryWrapper<>();
                        queryWrapper.lambda()
                                .eq(Advertise::getId,otcOrder.getAdvertiseId())
                                .eq(Advertise::getMemberId,member.getId());
                        Advertise advertise = advertisesService.getOne(queryWrapper);
                        //上架广告
                        boolean onShelves = advertise.getStatus() == 0;

                        if (onShelves){
                            //更改广告
                            UpdateWrapper<Advertise> advertiseUpdateWrapper = new UpdateWrapper<>();
                            advertiseUpdateWrapper.lambda()
                                    .ge(Advertise::getDealAmount,otcOrder.getNumber())
                                    .eq(Advertise::getId,otcOrder.getAdvertiseId())
                                    .eq(Advertise::getStatus,0)
                                    .set(Advertise::getDealAmount,advertise.getDealAmount().subtract(otcOrder.getNumber()))
                                    .set(Advertise::getRemainAmount,advertise.getRemainAmount().add(otcOrder.getNumber()));
                            if (!advertisesService.update(advertiseUpdateWrapper)){
                                log.error("广告更新失败 advertise_id = {},member_id = {}",advertise.getId(),advertise.getMemberId());
                                throw new BtBankException(CommonMsgCode.FAILURE);
                            }
                        }else{
                            UpdateWrapper<Advertise> advertiseUpdateWrapper = new UpdateWrapper<>();
                            advertiseUpdateWrapper.lambda()
                                    .ge(Advertise::getDealAmount,otcOrder.getNumber())
                                    .eq(Advertise::getId,otcOrder.getAdvertiseId())
                                    .set(Advertise::getDealAmount,advertise.getDealAmount().subtract(otcOrder.getNumber()));
                            if (!advertisesService.update(advertiseUpdateWrapper)){
                                log.error("广告更新失败 advertise_id = {},member_id = {}",advertise.getId(),advertise.getMemberId());
                                throw new BtBankException(CommonMsgCode.FAILURE);
                            }
                        }
                        if (advertise != null && onShelves){
                            OtcCoin otcCoin = otcCoinService.getById(otcOrder.getCoinId());
                            record = walletService.realseFreeze(TransactionType.OTC_WITHDRAW_FROZEN_CANCEL, otcOrder.getMemberId(), otcCoin.getUnit(), otcCoin.getUnit(),
                                    otcOrder.getNumber(), otcOrder.getId(), "提现广告取消");
                            if (record == null){
                                log.error("提现广告取消失败 record = {}",record);
                                throw new BtBankException(CommonMsgCode.FAILURE);
                            }
                        }
                    }
                }else{//代表该会员不是广告发布者，并且是付款者

                    QueryWrapper<Advertise> queryWrapper = new QueryWrapper<>();
                    queryWrapper.lambda()
                            .eq(Advertise::getId,otcOrder.getAdvertiseId())
                            .eq(Advertise::getMemberId,member.getId());
                    Advertise advertise = advertisesService.getOne(queryWrapper);
                    //上架广告
                    boolean onShelves = advertise.getStatus() == 0;

                    if (onShelves){
                        UpdateWrapper<Advertise> advertiseUpdateWrapper = new UpdateWrapper<>();
                        advertiseUpdateWrapper.lambda()
                                .ge(Advertise::getDealAmount,otcOrder.getNumber())
                                .eq(Advertise::getId,otcOrder.getAdvertiseId())
                                .eq(Advertise::getStatus,0)
                                .set(Advertise::getDealAmount,advertise.getDealAmount().subtract(otcOrder.getNumber().add(otcOrder.getCommission())))
                                .set(Advertise::getRemainAmount,advertise.getRemainAmount().add(otcOrder.getNumber().add(otcOrder.getCommission())));
                        if (!advertisesService.update(advertiseUpdateWrapper)){
                            log.error("广告更新失败 advertise_id = {},member_id = {}",advertise.getId(),advertise.getMemberId());
                            throw new BtBankException(CommonMsgCode.FAILURE);
                        }
                    }else{
                        UpdateWrapper<Advertise> advertiseUpdateWrapper = new UpdateWrapper<>();
                        advertiseUpdateWrapper.lambda()
                                .ge(Advertise::getDealAmount,otcOrder.getNumber())
                                .eq(Advertise::getId,otcOrder.getAdvertiseId())
                                .set(Advertise::getDealAmount,advertise.getDealAmount().subtract(otcOrder.getNumber().add(otcOrder.getCommission())));
                        if (!advertisesService.update(advertiseUpdateWrapper)){
                            log.error("广告更新失败 advertise_id = {},member_id = {}",advertise.getId(),advertise.getMemberId());
                            throw new BtBankException(CommonMsgCode.FAILURE);
                        }
                    }
                    if (advertise != null && onShelves){
                        OtcCoin otcCoin = otcCoinService.getById(otcOrder.getCoinId());
                        record = walletService.realseFreeze(TransactionType.OTC_WITHDRAW_FROZEN_CANCEL, otcOrder.getMemberId(), otcCoin.getUnit(), otcCoin.getUnit(),
                                otcOrder.getNumber(), otcOrder.getId(), "广告");
                        if (record == null){
                            log.error("提现广告取消失败 record = {}",record);
                            throw new BtBankException(CommonMsgCode.FAILURE);
                        }
                    }
                }
            }
            throw new BtBankException(CommonMsgCode.FAILURE);
        }catch (RuntimeException e){
            log.error("otc订单取消失败 orderSn = {}, err = {}",otcOrder.getOrderSn(),e.getMessage());
            if (record != null) {
                walletService.rollbackTrade(record.getMemberId(), record.getId());
            }
        }
    }

    @Override
    public void withdrawLimitValidate(BigDecimal amount) {
        //获取当前时间
        LocalTime localTime=LocalTime.now();

        // 获取时间限制
        String start = otcConfigService.getValue(OtcConfigType.OTC_WITHDRAW_LIMIT_MIN,
                String::new,
                "00:00");
        String end = otcConfigService.getValue(OtcConfigType.OTC_WITHDRAW_LIMIT_MAX,
                String::new,
                "00:00");
        LocalTime min = LocalTime.parse(start);
        LocalTime max = LocalTime.parse(end);
        //凌晨0-8点
        String limitStr;
        BtBankMsgCode code=BtBankMsgCode.OVER_FLOW_LIMIT_AMOUNT2;
        if (localTime.isAfter(min)&&localTime.isBefore(max)){
            limitStr = otcConfigService.getValue("OTC_WITHDRAW_SINGLE_LIMIT_0_8");
            code=BtBankMsgCode.OVER_FLOW_LIMIT_AMOUNT1;
        }else {
            limitStr = otcConfigService.getValue("OTC_WITHDRAW_SINGLE_LIMIT_OTHER");
        }
        BigDecimal limitAmount=new BigDecimal(limitStr);
        boolean compare = BigDecimalUtils.compare(limitAmount, amount);
        if (!compare){
            throw new BtBankException(code);
        }
    }

    @Override
    public void updateQueueStatus(Integer size) {
        minerOrderService.updateQueueStatus(size);
    }

    /**
     * 校验提现订单
     * @param otcOrder
     */
    private int checkOtcOrder(OtcOrder otcOrder,Member member){
        int ret = 0;
        if (otcOrder.getStatus() == OtcMinerOrderStatus.Paid.getCode()){
            throw new BtBankException(BtBankMsgCode.CAN_NOT_CANCEL_OTC_ORDER);
        }

        if (otcOrder.getAdvertiseType().equals(0) && otcOrder.getMemberId().equals(member.getId())){
            //代表该会员是广告发布者，购买类型的广告，并且是付款者
            ret = 1;
        } else if (otcOrder.getAdvertiseType().equals(1) && otcOrder.getCustomerId().equals(member.getId())){
            //代表该会员不是广告发布者，并且是付款者
            ret = 2;
        }
        if (ret == 0){
            throw new BtBankException(BtBankMsgCode.OTC_ORDER_ILLEGAL);
        }
        return ret;
    }

    public OtcMinerServiceImpl getService() {
        return SpringContextUtil.getBean(OtcMinerServiceImpl.class);
    }
}
