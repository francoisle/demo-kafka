package com.example.kafka.handlers;

import com.example.kafka.models.Data;
import org.springframework.kafka.support.serializer.FailedDeserializationInfo;

import java.util.function.Function;

public class BadData extends Data {
    private final FailedDeserializationInfo failedDeserializationInfo;

    public BadData(FailedDeserializationInfo failedDeserializationInfo) {
        this.failedDeserializationInfo = failedDeserializationInfo;
    }

    public FailedDeserializationInfo getFailedDeserializationInfo() {
        return this.failedDeserializationInfo;
    }

}

