package com.hackathon.finwise.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${gemini.api.key}") // Add this to your application.properties
    private String geminiApiKey;

    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key={key}";

    public String extractGoalBasedPlan(String userInput) {
        try {
            // Build a more structured prompt
            String prompt = "Extract the following financial goal information from this text in JSON format with " +
                    "keys 'goal', 'targetAmount' (as number), and 'years' (as number). " +
                    "Text: " + userInput + "\n\n" +
                    "Example output format: {\"goal\": \"buy a car\", \"targetAmount\": 30000, \"years\": 3}";

            Map<String, Object> textPart = Map.of("text", prompt);
            Map<String, Object> message = Map.of("parts", List.of(textPart));
            Map<String, Object> requestBody = Map.of("contents", List.of(message));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    GEMINI_URL.replace("{key}", geminiApiKey),
                    request,
                    Map.class
            );

            if (response.getBody() != null) {
                // Extract the generated text from the complex response structure
                String generatedText = extractGeneratedText(response.getBody());
                return generatedText;
            }
            return "{}"; // Return empty JSON if no response
        } catch (Exception ex) {
            ex.printStackTrace();
            return "{\"error\": \"" + ex.getMessage() + "\"}";
        }
    }

    private String extractGeneratedText(Map<?, ?> responseBody) {
        try {
            // Navigate through the complex Gemini response structure
            if (responseBody.containsKey("candidates")) {
                List<?> candidates = (List<?>) responseBody.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<?, ?> firstCandidate = (Map<?, ?>) candidates.get(0);
                    Map<?, ?> content = (Map<?, ?>) firstCandidate.get("content");
                    List<?> parts = (List<?>) content.get("parts");
                    if (!parts.isEmpty()) {
                        Map<?, ?> firstPart = (Map<?, ?>) parts.get(0);
                        return firstPart.get("text").toString();
                    }
                }
            }
            return "{}";
        } catch (Exception e) {
            return "{}";
        }
    }
}