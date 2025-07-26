package com.skillgapguide.sgg.Repository;

import com.skillgapguide.sgg.Dto.SkillMatchResultDTO;
import com.skillgapguide.sgg.Entity.JobCvSkillScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface JobCvSkillScoreRepository extends JpaRepository<JobCvSkillScore, Integer> {
    @Query("SELECT new com.skillgapguide.sgg.Dto.SkillMatchResultDTO(cv.skill, job.skill , s.score)  " +
            "FROM JobCvSkillScore s " +
            "JOIN JobDesSkills job on s.jobSkill = job.id " +
            "left JOIN UserCvSkills cv on s.cvSkill = cv.id " +
            "WHERE job.jobId = :jobId AND cv.cvId = :cvId")
    List<SkillMatchResultDTO> findByCvSkillAndJobSkill(@Param("cvId") int cvId, @Param("jobId") int jobId);
    @Modifying
    @Transactional
    @Query("DELETE FROM JobCvSkillScore s " +
            "WHERE s.jobSkill IN (SELECT job.id FROM JobDesSkills job WHERE job.jobId = :jobId) " +
            "AND s.cvSkill IN (SELECT cv.id FROM UserCvSkills cv WHERE cv.cvId = :cvId)")
    void deleteByJobIdAndCvId(@Param("jobId") int jobId, @Param("cvId") int cvId);
}
