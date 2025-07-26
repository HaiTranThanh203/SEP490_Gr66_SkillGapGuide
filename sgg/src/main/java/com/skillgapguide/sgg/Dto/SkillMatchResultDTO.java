package com.skillgapguide.sgg.Dto;


public class SkillMatchResultDTO {
    public String cvSkill;
    public String jobSkill;
    public Double score;

    public SkillMatchResultDTO(String cvSkill, String jobSkill, Double score) {
        this.cvSkill = cvSkill;
        this.jobSkill = jobSkill;
        this.score = score;
    }
    @Override
    public String toString() {
        return "{" +
                "cvSkill='" + cvSkill + '\'' +
                ", jobSkill='" + jobSkill + '\'' +
                ", score=" + score +
                '}';
    }
}
