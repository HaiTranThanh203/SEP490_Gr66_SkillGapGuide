package com.skillgapguide.sgg.Repository;
import com.skillgapguide.sgg.Entity.JobGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import java.util.List;
@Repository
public interface JobGroupRepository extends JpaRepository<JobGroup, Integer>{
    List<JobGroup> findByStatusIgnoreCase(String status);
    Optional<JobGroup> findByNameIgnoreCase(String name);
    List<JobGroup> findByOccupationGroupId(Integer groupId);

}
