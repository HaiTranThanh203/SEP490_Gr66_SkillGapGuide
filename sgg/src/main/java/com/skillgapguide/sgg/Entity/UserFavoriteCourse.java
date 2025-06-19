package com.skillgapguide.sgg.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@jakarta.persistence.Table(name = "user_favorite_course", schema = "skill_gap_guide", catalog = "")
@Data
public class UserFavoriteCourse {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @jakarta.persistence.Column(name = "user_id")
    private int userId;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @jakarta.persistence.Column(name = "course_id")
    private int courseId;

    @Basic
    @Column(name = "status")
    private String status;

    @Basic
    @Column(name = "created_at")
    private Timestamp createdAt;
}
