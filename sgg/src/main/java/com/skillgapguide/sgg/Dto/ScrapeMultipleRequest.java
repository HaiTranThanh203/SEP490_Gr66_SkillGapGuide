package com.skillgapguide.sgg.Dto;

import lombok.Data;

import java.util.List;
@Data
public class ScrapeMultipleRequest {
    private List<String> urls;
}
