package com.sportslens.ai.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class BrowsingHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "news_article_id", nullable = false)
    private NewsArticle newsArticle;

    private LocalDateTime viewedAt;

    public BrowsingHistory() {
    }

    public BrowsingHistory(User user, NewsArticle newsArticle) {
        this.user = user;
        this.newsArticle = newsArticle;
        this.viewedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public NewsArticle getNewsArticle() {
        return newsArticle;
    }

    public void setNewsArticle(NewsArticle newsArticle) {
        this.newsArticle = newsArticle;
    }

    public LocalDateTime getViewedAt() {
        return viewedAt;
    }

    public void setViewedAt(LocalDateTime viewedAt) {
        this.viewedAt = viewedAt;
    }
} 