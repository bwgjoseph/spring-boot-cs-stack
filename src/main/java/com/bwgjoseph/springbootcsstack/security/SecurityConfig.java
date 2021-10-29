package com.bwgjoseph.springbootcsstack.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private SecurityProperties securityProperties;

    @Bean
    public ConfigAuthenticationFilter configAuthenticationFilter(SecurityProperties securityProperties) throws Exception {
        ConfigAuthenticationFilter caf = new ConfigAuthenticationFilter(securityProperties);
        caf.setAuthenticationManager(authenticationManager());

        return caf;
    }

    @Bean
    public PreAuthenticatedAuthenticationProvider provider(SecurityProperties securityProperties) {
        PreAuthenticatedAuthenticationProvider p = new PreAuthenticatedAuthenticationProvider();
        UserDetailsByNameServiceWrapper<PreAuthenticatedAuthenticationToken> wrapper = new UserDetailsByNameServiceWrapper<>(this.userClaimDetailsService(securityProperties));
        p.setPreAuthenticatedUserDetailsService(wrapper);
        return p;
    }

    @Bean("userDetailsService")
    public UserDetailsService userClaimDetailsService(SecurityProperties securityProperties) {
        return new UserClaimDetailsService(securityProperties);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            // ensure all request are authenticated
            .authorizeRequests()
            .anyRequest()
            .authenticated()
            .and()
            // add PreAuthenticatedAuthenticationToken
            .addFilterBefore(this.configAuthenticationFilter(this.securityProperties), AbstractPreAuthenticatedProcessingFilter.class)
            .authenticationProvider(this.provider(this.securityProperties))
            // disable auth caching
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            // Will get 403 forbidden if not disabled
            .and()
            .csrf()
            .disable();
    }

}
