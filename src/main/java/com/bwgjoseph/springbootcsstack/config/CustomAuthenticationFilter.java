package com.bwgjoseph.springbootcsstack.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * This custom filter ensure that it will setup the `SecurityContextHolder` with
 * the Authenticated Principal if found, otherwise, an exception will be thrown
 *
 * It will auto backoff if there is a valid `SecurityContextHolder`
 */
public class CustomAuthenticationFilter extends OncePerRequestFilter {
    private final UserClaimDetailsService userClaimDetailsService;
    private final SecurityProperties securityProperties;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public CustomAuthenticationFilter(UserClaimDetailsService userClaimDetailsService, SecurityProperties securityProperties) {
        this.userClaimDetailsService = userClaimDetailsService;
        this.securityProperties = securityProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            SecurityContextHolder.createEmptyContext();
            UserDetails user = this.userClaimDetailsService.loadUserByUsername(this.securityProperties.getUser().getName());
            Authentication auth = new PreAuthenticatedAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            this.applicationEventPublisher.publishEvent(new AuditApplicationEvent(
                new AuditEvent(user.getUsername(), "USER_REQUEST_AUDIT_EVENT")
            ));
        }

        filterChain.doFilter(request, response);
    }
}
