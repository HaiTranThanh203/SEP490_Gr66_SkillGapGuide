package com.skillgapguide.sgg.Entity;

import jakarta.persistence.*;
import lombok.Data;
import org.apache.james.mime4j.dom.datetime.DateTime;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Table(name = "feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer feedback_id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Integer star;

    @Column(nullable = false,name = "create_at")
    private Timestamp createAt;
}
