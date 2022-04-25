package com.example.springbootredisdessert.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ObjectMapperUtils {

    private final ObjectMapper mapper = createObjectMapper();

    public static final String DESSERT_KEY = "DK";

    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    public <T> T objectMapper(Object obj, Class<T> contentClass) {
        return mapper.convertValue(obj, contentClass);
    }


}
