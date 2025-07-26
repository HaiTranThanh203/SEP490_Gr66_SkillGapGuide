package com.skillgapguide.sgg.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillgapguide.sgg.Response.AIResponse;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class LMStudioService {
    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LMStudioService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("http://localhost:1234")
                .build();
    }

    public Mono<String> callLMApi(String prompt) {
        Map<String, Object> requestBody = Map.of(
                "model", "mistralai/mistral-7b-instruct-v0.3",
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0.7,
                "max_tokens", 10000,
                "stream", false
        );
        return webClient.post()
                .uri("/v1/chat/completions")
                .header("Authorization", "Bearer lm-studio")
                .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(this::parseAiResponse)
                .timeout(Duration.ofMinutes(2))
                .onErrorResume(e -> Mono.just("Lỗi: " + e.getMessage()));
    }

    private Mono<String> parseAiResponse(String json) {
        return Mono.fromCallable(() -> {
            AIResponse response = objectMapper.readValue(json, AIResponse.class);
            if (response.getChoices() != null && !response.getChoices().isEmpty()) {
                return response.getChoices().get(0).getMessage().getContent();
            }
            return "Không có phản hồi từ AI";
        }).onErrorResume(e -> Mono.just("Lỗi parse JSON: " + e.getMessage()));
    }
}
