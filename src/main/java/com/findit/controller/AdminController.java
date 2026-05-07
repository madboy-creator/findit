package com.findit.controller;

import com.findit.entity.User;
import com.findit.repository.UserRepository;
import com.findit.service.ClaimService;
import com.findit.service.ItemService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    private final ClaimService claimService;
    private final ItemService itemService;
    private final UserRepository userRepository;
    
    public AdminController(ClaimService claimService, ItemService itemService, UserRepository userRepository) {
        this.claimService = claimService;
        this.itemService = itemService;
        this.userRepository = userRepository;
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("foundItems", itemService.getTotalFoundItems());
        stats.put("lostItems", itemService.getTotalLostItems());
        stats.put("pendingClaims", claimService.getPendingClaimsCount());
        stats.put("approvedClaims", claimService.getApprovedClaimsCount());
        stats.put("rejectedClaims", claimService.getRejectedClaimsCount());
        
        model.addAttribute("stats", stats);
        model.addAttribute("pendingClaims", claimService.getPendingClaims());
        model.addAttribute("recentFoundItems", itemService.getRecentFoundItems(5));
        model.addAttribute("recentLostItems", itemService.getRecentLostItems(5));
        return "admin/dashboard";
    }
    
    @GetMapping("/claims")
    public String pendingClaims(Model model) {
        model.addAttribute("claims", claimService.getPendingClaims());
        return "admin/pending-claims";
    }
    
    @PostMapping("/claim/{claimId}/approve")
    public String approveClaim(@PathVariable Long claimId, @AuthenticationPrincipal User admin,
                               RedirectAttributes ra) {
        claimService.approveClaim(claimId, admin);
        ra.addFlashAttribute("success", "Claim approved!");
        return "redirect:/admin/dashboard";
    }
    
    @PostMapping("/claim/{claimId}/reject")
    public String rejectClaim(@PathVariable Long claimId, @RequestParam String reason,
                              @AuthenticationPrincipal User admin, RedirectAttributes ra) {
        claimService.rejectClaim(claimId, admin, reason);
        ra.addFlashAttribute("success", "Claim rejected.");
        return "redirect:/admin/dashboard";
    }
}