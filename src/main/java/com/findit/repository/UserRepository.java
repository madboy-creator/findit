package com.findit.repository;

import com.findit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByStudentId(String studentId);
    Optional<User> findByStudentId(String studentId);
    long countByRole(String role);
    List<User> findByNameContainingOrEmailContaining(String name, String email);

}