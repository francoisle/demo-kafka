package com.example.kafka.engine;

import com.example.kafka.exception.ProcessingException;
import com.example.kafka.models.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class Consumer {

    private final Logger logger = LoggerFactory.getLogger(Producer.class);

    @KafkaListener(topics = "messages", groupId = "group_id")
    public void consume(Data data, Acknowledgment acknowledgment) {
        logger.info("Received message {}", data);

        if (!data.isValid()) { // Arbitrary control to test error handling
            logger.warn("Message is not valid");
            throw new ProcessingException();
        }

        logger.info("Message valid. Will acknowledge");
        acknowledgment.acknowledge();
    }
}
