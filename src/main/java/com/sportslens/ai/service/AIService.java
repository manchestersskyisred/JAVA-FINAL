package com.sportslens.ai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportslens.ai.dto.AiAnalysisResult;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class AIService {

    private static final Logger logger = LoggerFactory.getLogger(AIService.class);
    private static final String DEEPSEEK_API_URL = "https://api.deepseek.com/chat/completions";

    @Value("${app.deepseek.api-key}")
    private String deepseekApiKey;

    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiAnalysisResult getAiAnalysisForArticle(String title, String content) {
        if (deepseekApiKey == null || deepseekApiKey.startsWith("YOUR_") || deepseekApiKey.trim().isEmpty()) {
            logger.warn("DeepSeek API key is not configured. Skipping AI analysis.");
            return null;
        }

        String truncatedContent = content.length() > 4000 ? content.substring(0, 4000) : content;

        String systemPrompt = "You are a professional sports news editor. Analyze the provided article and return a JSON object with three fields: 'translatedTitle' (Chinese translation of the title), 'chineseSummary' (a one-sentence summary in Chinese), and 'keywords' (3-5 relevant English keywords, comma-separated).";
        
        String userPrompt = String.format("Title: \\\"%s\\\"\\n\\nArticle Content: \\\"%s\\\"", escapeJson(title), escapeJson(truncatedContent));

        String jsonPayload = String.format("{\"model\": \"deepseek-chat\", \"messages\": [{\"role\": \"system\", \"content\": \"%s\"}, {\"role\": \"user\", \"content\": \"%s\"}], \"max_tokens\": 1024, \"response_format\": {\"type\": \"json_object\"}}",
            systemPrompt, userPrompt);

        RequestBody body = RequestBody.create(jsonPayload, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(DEEPSEEK_API_URL)
                .header("Authorization", "Bearer " + deepseekApiKey)
                .post(body)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "No response body";
                logger.error("Failed to call DeepSeek API: {} - {}", response.code(), errorBody);
                return null;
            }

            String responseBody = response.body().string();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            String jsonContentString = rootNode.path("choices").get(0).path("message").path("content").asText();
            
            logger.debug("Received JSON from AI: {}", jsonContentString);

            return objectMapper.readValue(jsonContentString, AiAnalysisResult.class);

        } catch (JsonProcessingException e) {
            logger.error("Failed to parse JSON response from DeepSeek API: {}", e.getMessage());
        } catch (IOException e) {
            logger.error("IOException while calling DeepSeek API", e);
        } catch (Exception e) {
            logger.error("An unexpected error occurred in AIService", e);
        }
        return null;
    }

    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\b", "\\b")
                   .replace("\f", "\\f")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}