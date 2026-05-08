package com.findit.repository;

import com.findit.entity.Item;
import com.findit.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    
    // Find recent items by type
    List<Item> findTop10ByTypeAndStatusOrderByCreatedAtDesc(String type, String status);
    
    // Find items by reporter
    List<Item> findByReporterOrderByCreatedAtDesc(User reporter);
    
    // Count items by type and status
    long countByTypeAndStatus(String type, String status);
    
    // Search items with filters
    @Query("SELECT i FROM Item i WHERE " +
           "(:query IS NULL OR LOWER(i.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(i.description) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:category IS NULL OR i.category = :category) AND " +
           "(:location IS NULL OR LOWER(i.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:type IS NULL OR i.type = :type) AND " +
           "i.status = 'ACTIVE' " +
           "ORDER BY i.createdAt DESC")
    Page<Item> searchItems(@Param("query") String query,
                          @Param("category") String category,
                          @Param("location") String location,
                          @Param("type") String type,
                          Pageable pageable);
    
    // Find by filters without search query
    @Query("SELECT i FROM Item i WHERE " +
           "(:category IS NULL OR i.category = :category) AND " +
           "(:location IS NULL OR LOWER(i.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:type IS NULL OR i.type = :type) AND " +
           "i.status = 'ACTIVE' " +
           "ORDER BY i.createdAt DESC")
    Page<Item> findByFilters(@Param("category") String category,
                            @Param("location") String location,
                            @Param("type") String type,
                            Pageable pageable);
    
    // Find items by location (for matching)
    List<Item> findByLocationContainingIgnoreCaseAndStatus(String location, String status);
    
    // Find items by category
    List<Item> findByCategoryAndStatus(String category, String status);
    
    // Find expired items (older than 90 days)
    @Query("SELECT i FROM Item i WHERE i.createdAt < :date AND i.status = 'ACTIVE'")
    List<Item> findExpiredItems(@Param("date") java.time.LocalDateTime date);
    Page<Item> findAllByStatus(String status, Pageable pageable);

}