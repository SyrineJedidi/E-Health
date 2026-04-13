package com.ehealth.patient.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Les comptes sont gérés par auth-service ; ici on ne charge que le sujet JWT pour la validation stateless.
 */
@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null || username.isBlank()) {
            throw new UsernameNotFoundException("Utilisateur inconnu");
        }
        return new User(
                username,
                "",
                List.of(new SimpleGrantedAuthority("ROLE_API_USER")));
    }
}
