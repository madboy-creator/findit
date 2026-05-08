package com.findit.controller;

import com.findit.service.ItemService;
import com.findit.service.ClaimService;
import com.findit.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student")
public class StudentController {
    
    private final ItemService itemService;
    private final ClaimService claimService;
    private final UserService userService;
    
    public StudentController(ItemService itemService, ClaimService claimService, UserService userService) {
        this.itemService = itemService;
        this.claimService = claimService;
        this.userService = userService;
    }
    
    @GetMapping("/dashboard")
    public String studentDashboard(Model model, Authentication authentication) {
        String email = authentication.getName();
        model.addAttribute("userName", userService.findByEmail(email).getName());
        model.addAttribute("recentFoundItems", itemService.getRecentFoundItems(6));
        model.addAttribute("recentLostItems", itemService.getRecentLostItems(6));
        model.addAttribute("myItems", itemService.getItemsByUser(email));
        model.addAttribute("myClaims", claimService.getClaimsByUser(email));
        return "student/dashboard";
    }
}