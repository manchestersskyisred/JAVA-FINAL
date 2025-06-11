package com.sportslens.ai.repository;

import com.sportslens.ai.domain.NewsArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long> {
    Optional<NewsArticle> findByUrl(String url);
    List<NewsArticle> findByTitleContainingIgnoreCaseOrTitleCnContainingIgnoreCaseOrKeywordsAiContainingIgnoreCaseOrderByPublishDateDesc(String title, String titleCn, String keywordsAi);
}