package com.example.kafka.handlers;

import com.example.kafka.models.Data;
import org.springframework.kafka.support.serializer.FailedDeserializationInfo;

import java.util.function.Function;

public class FailedDataProvider implements Function<FailedDeserializationInfo, Data> {

    @Override
    public Data apply(FailedDeserializationInfo info) {
        return new BadData(info);
    }

}