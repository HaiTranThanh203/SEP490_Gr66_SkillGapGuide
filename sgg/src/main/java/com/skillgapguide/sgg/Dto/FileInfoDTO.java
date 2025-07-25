package com.skillgapguide.sgg.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileInfoDTO {
    private String fileName;
    private String fileType;
    private LocalDateTime uploadDate;
}
