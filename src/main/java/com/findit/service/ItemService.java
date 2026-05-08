package com.findit.service;

import java.util.Objects;

import com.findit.entity.Item;
import com.findit.entity.User;
import com.findit.repository.ItemRepository;
import com.findit.exception.ResourceNotFoundException;
import com.findit.exception.UnauthorizedAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ItemService {
    
    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);
    
    private final ItemRepository itemRepository;
    private final UserService userService;
    
    public ItemService(ItemRepository itemRepository, UserService userService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
    }
    
    @Transactional
    public Item reportLostItem(String title, String category, String location, 
                               String description, LocalDateTime dateLost, String userEmail) {
        User reporter = userService.findByEmail(userEmail);
        
        Item item = new Item();
        item.setTitle(title);
        item.setCategory(category);
        item.setLocation(location);
        item.setDescription(description);
        item.setType("LOST");
        item.setDateLost(dateLost);
        item.setReporter(reporter);
        item.setStatus("ACTIVE");
        
        Item savedItem = itemRepository.save(item);
        logger.info("New lost item reported: {} by {}", title, userEmail);
        return savedItem;
    }
    
    @Transactional
    public Item reportFoundItem(String title, String category, String location, 
                                String description, LocalDateTime dateFound, String userEmail) {
        User reporter = userService.findByEmail(userEmail);
        
        Item item = new Item();
        item.setTitle(title);
        item.setCategory(category);
        item.setLocation(location);
        item.setDescription(description);
        item.setType("FOUND");
        item.setDateFound(dateFound);
        item.setReporter(reporter);
        item.setStatus("ACTIVE");
        
        Item savedItem = itemRepository.save(item);
        logger.info("New found item reported: {} by {}", title, userEmail);
        return savedItem;
    }
    
    public Item findById(Long id) {
        return itemRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
    }
    
    public List<Item> getRecentFoundItems(int limit) {
        List<Item> items = itemRepository.findTop10ByTypeAndStatusOrderByCreatedAtDesc("FOUND", "ACTIVE");
        return items.stream().limit(limit).toList();
    }
    
    public List<Item> getRecentLostItems(int limit) {
        List<Item> items = itemRepository.findTop10ByTypeAndStatusOrderByCreatedAtDesc("LOST", "ACTIVE");
        return items.stream().limit(limit).toList();
    }
    
    public List<Item> getItemsByUser(String userEmail) {
        User user = userService.findByEmail(userEmail);
        return itemRepository.findByReporterOrderByCreatedAtDesc(user);
    }
    
    @Transactional
    public Item updateItem(Long itemId, String title, String category, String location, 
                          String description, String userEmail) {
        Item item = findById(itemId);
        User user = userService.findByEmail(userEmail);
        
        if (!item.getReporter().getId().equals(user.getId()) && !"ADMIN".equals(user.getRole())) {
            throw new UnauthorizedAccessException("You don't have permission to update this item");
        }
        
        if (title != null && !title.trim().isEmpty()) item.setTitle(title);
        if (category != null && !category.trim().isEmpty()) item.setCategory(category);
        if (location != null && !location.trim().isEmpty()) item.setLocation(location);
        if (description != null) item.setDescription(description);
        
        return itemRepository.save(item);
    }
    
    @Transactional
    public void deleteItem(Long itemId, String userEmail) {
        Item item = findById(itemId);
        User user = userService.findByEmail(userEmail);
        
        if (!item.getReporter().getId().equals(user.getId()) && !"ADMIN".equals(user.getRole())) {
            throw new UnauthorizedAccessException("You don't have permission to delete this item");
        }
        
        itemRepository.delete(item);
        logger.info("Item deleted: {} by {}", itemId, userEmail);
    }
    
    public Page<Item> searchItems(String query, String category, String location, 
                                  String type, Pageable pageable) {
        if (query != null && !query.isEmpty()) {
            return itemRepository.searchItems(query, category, location, type, pageable);
        } else {
            return itemRepository.findByFilters(category, location, type, pageable);
        }
    }
    
    public long getFoundItemsCount() {
        return itemRepository.countByTypeAndStatus("FOUND", "ACTIVE");
    }
    
    public long getLostItemsCount() {
        return itemRepository.countByTypeAndStatus("LOST", "ACTIVE");
    }
    
    @Transactional
    public void markAsClaimed(Long itemId) {
        Item item = findById(itemId);
        item.setStatus("CLAIMED");
        itemRepository.save(item);
        logger.info("Item marked as claimed: {}", itemId);
    }
    public Page<Item> getAllActiveItems(Pageable pageable) {
        return itemRepository.findAllByStatus("ACTIVE", pageable);
    }

}