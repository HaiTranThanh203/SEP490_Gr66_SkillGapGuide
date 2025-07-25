package com.skillgapguide.sgg.Service;

import com.skillgapguide.sgg.Dto.AddJobGroupRequestDTO;
import com.skillgapguide.sgg.Dto.JobGroupDTO;
import com.skillgapguide.sgg.Entity.JobGroup;
import com.skillgapguide.sgg.Entity.MainJobCategory;
import com.skillgapguide.sgg.Repository.MainJobCategoryRepository;
import com.skillgapguide.sgg.Repository.JobGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobGroupService {
    private final JobGroupRepository occupationRepository;
    private final MainJobCategoryRepository occupationGroupsRepository;

    public List<JobGroupDTO> getAllJobGroup() {
        List<JobGroup> occupations = occupationRepository.findAll();
        return mapToDTOList(occupations);
    }

    public List<JobGroupDTO> getEnabledOccupations() {
        List<JobGroup> occupations = occupationRepository.findByStatusIgnoreCase("Enable");
        return mapToDTOList(occupations);
    }

    private List<JobGroupDTO> mapToDTOList(List<JobGroup> occupations) {
        return occupations.stream().map(o -> {
            JobGroupDTO dto = new JobGroupDTO();
            dto.setId(o.getId());
            dto.setName(o.getName());
            dto.setStatus(o.getStatus());
            dto.setGroupId(o.getOccupationGroup().getId());
            dto.setGroupName(o.getOccupationGroup().getName());
            return dto;
        }).collect(Collectors.toList());
    }

    public JobGroup addJobGroup(AddJobGroupRequestDTO dto) {
        // Check null
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên ngành nghề không được để trống.");
        }

        // Check status
        if (!dto.getStatus().equalsIgnoreCase("Enable") && !dto.getStatus().equalsIgnoreCase("Disable")) {
            throw new IllegalArgumentException("Trạng thái chỉ được là Enable hoặc Disable");
        }

        // Check trùng tên
        if (occupationRepository.findByNameIgnoreCase(dto.getName()).isPresent()) {
            throw new IllegalArgumentException("Tên ngành nghề đã tồn tại");
        }

        // Check occupation group tồn tại
        MainJobCategory group = occupationGroupsRepository.findById(dto.getOccupationGroupId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Occupation Group với ID: " + dto.getOccupationGroupId()));

        // Save
        JobGroup occupation = new JobGroup();
        occupation.setName(dto.getName());
        occupation.setStatus(dto.getStatus());
        occupation.setOccupationGroup(group);

        return occupationRepository.save(occupation); // Return the saved Occupation object
    }


    public void updateJobGroup(Integer id, AddJobGroupRequestDTO dto) {
        // Check null
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên ngành nghề không được để trống.");
        }

        if (dto.getStatus() == null ||
                (!dto.getStatus().equalsIgnoreCase("Enable") && !dto.getStatus().equalsIgnoreCase("Disable"))) {
            throw new IllegalArgumentException("Trạng thái chỉ được là 'Enable' hoặc 'Disable'.");
        }

        // Check tồn tại occupation
        JobGroup occupation = occupationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ngành nghề với ID: " + id));

        // Check tồn tại occupation group
        MainJobCategory group = occupationGroupsRepository.findById(dto.getOccupationGroupId())
                .orElseThrow(() -> new IllegalArgumentException("Occupation group không tồn tại với ID: " + dto.getOccupationGroupId()));

        // Check duplicate name
        Optional<JobGroup> duplicate = occupationRepository.findByNameIgnoreCase(dto.getName().trim());
        if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
            throw new IllegalArgumentException("Tên ngành nghề đã tồn tại.");
        }

        // Update
        occupation.setName(dto.getName().trim());
        occupation.setStatus(dto.getStatus().trim());
        occupation.setOccupationGroup(group);

        occupationRepository.save(occupation);
    }

    public boolean toggleOccupationStatus(Integer id) {
        Optional<JobGroup> optional = occupationRepository.findById(id);
        if (optional.isPresent()) {
            JobGroup occupation = optional.get();
            String currentStatus = occupation.getStatus();

            if (currentStatus == null) {
                throw new IllegalStateException("Status không được để trống.");
            }

            String normalized = currentStatus.trim().toLowerCase();

            if (!normalized.equals("enable") && !normalized.equals("disable")) {
                throw new IllegalStateException("Status hiện tại không hợp lệ: " + currentStatus);
            }

            String newStatus = normalized.equals("enable") ? "Disable" : "Enable";
            occupation.setStatus(newStatus);
            occupationRepository.save(occupation);
            return true;
        }
        return false;
    }

    public List<JobGroupDTO> getByGroupId(Integer groupId) {
        List<JobGroup> occupations = occupationRepository.findByOccupationGroupId(groupId);
        return occupations.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private JobGroupDTO convertToDTO(JobGroup o) {
        return new JobGroupDTO(
                o.getId(),
                o.getName(),
                o.getOccupationGroup().getId(),
                o.getOccupationGroup().getName(),
                o.getStatus()
        );
    }




}
