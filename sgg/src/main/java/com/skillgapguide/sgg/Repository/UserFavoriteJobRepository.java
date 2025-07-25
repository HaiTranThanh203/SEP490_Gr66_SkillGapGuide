package com.skillgapguide.sgg.Repository;

import com.skillgapguide.sgg.Dto.ViewFavoriteJobDTO;
import com.skillgapguide.sgg.Entity.Job;
import com.skillgapguide.sgg.Entity.User;
import com.skillgapguide.sgg.Entity.UserFavoriteJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserFavoriteJobRepository extends JpaRepository<UserFavoriteJob, Integer> {
    boolean existsByUserAndJob(User user, Job job);

    @Query("SELECT new com.skillgapguide.sgg.Dto.ViewFavoriteJobDTO(" +
            "j.jobId, j.title, j.company, j.description, j.sourceUrl, ufj.createdAt) " +
            "FROM UserFavoriteJob ufj " +
            "JOIN ufj.job j " +
            "WHERE ufj.user.userId = :userId " +
            "ORDER BY ufj.createdAt DESC")
    List<ViewFavoriteJobDTO> findFavoriteJobsByUserId(Integer userId);

    void deleteByUserAndJob(User user, Job job);
}
