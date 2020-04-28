package com.spark.bitrade;


//import com.codingapi.txlcn.tc.config.EnableDistributedTransaction;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
public class ExchangeApiApplication extends AdvancedApplication {

    public static void main(String[] args){
        SpringApplication.run(ExchangeApiApplication.class,args);
    }
}
