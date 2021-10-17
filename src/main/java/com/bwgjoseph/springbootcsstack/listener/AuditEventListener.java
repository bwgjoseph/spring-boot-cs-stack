package com.bwgjoseph.springbootcsstack.listener;

import java.util.Map;

import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuditEventListener {
    @EventListener
    public void listen(AuditApplicationEvent auditApplicationEvent) {
        AuditEvent auditEvent = auditApplicationEvent.getAuditEvent();
        Map<String, Object> data = auditEvent.getData();

        log.info("Principal {} - {} ", auditEvent.getPrincipal(), auditEvent.getType());
        log.info("Remote IP address: {}", data.get("remoteAddr"));
        log.info("Local IP address: {}", data.get("localAddr"));
        log.info("Session Id: {}", data.get("sessionId"));
        log.info("Request URL: {}", data.get("requestUrl"));
    }
}
