package com.skillgapguide.sgg.Controller;

import com.skillgapguide.sgg.Dto.ScrapeMultipleRequest;
import com.skillgapguide.sgg.Dto.ScrapeRequest;
import com.skillgapguide.sgg.Service.JobScrapingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/scrape")
@RequiredArgsConstructor
public class JobScraperController {
    private final JobScrapingService jobScrapingService;

    @PostMapping("/job")
    public ResponseEntity<String> scrapeSingleJob(@RequestBody ScrapeRequest request) {
        if (request.getUrl() == null || request.getUrl().isEmpty()) {
            return ResponseEntity.badRequest().body("URL không được để trống.");
        }
        try {
            jobScrapingService.scrapeAndSaveJob(request.getUrl());
            return ResponseEntity.ok("Đã cào và lưu thành công công việc từ URL: " + request.getUrl());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi xảy ra khi cào dữ liệu: " + e.getMessage());
        }
    }

    @PostMapping("/crawl-10-jobs")
    public ResponseEntity<String> scrapeTop10JobsByCategory(@RequestBody ScrapeRequest request) {
        if (request.getUrl() == null || request.getUrl().isEmpty()) {
            return ResponseEntity.badRequest().body("URL không được để trống.");
        }
        try {
            jobScrapingService.scrapeJobsWithCvCleanup("category", request.getUrl());
            return ResponseEntity.ok("Đã cào và lưu 10 công việc đầu tiên từ URL: " + request.getUrl());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi xảy ra khi cào dữ liệu: " + e.getMessage());
        }
    }

    @PostMapping("/crawl-10-jobs-by-specialization")
    public ResponseEntity<String> scrapeTop10JobsBySpecialization(@RequestBody ScrapeRequest request) {
        if (request.getUrl() == null || request.getUrl().isEmpty()) {
            return ResponseEntity.badRequest().body("Vị trí chuyên môn không được để trống.");
        }
        try {
            jobScrapingService.scrapeJobsWithCvCleanup("specialization", request.getUrl());
            return ResponseEntity.ok("Đã cào và lưu 10 công việc đầu tiên từ URL: : " + request.getUrl());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/crawl-4-jobs-by-links")
    public ResponseEntity<String> scrapeMultipleJobs(@RequestBody ScrapeMultipleRequest request) {
        if (request.getUrls() == null || request.getUrls().isEmpty()) {
            return ResponseEntity.badRequest().body("Danh sách URL không được để trống.");
        }
        try {
            jobScrapingService.scrapeJobsWithCvCleanup("multiple", request.getUrls());
            return ResponseEntity.ok("Đã cào và lưu thành công các công việc từ danh sách URL.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi xảy ra khi cào dữ liệu: " + e.getMessage());
        }
    }
}