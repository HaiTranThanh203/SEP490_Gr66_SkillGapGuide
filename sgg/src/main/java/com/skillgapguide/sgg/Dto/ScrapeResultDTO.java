package com.skillgapguide.sgg.Dto;

import com.skillgapguide.sgg.Entity.Course;
import lombok.Data;

import java.util.List;

@Data
public class ScrapeResultDTO {
    private List<Course> courses;
    private List<String> logs;

    public ScrapeResultDTO(List<Course> scrapedCourses, List<String> logs) {
        this.courses = scrapedCourses;
        this.logs = logs;
    }
}
