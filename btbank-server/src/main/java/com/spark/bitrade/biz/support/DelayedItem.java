package com.spark.bitrade.biz.support;

import java.time.Instant;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * DelayedItem
 *
 * @author biu
 * @since 2019/12/1 17:37
 */
public class DelayedItem<T> implements Delayed {

    private final T body;
    private final long time;

    public DelayedItem(T body, long timeout) {
        this.body = body;
        this.time = Instant.now().getEpochSecond() + timeout;
    }

    public T getBody() {
        return body;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        //return unit.convert(this.time - System.nanoTime(), TimeUnit.SECONDS);
        return unit.convert(time - Instant.now().getEpochSecond(), TimeUnit.SECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        if (this.getDelay(TimeUnit.SECONDS) > o.getDelay(TimeUnit.SECONDS)) {
            return 1;
        } else if (this.getDelay(TimeUnit.SECONDS) < o.getDelay(TimeUnit.SECONDS)) {
            return -1;
        }
        return 0;
    }

    @Override
    public int hashCode() {
        return body.hashCode();
    }
}
