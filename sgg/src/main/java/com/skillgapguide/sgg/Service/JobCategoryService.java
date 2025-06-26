package com.skillgapguide.sgg.Service;

import com.skillgapguide.sgg.Dto.JobCategoryDTO;
import com.skillgapguide.sgg.Entity.JobCategory;
import com.skillgapguide.sgg.Repository.JobCategoryRepository;
import com.skillgapguide.sgg.Repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class JobCategoryService {
    private final JobCategoryRepository JobCategoryRepository;
    private final JobCategoryRepository jobCategoryRepository;

    public List<JobCategoryDTO> getAllJobCategories(){
        List<JobCategory> jobCategories = JobCategoryRepository.findAll();
        List<JobCategoryDTO> jobCategoriesDTO = new ArrayList<>();
        for (JobCategory jobCategory : jobCategories) {
            JobCategoryDTO dto = new JobCategoryDTO();
            dto.setId(jobCategory.getJobCategoryId());
            dto.setName(jobCategory.getName());
            jobCategoriesDTO.add(dto);
        }
        return jobCategoriesDTO;
    }

    public boolean addJobCategory(JobCategoryDTO jobCategoryDTO) {
        if (jobCategoryDTO.getName() == null || jobCategoryDTO.getName().trim().isEmpty()) {
            return false;
        }
        Optional<JobCategory> jobCategory = JobCategoryRepository.findByNameIgnoreCase(jobCategoryDTO.getName());
        if (jobCategory.isPresent()) {
            return false;
        }
        JobCategory jobCategoryEntity = new JobCategory();
        jobCategoryEntity.setName(jobCategoryDTO.getName());
        JobCategoryRepository.save(jobCategoryEntity);
        return true;
    }

    public void editJobCategory(JobCategoryDTO dto) {
        if (dto.getId() == null || dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("ID hoặc tên danh mục không được để trống");
        }

        String trimmedName = dto.getName().trim();

        // Kiểm tra có tồn tại category cần sửa không
        JobCategory jobCategory = jobCategoryRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục với ID: " + dto.getId()));

        // Kiểm tra nếu tên mới đã tồn tại ở category khác
        Optional<JobCategory> existingByName = jobCategoryRepository.findByNameIgnoreCase(trimmedName);
        if (existingByName.isPresent() && !existingByName.get().getJobCategoryId().equals(dto.getId())) {
            throw new IllegalArgumentException("Tên danh mục đã tồn tại");
        }

        // Cập nhật tên mới
        jobCategory.setName(trimmedName);
        jobCategoryRepository.save(jobCategory);
        }
}
