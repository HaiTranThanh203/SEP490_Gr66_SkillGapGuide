package com.skillgapguide.sgg.Repository;


import com.skillgapguide.sgg.Dto.UserDetailDTO;
import com.skillgapguide.sgg.Dto.UserListRequest;
import com.skillgapguide.sgg.Dto.UserListResponse;
import com.skillgapguide.sgg.Entity.User;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    @Query(value = "select u.email as email, u.fullName as name, u.phone as phone,r.name as role, us.name as status from User as u join Role as r on u.roleId = r.roleId join UserStatus as us on u.status = us")
    Page<UserListResponse> getAllUser(Pageable pageable);
    @Query(value = "select u.email as email, u.fullName as name, u.phone as phone,r.name as role, us.name as status " +
            "from User as u join Role as r on u.roleId = r.roleId join UserStatus as us on u.status = us " +
            "WHERE (:keyword IS NULL OR LOWER(u.fullName) LIKE  CONCAT('%', :keyword, '%') OR LOWER(u.email) LIKE  CONCAT('%', :keyword, '%')) " +
            "AND ((:role IS NULL or :role = '') OR r.name = :role) " +
            "AND ((:status IS NULL or :status = '') OR us.name = :status)")
    Page<UserListResponse> filterUser(@Param("keyword") String keyword,
                                      @Param("role") String role,
                                      @Param("status") String status, Pageable pageable);
    @Query(value = "select new com.skillgapguide.sgg.Dto.UserDetailDTO(u.fullName, u.email, u.phone, r.name, sub.type, us.name) from User as u join Role as r on u.roleId = r.roleId join UserStatus as us on " +
            "u.status = us join Subscription  as sub on u.subscriptionId = sub.subscriptionId " +
            "where u.email = :email")
    UserDetailDTO getUserDetail(String email);
    Optional<User> findByPhone(String phone);


}
