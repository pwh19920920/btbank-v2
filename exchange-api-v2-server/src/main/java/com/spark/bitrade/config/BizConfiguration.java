package com.spark.bitrade.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * BizConfiguration
 *
 * @author biu
 * @since 2019/12/18 17:38
 */
@Configuration
public class BizConfiguration {

    @Bean("restTemplate")
    public RestTemplate buildRestTemplate(RestTemplateBuilder builder) {
        return builder.rootUri("https://www.silktraderdk.net").build();
    }

    @Bean("huoBiExchangeRestTemplate")
    public RestTemplate huoBiExchangeRestTemplate(RestTemplateBuilder builder) {
        return builder.rootUri("https://api.huobi.pro").build();
    }

    @Bean("huoBiOtcRestTemplate")
    public RestTemplate huoBiOtcRestTemplate(RestTemplateBuilder builder) {
        return builder.rootUri("https://otc-api.huobi.pro").build();
    }
}
