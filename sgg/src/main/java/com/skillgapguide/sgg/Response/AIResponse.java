package com.skillgapguide.sgg.Response;
import java.util.List;

import lombok.Data;

@Data
public class AIResponse {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<Choice> choices;
    private Usage usage;
    private Object stats;
    private String system_fingerprint;

    @Data
    public static class Choice {
        private int index;
        private Object logprobs;
        private String finish_reason;
        private Message message;
    }

    @Data
    public static class Message {
        private String role;
        private String content;
    }

    @Data
    public static class Usage {
        private int prompt_tokens;
        private int completion_tokens;
        private int total_tokens;
    }
}

