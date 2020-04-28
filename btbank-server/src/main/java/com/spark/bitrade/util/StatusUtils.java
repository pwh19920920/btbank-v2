package com.spark.bitrade.util;

/**
 * StatusUtils
 *
 * @author biu
 * @since 2019/12/23 17:42
 */
public final class StatusUtils {

    private StatusUtils() {
    }

    public static <T extends Number> boolean equals(T source, T target) {
        if (source == null) {
            return null == target;
        }

        return source.equals(target);
    }
}
