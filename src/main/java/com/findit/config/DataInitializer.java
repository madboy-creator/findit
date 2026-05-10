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
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Value("${DEFAULT_ADMIN_EMAIL:admin@findit.com}")
    private String adminEmail;
    
    @Value("${DEFAULT_ADMIN_PASSWORD:}")
    private String adminPassword;
    
    @Value("${DEFAULT_STUDENT_EMAIL:student@findit.com}")
    private String studentEmail;
    
    @Value("${DEFAULT_STUDENT_PASSWORD:}")
    private String studentPassword;
    
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public void run(String... args) {
        // Only create admin if password is provided via environment variable
        if (adminPassword != null && !adminPassword.isEmpty() && !userRepository.existsByEmail(adminEmail)) {
            User admin = new User();
            admin.setName("Admin User");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setStudentId("ADMIN001");
            admin.setPhone("+250700000000");
            admin.setRole("ADMIN");
            admin.setEnabled(true);
            admin.setAccountNonLocked(true);
            admin.setCreatedAt(java.time.LocalDateTime.now());
            admin.setUpdatedAt(java.time.LocalDateTime.now());
            userRepository.save(admin);
            log.info("Default admin created: {}", adminEmail);
        }
        
        // Only create student if password is provided via environment variable
        if (studentPassword != null && !studentPassword.isEmpty() && !userRepository.existsByEmail(studentEmail)) {
            User student = new User();
            student.setName("Demo Student");
            student.setEmail(studentEmail);
            student.setPassword(passwordEncoder.encode(studentPassword));
            student.setStudentId("STU2024001");
            student.setPhone("+250700000001");
            student.setRole("STUDENT");
            student.setEnabled(true);
            student.setAccountNonLocked(true);
            student.setCreatedAt(java.time.LocalDateTime.now());
            student.setUpdatedAt(java.time.LocalDateTime.now());
            userRepository.save(student);
            log.info("Default student created: {}", studentEmail);
        }
    }
}