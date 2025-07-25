package com.skillgapguide.sgg.Service;

import com.skillgapguide.sgg.Entity.*;
import com.skillgapguide.sgg.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobDeleteService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JobDesFileRepository jobDesFileRepository;
    @Autowired
    private JobCvSkillScoreRepository jobCvSkillScoreRepository;
    @Autowired
    private CVRepository cvRepository;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private EmbedService embedService;
    @Transactional
    public void deleteJob(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName(); // lấy từ JWT
        Integer userId = userRepository.findByEmail(email).map(User::getUserId).orElseThrow(() -> new RuntimeException("User not found"));
        Cv cv = cvRepository.findByUserId(userId);
        if (cv == null) {
            throw new IllegalStateException("Chưa upload cv");
        }
        jobRepository.deleteAllByCvId(cv.getId());
    }
    @Transactional
    public void deleteJobsByCvId(Integer cvId) {
        jobRepository.deleteByCvId(cvId);
    }
}
