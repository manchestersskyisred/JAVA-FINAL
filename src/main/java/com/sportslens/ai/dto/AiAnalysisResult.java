package com.sportslens.ai.dto;

public class AiAnalysisResult {

    private String translatedTitle;
    private String chineseSummary;
    private String keywords;

    // Getters and Setters
    public String getTranslatedTitle() {
        return translatedTitle;
    }

    public void setTranslatedTitle(String translatedTitle) {
        this.translatedTitle = translatedTitle;
    }

    public String getChineseSummary() {
        return chineseSummary;
    }

    public void setChineseSummary(String chineseSummary) {
        this.chineseSummary = chineseSummary;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
} 