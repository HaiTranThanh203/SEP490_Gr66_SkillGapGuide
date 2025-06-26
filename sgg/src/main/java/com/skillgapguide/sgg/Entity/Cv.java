package com.skillgapguide.sgg.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "CV")
public class Cv {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer userId;
    private String fileName;
    private String filePath;
    private String fileType;
    private LocalDateTime uploadDate;
}

