package com.spark.bitrade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * JobApplication
 *
 * @author biu
 * @since 2019/7/8 18:10
 */
@EnableDiscoveryClient
@EnableScheduling
@EnableFeignClients
@SpringBootApplication
public class JobApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobApplication.class, args);
    }
}
