package com.example.kafka.engine;

import com.example.kafka.models.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class Producer {

    private static final Logger logger = LoggerFactory.getLogger(Producer.class);
    private static final String TOPIC = "messages";

    private final KafkaTemplate<String, Data> kafkaTemplate;

    @Autowired
    public Producer(KafkaTemplate<String, Data> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(Data data) {
        logger.info("Sending data={} to topic={}", data, TOPIC);

        Message<Data> message = MessageBuilder
                .withPayload(data)
                .setHeader(KafkaHeaders.TOPIC, TOPIC)
                .build();

        this.kafkaTemplate.send(message);
    }
}
