package com.findit.repository;

import com.findit.entity.Claim;
import com.findit.entity.Item;
import com.findit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {
    List<Claim> findByClaimant(User claimant);
    List<Claim> findByItem(Item item);
    List<Claim> findByStatus(String status);
    List<Claim> findByStatusOrderByCreatedAtAsc(String status);
    // Fixed: added missing method used in ClaimService.approveClaim()
    List<Claim> findByItemAndStatusAndIdNot(Item item, String status, Long id);
    boolean existsByItemAndClaimant(Item item, User claimant);
    long countByStatus(String status);
}