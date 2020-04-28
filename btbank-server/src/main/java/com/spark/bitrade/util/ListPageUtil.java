package com.spark.bitrade.util;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 内存分页
 *
 * @author shenzucai
 * @time 2020.02.27 17:11
 */
public class ListPageUtil {

    public static <T> Page<T> getPage(List<T> tList, long current, long size) {
        if (size < 0) {
            size = 10L;
        }
        Page<T> tPage = new Page<T>(current, size, tList.size());
        Long tempStart = (tPage.getCurrent() - 1) * tPage.getSize();
        Long tempEnd = tPage.getCurrent() * tPage.getSize();
        Integer start = tempStart < 0 ? 0 : tempStart.intValue();
        Integer end = tempEnd > tList.size() ? tList.size() : tempEnd.intValue();
        tPage.setRecords(tList.subList(start, end));
        return tPage;
    }
}
