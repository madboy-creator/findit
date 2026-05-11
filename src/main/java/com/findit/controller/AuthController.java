package com.findit.controller;

import com.findit.entity.User;
import com.findit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "registered", required = false) String registered,
                           Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid email or password!");
        }
        if (registered != null) {
            model.addAttribute("message", "Registration successful! Please login.");
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, 
                              Model model) {
        try {
            // Check if email already exists
            if (userRepository.existsByEmail(user.getEmail())) {
                model.addAttribute("error", "Email already registered");
                return "auth/register";
            }
            
            // Set required fields (your entity already has defaults, but we set explicitly)
            user.setRole("STUDENT");
            user.setEnabled(true);
            user.setAccountNonLocked(true);
            user.setFailedAttempts(0);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            
            // Handle null values (in case form doesn't have these fields)
            if (user.getStudentId() == null || user.getStudentId().isEmpty()) {
                user.setStudentId("STU" + System.currentTimeMillis());
            }
            
            if (user.getPhone() == null || user.getPhone().isEmpty()) {
                user.setPhone("Not provided");
            }
            
            // Encode password
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            
            // Save user
            userRepository.save(user);
            
            return "redirect:/login?registered=true";
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/setup")
    @ResponseBody
    public String setupAdmin() {
        try {
            if (!userRepository.existsByEmail("admin@findit.com")) {
                User admin = new User();
                admin.setName("Administrator");
                admin.setEmail("admin@findit.com");
                admin.setPassword(passwordEncoder.encode("Admin123!"));
                admin.setStudentId("ADMIN001");
                admin.setPhone("+250788123456");
                admin.setRole("ADMIN");
                admin.setEnabled(true);
                admin.setAccountNonLocked(true);
                admin.setFailedAttempts(0);
                admin.setCreatedAt(LocalDateTime.now());
                admin.setUpdatedAt(LocalDateTime.now());
                userRepository.save(admin);
                return "<h3>✅ Admin user created successfully!</h3>" +
                       "<p>You can now login with:</p>" +
                       "<ul>" +
                       "<li><strong>Email:</strong> admin@findit.com</li>" +
                       "<li><strong>Password:</strong> Admin123!</li>" +
                       "</ul>" +
                       "<a href='/login'>Go to Login Page</a>";
            }
            return "<h3>ℹ️ Admin user already exists!</h3><a href='/login'>Go to Login Page</a>";
        } catch (Exception e) {
            return "<h3>❌ Error creating admin:</h3><p>" + e.getMessage() + "</p>";
        }
    }
}