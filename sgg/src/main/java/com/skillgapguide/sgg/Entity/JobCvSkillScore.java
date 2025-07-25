package com.skillgapguide.sgg.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "job_cv_skills_score")
public class JobCvSkillScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer jobSkill;
    private Integer cvSkill;
    @Column(name = "score")
    private double score;

    public JobCvSkillScore(Integer jobSkill, Integer cvSkill, double score) {
        this.jobSkill = jobSkill;
        this.cvSkill = cvSkill;
        this.score = score;
    }

    public JobCvSkillScore() {

    }
}
