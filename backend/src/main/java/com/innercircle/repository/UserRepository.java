package com.innercircle.repository;

import com.innercircle.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);
    boolean existsByHandle(String handle);
    Optional<User> findByEmail(String email);
    Page<User> findByStatusOrderByHandleAsc(String status, Pageable pageable);
    Page<User> findByHandleContainingIgnoreCaseAndStatusOrderByHandleAsc(String handle, String status, Pageable pageable);
}
