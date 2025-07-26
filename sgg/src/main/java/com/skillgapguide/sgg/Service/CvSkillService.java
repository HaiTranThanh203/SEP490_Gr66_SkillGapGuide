package com.skillgapguide.sgg.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillgapguide.sgg.Dto.ExtractCvSkillDTO;
import com.skillgapguide.sgg.Entity.UserCvSkills;
import com.skillgapguide.sgg.Repository.UserCvSkillsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class CvSkillService {
    private final UserCvSkillsRepository userCvSkillsRepository;
    private final EmbedService embedService;

    @Transactional
    public void saveCvSkillsToDb(String aiResponseJson, int cvId) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ExtractCvSkillDTO dto = mapper.readValue(aiResponseJson, ExtractCvSkillDTO.class);
        // Xoá cũ
        userCvSkillsRepository.deleteAllByCvId(cvId);
        // Lưu mới
        for (String skill : dto.getSkills()) {
            UserCvSkills entity = new UserCvSkills();
            entity.setCvId(cvId);
            entity.setSkill(skill);
            userCvSkillsRepository.save(entity);
            embedService.getCvSkillEmbedding(skill);   // OK nếu service này không cần transaction
        }
    }
}
