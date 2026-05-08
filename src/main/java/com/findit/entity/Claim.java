package com.findit.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "claims")
public class Claim {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String answers;
    private String status = "PENDING";
    private String rejectionReason;
    private String reviewedBy;
    private LocalDateTime reviewedAt;
    private LocalDateTime submittedAt;
    private LocalDateTime updatedAt;
    
    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;
    
    @ManyToOne
    @JoinColumn(name = "claimant_id")
    private User claimant;
    
    public Claim() {}
    
    // Getters
    public Long getId() { return id; }
    public String getAnswers() { return answers; }
    public String getStatus() { return status; }
    public String getRejectionReason() { return rejectionReason; }
    public String getReviewedBy() { return reviewedBy; }
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Item getItem() { return item; }
    public User getClaimant() { return claimant; }
    
    // Setters
    public void setId(Long id) { this.id = id; }
    public void setAnswers(String answers) { this.answers = answers; }
    public void setStatus(String status) { this.status = status; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setItem(Item item) { this.item = item; }
    public void setClaimant(User claimant) { this.claimant = claimant; }
}