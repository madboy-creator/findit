package com.findit.repository;

import com.findit.entity.Claim;
import com.findit.entity.Item;
import com.findit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {
    
    List<Claim> findByClaimantOrderBySubmittedAtDesc(User claimant);
    
    List<Claim> findByStatusOrderBySubmittedAtAsc(String status);
    
    @Query("SELECT c FROM Claim c ORDER BY c.submittedAt DESC")
    List<Claim> findAllByOrderBySubmittedAtDesc();
    
    boolean existsByItemAndClaimant(Item item, User claimant);
    
    long countByStatus(String status);
    
    List<Claim> findByItem(Item item);
    
    @Query("SELECT c FROM Claim c WHERE c.status = :status AND c.item.type = :itemType")
    List<Claim> findByStatusAndItemType(@Param("status") String status, @Param("itemType") String itemType);
    
    @Query("SELECT DISTINCT c FROM Claim c LEFT JOIN FETCH c.item LEFT JOIN FETCH c.claimant WHERE c.status = 'PENDING' ORDER BY c.submittedAt ASC")
    List<Claim> findPendingClaimsWithDetails();
}