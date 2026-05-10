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
        
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setStudentId(studentId);
        user.setPhone(phone);
        
        // FIXED: Only STUDENT role - NO automatic ADMIN
        user.setRole("STUDENT");
        
        user.setEnabled(true);
        user.setAccountNonLocked(true);
        user.setFailedAttempts(0);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        logger.info("New student registered: {} ({})", email, studentId);
        return savedUser;
    }
    
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
    
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
    
    public User save(User user) {
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
    }
    
    @Transactional
    public void increaseFailedAttempts(String email) {
        User user = findByEmail(email);
        int attempts = (user.getFailedAttempts() == null ? 0 : user.getFailedAttempts()) + 1;
        user.setFailedAttempts(attempts);
        
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
}