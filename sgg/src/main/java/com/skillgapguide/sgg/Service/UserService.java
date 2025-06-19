package com.skillgapguide.sgg.Service;


import com.skillgapguide.sgg.Dto.*;
import com.skillgapguide.sgg.Entity.*;
import com.skillgapguide.sgg.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class  UserService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private  final UserStatusRepository userStatusRepository;
    private final UserSubscriptionHistoryRepository userSubscriptionHistoryRepository;
    private final RoleRepository roleRepository;
    @Value("${application.base-url}")
    private String baseUrl;
    @Value("${application.frontend-url}")
    private String frontendUrl;


    public String forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalStateException("Email không chính xác, vui lòng kiểm tra lại."));

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(
                token,
                user,
                LocalDateTime.now().plusMinutes(3)
        );
        verificationTokenRepository.save(verificationToken);

        String verifyLink = frontendUrl + "/reset-password?token=" + token;
        emailService.sendMail(
                request.email(),
                "Đặt lại mật khẩu",
                "Click vào đây để thay đổi mật khẩu: " + verifyLink
        );

        return request.email();
    }

    public void resetPassword(String token, String newPassword) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalStateException("Token không hợp lệ"));

        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            verificationTokenRepository.delete(verificationToken);
            throw new IllegalStateException("Token đã hết hạn");
        }

        User user = verificationToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);
    }
    public void changePassword(ChangePasswordRequest request) {
        // Get currently logged in user from Security Context
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Người dùng không tồn tại"));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new IllegalStateException("Mật khẩu cũ không chính xác");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }
    public Page<UserListResponse> getAllUser(UserListRequest userFilterRequest){
        try{
            Pageable paging = PageRequest.of(userFilterRequest.getPageNo(), userFilterRequest.getPageSize());
            if ((userFilterRequest.getSearchText() == null || userFilterRequest.getSearchText().isBlank())
                    && (userFilterRequest.getRole() == null || userFilterRequest.getRole().isBlank())
                    && (userFilterRequest.getStatus() == null || userFilterRequest.getStatus().isBlank())) {
                return userRepository.getAllUser(paging);
            }
            Page<UserListResponse> test = userRepository.filterUser(userFilterRequest.getSearchText() == null ? "" : userFilterRequest.getSearchText().toLowerCase(),
                    userFilterRequest.getRole(),
                    userFilterRequest.getStatus(),
                    paging);
            return userRepository.filterUser(userFilterRequest.getSearchText() == null ? "" : userFilterRequest.getSearchText().toLowerCase(),
                    userFilterRequest.getRole(),
                    userFilterRequest.getStatus(),
                    paging);
        } catch (Exception e){
            throw new IllegalStateException("Error !êêê! "+e.getMessage());
        }
    }
    public String disableAccount(String email){
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalStateException("Người dùng không tồn tại"));
            UserStatus newStatus = userStatusRepository.findById(3)
                    .orElseThrow(() -> new IllegalStateException("Trạng thái không tồn tại"));
            user.setStatus(newStatus);
            userRepository.save(user);
            return "Cập nhật thành công";
        } catch (Exception e){
            return "Lỗi cập nhật" + e.getMessage();
        }
    }
    public String enableAccount(String email){
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalStateException("Người dùng không tồn tại"));
            UserStatus newStatus = userStatusRepository.findById(2)
                    .orElseThrow(() -> new IllegalStateException("Trạng thái không tồn tại"));
            user.setStatus(newStatus);
            userRepository.save(user);
            return "Cập nhật thành công";
        } catch (Exception e){
            return "Lỗi cập nhật" + e.getMessage();
        }
    }
    public UserDetailDTO getUserDetail(String email){
        try{
            return userRepository.getUserDetail(email);
        } catch (Exception e){
            throw new IllegalStateException("Error !êêê! "+e.getMessage());
        }
    }
    public UserDTO getCurrentUserProfile(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(email);
        userDTO.setFullName(user.getFullName());
        userDTO.setPhone(user.getPhone());
        userDTO.setAvatar(user.getAvatar());

        // Role mapping
        switch (user.getRoleId()) {
            case 1 -> userDTO.setRole("System Admin");
            case 2 -> userDTO.setRole("Business Admin");
            case 3 -> userDTO.setRole("Free User");
            case 4 -> userDTO.setRole("Premium User");
            default -> userDTO.setRole("Unknown");
        }
        return userDTO;
    }

    public void updateCurrentUserProfile(UpdateProfileRequest request){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setAvatar(request.getAvatar());

        userRepository.save(user);
    }

    public UserSubscriptionDTO getUserSubscription(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        UserSubscriptionDTO dto = new UserSubscriptionDTO();
        dto.setFullName(user.getFullName());

        switch (user.getRoleId()) {
            case 3 -> {
                dto.setRole("Free User");
                dto.setPremium(false);
            }
            case 4 -> {
                dto.setRole("Premium User");
                dto.setPremium(true);

                // Lấy thông tin đăng ký từ bảng lịch sử
                userSubscriptionHistoryRepository.findTopByUser_UserIdAndStatusOrderByStartDateDesc(user.getUserId(), "ACTIVE")
                        .ifPresent(history -> {
                            dto.setSubscriptionStart(history.getStartDate());
                            dto.setSubscriptionEnd(history.getEndDate());
                        });
            }
            default -> dto.setRole("Khác");
        }
        return dto;
    }

    public String updateUserRole(UserRoleUpdateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        if(user.getStatus().getStatusId() != 2){
            return "Chỉ người dùng VERIFIED mới có thể cập nhật vai trò!";
        }

        Role role = roleRepository.findById(request.getNewRoleId())
                .orElseThrow(() -> new RuntimeException("Vai trò không tồn tại"));

        user.setRoleId(role.getRoleId());
        userRepository.save(user);

        return "Cập nhật Role thành công";
    }

    public String createAdmin(CreateAdminRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return "Email đã tồn tại!";
        }

        if (userRepository.findByPhone(request.getPhone()).isPresent()) {
            return "Số điện thoại đã tồn tại!";
        }

        // Kiểm tra role
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò với id: " + request.getRoleId()));

        if (!role.getName().contains("Admin")) {
            return "Chỉ có thể tạo các vai trò dạng Admin!";
        }

        // Trạng thái VERIFIED
        UserStatus status = userStatusRepository.findByName("VERIFIED")
                .orElseThrow(() -> new RuntimeException("Trạng thái VERIFIED không tồn tại"));

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setRoleId(role.getRoleId());
        user.setSubscriptionId(2);
        user.setStatus(status);
        user.setAvatar(null);
        user.setProvider(User.Provider.valueOf("LOCAL"));

        userRepository.save(user);

        return "Tạo admin thành công";
    }
}