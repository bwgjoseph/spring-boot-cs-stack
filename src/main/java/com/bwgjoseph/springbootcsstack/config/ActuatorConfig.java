package com.bwgjoseph.springbootcsstack.config;

import org.springframework.boot.actuate.audit.InMemoryAuditEventRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActuatorConfig {
    @Bean
    public InMemoryAuditEventRepository repository(){
        return new InMemoryAuditEventRepository();
    }
}
