package com.findit.controller;

import com.findit.exception.DuplicateResourceException;
import com.findit.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    private final UserService userService;
    
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           @RequestParam(value = "expired", required = false) String expired,
                           Authentication authentication,
                           Model model) {
        
        if (authentication != null && authentication.isAuthenticated() 
            && !"anonymousUser".equals(authentication.getName())) {
            return "redirect:/dashboard";
        }
        
        model.addAttribute("hideNavbar", true);
        if (error != null) {
            model.addAttribute("error", "Invalid email or password");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully");
        }
        if (expired != null) {
            model.addAttribute("error", "Your session has expired. Please login again");
        }
        return "auth/login";
    }
    
    @GetMapping("/register")
    public String registerPage(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated() 
            && !"anonymousUser".equals(authentication.getName())) {
            return "redirect:/dashboard";
        }
        
        model.addAttribute("hideNavbar", true);
        model.addAttribute("user", new UserRegistrationDto());
        return "auth/register";
    }
    
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDto registrationDto,
                              RedirectAttributes redirectAttributes,
                              Authentication authentication) {
        
        if (authentication != null && authentication.isAuthenticated() 
            && !"anonymousUser".equals(authentication.getName())) {
            return "redirect:/dashboard";
        }
        
        try {
            userService.registerUser(
                registrationDto.getName(),
                registrationDto.getEmail(),
                registrationDto.getPassword(),
                registrationDto.getStudentId(),
                registrationDto.getPhone()
            );
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            logger.info("New user registered: {}", registrationDto.getEmail());
            return "redirect:/login?registered=true";
        } catch (DuplicateResourceException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            logger.warn("Registration failed: {}", e.getMessage());
            return "redirect:/register";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            logger.warn("Registration validation failed: {}", e.getMessage());
            return "redirect:/register";
        }
    }
    
    @GetMapping("/logout-success")
    public String logoutSuccess() {
        return "redirect:/login?logout=true";
    }
    
    static class UserRegistrationDto {
        private String name;
        private String email;
        private String password;
        private String studentId;
        private String phone;
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getStudentId() { return studentId; }
        public void setStudentId(String studentId) { this.studentId = studentId; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }
}