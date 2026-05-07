package com.findit.service;

import com.findit.entity.Claim;
import com.findit.entity.Item;
import com.findit.entity.User;
import com.findit.repository.ClaimRepository;
import com.findit.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final ItemRepository itemRepository;

    public ClaimService(ClaimRepository claimRepository, ItemRepository itemRepository) {
        this.claimRepository = claimRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional
    public Claim submitClaim(Item item, User claimant, String answers) {
        if (claimRepository.existsByItemAndClaimant(item, claimant)) {
            throw new RuntimeException("You have already submitted a claim for this item");
        }

        Claim claim = new Claim();
        claim.setItem(item);
        claim.setClaimant(claimant);
        claim.setAnswers(answers);
        claim.setStatus("PENDING");

        return claimRepository.save(claim);
    }

    @Transactional
    public Claim approveClaim(Long claimId, User admin) {
        Claim claim = claimRepository.findById(Objects.requireNonNull(claimId, "claimId must not be null"))
                .orElseThrow(() -> new RuntimeException("Claim not found with id: " + claimId));

        claim.setStatus("APPROVED");
        claim.setReviewedBy(admin);
        claim.setReviewedAt(LocalDateTime.now());

        Item item = claim.getItem();
        if (item != null) {
            item.setStatus("CLAIMED");
            itemRepository.save(item);
        }

        return claimRepository.save(claim);
    }

    @Transactional
    public Claim rejectClaim(Long claimId, User admin, String reason) {
        Claim claim = claimRepository.findById(Objects.requireNonNull(claimId, "claimId must not be null"))
                .orElseThrow(() -> new RuntimeException("Claim not found with id: " + claimId));

        claim.setStatus("REJECTED");
        claim.setReviewedBy(admin);
        claim.setReviewedAt(LocalDateTime.now());
        claim.setRejectionReason(reason);

        return claimRepository.save(claim);
    }

    public List<Claim> getPendingClaims() {
        return claimRepository.findByStatusOrderByCreatedAtAsc("PENDING");
    }

    public List<Claim> getUserClaims(User user) {
        return claimRepository.findByClaimant(user);
    }

    public Claim getClaimById(Long id) {
        return claimRepository.findById(Objects.requireNonNull(id, "id must not be null"))
                .orElseThrow(() -> new RuntimeException("Claim not found with id: " + id));
    }

    public long getPendingClaimsCount() {
        return claimRepository.countByStatus("PENDING");
    }

    public long getApprovedClaimsCount() {
        return claimRepository.countByStatus("APPROVED");
    }

    public long getRejectedClaimsCount() {
        return claimRepository.countByStatus("REJECTED");
    }
}