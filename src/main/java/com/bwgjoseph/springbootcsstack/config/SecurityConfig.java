package com.bwgjoseph.springbootcsstack.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private SecurityProperties securityProperties;

    @Bean("userDetailsService")
    public UserClaimDetailsService userClaimDetailsService(SecurityProperties securityProperties) {
        return new UserClaimDetailsService(securityProperties);
    }

    @Bean
    public CustomAuthenticationFilter customAuthenticationFilter(UserClaimDetailsService userClaimDetailsService, SecurityProperties securityProperties) {
        return new CustomAuthenticationFilter(userClaimDetailsService, securityProperties);
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
            .addFilterBefore(this.customAuthenticationFilter(this.userClaimDetailsService(this.securityProperties), this.securityProperties), BasicAuthenticationFilter.class)
            // disable auth caching
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            // .and()
            // .httpBasic()
            // Will get 403 forbidden if not disabled
            .and()
            .csrf()
            .disable();
    }

}
