package com.bwgjoseph.springbootcsstack.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.RequiredArgsConstructor;

/**
 * This custom filter ensure that it will setup the `SecurityContextHolder` with
 * the Authenticated Principal if found, otherwise, an exception will be thrown
 *
 * It will auto backoff if there is a valid `SecurityContextHolder`
 */
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends OncePerRequestFilter {
    private final UserClaimDetailsService userClaimDetailsService;
    private final SecurityProperties securityProperties;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            SecurityContextHolder.createEmptyContext();
            UserDetails user = this.userClaimDetailsService.loadUserByUsername(this.securityProperties.getUser().getName());
            Authentication auth = new PreAuthenticatedAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            this.publishEvent(request, user.getUsername());
        }

        filterChain.doFilter(request, response);
    }

    public void publishEvent(HttpServletRequest request, String username) {
        Map<String, Object> data = new HashMap<>();
        data.put("requestUrl", request.getRequestURI());
        data.put("sessionId", request.getSession().getId());
        data.put("remoteAddr", request.getRemoteAddr());
        data.put("localAddr", request.getLocalAddr());

        this.applicationEventPublisher.publishEvent(new AuditApplicationEvent(
            new AuditEvent(username, "USER_REQUEST_AUDIT_EVENT", data)
        ));
    }
}
