package com.spark.bitrade.api.vo;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.spark.bitrade.trans.Tuple2;
import lombok.Builder;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * QueryVo
 *
 * @author biu
 * @since 2019/12/24 12:21
 */
@Data
@Builder
public class QueryVo {

    private int current = 1;
    private int size = 20;
    private Tuple2<Date, Date> range;

    public void parseRange(String pattern, String separator, String range) {
        if (StringUtils.hasText(range)) {
            String[] strings = range.split(separator);
            if (strings.length == 2) {
                try {
                    SimpleDateFormat df = new SimpleDateFormat(pattern);
                    Date start = df.parse(strings[0].trim());
                    Date end = df.parse(strings[1].trim());
                    this.range = new Tuple2<>(resetHMS(start, true), resetHMS(end, false));
                } catch (ParseException ex) {
                    throw new IllegalArgumentException("日期范围参数无效");
                }
                return;
            }
            throw new IllegalArgumentException("日期范围参数无效");
        }
    }

    private Date resetHMS(Date date, boolean isStartOfDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        if (isStartOfDay) {
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
        }

        return calendar.getTime();
    }

    public <T> IPage<T> toPage() {
        return new Page<>(current, size);
    }

    public <T> void fillRange(QueryWrapper<T> query, String column) {
        if (range != null) {
            query.ge(column, range.getFirst()).le(column, range.getSecond());
        }
    }
}
