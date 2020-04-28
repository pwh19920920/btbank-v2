package com.spark.bitrade;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class UcenterApplication extends AdvancedApplication {

    public static void main(String[] args) {
        SpringApplication.run(UcenterApplication.class, args);
    }
}
