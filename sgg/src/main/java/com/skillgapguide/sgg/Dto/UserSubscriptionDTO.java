package com.skillgapguide.sgg.Dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserSubscriptionDTO {
    private String fullName;
    private String role;
    private boolean isPremium;
    private LocalDateTime subscriptionStart;
    private LocalDateTime subscriptionEnd;
}
