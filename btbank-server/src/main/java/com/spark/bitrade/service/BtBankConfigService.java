package com.spark.bitrade.service;

import com.spark.bitrade.constant.BtBankSystemConfig;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.repository.entity.BtBankDataDict;
import com.spark.bitrade.repository.entity.BtBankMinerBalanceTransaction;
import com.spark.bitrade.repository.service.BtBankDataDictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

@Slf4j
@Service
public class BtBankConfigService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private BtBankDataDictService dictService;

    @SuppressWarnings("unchecked")
    public Object getConfig(String key) {
        String redisKey = BtBankSystemConfig.REDIS_DICT_PREFIX + key;
        Object o = redisTemplate.opsForValue().get(redisKey);
        if (o == null) {
            BtBankDataDict one = dictService.findFirstByDictIdAndDictKey(BtBankSystemConfig.BT_BANK_MINER_CONFIG,
                    key);
            if (one != null) {
                o = one.getDictVal();
                redisTemplate.opsForValue().set(redisKey, o);
            }
        }

        if (o == null) {
            log.warn(" BtBankSystemConfig [{}] value not exists", key);
        }
        return o;
    }

    /**
     * 判断是否是新用户
     * @author daring5920
     * @time 2019.12.08 10:35
     * @param member
     * @return true
     */
    public Boolean isNewMemberConfig(Member member) {
        String redisKey = "BT_BANNK_RED_PACKET_CONFIG" + "NEW_MEMBER_REGISTRATION";
        Object o = redisTemplate.opsForValue().get(redisKey);
        if (o == null) {
            BtBankDataDict one = dictService.findFirstByDictIdAndDictKey("BT_BANNK_RED_PACKET_CONFIG",
                    "NEW_MEMBER_REGISTRATION");
            if (one != null) {
                o = one.getDictVal();
                redisTemplate.opsForValue().set(redisKey, o);
            }
        }

        if (o == null) {
            log.warn(" BtBankSystemConfig [{}] value not exists", "NEW_MEMBER_REGISTRATION");
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime newTime = LocalDateTime.parse(String.valueOf(o), dtf);
        LocalDateTime registTime = member.getRegistrationTime().toInstant()
                .atZone( ZoneId.systemDefault() )
                .toLocalDateTime();
        return newTime.isBefore(registTime);
    }

    /**
     * 判断是否是第一次挖矿
     * @author daring5920
     * @time 2019.12.08 10:35
     * @param btBankMinerBalanceTransaction
     * @return true
     */
    public Boolean isFirestMineConfig(BtBankMinerBalanceTransaction btBankMinerBalanceTransaction) {
        String redisKey = "BT_BANNK_RED_PACKET_CONFIG" + "NEW_MEMBER_REGISTRATION";
        Object o = redisTemplate.opsForValue().get(redisKey);
        if (o == null) {
            BtBankDataDict one = dictService.findFirstByDictIdAndDictKey("BT_BANNK_RED_PACKET_CONFIG",
                    "NEW_MEMBER_REGISTRATION");
            if (one != null) {
                o = one.getDictVal();
                redisTemplate.opsForValue().set(redisKey, o);
            }
        }

        if (o == null) {
            log.warn(" BtBankSystemConfig [{}] value not exists", "NEW_MEMBER_REGISTRATION");
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime newTime = LocalDateTime.parse(String.valueOf(o), dtf);
        LocalDateTime createTime = btBankMinerBalanceTransaction.getCreateTime().toInstant()
                .atZone( ZoneId.systemDefault() )
                .toLocalDateTime();
        return newTime.isBefore(createTime);
    }

    public <T> T getConfig(String key, Function<Object, T> convert, T defaultValue) {
        Object value = getConfig(key);
        if (value == null) {
            return defaultValue;
        }
        return convert.apply(value);
    }
}
