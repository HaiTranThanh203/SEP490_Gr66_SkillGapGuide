package com.skillgapguide.sgg.Repository;

import com.skillgapguide.sgg.Entity.UserSubscriptionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSubscriptionHistoryRepository extends JpaRepository<UserSubscriptionHistory, Integer> {
    Optional<UserSubscriptionHistory> findTopByUser_UserIdAndStatusOrderByStartDateDesc(Integer userId, String status);

}
