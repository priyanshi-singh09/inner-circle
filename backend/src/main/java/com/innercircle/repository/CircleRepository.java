package com.innercircle.repository;

import com.innercircle.entity.Circle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface CircleRepository extends JpaRepository<Circle, UUID> {
    Optional<Circle> findByName(String name);
}
