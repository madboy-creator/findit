package com.findit.service;

import com.findit.entity.User;
import com.findit.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Transactional
    public User registerUser(String name, String email, String password, String studentId, String phone) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }
        if (userRepository.existsByStudentId(studentId)) {
            throw new RuntimeException("Student ID already registered");
        }
        
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setStudentId(studentId);
        user.setPhone(phone);
        user.setRole("STUDENT");
        user.setEnabled(true);
        
        return userRepository.save(user);
    }
    
    public User findByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found with email: " + email);
        }
        return userOptional.get();
    }
    
    public User findById(Long id) {
    Optional<User> userOptional = userRepository.findById(id.longValue());
    if (userOptional.isEmpty()) {
        throw new RuntimeException("User not found with id: " + id);
    }
    return userOptional.get();
}
}