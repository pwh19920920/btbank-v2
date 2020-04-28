package com.spark.bitrade.api.open;

import com.aliyuncs.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.api.vo.EnterpriseOrderVo;
import com.spark.bitrade.api.vo.EpmMinerVO;
import com.spark.bitrade.api.vo.EpmOrderVO;
import com.spark.bitrade.api.vo.QueryVo;
import com.spark.bitrade.biz.EnterpriseMiningService;
import com.spark.bitrade.config.bean.OpenEpmOptions;
import com.spark.bitrade.constant.BtBankMsgCode;
import com.spark.bitrade.constant.EnterpriseTransactionType;
import com.spark.bitrade.constants.CommonMsgCode;
import com.spark.bitrade.exception.BtBankException;
import com.spark.bitrade.repository.entity.EnterpriseMiner;
import com.spark.bitrade.repository.entity.EnterpriseMinerTransaction;
import com.spark.bitrade.repository.service.EnterpriseMinerService;
import com.spark.bitrade.repository.service.EnterpriseMinerTransactionService;
import com.spark.bitrade.util.MessageRespResult;
import com.spark.bitrade.util.StatusUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * OpenEnterpriseController
 *
 * @author biu
 * @since 2019/12/25 14:39
 */
@Api(tags = "企业矿工开放接口控制器")
@RequestMapping(path = "api/v2/no-auth/epm")
@RestController
public class OpenEnterpriseController {

    private OpenEpmOptions openEpmOptions;
    private EnterpriseMiningService miningService;
    private EnterpriseMinerService minerService;
    private EnterpriseMinerTransactionService transactionService;
    private StringRedisTemplate redisTemplate;

    /* @ApiOperation(value = "获取矿工列表", notes = "返回所有有效的矿工")
    @GetMapping(path = "/miners")
    public MessageRespResult miners(@RequestParam("current") Integer current, @RequestParam("size") Integer size) {
        QueryWrapper<EnterpriseMiner> query = new QueryWrapper<>();
        query.eq("deleted", 0);
        IPage<EnterpriseMiner> page = minerService.page(new Page<>(current, size), query);

        return MessageRespResult.success4Data(page.convert(EpmMinerVO::of));
    }*/

    @ApiOperation(value = "获取矿工", notes = "返回矿工信息")
    @GetMapping(path = "/miners", params = "memberId")
    public MessageRespResult miners(@RequestParam("memberId") Long memberId) {

        EnterpriseMiner miner = minerService.findByMemberId(memberId);
        if (miner != null) {
            return MessageRespResult.success4Data(EpmMinerVO.of(miner));
        }
        return BtBankMsgCode.ENTERPRISE_MINER_NOT_FOUND.resp();
    }

    @ApiOperation(value = "获取矿工", notes = "返回矿工信息")
    @GetMapping(path = "/miners", params = "minerId")
    public MessageRespResult miners(@RequestParam("minerId") Integer minerId) {

        EnterpriseMiner miner = minerService.getById(minerId);
        if (miner != null) {
            return MessageRespResult.success4Data(EpmMinerVO.of(miner));
        }
        return BtBankMsgCode.ENTERPRISE_MINER_NOT_FOUND.resp();
    }

    @ApiOperation(value = "获取订单列表", notes = "返回所有挖矿订单")
    @GetMapping(path = "/orders")
    public MessageRespResult order(@RequestParam("current") Integer current,
                                   @RequestParam("size") Integer size,
                                   @RequestParam(value = "minerId", required = false) Integer minerId,
                                   @RequestParam(value = "memberId", required = false) Long memberId,
                                   @RequestParam(value = "range", required = false) String range) {

        QueryWrapper<EnterpriseMinerTransaction> query = new QueryWrapper<>();
        query.eq("type", EnterpriseTransactionType.MiningOrder.code());

        if (minerId != null) {
            query.eq("miner_id", minerId);
        }

        if (memberId != null) {
            query.eq("member_id", memberId);
        }

        QueryVo vo = QueryVo.builder().current(current).size(size).build();
        vo.parseRange("yyyy-MM-dd", ",", range);

        vo.fillRange(query, "create_time");
        query.orderByDesc("create_time");

        IPage<EnterpriseMinerTransaction> page = transactionService.page(vo.toPage(), query);

        return MessageRespResult.success4Data(page.convert(EpmOrderVO::of));
    }

