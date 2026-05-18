package com.datacleaner.repository;

import com.datacleaner.entity.UploadHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadHistoryRepository
        extends JpaRepository<UploadHistory, Long> {
}