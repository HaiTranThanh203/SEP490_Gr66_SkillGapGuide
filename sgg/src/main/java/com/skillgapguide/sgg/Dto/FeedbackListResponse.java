package com.skillgapguide.sgg.Dto;

import lombok.Data;


public interface FeedbackListResponse {
    String getEmail();
    String getContent();
    Integer getStar();
}