    @ApiOperation(value = "获取订单", notes = "返回订单详情")
    @GetMapping(path = "/orders", params = "orderSn")
    public MessageRespResult order(@RequestParam("orderSn") String orderSn) {

        QueryWrapper<EnterpriseMinerTransaction> query = new QueryWrapper<>();
        query.eq("order_sn", orderSn).eq("type", EnterpriseTransactionType.MiningOrder.code());

        EnterpriseMinerTransaction one = transactionService.getOne(query);
        if (one != null)
            return MessageRespResult.success4Data(EpmOrderVO.of(one));
        else
            return BtBankMsgCode.ENTERPRISE_MINER_ORDER_NOT_FOUND.resp();
    }

    @ApiOperation(value = "获取订单", notes = "返回订单详情")
    @GetMapping(path = "/orders", params = "txId")
    public MessageRespResult order(@RequestParam("txId") Long txId) {
        EnterpriseMinerTransaction one = transactionService.getById(txId);
        if (one != null)
            return MessageRespResult.success4Data(EpmOrderVO.of(one));
        else
            return BtBankMsgCode.ENTERPRISE_MINER_ORDER_NOT_FOUND.resp();
    }

    @ApiOperation(value = "挖矿订单下单", notes = "指定矿工接单挖矿")
    @PostMapping(path = "/orders", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public MessageRespResult order(@RequestBody EnterpriseOrderVo vo) {

        if (!vo.validate()) {
            return CommonMsgCode.INVALID_PARAMETER.resp();
        }
        if (vo.getMinerId() == null || vo.getMinerId() == 0) {
            return CommonMsgCode.INVALID_PARAMETER.resp();
        }

        EnterpriseMiner miner = minerService.getById(vo.getMinerId());
        if (miner == null || StringUtils.isEmpty(miner.getEnterpriseKey())) {
            return CommonMsgCode.INVALID_PARAMETER.resp();
        }
        // 开启验证
        if (openEpmOptions.isValid()) {
            if (!vo.checkSign(miner.getEnterpriseKey())) {
                throw new BtBankException(CommonMsgCode.INVALID_REQUEST_METHOD);
            }

            if (vo.isExpired(openEpmOptions.getTimeoutSeconds())) {
                throw new BtBankException(CommonMsgCode.INVALID_REQUEST_METHOD);
            }
        }

        String key = "locked:enterprise:" + vo.getOrderSn();

        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        Long increment = operations.increment(key, 1);

        // 防重放
        if (StatusUtils.equals(1L, increment)) {
            redisTemplate.expire(key, 5, TimeUnit.SECONDS);
            try {
                EnterpriseMinerTransaction tx = miningService.mining(vo);
                return MessageRespResult.success4Data(tx.getId());
            } finally {
                // 若主从不同步，则可删除下面代码
                redisTemplate.delete(key);
            }
        } else {
            redisTemplate.expire(key, 5, TimeUnit.SECONDS);
            return CommonMsgCode.INVALID_REQUEST_METHOD.resp();
        }
    }

    // ------------------------------------
    // > S E T T E R S
    // ------------------------------------

    @Autowired
    public void setOpenEpmOptions(OpenEpmOptions openEpmOptions) {
        this.openEpmOptions = openEpmOptions;
    }

    @Autowired
    public void setMiningService(EnterpriseMiningService miningService) {
        this.miningService = miningService;
    }

    @Autowired
    public void setMinerService(EnterpriseMinerService minerService) {
        this.minerService = minerService;
    }

    @Autowired
    public void setTransactionService(EnterpriseMinerTransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Autowired
    public void setRedisConnectionFactory(RedisConnectionFactory factory) {
        this.redisTemplate = new StringRedisTemplate(factory);
    }
}
