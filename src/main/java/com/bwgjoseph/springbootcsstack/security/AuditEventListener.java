package com.bwgjoseph.springbootcsstack.security;

import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuditEventListener {
    @EventListener
    public void listen(AuditApplicationEvent auditApplicationEvent) {
        log.info(auditApplicationEvent.getAuditEvent().toString());
    }
}
