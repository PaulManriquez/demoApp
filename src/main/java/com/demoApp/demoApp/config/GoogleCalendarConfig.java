package com.demoApp.demoApp.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(GoogleCalendarProperties.class)
public class GoogleCalendarConfig {
}

