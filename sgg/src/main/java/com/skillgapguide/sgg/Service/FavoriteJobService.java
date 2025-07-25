package com.skillgapguide.sgg.Service;

import com.skillgapguide.sgg.Dto.FavoriteJobDTO;
import com.skillgapguide.sgg.Dto.ViewFavoriteJobDTO;
import com.skillgapguide.sgg.Entity.Job;
import com.skillgapguide.sgg.Entity.User;
import com.skillgapguide.sgg.Entity.UserFavoriteJob;
import com.skillgapguide.sgg.Repository.JobRepository;
import com.skillgapguide.sgg.Repository.UserFavoriteJobRepository;
import com.skillgapguide.sgg.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteJobService {
    private final UserFavoriteJobRepository userFavoriteJobRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    public FavoriteJobDTO addFavoriteJob(Integer jobId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Công việc không tồn tại"));

        boolean exists = userFavoriteJobRepository.existsByUserAndJob(user, job);
        if (exists) {
            throw new IllegalArgumentException("Công việc đã có trong danh sách yêu thích");
        }

        UserFavoriteJob favoriteJob = new UserFavoriteJob();
        favoriteJob.setUser(user);
        favoriteJob.setJob(job);
        favoriteJob.setCreatedAt(LocalDateTime.now());
        userFavoriteJobRepository.save(favoriteJob);
        return new FavoriteJobDTO(job.getJobId(),job.getTitle(),favoriteJob.getCreatedAt());
    }

    public List<ViewFavoriteJobDTO> getFavoriteJobs() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));
        return  userFavoriteJobRepository.findFavoriteJobsByUserId(user.getUserId());
    }

    @Transactional
    public void removeFavoriteJob(Integer jobId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Công việc không tồn tại"));

        boolean exists = userFavoriteJobRepository.existsByUserAndJob(user, job);
        if (!exists) {
            throw new IllegalArgumentException("Công việc không có trong danh sách yêu thích");
        }

        userFavoriteJobRepository.deleteByUserAndJob(user, job);
    }

}
