package com.spark.bitrade.biz.impl;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;

//@Configuration
@EnableTransactionManagement
@MapperScan("com.spark.bitrade.repository")
@ComponentScan(basePackages = {"com.spark.bitrade.repository", "com.spark.bitrade.biz"})
@TestPropertySource(properties = {"mybatis-plus.mapper-locations=classpath:/mapper/*.xml"})
public class TestDatabaseConfig {
    @Bean
    public DataSource dataSource() {
        EmbeddedDatabase build =
                new EmbeddedDatabaseBuilder()
                        .setType(EmbeddedDatabaseType.H2)
                        .setScriptEncoding("UTF-8")
                        .ignoreFailedDrops(true)
                        .addScript("classpath:scheme.sql")
                        .addScript("classpath:data.sql")
                        .build();
        return build;
    }

    @Bean
    public DataSourceTransactionManager transactionManager(DataSource ds) {
        return new DataSourceTransactionManager(ds);
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) throws IOException {
        final DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(databasePopulator());
        initializer.setEnabled(true);
        return initializer;
    }

    private DatabasePopulator databasePopulator() throws IOException {
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
        resourceDatabasePopulator.setContinueOnError(false);
        resourceDatabasePopulator.addScripts(
                new PathMatchingResourcePatternResolver().getResources("classpath*:scheme.sql"));
        return resourceDatabasePopulator;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource ds) {
        return new JdbcTemplate(ds);
    }

}
