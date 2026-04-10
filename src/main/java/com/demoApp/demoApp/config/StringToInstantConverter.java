package com.demoApp.demoApp.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Component
public class StringToInstantConverter implements Converter<String, Instant> {

    @Override
    public Instant convert(String source) {
        return LocalDate.parse(source)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();
    }
}