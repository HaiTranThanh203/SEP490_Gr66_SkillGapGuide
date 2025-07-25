package com.skillgapguide.sgg.Repository;

import com.skillgapguide.sgg.Entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Integer> {
    // Spring Data JPA sẽ tự động cung cấp các phương thức CRUD
    boolean existsBySourceUrl(String sourceUrl);
    List<Job> getJobsByCvId(int cvId);
    void deleteAllByCvId(int cvId);
    boolean existsByCvId(Integer cvId);
    void deleteByCvId(Integer cvId);
}
