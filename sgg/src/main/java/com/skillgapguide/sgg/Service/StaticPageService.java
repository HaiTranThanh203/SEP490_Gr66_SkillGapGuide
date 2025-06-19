package com.skillgapguide.sgg.Service;

import com.skillgapguide.sgg.Dto.StaticPageDTO;
import com.skillgapguide.sgg.Dto.UserListRequest;
import com.skillgapguide.sgg.Entity.Staticpage;
import com.skillgapguide.sgg.Entity.User;
import com.skillgapguide.sgg.Repository.StaticPageRepository;
import com.skillgapguide.sgg.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaticPageService {
    @Autowired
    private StaticPageRepository staticPageRepository;
    @Autowired
    private UserRepository userRepository;
    public List<StaticPageDTO> getContent(String name){
        return staticPageRepository.findByName(name);
    }
    public String updateContent(StaticPageDTO dto, String name){
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName(); // lấy từ JWT
            Integer updateByUserId = userRepository.findByEmail(email)
                    .map(User::getUserId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            if(!staticPageRepository.existsByNameAndTitle(name, dto.getTitle())){
                throw new IllegalStateException("Thông tin không hợp lệ");
            }
            staticPageRepository.updateByNameAndTitle(name, dto.getTitle(), dto.getContent(), updateByUserId);
            return "Cập nhật thành công";
        } catch (Exception e){
            throw new IllegalStateException("Lỗi cập nhật"+e.getMessage());
        }
    }
}
