package com.skillgapguide.sgg.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewFavoriteJobDTO {
    private Integer jobId;
    private String title;
    private String company;
    private String description;
    private String sourceUrl;
    private LocalDateTime createdAt;
}
