package com.spark.bitrade.repository.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.api.dto.IntKV;
import com.spark.bitrade.constant.BtBankSystemConfig;
import com.spark.bitrade.constant.WelfareDateDef;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.repository.entity.WelfareNewQualification;
import com.spark.bitrade.repository.mapper.WelfareMapper;
import com.spark.bitrade.repository.mapper.WelfareNewQualificationMapper;
import com.spark.bitrade.repository.service.WelfareNewQualificationService;
import com.spark.bitrade.service.BtBankConfigService;
import com.spark.bitrade.service.MemberAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 新人福利参与资格(WelfareNewQualification)表服务实现类
 *
 * @author biu
 * @since 2020-04-08 14:17:15
 */
@Slf4j
@Service("welfareNewQualificationService")
public class WelfareNewQualificationServiceImpl extends ServiceImpl<WelfareNewQualificationMapper, WelfareNewQualification>
        implements WelfareNewQualificationService {

    private MemberAccountService memberAccountService;
    private BtBankConfigService configService;
    private StringRedisTemplate redisTemplate;
    private WelfareMapper welfareMapper;

    @Autowired
    public void setMemberAccountService(MemberAccountService memberAccountService) {
        this.memberAccountService = memberAccountService;
    }

    @Autowired
    public void setConfigService(BtBankConfigService configService) {
        this.configService = configService;
    }

    @Autowired
    public void setRedisConnectionFactory(RedisConnectionFactory connectionFactory) {
        this.redisTemplate = new StringRedisTemplate(connectionFactory);
    }

    @Autowired
    public void setWelfareMapper(WelfareMapper welfareMapper) {
        this.welfareMapper = welfareMapper;
    }

    /*
        0、若自己未购买则根据规则插入一条自己购买机会数据，可能为已失效 21天失效
        1、直接统计所有未使用的资格数量，排查已失效
        2、该类型机会数量更新由下级购买时处理
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Integer chances(Member member) {
        // 仅限4月1日之后注册的员工 的 有效矿工
        if (member.getRegistrationTime().compareTo(WelfareDateDef.LIMIT_DATE) < 0
                || welfareMapper.isAvailableMiner(member.getId()) < 1) {
            return 0;
        }

        Date now = Calendar.getInstance().getTime();
        Integer config = configService.getConfig(BtBankSystemConfig.WELFARE_NEW_EXPIRED_DAYS, r -> Integer.parseInt(r.toString()), 21);
        Date expiredDate = WelfareDateDef.getExpiredDate(member.getRegistrationTime(), config);
        boolean isExpired = expiredDate.compareTo(now) < 1; // 21 天未使用则失效，包含注册之日
        // 查询统计看是否已插入
        final String key = member.getId() + ":0";
        WelfareNewQualification self = getById(key);
        if (self == null) {
            // 插入自己的那一条
            WelfareNewQualification qualification = new WelfareNewQualification();
            qualification.setId(key);
            qualification.setMemberId(member.getId());
            qualification.setMobilePhone(member.getMobilePhone());
            qualification.setRealName(member.getRealName());
            qualification.setSubId(0L);

            qualification.setStatus(0);

            if (isExpired) {
                qualification.setStatus(2);
            }
            // 设置为创建时间
            qualification.setCreateTime(member.getRegistrationTime());
            save(qualification);

            log.info("新人福利包购买资格初始化 [ id = {}, member_id = {}, status = {} ]", key, member.getId(), qualification.getStatus());
        } else {
            // 检查是否过期未使用
            if (isExpired && self.getStatus() == 0) {
                // 标记过期
                lambdaUpdate().eq(WelfareNewQualification::getId, key)
                        .set(WelfareNewQualification::getStatus, 2)
                        .set(WelfareNewQualification::getUpdateTime, now)
                        .update();
                log.info("新人福利包购买资格失效[ id = {}, member_id = {} ]", key, member.getId());
            }
        }

        // 获取所有未使用的资格数量
        Map<Integer, Integer> kv = baseMapper.countKV(member.getId()).stream().collect(Collectors.toMap(IntKV::getK, IntKV::getV));
        return kv.getOrDefault(0, 0);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean increase(Long memberId, Long subId) {

        final String key = memberId + ":" + subId;

        if (welfareMapper.isAvailableMiner(memberId) < 1) {
            log.info("增加新人福利包次数失败, 不是有效矿工 [ member_id = {}, sub_id = {} ]", memberId, subId);
            return true;
        }

        if (welfareMapper.isAutoAuthRealName(subId) < 1) {
            log.info("增加新人福利包次数失败, 下级不是自动实名矿工 [ member_id = {}, sub_id = {} ]", memberId, subId);
            return true;
        }

        // 只给一次机会
        if (getById(key) != null) {
            log.info("增加新人福利包次数失败, 只能增加一次 [ member_id = {}, sub_id = {} ]", memberId, subId);
            return true;
        }

        Member member = memberAccountService.findMemberByMemberId(memberId);
        if (member.getRegistrationTime().compareTo(WelfareDateDef.LIMIT_DATE) < 0) {
            // 4月1日之前的用户没有资格
            log.info("增加新人福利包次数失败, 4月1日之前用户没有资格 [ member_id = {}, sub_id = {} ]", memberId, subId);
            return true;
        }

        Integer config = configService.getConfig(BtBankSystemConfig.WELFARE_NEW_TOP_LIMIT,
                (r) -> Integer.parseInt(r.toString()), 10);

        Integer count = lambdaQuery().eq(WelfareNewQualification::getMemberId, memberId)
                .ne(WelfareNewQualification::getSubId, 0).count();

        // 每个人除去自己的最多有 config 次机会
        if (count >= config) {
            log.info("增加新人福利包次数失败, 已经超过次数 [ member_id = {}, sub_id = {} ]", memberId, subId);
            redisTemplate.delete("welfare:new:" + memberId);
            return true;
        }

        // 注意：次数有并发问题，可能插入超过 config 的记录条数
        Long increment = redisTemplate.opsForValue().increment("welfare:new:" + memberId, 1);
        if (increment == null) {
            log.error("增加新人福利包次数失败, Redis无法写入 [ member_id = {}, sub_id = {} ]", memberId, subId);
            return false;
        }

        if (increment > config) {
            log.info("增加新人福利包次数失败, 其他下级已经写入 [ member_id = {}, sub_id = {} ]", memberId, subId);
            return true;
        }

        WelfareNewQualification qualification = new WelfareNewQualification();

        qualification.setId(key);
        qualification.setMemberId(member.getId());
        qualification.setMobilePhone(member.getMobilePhone());
        qualification.setRealName(member.getRealName());
        qualification.setSubId(subId);
        qualification.setStatus(0);
        qualification.setCreateTime(Calendar.getInstance().getTime());

        return save(qualification);
    }

    @Override
    public boolean decrease(Long memberId, String refId) {
        return retBool(baseMapper.decrease(memberId, refId));
    }

    @Deprecated // 暂不使用，不可撤回
    @Override
    public boolean refund(Long memberId, String refId) {
        return lambdaUpdate().eq(WelfareNewQualification::getMemberId, memberId)
                .eq(WelfareNewQualification::getRefId, refId)
                .eq(WelfareNewQualification::getStatus, 1)
                .set(WelfareNewQualification::getStatus, 0)
                .set(WelfareNewQualification::getRefId, null).update();
    }

    @Deprecated // 暂不使用，代码未测试
    @Override
    public void calculate() {
        // 封盘之后统计
        Date openningTime = WelfareDateDef.getOpenningTime();
        Date closingTime = WelfareDateDef.getClosingTime();

        // 封盘之后统计
        Date now = Calendar.getInstance().getTime();
        if (now.compareTo(closingTime) < 0) {
            return;
        }

        // 结束时间点延后一小时
        Calendar of = WelfareDateDef.of(closingTime);
        of.add(Calendar.HOUR_OF_DAY, 1);

        // 已存在的ID
        Set<String> existIds = baseMapper.getExistIds();
        // 每个memberId出现的次数
        Map<Long, Long> map = existIds.stream().filter(r -> !r.endsWith(":0")).map(r -> Long.parseLong(r.split(":")[0]))
                .collect(Collectors.groupingBy(p -> p, Collectors.counting()));

        List<WelfareNewQualification> list = new ArrayList<>();
        // 排除已经给过次数的
        Integer config = configService.getConfig(BtBankSystemConfig.WELFARE_NEW_TOP_LIMIT, (r) -> Integer.parseInt(r.toString()), 10);
        for (WelfareNewQualification qualification : baseMapper.getNewInvolvement(openningTime, of.getTime())) { // .stream().filter(r -> !existIds.contains(r.getId()))
            if (existIds.contains(qualification.getId())) {
                continue;
            }
            Long count = map.getOrDefault(qualification.getMemberId(), 0L);

            if (count > config) {
                continue;
            }

            list.add(qualification);
            map.put(qualification.getMemberId(), count + 1);
        }

        if (!list.isEmpty()) {
            boolean f = saveBatch(list);
            log.info("计算新人福利包参与机会 [ size = {}, f = {} ]", list.size(), f);
        } else {
            log.info("计算新人福利包参与机会 [ size = 0, f = false ]");
        }
    }
}