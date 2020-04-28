package com.spark.bitrade.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ContextConfig extends WebMvcConfigurerAdapter {
    //edit by yangch 时间： 2018.07.06 原因：api网关层处理，重复处理会报跨域问题
    /**
     * 解决 - Spring Boot enable <async-supported> like in web.xml
     * @return
     */
    @Bean
    public ServletRegistrationBean dispatcherServlet() {
        ServletRegistrationBean registration = new ServletRegistrationBean(
                new DispatcherServlet(), "/*");

        Map<String,String> params = new HashMap<String,String>();
        //params.put("org.atmosphere.servlet","org.springframework.web.servlet.DispatcherServlet");
        params.put("contextClass","org.springframework.web.context.support.AnnotationConfigWebApplicationContext");
        params.put("contextConfigLocation","net.org.selector.animals.config.ComponentConfiguration");
        registration.setInitParameters(params);
        registration.setAsyncSupported(true);
        return registration;
    }

    @Bean(name = "taskExecutorAutoUnlock")
    public Executor taskExecutorWbswryxx(@Value("${async.corePollSize:10}") Integer corePollSize
            ,@Value("${async.maxPoolSize:20}") Integer maxPoolSize
            ,@Value("${async.queueCapacity:1000}") Integer queueCapacity) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePollSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(600);
        executor.setThreadNamePrefix("taskExecutorAutoUnlock-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(3600);
        return executor;
    }


}
