package com.skillgapguide.sgg.Controller;

import com.skillgapguide.sgg.Dto.*;
import com.skillgapguide.sgg.Entity.JobGroup;
import com.skillgapguide.sgg.Entity.MainJobCategory;
import com.skillgapguide.sgg.Response.EHttpStatus;
import com.skillgapguide.sgg.Response.Response;
import com.skillgapguide.sgg.Service.JobCategoryService;
import com.skillgapguide.sgg.Service.MainJobCategoryService;
import com.skillgapguide.sgg.Service.JobGroupService;
import com.skillgapguide.sgg.Service.SpecializationsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;


import java.util.List;

@RestController
@RequestMapping("/api/businessadmin")
@RequiredArgsConstructor
public class ContentManagerController {
    private final JobCategoryService jobCategoryService;
    private final SpecializationsService specializationService;
    private final MainJobCategoryService occupationGroupService;
    private final JobGroupService occupationService;

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

    @GetMapping("/view-occupation-groups")
    public Response<List<MainJobCategoryDTO>> getAllOccupationGroups() {
        List<MainJobCategoryDTO> list = occupationGroupService.getAllMainJobCategory();
        return new Response<>(EHttpStatus.OK, "Lấy danh sách nhóm nghề nghiệp thành công", list);
    }

    @GetMapping("/view-occupation-groups-enable")
    public Response<List<MainJobCategoryDTO>> getEnabledOccupationGroups() {
        List<MainJobCategoryDTO> list = occupationGroupService.getEnabledOccupationGroups();
        return new Response<>(EHttpStatus.OK, "Lấy danh sách nhóm nghề nghiệp đang hoạt động thành công", list);
    }

