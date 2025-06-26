package com.skillgapguide.sgg.Repository;

import com.skillgapguide.sgg.Entity.Course;
import com.skillgapguide.sgg.Entity.UserFavoriteCourse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FavoriteCourseRepository extends JpaRepository<UserFavoriteCourse, Integer> {
    Page<UserFavoriteCourse> findByUserId(Integer userId, Pageable pageable);
    UserFavoriteCourse findByUserId(Integer userId);
    @Query("SELECT ufc FROM UserFavoriteCourse ufc WHERE ufc.userId = :userId AND ufc.course.courseId = :courseId")
    Optional<UserFavoriteCourse> findByUserIdAndCourseId(Integer userId, Integer courseId);
}
