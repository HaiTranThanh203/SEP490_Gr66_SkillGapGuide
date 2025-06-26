package com.skillgapguide.sgg.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@jakarta.persistence.Table(name = "user_favorite_course", schema = "skill_gap_guide", catalog = "")
@Data
public class UserFavoriteCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "user_id")
    private int userId;

    @ManyToOne
    @JoinColumn(name = "course_id", referencedColumnName = "course_id")
    private Course course;

    @Basic
    @Column(name = "status")
    private String status;

    @Basic
    @Column(name = "created_at")
    private Timestamp createdAt;
}
