package com.skillgapguide.sgg.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFileHistoryDTO {
    private List<FileInfoDTO> cvFiles;
    private List<FileInfoDTO> jobDesFile;
}
