package com.skillgapguide.sgg.Repository;

import com.skillgapguide.sgg.Entity.JobCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobCategoryRepository extends JpaRepository<JobCategory, Integer> {
    // Tự động có các phương thức như save(), findById(), findAll()
    // Thêm phương thức để tìm category theo tên (hữu ích cho việc "tìm hoặc tạo mới")
    Optional<JobCategory> findByName(String name);
    Optional<JobCategory> findByNameIgnoreCase(String name);
}
