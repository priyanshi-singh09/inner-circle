package com.innercircle.repository;

import com.innercircle.entity.Circle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CircleRepository extends JpaRepository<Circle, UUID> {
    Optional<Circle> findByName(String name);

    @Query("SELECT c FROM Circle c WHERE c.minAge <= :age AND (c.maxAge IS NULL OR c.maxAge >= :age)")
    Optional<Circle> findByAge(@Param("age") int age);
}
