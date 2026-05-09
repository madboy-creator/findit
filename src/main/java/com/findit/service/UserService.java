package com.findit.service;

import com.findit.entity.User;
import com.findit.repository.UserRepository;
import com.findit.exception.ResourceNotFoundException;
import com.findit.exception.DuplicateResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private static final int MAX_FAILED_ATTEMPTS = 5;
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Transactional
    public User registerUser(String name, String email, String password, String studentId, String phone) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email already registered: " + email);
        }
        if (userRepository.existsByStudentId(studentId)) {
            throw new DuplicateResourceException("Student ID already registered: " + studentId);
        }
        
        validatePasswordStrength(password);
        
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setStudentId(studentId);
        user.setPhone(phone);
        
        if (email.endsWith("@findit.com") || email.equals("admin@findit.com")) {
            user.setRole("ADMIN");
        } else {
            user.setRole("STUDENT");
        }
        
        user.setEnabled(true);
        user.setAccountNonLocked(true);
        user.setFailedAttempts(0);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        logger.info("New user registered: {} ({}) as {}", email, studentId, user.getRole());
        return savedUser;
    }
    
    public User findByEmail(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new ResourceNotFoundException("User not found with email: " + email);
        }
        return userOpt.get();
    }
    
    public User findById(Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        return userOpt.get();
    }
    
    public User save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public long getTotalUserCount() {
        return userRepository.count();
    }
    
    public void updateLastLogin(String email) {
        User user = findByEmail(email);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        logger.info("Last login updated for user: {}", email);
    }
    
    @Transactional
    public void increaseFailedAttempts(String email) {
        User user = findByEmail(email);
        int attempts = (user.getFailedAttempts() == null ? 0 : user.getFailedAttempts()) + 1;
        user.setFailedAttempts(attempts);
        logger.warn("Failed attempts for {}: {}/{}", email, attempts, MAX_FAILED_ATTEMPTS);
        
        if (attempts >= MAX_FAILED_ATTEMPTS) {
            user.setAccountNonLocked(false);
            user.setLockTime(LocalDateTime.now());
            logger.warn("Account locked for user: {}", email);
        }
        userRepository.save(user);
    }
    
    @Transactional
    public void resetFailedAttempts(String email) {
        User user = findByEmail(email);
        user.setFailedAttempts(0);
        user.setAccountNonLocked(true);
        user.setLockTime(null);
        userRepository.save(user);
        logger.info("Failed attempts reset for user: {}", email);
    }
    
    public void deleteUser(Long userId) {
        User user = findById(userId);
        userRepository.delete(user);
        logger.info("User deleted: {} ({})", user.getEmail(), user.getId());
    }
    
    public List<User> searchUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return userRepository.findAll();
        }
        return userRepository.findByNameContainingOrEmailContaining(query, query);
    }
    
    private void validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter");
        }
        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Password must contain at least one digit");
        }
        if (!password.matches(".*[@#$%^&+=!].*")) {
            throw new IllegalArgumentException("Password must contain at least one special character (@#$%^&+=!)");
        }
    }
}