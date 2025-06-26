package com.skillgapguide.sgg.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Course")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Integer courseId;
    @Column(nullable = false)
    private String title;
    private String rating;
    private String difficulty;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private String provider;
    private String status;
    private String url;
    @Column(name = "create_at", nullable = false)
    private java.sql.Timestamp createdAt;
}

