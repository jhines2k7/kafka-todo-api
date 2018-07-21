package com.jhinesconsulting.kafkatodoapi;

import com.jhinesconsulting.TodoEvent;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

@Configuration
public class KafkaConfig {
    @Autowired
    ApplicationProperties applicationProperties;

    @Bean
    public TodoEventRecordConsumer todoEventRecordConsumer() {
        Properties properties = new Properties();

        properties.setProperty(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                applicationProperties.getBootstrapServersUrl() + ":" + applicationProperties.getBootstrapServersPort()
        );
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
        properties.setProperty(
                "schema.registry.url", "http://" + applicationProperties.getBootstrapServersUrl() + ":" + applicationProperties.getSchemaRegistryPort());
        properties.setProperty("specific.avro.reader", "true");

        KafkaConsumer<String, TodoEvent> kafkaConsumer = new KafkaConsumer<>(properties);

        kafkaConsumer.subscribe(Collections.singleton(applicationProperties.getTopic()));

        return new TodoEventRecordConsumer(kafkaConsumer, todoEventRecordStore());
    }

    @Bean
    public TodoEventRecordProducer counterEventRecordProducer() {
        Properties properties = new Properties();

        properties.setProperty("bootstrap.servers", applicationProperties.getBootstrapServersUrl() + ":" + applicationProperties.getBootstrapServersPort());
        properties.setProperty("acks", "all");
        properties.setProperty("retries", "10");

        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer", KafkaAvroSerializer.class.getName());
        properties.setProperty("schema.registry.url", "http://" + applicationProperties.getBootstrapServersUrl() + ":" + applicationProperties.getSchemaRegistryPort());

        Producer<String, TodoEvent> kafkaProducer = new KafkaProducer<>(properties);

        return new TodoEventRecordProducer(kafkaProducer);
    }

    @Bean
    public TodoEventRecordStore todoEventRecordStore() {
        return new TodoEventRecordStore();
    }
}
