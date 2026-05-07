package com.findit.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String location;

    @Column(name = "found_date")
    private LocalDateTime foundDate;

    private String photoUrl;

    // Fixed: default changed from "PENDING" to "ACTIVE" to match ItemService
    @Column(nullable = false)
    private String status = "ACTIVE";

    @Column(name = "is_lost")
    private boolean lost = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posted_by", nullable = false)
    private User postedBy;

    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
    private List<Claim> claims = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        foundDate = LocalDateTime.now();
        if (status == null) status = "ACTIVE";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDateTime getFoundDate() { return foundDate; }
    public void setFoundDate(LocalDateTime foundDate) { this.foundDate = foundDate; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isLost() { return lost; }
    public void setLost(boolean lost) { this.lost = lost; }

    public User getPostedBy() { return postedBy; }
    public void setPostedBy(User postedBy) { this.postedBy = postedBy; }

    public List<Claim> getClaims() { return claims; }
    public void setClaims(List<Claim> claims) { this.claims = claims; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getFormattedCreatedAt() {
        if (createdAt == null) return "";
        return createdAt.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"));
    }

    public String getFormattedFoundDate() {
        if (foundDate == null) return "";
        return foundDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));
    }

    // Fixed: added ACTIVE case + more categories
    public String getStatusBadgeClass() {
        switch (status) {
            case "ACTIVE":   return "bg-primary text-white";
            case "CLAIMED":  return "bg-info text-white";
            case "RESOLVED": return "bg-success text-white";
            default:         return "bg-secondary text-white";
        }
    }

    // Fixed: added more common campus item categories
    public String getCategoryIcon() {
        switch (category) {
            case "PHONE":        return "📱";
            case "LAPTOP":       return "💻";
            case "ID_CARD":      return "🪪";
            case "WATER_BOTTLE": return "💧";
            case "KEYS":         return "🔑";
            case "WALLET":       return "👛";
            case "BOOK":         return "📚";
            case "BAG":          return "🎒";
            case "GLASSES":      return "👓";
            case "CHARGER":      return "🔌";
            default:             return "📦";
        }
    }
}