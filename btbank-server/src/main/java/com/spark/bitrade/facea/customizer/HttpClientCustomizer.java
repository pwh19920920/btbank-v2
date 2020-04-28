package com.spark.bitrade.facea.customizer;

import com.spark.bitrade.face.spring.boot.autoconfigure.HttpClientProvider;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HttpClientCustomizer implements HttpClientProvider {

    @Override
    public HttpClient get() {
        log.info("自定义HttpClient执行");
        return HttpClientBuilder.create().build();
    }
}
