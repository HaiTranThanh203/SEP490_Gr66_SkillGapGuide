package com.skillgapguide.sgg.Repository;

import com.skillgapguide.sgg.Dto.FeedbackDetailResponse;
import com.skillgapguide.sgg.Dto.FeedbackListResponse;
import com.skillgapguide.sgg.Entity.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback,Integer> {
    @Query(value = "select u.email as email,f.content as content,f.star as star from Feedback as f join User as u on f.userId = u.userId")
    Page<FeedbackListResponse> getAllFeedback(Pageable pageable);
    @Query(value = "select u.email as email,f.content as content,f.star as star from Feedback as f join User as u on f.userId = u.userId" +
            " where f.star = :star")
    Page<FeedbackListResponse> getAllFeedbackByStar(int star,Pageable pageable);
    @Query(value = "select u.email as email,f.content as content,f.star as star,u.fullName as name,f.createAt as time from Feedback as f join User as u on f.userId = u.userId" +
            " where u.email = :email")
    FeedbackDetailResponse getFeedbackDetail(String email);
}
