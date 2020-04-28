package com.spark.bitrade;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.servlet.annotation.WebFilter;

/**
 * @author davi
 */
@EnableFeignClients
@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableCaching
@EnableDiscoveryClient
@WebFilter(filterName = "CrossFilter", urlPatterns = "/*",asyncSupported=true)
@MapperScan({"com.spark.bitrade.repository.mapper","com.spark.bitrade.mapper"})
public class BtBankApplication extends AdvancedApplication {

    public static void main(String[] args) {
        SpringApplication.run(BtBankApplication.class, args);
    }
}
