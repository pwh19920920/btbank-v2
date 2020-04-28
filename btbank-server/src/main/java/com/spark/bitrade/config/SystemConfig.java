package com.spark.bitrade.config;

import com.spark.bitrade.config.bean.ImConfig;
import com.spark.bitrade.config.bean.OpenEpmOptions;
import com.spark.bitrade.util.IdWorkByTwitter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author davi
 */
@Configuration
public class SystemConfig {
    @Bean
    public IdWorkByTwitter idWorkByTwitter(
            @Value("${spark.system.work-id:0}") long workId,
            @Value("${spark.system.data-center-id:0}") long dataCenterId) {
        return new IdWorkByTwitter(workId, dataCenterId);
    }

    @Bean
    @ConfigurationProperties(prefix = "epm")
    public OpenEpmOptions openEmpOptions() {
        return new OpenEpmOptions();
    }
    @Bean
    @ConfigurationProperties(prefix = "im")
    public ImConfig imConfig() {
        return new ImConfig();
    }
}
