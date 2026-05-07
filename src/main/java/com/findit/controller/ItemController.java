package com.findit.controller;

import com.findit.entity.Item;
import com.findit.entity.User;
import com.findit.repository.ClaimRepository;
import com.findit.service.ItemService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ItemController {

    private final ItemService itemService;
    private final ClaimRepository claimRepository;

    public ItemController(ItemService itemService, ClaimRepository claimRepository) {
        this.itemService = itemService;
        this.claimRepository = claimRepository;
    }

    // Fixed: was returning "dashboard", template is at "items/dashboard"
    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("userName", user != null ? user.getName() : "Guest");
        model.addAttribute("recentFoundItems", itemService.getRecentFoundItems(6));
        model.addAttribute("recentLostItems", itemService.getRecentLostItems(6));
        
        // Student-specific stats
        List<Item> userItems = itemService.getUserItems(user);
        long foundCount = userItems.stream().filter(i -> !i.isLost()).count();
        long lostCount = userItems.stream().filter(Item::isLost).count();
        long claimsCount = claimRepository.findByClaimant(user).size();
        
        model.addAttribute("myFoundItemsCount", foundCount);
        model.addAttribute("myLostItemsCount", lostCount);
        model.addAttribute("myClaimsCount", claimsCount);
        
        return "student/dashboard";
    }

    @GetMapping("/report-found")
    public String showReportFoundForm() {
        return "items/report-found";
    }

    @PostMapping("/report-found")
    public String submitReportFound(@RequestParam String title,
                                    @RequestParam String description,
                                    @RequestParam String category,
                                    @RequestParam String location,
                                    @RequestParam(required = false) MultipartFile photo,
                                    @AuthenticationPrincipal User user,
                                    RedirectAttributes redirectAttributes) {
        try {
            itemService.reportFound(title, description, category, location, photo, user);
            redirectAttributes.addFlashAttribute("success", "Item reported successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/report-lost")
    public String showReportLostForm() {
        return "items/report-lost";
    }

    @PostMapping("/report-lost")
    public String submitReportLost(@RequestParam String title,
                                   @RequestParam String description,
                                   @RequestParam String category,
                                   @RequestParam String location,
                                   @RequestParam(required = false) MultipartFile photo,
                                   @AuthenticationPrincipal User user,
                                   RedirectAttributes redirectAttributes) {
        try {
            itemService.reportLost(title, description, category, location, photo, user);
            redirectAttributes.addFlashAttribute("success", "Lost item reported successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/item/{id}")
    public String viewItemDetail(@PathVariable Long id, Model model,
                                 @AuthenticationPrincipal User user) {
        Item item = itemService.getItemById(id);
        model.addAttribute("item", item);
        if (user != null && "ADMIN".equals(user.getRole())) {
            model.addAttribute("claims", claimRepository.findByItem(item));
        }
        return "items/item-detail";
    }

    @GetMapping("/search")
    public String searchItems(@RequestParam(required = false) String keyword,
                              @RequestParam(required = false) String category,
                              @RequestParam(defaultValue = "false") boolean lost,
                              Model model) {
        List<Item> items;

        if (keyword != null && !keyword.trim().isEmpty()) {
            items = itemService.searchItems(keyword, lost);
            model.addAttribute("keyword", keyword);
        } else if (category != null && !category.isEmpty()) {
            items = itemService.getItemsByCategory(category, lost);
            model.addAttribute("category", category);
        } else {
            items = lost ? itemService.getAllLostItems() : itemService.getAllFoundItems();
        }

        model.addAttribute("items", items);
        model.addAttribute("isLost", lost);
        return "items/search";
    }

    @GetMapping("/my-items")
    public String viewMyItems(@AuthenticationPrincipal User user, Model model) {
        List<Item> userItems = itemService.getUserItems(user);

        List<Item> foundItems = userItems.stream()
                .filter(item -> !item.isLost())
                .collect(Collectors.toList());
        List<Item> lostItems = userItems.stream()
                .filter(Item::isLost)
                .collect(Collectors.toList());

        for (Item item : foundItems) {
            item.setClaims(claimRepository.findByItem(item));
        }

        model.addAttribute("foundItems", foundItems);
        model.addAttribute("lostItems", lostItems);
        return "items/my-items";
    }
}