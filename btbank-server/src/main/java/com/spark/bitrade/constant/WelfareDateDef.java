package com.spark.bitrade.constant;

import com.spark.bitrade.trans.Tuple2;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class WelfareDateDef {

    public static final Date LIMIT_DATE = convert("2020-04-01 00:00:00");

    public static final Date EXPIRED_DATE = convert("2020-04-22 00:00:00");

    private WelfareDateDef() {
    }

    public static Tuple2<Date, Date> getTimeRange(Date date) {
        Calendar instance = getCalendarInstance(date);
        Date begin = instance.getTime();
        instance.add(Calendar.DATE, 1);
        Date end = instance.getTime();

        return new Tuple2<>(begin, end);
    }

    public static Date getOpenningTime() {
        Calendar instance = getCalendarInstance();
        instance.set(Calendar.HOUR_OF_DAY, 10);
        return instance.getTime();
    }

    public static Date getOpenningTime(Date date) {
        Calendar instance = getCalendarInstance(date);
        instance.set(Calendar.HOUR_OF_DAY, 10);
        return instance.getTime();
    }

    public static Date getClosingTime() {
        Calendar instance = getCalendarInstance();
        instance.set(Calendar.HOUR_OF_DAY, 22);
        return instance.getTime();
    }

    public static Date getClosingTime(Date date) {
        Calendar instance = getCalendarInstance(date);
        instance.set(Calendar.HOUR_OF_DAY, 22);
        return instance.getTime();
    }

    public static Date getExpiredDate(Date date, Integer days) {
        if (days == null) {
            days = 20;
        }
        Calendar of = of(date);
        of.add(Calendar.DATE, days - 1);

        return of.getTime();
    }

    public static Date getReleaseTime(Date closing) {
        Calendar instance = getCalendarInstance();
        instance.setTime(closing);
        instance.add(Calendar.HOUR,/*13天12小时*/ 13 * 24 + 12);
        return instance.getTime();
    }

    public static Calendar of(Date date) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        return instance;
    }

    private static Calendar getCalendarInstance() {
        Calendar instance = Calendar.getInstance();
        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        instance.set(Calendar.MILLISECOND, 0);
        return instance;
    }

    private static Calendar getCalendarInstance(Date date) {
        Calendar instance = of(date);
        instance.set(Calendar.HOUR_OF_DAY, 0);
        instance.set(Calendar.MINUTE, 0);
        instance.set(Calendar.SECOND, 0);
        instance.set(Calendar.MILLISECOND, 0);
        return instance;
    }

    private static Date convert(String datetime) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(datetime);
        } catch (ParseException e) {
            return Calendar.getInstance().getTime();
        }
    }

}
