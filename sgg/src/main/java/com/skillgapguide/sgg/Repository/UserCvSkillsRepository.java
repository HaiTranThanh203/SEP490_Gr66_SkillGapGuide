package com.skillgapguide.sgg.Repository;

import com.skillgapguide.sgg.Entity.UserCvSkills;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCvSkillsRepository extends JpaRepository<UserCvSkills,Integer> {
    List<UserCvSkills> findByCvId(int cvId);
    void deleteAllByCvId(int cvId);
}
