package com.skillgapguide.sgg.Repository;

import com.skillgapguide.sgg.Entity.MainJobCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MainJobCategoryRepository extends JpaRepository<MainJobCategory, Integer> {
    List<MainJobCategory> findAllByStatusIgnoreCase(String status);
    Optional<MainJobCategory> findByNameIgnoreCase(String name);
    Optional<MainJobCategory> findById(Integer id);
}
