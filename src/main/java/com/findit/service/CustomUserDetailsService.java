package com.findit.service;

import com.findit.entity.User;
import com.findit.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    
    private final UserRepository userRepository;
    
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        
        if (user.getAccountNonLocked() != null && !user.getAccountNonLocked()) {
            if (user.getLockTime() != null) {
                LocalDateTime lockTime = user.getLockTime();
                if (lockTime.plusMinutes(15).isBefore(LocalDateTime.now())) {
                    user.setAccountNonLocked(true);
                    user.setFailedAttempts(0);
                    user.setLockTime(null);
                    userRepository.save(user);
                    logger.info("Account automatically unlocked for user: {}", email);
                } else {
                    logger.warn("Attempt to login to locked account: {}", email);
                    throw new RuntimeException("Account is locked. Please try again later.");
                }
            }
        }
        
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole())))
                .accountExpired(false)
                .accountLocked(user.getAccountNonLocked() != null && !user.getAccountNonLocked())
                .credentialsExpired(false)
                .disabled(user.getEnabled() != null && !user.getEnabled())
                .build();
    }
}