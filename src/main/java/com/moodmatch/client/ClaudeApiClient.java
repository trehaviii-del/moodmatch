package com.moodmatch.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

@Component
public class ClaudeApiClient {

    private static final String API_URL = "https://api.anthropic.com/v1/messages";
    private static final String MODEL = "claude-sonnet-4-6";

    @Value("${claude.api.key:}")
    private String apiKey;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String sendMessage(String prompt) {
        try {
            Map<String, Object> body = Map.of(
                "model", MODEL,
                "max_tokens", 1024,
                "messages", List.of(
                    Map.of("role", "user", "content", prompt)
                )
            );

            String requestBody = objectMapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .header("x-api-key", apiKey)
                .header("anthropic-version", "2023-06-01")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Claude API error: " + response.statusCode() + " - " + response.body());
            }

            Map<?, ?> responseMap = objectMapper.readValue(response.body(), Map.class);
            List<?> content = (List<?>) responseMap.get("content");
            Map<?, ?> firstContent = (Map<?, ?>) content.get(0);
            return (String) firstContent.get("text");

        } catch (Exception e) {
            throw new RuntimeException("Failed to call Claude API", e);
        }
    }
}
