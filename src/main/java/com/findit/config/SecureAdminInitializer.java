package com.findit.config;

import com.findit.entity.User;
import com.findit.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SecureAdminInitializer implements CommandLineRunner {
    
    private static final Logger log = LoggerFactory.getLogger(SecureAdminInitializer.class);
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Value("${ADMIN_EMAIL:}")
    private String adminEmail;
    
    @Value("${ADMIN_PASSWORD:}")
    private String adminPassword;
    
    @Value("${ADMIN_NAME:Admin}")
    private String adminName;
    
    @Value("${ADMIN_STUDENT_ID:ADMIN001}")
    private String adminStudentId;
    
    @Value("${ADMIN_PHONE:+250700000000}")
    private String adminPhone;
    
    public SecureAdminInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public void run(String... args) {
        if (adminEmail != null && !adminEmail.isEmpty() && 
            adminPassword != null && !adminPassword.isEmpty()) {
            
            if (!userRepository.existsByEmail(adminEmail)) {
                User admin = new User();
                admin.setName(adminName);
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setStudentId(adminStudentId);
                admin.setPhone(adminPhone);
                admin.setRole("ADMIN");
                admin.setEnabled(true);
                admin.setAccountNonLocked(true);
                admin.setFailedAttempts(0);
                admin.setCreatedAt(java.time.LocalDateTime.now());
                admin.setUpdatedAt(java.time.LocalDateTime.now());
                
                userRepository.save(admin);
                log.info("ADMIN USER CREATED FROM ENVIRONMENT VARIABLES");
                log.info("Email: {}", adminEmail);
            } else {
                log.info("Admin user already exists. Skipping creation.");
            }
        } else {
            log.info("No admin environment variables set. Skipping admin creation.");
        }
    }
}
