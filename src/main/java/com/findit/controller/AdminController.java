package com.findit.controller;

import com.findit.entity.User;
import com.findit.service.ClaimService;
import com.findit.service.ItemService;
import com.findit.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    
    private final ClaimService claimService;
    private final ItemService itemService;
    private final UserService userService;
    
    public AdminController(ClaimService claimService, ItemService itemService, UserService userService) {
        this.claimService = claimService;
        this.itemService = itemService;
        this.userService = userService;
    }
    
    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalUsers", userService.getTotalUserCount());
        stats.put("foundItems", itemService.getFoundItemsCount());
        stats.put("lostItems", itemService.getLostItemsCount());
        stats.put("pendingClaims", claimService.getPendingClaimsCount());
        
        model.addAttribute("stats", stats);
        model.addAttribute("pendingClaims", claimService.getPendingClaims());
        model.addAttribute("recentFoundItems", itemService.getRecentFoundItems(5));
        model.addAttribute("recentLostItems", itemService.getRecentLostItems(5));
        
        return "admin/dashboard";
    }
    
    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }
    
    @GetMapping("/items")
    public String viewAllItems(@RequestParam(required = false) String type, Model model) {
        if ("FOUND".equals(type)) {
            model.addAttribute("items", itemService.getRecentFoundItems(100));
            model.addAttribute("pageTitle", "Found Items");
        } else if ("LOST".equals(type)) {
            model.addAttribute("items", itemService.getRecentLostItems(100));
            model.addAttribute("pageTitle", "Lost Items");
        } else {
            model.addAttribute("items", itemService.getRecentFoundItems(50));
            model.addAttribute("pageTitle", "All Items");
        }
        return "admin/items";
    }
    
    @GetMapping("/claims/pending")
    public String pendingClaims(Model model) {
        model.addAttribute("claims", claimService.getPendingClaims());
        return "admin/pending-claims";
    }
    
    @PostMapping("/claim/{id}/approve")
    public String approveClaim(@PathVariable Long id, Authentication authentication, 
                              RedirectAttributes redirectAttributes) {
        try {
            claimService.approveClaim(id, authentication.getName());
            redirectAttributes.addFlashAttribute("success", "Claim approved!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/claims/pending";
    }
    
    @PostMapping("/claim/{id}/reject")
    public String rejectClaim(@PathVariable Long id, @RequestParam String reason,
                             Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            claimService.rejectClaim(id, reason, authentication.getName());
            redirectAttributes.addFlashAttribute("success", "Claim rejected!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/claims/pending";
    }
    
    @PostMapping("/user/{id}/role")
    public String updateUserRole(@PathVariable Long id, @RequestParam String role,
                                RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findById(id);
            user.setRole(role);
            userService.save(user);
            redirectAttributes.addFlashAttribute("success", "User role updated to " + role);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }
    
    @PostMapping("/user/{id}/toggle")
    public String toggleUserEnabled(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findById(id);
            user.setEnabled(!user.getEnabled());
            userService.save(user);
            redirectAttributes.addFlashAttribute("success", "User " + (user.getEnabled() ? "enabled" : "disabled"));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }
}