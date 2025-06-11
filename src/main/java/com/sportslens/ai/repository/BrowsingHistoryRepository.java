package com.sportslens.ai.repository;

import com.sportslens.ai.domain.BrowsingHistory;
import com.sportslens.ai.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BrowsingHistoryRepository extends JpaRepository<BrowsingHistory, Long> {
    List<BrowsingHistory> findByUserOrderByViewedAtDesc(User user);
}