package com.findit.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String password;
    private String studentId;
    private String phone;
    private String role = "STUDENT";
    private Boolean enabled = true;
    private Boolean accountNonLocked = true;
    private Integer failedAttempts = 0;
    private LocalDateTime lockTime;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "reporter")
    private List<Item> items = new ArrayList<>();
    
    @OneToMany(mappedBy = "claimant")
    private List<Claim> claims = new ArrayList<>();
    
    public User() {}
    
    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getStudentId() { return studentId; }
    public String getPhone() { return phone; }
    public String getRole() { return role; }
    public Boolean getEnabled() { return enabled; }
    public Boolean getAccountNonLocked() { return accountNonLocked; }
    public Integer getFailedAttempts() { return failedAttempts; }
    public LocalDateTime getLockTime() { return lockTime; }
    public LocalDateTime getLastLogin() { return lastLogin; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public List<Item> getItems() { return items; }
    public List<Claim> getClaims() { return claims; }
    
    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setRole(String role) { this.role = role; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    public void setAccountNonLocked(Boolean accountNonLocked) { this.accountNonLocked = accountNonLocked; }
    public void setFailedAttempts(Integer failedAttempts) { this.failedAttempts = failedAttempts; }
    public void setLockTime(LocalDateTime lockTime) { this.lockTime = lockTime; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setItems(List<Item> items) { this.items = items; }
    public void setClaims(List<Claim> claims) { this.claims = claims; }
}