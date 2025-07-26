package com.skillgapguide.sgg.Service;

import com.skillgapguide.sgg.Dto.AddSpecializationRequestDTO;
import com.skillgapguide.sgg.Dto.SpecializationDTO;
import com.skillgapguide.sgg.Entity.JobGroup;
import com.skillgapguide.sgg.Entity.Specialization;
import com.skillgapguide.sgg.Repository.JobGroupRepository;
import com.skillgapguide.sgg.Repository.SpecializationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpecializationsService {

    private final SpecializationRepository specializationRepository;
    private final JobGroupRepository occupationRepository;

    private SpecializationDTO mapToDTO(Specialization specialization) {
        return new SpecializationDTO(
                specialization.getId(),
                specialization.getName(),
                specialization.getOccupation().getId(),
                specialization.getOccupation().getName(),
                specialization.getStatus()
        );
    }

    public List<SpecializationDTO> getAll() {
        return specializationRepository.findAll()
                .stream().map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<SpecializationDTO> getAllEnable() {
        return specializationRepository.findByStatusIgnoreCase("enable")
                .stream().map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public SpecializationDTO add(AddSpecializationRequestDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên chuyên ngành không được để trống.");
        }

        // Kiểm tra trạng thái hợp lệ
        if (!dto.getStatus().equalsIgnoreCase("Enable") && !dto.getStatus().equalsIgnoreCase("Disable")) {
            throw new IllegalArgumentException("Trạng thái chỉ được là Enable hoặc Disable");
        }

        if (specializationRepository.findByNameIgnoreCase(dto.getName().trim()).isPresent()) {
            throw new IllegalArgumentException("Tên chuyên ngành đã tồn tại");
        }

        JobGroup occupation = occupationRepository.findById(dto.getOccupationId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ngành nghề với ID: " + dto.getOccupationId()));

        Specialization specialization = new Specialization();
        specialization.setName(dto.getName().trim());
        specialization.setStatus(dto.getStatus().trim());
        specialization.setOccupation(occupation);

        Specialization saved = specializationRepository.save(specialization);

        SpecializationDTO result = new SpecializationDTO();
        result.setId(saved.getId());
        result.setName(saved.getName());
        result.setStatus(saved.getStatus());
        result.setOccupationId(occupation.getId());
        result.setOccupationName(occupation.getName());

        return result;
    }


    public void update(Integer id, AddSpecializationRequestDTO dto) {
        Specialization specialization = specializationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chuyên ngành với ID: " + id));

        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên chuyên ngành không được để trống.");
        }

        if (dto.getStatus() == null ||
                (!dto.getStatus().equalsIgnoreCase("enable") && !dto.getStatus().equalsIgnoreCase("disable"))) {
            throw new IllegalArgumentException("Trạng thái chỉ được là 'enable' hoặc 'disable'.");
        }

        Optional<Specialization> existing = specializationRepository.findByNameIgnoreCase(dto.getName().trim());
        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            throw new IllegalArgumentException("Tên chuyên ngành đã tồn tại.");
        }

        JobGroup occupation = occupationRepository.findById(dto.getOccupationId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ngành nghề với ID: " + dto.getOccupationId()));

        specialization.setName(dto.getName().trim());
        specialization.setStatus(dto.getStatus().trim());
        specialization.setOccupation(occupation);

         specializationRepository.save(specialization);
    }

    public boolean toggleStatus(Integer id) {
        Optional<Specialization> optional = specializationRepository.findById(id);
        if (optional.isPresent()) {
            Specialization specialization = optional.get();
            String currentStatus = specialization.getStatus();

            if (currentStatus == null) {
                throw new IllegalStateException("Status không được để trống.");
            }

            String normalized = currentStatus.trim().toLowerCase();
            if (!normalized.equals("enable") && !normalized.equals("disable")) {
                throw new IllegalStateException("Status hiện tại không hợp lệ: " + currentStatus);
            }

            specialization.setStatus(normalized.equals("enable") ? "Disable" : "Enable");
            specializationRepository.save(specialization);
            return true;
        }
        return false;
    }

    public List<SpecializationDTO> searchByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên chuyên ngành không được để trống để tìm kiếm.");
        }

        List<Specialization> specializations = specializationRepository.findByNameContainingIgnoreCase(name.trim());
        return specializations.stream()
                .map(specialization -> new SpecializationDTO(
                        specialization.getId(),
                        specialization.getName(),
                        specialization.getOccupation().getId(),
                        specialization.getOccupation().getName(),
                        specialization.getStatus()
                ))
                .collect(Collectors.toList());
    }

    public List<SpecializationDTO> getByFilters(Integer occupationId, Integer groupId) {
        return specializationRepository.findByFilters(occupationId, groupId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private SpecializationDTO convertToDTO(Specialization s) {
        return new SpecializationDTO(
                s.getId(),
                s.getName(),
                s.getOccupation().getId(),
                s.getOccupation().getName(),
                s.getStatus()
        );
    }
}
