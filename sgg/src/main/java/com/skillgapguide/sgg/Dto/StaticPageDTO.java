package com.skillgapguide.sgg.Dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class StaticPageDTO {
        private String title;
        private String content;

    public StaticPageDTO(String title, String content) {
        this.title = title;
        this.content = content;
    }
}

