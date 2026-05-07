package com.findit.service;

import com.findit.entity.Item;
import com.findit.entity.User;
import com.findit.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.Objects;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Transactional
    public Item reportFound(String title, String description, String category,
                            String location, MultipartFile photo, User postedBy) {
        Item item = new Item();
        item.setTitle(title);
        item.setDescription(description);
        item.setCategory(category);
        item.setLocation(location);
        item.setLost(false);
        item.setPostedBy(postedBy);
        item.setStatus("ACTIVE");

        if (photo != null && !photo.isEmpty()) {
            item.setPhotoUrl(savePhoto(photo));
        }
        return itemRepository.save(item);
    }

    @Transactional
    public Item reportLost(String title, String description, String category,
                           String location, MultipartFile photo, User postedBy) {
        Item item = new Item();
        item.setTitle(title);
        item.setDescription(description);
        item.setCategory(category);
        item.setLocation(location);
        item.setLost(true);
        item.setPostedBy(postedBy);
        item.setStatus("ACTIVE");

        if (photo != null && !photo.isEmpty()) {
            item.setPhotoUrl(savePhoto(photo));
        }
        return itemRepository.save(item);
    }

    // Use the existing methods that work
    public List<Item> getAllFoundItems() {
        return itemRepository.findByLostFalseOrderByCreatedAtDesc();
    }

    public List<Item> getAllLostItems() {
        return itemRepository.findByLostTrueOrderByCreatedAtDesc();
    }

    public List<Item> getRecentFoundItems(int limit) {
        List<Item> all = itemRepository.findByLostFalseOrderByCreatedAtDesc();
        return all.size() > limit ? all.subList(0, limit) : all;
    }

    public List<Item> getRecentLostItems(int limit) {
        List<Item> all = itemRepository.findByLostTrueOrderByCreatedAtDesc();
        return all.size() > limit ? all.subList(0, limit) : all;
    }

    public List<Item> searchItems(String keyword, boolean isLost) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return isLost ? getAllLostItems() : getAllFoundItems();
        }
        return itemRepository.searchItems(keyword.trim(), isLost);
    }

    public List<Item> getItemsByCategory(String category, boolean isLost) {
        if (category == null || category.isEmpty()) {
            return isLost ? getAllLostItems() : getAllFoundItems();
        }
        return itemRepository.findByCategoryAndLost(category, isLost);
    }

    public Item getItemById(Long id) {
        return itemRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
    }

    public List<Item> getUserItems(User user) {
        return itemRepository.findByPostedBy(user);
    }

    public long getTotalFoundItems() {
        return itemRepository.countByLost(false);
    }

    public long getTotalLostItems() {
        return itemRepository.countByLost(true);
    }

    public long getPendingFoundItems() {
        return itemRepository.countByStatusAndLost("ACTIVE", false);
    }

    private String savePhoto(MultipartFile photo) {
        try {
            Path uploadPath = Paths.get("uploads/");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = photo.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                if (!extension.matches("\\.(jpg|jpeg|png|gif|webp)")) {
                    throw new RuntimeException("Invalid file type. Only images are allowed.");
                }
            }

            String safeFileName = UUID.randomUUID().toString() + extension;
            Path filePath = uploadPath.resolve(safeFileName);
            Files.copy(photo.getInputStream(), filePath);
            return "/uploads/" + safeFileName;

        } catch (IOException e) {
            throw new RuntimeException("Failed to save photo: " + e.getMessage());
        }
    }
}