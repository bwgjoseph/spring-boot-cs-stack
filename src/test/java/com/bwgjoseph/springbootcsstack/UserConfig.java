package com.bwgjoseph.springbootcsstack;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.bwgjoseph.springbootcsstack.context.AuthenticatedPrincipalContext;
import com.bwgjoseph.springbootcsstack.core.UserClaim;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.memory.UserAttribute;
import org.springframework.security.core.userdetails.memory.UserAttributeEditor;
import org.springframework.security.provisioning.UserDetailsManager;

@TestConfiguration
public class UserConfig {
    @Bean
    @Primary
    public UserDetailsService userDetailsService(CustomInMemoryUserDetailsManager customInMemoryUserDetailsManager) {
        return customInMemoryUserDetailsManager;
    }

    @Bean
    public CustomInMemoryUserDetailsManager customInMemoryUserDetailsManager() {
        UserClaim user = new UserClaim("user", List.of(new SimpleGrantedAuthority("USER")));
        UserClaim admin = new UserClaim("admin", List.of(new SimpleGrantedAuthority("ADMIN")));
        return new CustomInMemoryUserDetailsManager(List.of(user, admin));
    }

    @Bean
    public AuthenticatedPrincipalContext authenticatedPrincipalContext() {
        return new AuthenticatedPrincipalContext();
    }
}

class CustomInMemoryUserDetailsManager implements UserDetailsManager {

	private final Map<String, UserClaim> users = new HashMap<>();

	public CustomInMemoryUserDetailsManager() {
	}

	public CustomInMemoryUserDetailsManager(Collection<UserDetails> users) {
		for (UserDetails user : users) {
			createUser(user);
		}
	}

	public CustomInMemoryUserDetailsManager(UserDetails... users) {
		for (UserDetails user : users) {
			createUser(user);
		}
	}

	public CustomInMemoryUserDetailsManager(Properties users) {
		Enumeration<?> names = users.propertyNames();
		UserAttributeEditor editor = new UserAttributeEditor();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			editor.setAsText(users.getProperty(name));
			UserAttribute attr = (UserAttribute) editor.getValue();
			createUser(createUserDetails(name, attr));
		}
	}

	private UserClaim createUserDetails(String name, UserAttribute attr) {
		return new UserClaim(name, attr.getAuthorities());
	}

	@Override
	public void createUser(UserDetails user) {
		this.users.put(user.getUsername().toLowerCase(), (UserClaim) new UserClaim(user.getUsername(), user.getAuthorities()));
	}

	@Override
	public void deleteUser(String username) {
		this.users.remove(username.toLowerCase());
	}

	@Override
	public void updateUser(UserDetails user) {
		this.users.put(user.getUsername().toLowerCase(), (UserClaim) new UserClaim(user.getUsername(), user.getAuthorities()));
	}

	@Override
	public boolean userExists(String username) {
		return this.users.containsKey(username.toLowerCase());
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserDetails user = this.users.get(username.toLowerCase());
		if (user == null) {
			throw new UsernameNotFoundException(username);
		}
		return new UserClaim(user.getUsername(), user.getPassword(), user.isEnabled(), user.isAccountNonExpired(),
				user.isCredentialsNonExpired(), user.isAccountNonLocked(), user.getAuthorities());
	}

    @Override
    public void changePassword(String oldPassword, String newPassword) {
        throw new UnsupportedOperationException();
    }

}
