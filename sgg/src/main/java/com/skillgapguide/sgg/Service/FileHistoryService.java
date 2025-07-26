package com.skillgapguide.sgg.Service;

import com.skillgapguide.sgg.Dto.FileInfoDTO;
import com.skillgapguide.sgg.Dto.UserFileHistoryDTO;
import com.skillgapguide.sgg.Repository.CVRepository;
import com.skillgapguide.sgg.Repository.JobDesFileRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileHistoryService {
    private final CVRepository cvRepository;
    private final JobDesFileRepository jobDesFileRepository;

    public UserFileHistoryDTO getUserFileHistory(Integer userId) {
        List<FileInfoDTO> cvFiles = cvRepository.findListCVByUserId(userId)
                .stream()
                .map(cv -> new FileInfoDTO(cv.getFileName(), cv.getFileType(),cv.getUploadDate()))
                .collect(Collectors.toList());

        List<FileInfoDTO> jobDesFile = jobDesFileRepository.findByUserId(userId)
                .stream()
                .map(jd -> new FileInfoDTO(jd.getFileName(), jd.getFileType(),jd.getUploadDate()))
                .collect(Collectors.toList());

        UserFileHistoryDTO userFileHistoryDTO = new UserFileHistoryDTO();
        userFileHistoryDTO.setCvFiles(cvFiles);
        userFileHistoryDTO.setJobDesFile(jobDesFile);
        return userFileHistoryDTO;
    }
}