    @PostMapping("/add-occupation-groups")
    public Response<?> addOccupationGroup(@RequestBody MainJobCategoryDTO dto) {
        try {
            MainJobCategory newGroup = occupationGroupService.addMainJobCategory(dto); // Get the saved group
            return new Response<>(EHttpStatus.OK, "Thêm nhóm nghề nghiệp thành công", newGroup); // Return the new group object
        } catch (IllegalArgumentException e) {
            return new Response<>(EHttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
    }


    @PutMapping("/edit-occupation-groups")
    public ResponseEntity<?> editOccupationGroup(@RequestBody MainJobCategoryDTO dto) {
        try {
            occupationGroupService.editMainJobCategory(dto);
            return ResponseEntity.ok(new Response<>(EHttpStatus.OK, "Cập nhật nhóm nghề nghiệp thành công", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new Response<>(EHttpStatus.BAD_REQUEST, e.getMessage(), null));
        }
    }

    @PutMapping("/disable-occupation-groups/{id}")
    public ResponseEntity<?> toggleOccupationGroupStatus(@PathVariable Integer id) {
        try {
            boolean success = occupationGroupService.toggleOccupationGroupStatus(id);
            if (success) {
                return ResponseEntity.ok("Cập nhật trạng thái thành công");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Không tìm thấy Occupation Group với ID: " + id);
            }
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/view-occupations")
    public Response<List<JobGroupDTO>> getAllOccupations() {
        return new Response<>(EHttpStatus.OK, "Lấy tất cả ngành nghề thành công", occupationService.getAllJobGroup());
    }

    @GetMapping("/view-occupations-enable")
    public Response<List<JobGroupDTO>> getEnabledOccupations() {
        return new Response<>(EHttpStatus.OK, "Lấy ngành nghề đang kích hoạt thành công", occupationService.getEnabledOccupations());
    }

    @PostMapping("/add-occupations")
    public Response<?> addOccupation(@RequestBody AddJobGroupRequestDTO dto) {
        try {
            JobGroup newOccupation = occupationService.addJobGroup(dto); // Get the saved Occupation object
            return new Response<>(EHttpStatus.OK, "Thêm ngành nghề thành công", newOccupation); // Return the new object
        } catch (IllegalArgumentException e) {
            return new Response<>(EHttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
    }


    @PutMapping("/edit-occupations/{id}")
    public Response<?> updateOccupation(@PathVariable Integer id, @RequestBody AddJobGroupRequestDTO dto) {
        try {
            occupationService.updateJobGroup(id, dto);
            return new Response<>(EHttpStatus.OK, "Cập nhật ngành nghề thành công", null);
        } catch (IllegalArgumentException e) {
            return new Response<>(EHttpStatus.BAD_REQUEST, e.getMessage(), null);
        }
    }

    @PutMapping("/disable-occupation/{id}")
    public ResponseEntity<Response<Boolean>> toggleOccupationStatus(@PathVariable Integer id) {
        boolean result = occupationService.toggleOccupationStatus(id);
        if (result) {
            return ResponseEntity.ok(new Response<>(EHttpStatus.OK, "Thay đổi trạng thái thành công", true));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Response<>(EHttpStatus.NO_RESULT_FOUND, "Không tìm thấy ngành nghề", false));
        }
    }

    //specialization
    @GetMapping("/view-specialization")
    public ResponseEntity<Response<List<SpecializationDTO>>> getAll() {
        return ResponseEntity.ok(new Response<>(EHttpStatus.OK, "Thành công", specializationService.getAll()));
    }

    @GetMapping("/view-specialization-enable")
    public ResponseEntity<Response<List<SpecializationDTO>>> getAllEnable() {
        return ResponseEntity.ok(new Response<>(EHttpStatus.OK, "Thành công", specializationService.getAllEnable()));
    }

    @PostMapping("/add-specialization")
    public ResponseEntity<Response<?>> addSpecialization(@RequestBody AddSpecializationRequestDTO dto) {
        try {
            SpecializationDTO specializationDTO = specializationService.add(dto);
            return ResponseEntity.ok(new Response<>(EHttpStatus.OK, "Thêm chuyên ngành thành công", specializationDTO));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new Response<>(EHttpStatus.BAD_REQUEST, e.getMessage(), null));
        }
    }


    @PutMapping("/edit-specialization/{id}")
    public ResponseEntity<Response<?>> editSpecialization(@PathVariable Integer id, @RequestBody AddSpecializationRequestDTO dto) {
        try {
            specializationService.update(id, dto); // Cập nhật chuyên ngành
            return ResponseEntity.ok(new Response<>(EHttpStatus.OK, "Cập nhật chuyên ngành thành công", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new Response<>(EHttpStatus.BAD_REQUEST, e.getMessage(), null));
        }
    }

    @PutMapping("/disable-specialization/{id}")
    public ResponseEntity<Response<String>> toggleStatus(@PathVariable Integer id) {
        boolean result = specializationService.toggleStatus(id);
        if (result) {
            return ResponseEntity.ok(new Response<>(EHttpStatus.OK, "Thay đổi trạng thái thành công", null));
        } else {
            return ResponseEntity.ok(new Response<>(EHttpStatus.NO_RESULT_FOUND, "Không tìm thấy chuyên ngành", null));
        }
    }

    @GetMapping("/search-specialization")
    public ResponseEntity<Response<List<SpecializationDTO>>> searchByName(@RequestParam String name) {
        try {
            List<SpecializationDTO> result = specializationService.searchByName(name);
            return ResponseEntity.ok(new Response<>(EHttpStatus.OK, "Tìm kiếm thành công", result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new Response<>(EHttpStatus.BAD_REQUEST, e.getMessage(), null));
        }
    }

    @GetMapping("/filter-occupation-byGroup")
    public ResponseEntity<List<JobGroupDTO>> getOccupations(@RequestParam Integer groupId) {
        return ResponseEntity.ok(occupationService.getByGroupId(groupId));
    }

    @GetMapping("/filter-specializations-byOccupation")
    public ResponseEntity<List<SpecializationDTO>> getSpecializations(
            @RequestParam(required = false) Integer occupationId,
            @RequestParam(required = false) Integer groupId) {

        return ResponseEntity.ok(specializationService.getByFilters(occupationId, groupId));
    }



}
