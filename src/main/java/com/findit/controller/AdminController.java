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
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        logger.debug("Admin dashboard accessed");
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
    public String manageUsers(@RequestParam(required = false) String search, Model model) {
        List<User> users;
        if (search != null && !search.trim().isEmpty()) {
            users = userService.searchUsers(search);
            model.addAttribute("searchQuery", search);
        } else {
            users = userService.getAllUsers();
        }
        model.addAttribute("users", users);
        return "admin/users";
    }
    
    @PostMapping("/user/{id}/role")
    public String updateUserRole(@PathVariable Long id, @RequestParam String role) {
        User user = userService.findById(Objects.requireNonNull(id));
        user.setRole(role);
        userService.save(user);
        return "redirect:/admin/users";
    }
    
    @PostMapping("/user/{id}/toggle")
    public String toggleUserEnabled(@PathVariable Long id) {
        User user = userService.findById(Objects.requireNonNull(id));
        user.setEnabled(!user.getEnabled());
        userService.save(user);
        return "redirect:/admin/users";
    }
    
    @DeleteMapping("/user/{id}/delete")
    @ResponseBody
    public Map<String, String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(Objects.requireNonNull(id));
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        return response;
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
}