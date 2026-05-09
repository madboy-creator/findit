package com.findit.service;

import com.findit.entity.Claim;
import com.findit.entity.Item;
import com.findit.entity.User;
import com.findit.repository.ClaimRepository;
import com.findit.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Service
@Transactional
public class ClaimService {
    
    private static final Logger logger = LoggerFactory.getLogger(ClaimService.class);
    
    private final ClaimRepository claimRepository;
    private final ItemService itemService;
    private final UserService userService;
    
    public ClaimService(ClaimRepository claimRepository, ItemService itemService, UserService userService) {
        this.claimRepository = claimRepository;
        this.itemService = itemService;
        this.userService = userService;
    }
    
    @Transactional
    public Claim submitClaim(Long itemId, String answers, String userEmail) {
        Item item = itemService.findById(itemId);
        User claimant = userService.findByEmail(userEmail);
        
        if ("CLAIMED".equals(item.getStatus())) {
            throw new IllegalStateException("This item has already been claimed");
        }
        
        if (claimRepository.existsByItemAndClaimant(item, claimant)) {
            throw new IllegalStateException("You have already submitted a claim for this item");
        }
        
        Claim claim = new Claim();
        claim.setItem(item);
        claim.setClaimant(claimant);
        claim.setAnswers(answers);
        claim.setStatus("PENDING");
        claim.setSubmittedAt(LocalDateTime.now());
        
        Claim savedClaim = claimRepository.save(claim);
        logger.info("New claim submitted for item {} by {}", itemId, userEmail);
        return savedClaim;
    }
    
    public List<Claim> getClaimsByUser(String userEmail) {
        User user = userService.findByEmail(userEmail);
        List<Claim> claims = claimRepository.findByClaimantOrderBySubmittedAtDesc(user);
        return claims == null ? new ArrayList<>() : claims;
    }
    
    public List<Claim> getPendingClaims() {
        return claimRepository.findByStatusOrderBySubmittedAtAsc("PENDING");
    }
    
    public Claim findById(Long id) {
        return claimRepository.findById(java.util.Objects.requireNonNull(id))
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + id));
    }
    
    @Transactional
    public void approveClaim(Long claimId, String adminEmail) {
        Claim claim = findById(claimId);
        claim.setStatus("APPROVED");
        claim.setReviewedBy(adminEmail);
        claim.setReviewedAt(LocalDateTime.now());
        claimRepository.save(claim);
        itemService.markAsClaimed(claim.getItem().getId());
        logger.info("Claim {} approved by admin {}", claimId, adminEmail);
    }
    
    @Transactional
    public void rejectClaim(Long claimId, String reason, String adminEmail) {
        Claim claim = findById(claimId);
        claim.setStatus("REJECTED");
        claim.setRejectionReason(reason);
        claim.setReviewedBy(adminEmail);
        claim.setReviewedAt(LocalDateTime.now());
        claimRepository.save(claim);
        logger.info("Claim {} rejected by admin {}: {}", claimId, adminEmail, reason);
    }
    
    public long getPendingClaimsCount() {
        return claimRepository.countByStatus("PENDING");
    }
    
    public List<Claim> getAllClaims() {
        return claimRepository.findAllByOrderBySubmittedAtDesc();
    }
}