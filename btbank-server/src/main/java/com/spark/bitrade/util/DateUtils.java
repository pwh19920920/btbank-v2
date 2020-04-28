package com.spark.bitrade.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * DateUtils
 *
 * @author biu
 * @since 2019/11/28 17:09
 */
public final class DateUtils {

    private static final String[] PARSEPATTERNS = new String[] {
            "yyyy-MM-dd HH:mm:ss", "yyyy/MM/dd HH:mm:ss",
            "yyyy.MM.dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy/MM/dd HH:mm",
            "yyyy.MM.dd HH:mm", "yyyy-MM-dd HH", "yyyy/MM/dd HH",
            "yyyy.MM.dd HH", "yyyy-MM-dd", "yyyy/MM/dd", "yyyy.MM.dd" };

    private DateUtils() {
    }

    public static Calendar getCalendarOfYesterday() {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DATE, -1);

        return calendar;
    }

    public static Date getDateBeforeMinutes(int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -Math.abs(minutes));
        return calendar.getTime();
    }

    public static Date parseDatetime(String string) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(string);
        } catch (ParseException e) {
            return null;
        }
    }
    /**
     * 获取本月第一天
     * @return String
     * **/
    public static Date getMonthStart(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.add(Calendar.MONTH,0);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }
    /**
     * 获取本月最后一天
     * @return String
     * **/
    public static Date getMonthEnd(){
        Calendar cal=Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH,   cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    /**
     * 获取本周的最后一天
     * @return String
     * **/
    public static  Date getWeekEnd(){
        Calendar cal=Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getActualMaximum(Calendar.DAY_OF_WEEK));
        cal.add(Calendar.DAY_OF_WEEK, 1);
        cal.set(Calendar.HOUR, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }
    /**
     * 获取本周的第一天
     * @return String
     * **/
    public static Date getWeekSstart() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        // 获得当前日期是一个星期的第几天
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // 获得当前日期是一个星期的第几天
        int day = cal.get(Calendar.DAY_OF_WEEK);
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }
    public static Date subDate(Date date,int daysbefore) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE,0-daysbefore);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }


    /**
     * 计算两个时间相差的分钟数
     *
     * @param smdate 开始日期
     * @param bdate  结束日期
     * @return
     * @throws ParseException
     */
    public static int minuteBetween(Date smdate, Date bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        smdate = sdf.parse(sdf.format(smdate));
        bdate = sdf.parse(sdf.format(bdate));
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 60);
        return Integer.parseInt(String.valueOf(between_days));

    }

    /**
     * 将字符串转换成日期类型,自动匹配格式
     *
     * @param date
     * @return
     */
    public static Date stringToDate(String date) {
        try {
            return org.apache.commons.lang3.time.DateUtils.parseDate(date, PARSEPATTERNS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
