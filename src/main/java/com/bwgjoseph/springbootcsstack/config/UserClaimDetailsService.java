package com.bwgjoseph.springbootcsstack.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.bwgjoseph.springbootcsstack.core.UserClaim;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Custom `UserDetailsService` implementation with in-memory users setup
 * and re-using the `SecurityProperties.User` to setup the user
 */
public class UserClaimDetailsService implements UserDetailsService {
    private final Map<String, UserClaim> users = new HashMap<>();
    private final SecurityProperties securityProperties;

    public UserClaimDetailsService(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    public void createUser(UserClaim userClaim) {
        this.users.put(userClaim.getUsername(), userClaim);
    }

    @PostConstruct
    public void init() {
        String user = this.securityProperties.getUser().getName();
        Set<GrantedAuthority> authorities = this.securityProperties.getUser().getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());

        this.users.put(user, new UserClaim(user, authorities));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserClaim uc = this.users.get(username);

        if (uc == null) {
            throw new UsernameNotFoundException(username + " not found");
        }

        return uc;
    }

}
