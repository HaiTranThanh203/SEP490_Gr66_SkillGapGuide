package com.skillgapguide.sgg.Controller;

import com.skillgapguide.sgg.Dto.JobCategoryDTO;
import com.skillgapguide.sgg.Response.EHttpStatus;
import com.skillgapguide.sgg.Response.Response;
import com.skillgapguide.sgg.Service.JobCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/businessadmin")
@RequiredArgsConstructor
public class BusinessAdminController {
    private final JobCategoryService jobCategoryService;

    @GetMapping("/view-job-category")
    public Response<List<JobCategoryDTO>> findAll() {
        List<JobCategoryDTO> result = jobCategoryService.getAllJobCategories();
        if(result.isEmpty()){
            return new Response<>(EHttpStatus.NO_RESULT_FOUND);
        }
        return new Response<>(EHttpStatus.OK, result);
    }

    @PostMapping("add-job-category")
    public Response<?> addJobCategory(@RequestBody JobCategoryDTO jobCategoryDTO) {
        if (jobCategoryDTO.getName() == null || jobCategoryDTO.getName().trim().isEmpty()) {
            return new Response<>(EHttpStatus.LACK_INFORMATION, "Tên danh mục không được để trống");
        }
        boolean added = jobCategoryService.addJobCategory(jobCategoryDTO);
        if(!added){
            return new Response<>(EHttpStatus.ALREADY_EXISTS, "Job category đã tồn tại", null);
        }
        return new Response<>(EHttpStatus.OK, "Thêm job category thành công", null);
    }

    @PutMapping("/edit-job-category")
    public Response<?> editJobCategory(@RequestBody JobCategoryDTO dto) {
        try {
            jobCategoryService.editJobCategory(dto);
            return new Response<>(EHttpStatus.OK, "Cập nhật danh mục công việc thành công", null);
        } catch (IllegalArgumentException e) {
            return new Response<>(EHttpStatus.NO_RESULT_FOUND, e.getMessage(), null);
        }
    }


}
