package com.findit.repository;

import com.findit.entity.Item;
import com.findit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByPostedBy(User user);
    
    // These two methods already exist and work
    List<Item> findByLostFalseOrderByCreatedAtDesc();
    List<Item> findByLostTrueOrderByCreatedAtDesc();
    
    List<Item> findByCategoryAndLost(String category, boolean lost);
    
    @Query("SELECT i FROM Item i WHERE i.lost = :isLost AND " +
           "(LOWER(i.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(i.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(i.category) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(i.location) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Item> searchItems(@Param("keyword") String keyword, @Param("isLost") boolean isLost);
    
    long countByLost(boolean isLost);
    long countByStatusAndLost(String status, boolean lost);
}