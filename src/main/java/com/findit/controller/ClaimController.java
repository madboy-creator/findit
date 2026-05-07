package com.findit.controller;

import com.findit.entity.Item;
import com.findit.entity.User;
import com.findit.service.ClaimService;
import com.findit.service.ItemService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ClaimController {
    
    private final ClaimService claimService;
    private final ItemService itemService;
    
    public ClaimController(ClaimService claimService, ItemService itemService) {
        this.claimService = claimService;
        this.itemService = itemService;
    }
    
    @GetMapping("/claim/{itemId}")
    public String submitClaimPage(@PathVariable Long itemId, Model model) {
        Item item = itemService.getItemById(itemId);
        if ("CLAIMED".equals(item.getStatus()) || "RESOLVED".equals(item.getStatus())) {
            return "redirect:/item/" + itemId + "?error=Item already claimed";
        }
        model.addAttribute("item", item);
        return "claims/submit-claim";
    }
    
    @PostMapping("/claim/{itemId}")
    public String submitClaim(@PathVariable Long itemId, @RequestParam String answers,
                              @AuthenticationPrincipal User claimant, RedirectAttributes ra) {
        try {
            Item item = itemService.getItemById(itemId);
            claimService.submitClaim(item, claimant, answers);
            ra.addFlashAttribute("success", "Claim submitted! Admin will review.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard";
    }
    
    @GetMapping("/my-claims")
    public String myClaims(@AuthenticationPrincipal User user, Model model) {
        List<com.findit.entity.Claim> allClaims = claimService.getUserClaims(user);
        
        List<com.findit.entity.Claim> pendingClaims = allClaims.stream()
                .filter(c -> "PENDING".equals(c.getStatus()))
                .collect(Collectors.toList());
        List<com.findit.entity.Claim> approvedClaims = allClaims.stream()
                .filter(c -> "APPROVED".equals(c.getStatus()))
                .collect(Collectors.toList());
        List<com.findit.entity.Claim> rejectedClaims = allClaims.stream()
                .filter(c -> "REJECTED".equals(c.getStatus()))
                .collect(Collectors.toList());
        
        model.addAttribute("pendingClaims", pendingClaims);
        model.addAttribute("approvedClaims", approvedClaims);
        model.addAttribute("rejectedClaims", rejectedClaims);
        return "claims/my-claims";
    }
}