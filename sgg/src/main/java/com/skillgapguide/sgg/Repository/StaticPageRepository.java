package com.skillgapguide.sgg.Repository;

import com.skillgapguide.sgg.Dto.StaticPageDTO;
import com.skillgapguide.sgg.Entity.Staticpage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface StaticPageRepository extends JpaRepository<Staticpage,Integer> {
    @Query(value = "select new com.skillgapguide.sgg.Dto.StaticPageDTO(s.title,s.content) from Staticpage as s where s.name = :name")
    List<StaticPageDTO> findByName(String name);
    @Modifying
    @Transactional
    @Query(value = "update  Staticpage s set s.content= :content, s.updateAt = CURRENT_TIMESTAMP, s.updateBy =:updateBy where s.name=:name and s.title=:title")
    int updateByNameAndTitle(@Param("name") String name,
                             @Param("title") String title,
                             @Param("content") String content,
                             @Param("updateBy") Integer updateBy);
    boolean existsByNameAndTitle(String name, String title);
}
