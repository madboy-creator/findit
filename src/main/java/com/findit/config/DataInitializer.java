package com.findit.config;

import com.findit.entity.User;
import com.findit.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin@findit.com")) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@findit.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setStudentId("ADMIN001");
            admin.setPhone("+250 700 000 000");
            admin.setRole("ADMIN");
            admin.setEnabled(true);
            userRepository.save(admin);
            System.out.println("✅ Admin user created: admin@findit.com / admin123");
        }

        if (!userRepository.existsByEmail("student@findit.com")) {
            User student = new User();
            student.setName("Demo Student");
            student.setEmail("student@findit.com");
            student.setPassword(passwordEncoder.encode("student123"));
            student.setStudentId("STU2024001");
            student.setPhone("+250 700 000 001");
            student.setRole("STUDENT");
            student.setEnabled(true);
            userRepository.save(student);
            System.out.println("✅ Demo student created: student@findit.com / student123");
        }

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🎉 FindIt Application Ready!");
        System.out.println("📍 http://localhost:8080");
        System.out.println("🔐 Admin:   admin@findit.com / admin123");
        System.out.println("🔐 Student: student@findit.com / student123");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
}