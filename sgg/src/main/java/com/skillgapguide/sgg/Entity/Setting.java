package com.skillgapguide.sgg.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Setting")
public class Setting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer setting_id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "value")
    private String value;
}

