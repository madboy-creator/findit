package com.findit.controller;

import com.findit.entity.User;
import com.findit.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    
    private final UserService userService;
    
    public DashboardController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/dashboard")
    public String redirectToRoleDashboard(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        
        if ("ADMIN".equals(user.getRole())) {
            return "redirect:/admin/dashboard";
        } else {
            return "redirect:/student/dashboard";
        }
    }
}