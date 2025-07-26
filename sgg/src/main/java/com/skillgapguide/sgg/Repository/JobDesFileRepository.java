package com.skillgapguide.sgg.Repository;

import com.skillgapguide.sgg.Entity.JobDesFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobDesFileRepository extends JpaRepository<JobDesFile, Integer> {
    List<JobDesFile> findByUserId(Integer userId);
    void deleteAllByJobId(int jobId);
}
