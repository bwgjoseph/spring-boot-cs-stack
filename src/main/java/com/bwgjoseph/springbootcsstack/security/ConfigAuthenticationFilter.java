package com.bwgjoseph.springbootcsstack.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConfigAuthenticationFilter extends AbstractPreAuthenticatedProcessingFilter {
    private final SecurityProperties securityProperties;

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        return this.securityProperties.getUser().getName();
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return "N/A";
    }
}
