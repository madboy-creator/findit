package com.findit.service;

import com.findit.entity.User;
import com.findit.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("========== loadUserByUsername CALLED ==========");
        System.out.println("Email received: " + email);
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("User NOT found in database: " + email);
                    return new UsernameNotFoundException("User not found: " + email);
                });
        
        System.out.println("User FOUND: " + user.getEmail());
        System.out.println("Role: " + user.getRole());
        System.out.println("Stored password hash: " + user.getPassword());
        
        // Test password match
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean matches = encoder.matches("admin123", user.getPassword());
        System.out.println("Does 'admin123' match stored password? " + matches);
        
        System.out.println("==============================================");
        
        return user;
    }
}