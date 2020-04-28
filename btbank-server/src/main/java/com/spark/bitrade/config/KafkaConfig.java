package com.spark.bitrade.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * KafkaConfig
 *
 * @author biu
 * @since 2019/12/2 10:36
 */
@Configuration
public class KafkaConfig {

    private final Environment environment;

    public KafkaConfig(Environment environment) {
        this.environment = environment;
    }

    private Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getProperty("spring.kafka.bootstrap-servers"));
        props.put(ProducerConfig.RETRIES_CONFIG, environment.getProperty("kafka.producer.retries"));
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, environment.getProperty("kafka.producer.batch.size"));
        props.put(ProducerConfig.LINGER_MS_CONFIG, environment.getProperty("kafka.producer.linger"));
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, environment.getProperty("kafka.producer.buffer.memory"));
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }

    private ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<String, String>(producerFactory());
    }
}
