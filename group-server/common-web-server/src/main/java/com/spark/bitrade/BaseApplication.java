package com.spark.bitrade;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author young
 * @date 2019-05-10 10:32:29
 */
@EnableFeignClients
@EnableScheduling
@EnableAsync
@EnableCaching
public abstract class BaseApplication {
}
