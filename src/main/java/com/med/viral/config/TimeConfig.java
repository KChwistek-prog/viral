package com.med.viral.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
public class TimeConfig {
    @Bean
    public Clock clock(){
        return Clock.systemDefaultZone();
    }
}
