package com.bwgjoseph.springbootcsstack.context;

import com.bwgjoseph.springbootcsstack.core.UserClaim;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * A facade to get the current authenticated principal object (UserClaim)
 * anywhere in the application as `@AuthenticatedPrincipal` is only
 * accessible in `Controllers class Args`
 */
@Component
public class AuthenticatedPrincipalContext {

    public UserClaim getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return (auth == null) ? null : (UserClaim) auth.getPrincipal();
    }
}
