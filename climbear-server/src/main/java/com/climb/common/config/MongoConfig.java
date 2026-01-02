package com.climb.common.config;

import io.micrometer.common.lang.NonNullApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;

@Configuration
public class MongoConfig {

    // Date → Timestamp 변환기
    @NonNullApi
    public static class DateToTimestampConverter implements Converter<Date, Timestamp> {
        @Override
        public Timestamp convert(Date source) {
            return new Timestamp(source.getTime());
        }
    }

    // Timestamp → Date 변환기
    @NonNullApi
    public static class TimestampToDateConverter implements Converter<Timestamp, Date> {
        @Override
        public Date convert(Timestamp source) {
            return new Date(source.getTime());
        }
    }

    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(Arrays.asList(
                new DateToTimestampConverter(),
                new TimestampToDateConverter()
        ));
    }
}