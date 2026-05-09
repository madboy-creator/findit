package com.findit.controller;

import com.findit.entity.Item;
import com.findit.service.ItemService;
import com.findit.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;

@Controller
public class ItemController {
    
    private static final Logger log = LoggerFactory.getLogger(ItemController.class);
    
    private final ItemService itemService;
    private final UserService userService; // Reserved for future user operations
    
    public ItemController(ItemService itemService, UserService userService) {
        this.itemService = itemService;
        this.userService = userService;
    }
    
    @GetMapping("/report-lost")
    public String showReportLostForm(Model model) {
        model.addAttribute("item", new ItemDto());
        return "items/report-lost";
    }
    
    @PostMapping("/report-lost")
    public String reportLostItem(@Valid @ModelAttribute("item") ItemDto itemDto,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        try {
            itemService.reportLostItem(
                itemDto.getTitle(),
                itemDto.getCategory(),
                itemDto.getLocation(),
                itemDto.getDescription(),
                LocalDateTime.parse(itemDto.getDateLost() + "T00:00:00"),
                authentication.getName()
            );
            redirectAttributes.addFlashAttribute("success", "Lost item reported successfully!");
            return "redirect:/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to report item: " + e.getMessage());
            return "redirect:/report-lost";
        }
    }
    
    @GetMapping("/report-found")
    public String showReportFoundForm(Model model) {
        model.addAttribute("item", new ItemDto());
        return "items/report-found";
    }
    
    @PostMapping("/report-found")
    public String reportFoundItem(@Valid @ModelAttribute("item") ItemDto itemDto,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        try {
            itemService.reportFoundItem(
                itemDto.getTitle(),
                itemDto.getCategory(),
                itemDto.getLocation(),
                itemDto.getDescription(),
                LocalDateTime.parse(itemDto.getDateFound() + "T00:00:00"),
                authentication.getName()
            );
            redirectAttributes.addFlashAttribute("success", "Found item reported successfully!");
            return "redirect:/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to report item: " + e.getMessage());
            return "redirect:/report-found";
        }
    }
    
    @GetMapping("/my-items")
    public String myItems(Model model, Authentication authentication) {
        model.addAttribute("items", itemService.getItemsByUser(authentication.getName()));
        return "items/my-items";
    }
    
    @GetMapping("/item/{id}")
    public String viewItem(@PathVariable Long id, Model model) {
        model.addAttribute("item", itemService.findById(id));
        return "items/item-detail";
    }
    
    @GetMapping("/search")
    public String searchItems(@RequestParam(required = false, defaultValue = "") String query,
                             @RequestParam(required = false, defaultValue = "") String category,
                             @RequestParam(required = false, defaultValue = "") String location,
                             @RequestParam(required = false, defaultValue = "") String type,
                             @RequestParam(defaultValue = "0") int page,
                             Model model) {
        Pageable pageable = PageRequest.of(page, 12);
        Page<Item> items = itemService.searchItems(query, category, location, type, pageable);
        model.addAttribute("items", items);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", items.getTotalPages());
        log.debug("Search performed with query: {}", query);
        return "items/search";
    }
    
    static class ItemDto {
        private String title;
        private String category;
        private String location;
        private String description;
        private String dateLost;
        private String dateFound;
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getDateLost() { return dateLost; }
        public void setDateLost(String dateLost) { this.dateLost = dateLost; }
        public String getDateFound() { return dateFound; }
        public void setDateFound(String dateFound) { this.dateFound = dateFound; }
    }
}