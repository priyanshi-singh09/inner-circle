package com.innercircle.repository;

import com.innercircle.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface BlockRepository extends JpaRepository<User, UUID> {
}
