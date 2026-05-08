package com.findit.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "items")
public class Item {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String category;
    private String location;
    private String description;
    private String type;
    private String status = "ACTIVE";
    private LocalDateTime dateLost;
    private LocalDateTime dateFound;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @ManyToOne
    @JoinColumn(name = "reporter_id")
    private User reporter;
    
    public Item() {}
    
    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getLocation() { return location; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public String getStatus() { return status; }
    public LocalDateTime getDateLost() { return dateLost; }
    public LocalDateTime getDateFound() { return dateFound; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public User getReporter() { return reporter; }
    
    // Setters
    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setCategory(String category) { this.category = category; }
    public void setLocation(String location) { this.location = location; }
    public void setDescription(String description) { this.description = description; }
    public void setType(String type) { this.type = type; }
    public void setStatus(String status) { this.status = status; }
    public void setDateLost(LocalDateTime dateLost) { this.dateLost = dateLost; }
    public void setDateFound(LocalDateTime dateFound) { this.dateFound = dateFound; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setReporter(User reporter) { this.reporter = reporter; }
}