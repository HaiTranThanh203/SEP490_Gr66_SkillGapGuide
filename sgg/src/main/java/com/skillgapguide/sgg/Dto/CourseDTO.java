package com.skillgapguide.sgg.Dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class CourseDTO {
    private String title;
    private String rating;
    private String difficulty;
    private String description;
    private String provider;
    private String status;
    private String url;
    private java.sql.Timestamp createdAt;
}
