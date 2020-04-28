package com.spark.bitrade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

/**
 * @author Zhang Jinwei
 * @date 2018年02月06日
 */
@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
public class KMSApplication {
    public static void main(String[] args) {
        SpringApplication.run(KMSApplication.class, args);
    }
}
