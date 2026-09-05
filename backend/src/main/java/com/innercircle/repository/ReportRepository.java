package com.innercircle.repository;

import com.innercircle.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID> {
    Page<Report> findByReporter_IdOrderByCreatedAtDesc(UUID reporterId, Pageable pageable);
}
