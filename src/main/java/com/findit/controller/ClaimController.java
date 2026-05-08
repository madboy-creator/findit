package com.findit.controller;

import com.findit.entity.Item;
import com.findit.service.ClaimService;
import com.findit.service.ItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ClaimController {
    
    private static final Logger logger = LoggerFactory.getLogger(ClaimController.class);
    private final ClaimService claimService;
    private final ItemService itemService;
    
    public ClaimController(ClaimService claimService, ItemService itemService) {
        this.claimService = claimService;
        this.itemService = itemService;
    }
    
    @GetMapping("/my-claims")
    public String myClaims(Model model, Authentication authentication) {
        model.addAttribute("claims", claimService.getClaimsByUser(authentication.getName()));
        logger.debug("User {} viewed their claims", authentication.getName());
        return "claims/my-claims";
    }
    
    @GetMapping("/claim/submit/{id}")
    public String showClaimForm(@PathVariable Long id, Model model) {
        try {
            Item item = itemService.findById(id);
            model.addAttribute("item", item);
            return "claims/submit-claim";
        } catch (Exception e) {
            logger.error("Failed to load claim form for item {}: {}", id, e.getMessage());
            return "redirect:/item/" + id;
        }
    }
    
    @PostMapping("/claim/submit/{id}")
    public String submitClaim(@PathVariable Long id,
                             @RequestParam String answers,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        try {
            claimService.submitClaim(id, answers, authentication.getName());
            redirectAttributes.addFlashAttribute("success", "Claim submitted successfully! Awaiting admin review.");
            logger.info("Claim submitted for item {} by user {}", id, authentication.getName());
            return "redirect:/my-claims";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to submit claim: " + e.getMessage());
            logger.error("Claim submission failed for item {}: {}", id, e.getMessage());
            return "redirect:/item/" + id;
        }
    }
}