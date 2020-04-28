package com.spark.bitrade.api.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * OtcLimitDTO
 *
 * @author biu
 * @since 2019/11/28 16:23
 */
@Data
public class OtcLimitDTO {

    private Long id;
    private String orderSn;
    private Long memberId;
    private Integer coinId;
    private BigDecimal number;
    private Date limitTime;
    private Integer state;

    /**
     * 是否有效
     * <p>
     * <li> 10点之前 统计昨天0点之后的订单
     * <li> 10点到16点之间  统计昨天12点之后的订单
     * <li> 16点之后 统计今天的订单
     *
     * @return bool
     */
    public boolean isValid(Date date) {
        return isValid(Range.of(date));
    }

    /**
     * 是否有效
     * <p>
     * <li> 10点之前 统计昨天0点之后的订单
     * <li> 10点到16点之间 统计昨天12点之后的订单
     * <li> 16点之后 统计今天的订单
     *
     * @return bool
     */
    public boolean isValid(Range range) {

        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        instance.set(Calendar.MILLISECOND, 0);


        switch (range) {
            case TEN_BEFORE:
                instance.add(Calendar.DATE, -1);
                break;
            case TEN_TO_SIXTEEN:
                instance.add(Calendar.DATE, -1);
                instance.set(Calendar.HOUR_OF_DAY, 12);
                break;
            case SIXTEEN_AFTER:
            default:
        }

        return limitTime.getTime() > instance.getTimeInMillis();
    }

    public enum Range {
        TEN_BEFORE, TEN_TO_SIXTEEN, SIXTEEN_AFTER;

        public static Range of(Date date) {
            Calendar instance = Calendar.getInstance();
            instance.setTime(date);

            long timeInMillis = instance.getTimeInMillis();

            instance.set(Calendar.HOUR_OF_DAY, 10);
            instance.set(Calendar.MINUTE, 0);
            instance.set(Calendar.SECOND, 0);
            instance.set(Calendar.MILLISECOND, 0);

            long ten = instance.getTimeInMillis();

            instance.set(Calendar.HOUR_OF_DAY, 16);
            long sixteen = instance.getTimeInMillis();

            if (timeInMillis < ten) {
                return TEN_BEFORE;
            }

            if (timeInMillis > sixteen) {
                return SIXTEEN_AFTER;
            }

            return TEN_TO_SIXTEEN;
        }
    }
}
