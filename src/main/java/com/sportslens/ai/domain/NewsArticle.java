package com.sportslens.ai.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "news_articles")
public class NewsArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(unique = true, length = 512)
    private String url;

    private String source;
    private LocalDateTime publishDate;

    @Column(columnDefinition = "TEXT")
    private String rawContent;

    @Column(columnDefinition = "TEXT")
    private String titleCn;

    @Column(columnDefinition = "TEXT")
    private String summaryAi;

    @Column(columnDefinition = "TEXT")
    private String summaryAiCn;

    private String keywordsAi;
    private String categoryAi;
    private String sentimentAi;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public LocalDateTime getPublishDate() { return publishDate; }
    public void setPublishDate(LocalDateTime publishDate) { this.publishDate = publishDate; }
    public String getRawContent() { return rawContent; }
    public void setRawContent(String rawContent) { this.rawContent = rawContent; }
    public String getTitleCn() { return titleCn; }
    public void setTitleCn(String titleCn) { this.titleCn = titleCn; }
    public String getSummaryAi() { return summaryAi; }
    public void setSummaryAi(String summaryAi) { this.summaryAi = summaryAi; }
    public String getSummaryAiCn() { return summaryAiCn; }
    public void setSummaryAiCn(String summaryAiCn) { this.summaryAiCn = summaryAiCn; }
    public String getKeywordsAi() { return keywordsAi; }
    public void setKeywordsAi(String keywordsAi) { this.keywordsAi = keywordsAi; }
    public String getCategoryAi() { return categoryAi; }
    public void setCategoryAi(String categoryAi) { this.categoryAi = categoryAi; }
    public String getSentimentAi() { return sentimentAi; }
    public void setSentimentAi(String sentimentAi) { this.sentimentAi = sentimentAi; }
}