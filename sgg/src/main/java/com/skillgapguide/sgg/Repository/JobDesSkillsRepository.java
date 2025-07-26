package com.skillgapguide.sgg.Repository;

import com.skillgapguide.sgg.Entity.JobDesSkills;
import com.skillgapguide.sgg.Entity.UserCvSkills;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobDesSkillsRepository extends JpaRepository<JobDesSkills,Integer> {
    List<JobDesSkills> findByJobId(int jobId);
    void deleteAllByJobId(int jobId);
}
