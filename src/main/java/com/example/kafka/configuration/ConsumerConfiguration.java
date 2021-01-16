package com.example.kafka.configuration;

import com.example.kafka.models.Data;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class ConsumerConfiguration {

    private final Logger logger = LoggerFactory.getLogger(ConsumerConfiguration.class);

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Autowired
    private KafkaTemplate<String, Data> templateData;

    @Autowired
    private KafkaTemplate<String, String> templateString;

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        props.put(ConsumerConfig.GROUP_ID_CONFIG, "json");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        return props;
    }

    @Bean
    public ConsumerFactory<String, Data> consumerFactory() {
        JsonDeserializer<Data> deserializer = new JsonDeserializer<>();
        deserializer.addTrustedPackages("com.example.kafka.models");
        ErrorHandlingDeserializer<Data> errorHandlingDeserializer = new ErrorHandlingDeserializer<>(deserializer);

        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new StringDeserializer(),
                errorHandlingDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Data> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Data> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        SeekToCurrentErrorHandler seekToCurrentErrorHandler = new SeekToCurrentErrorHandler((record, exception) -> {
            if (exception.getCause() instanceof DeserializationException) {
                String rawMessage = new String(((DeserializationException) exception.getCause()).getData());
                logger.error("Deserialization error for message: {}", rawMessage);
                this.templateString.send(record.topic() + ".deserialization.DLT", rawMessage); // Send message to Dead Letter Topic
                return;
            }

            logger.error("An error occurred: {} with data: {}", exception.getMessage(), record);
            this.templateData.send(record.topic() + ".DLT", (Data) record.value()); // Send message to Dead Letter Topic
        }, new FixedBackOff(1000L, 3L));

        seekToCurrentErrorHandler.setAckAfterHandle(true); // Commit message that have been successfully

        factory.setErrorHandler(seekToCurrentErrorHandler);

        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        factory.getContainerProperties().setSyncCommits(true);
        return factory;
    }
}
