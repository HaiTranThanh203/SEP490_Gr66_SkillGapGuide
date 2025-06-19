package com.skillgapguide.sgg.Controller;

import com.skillgapguide.sgg.Dto.UpdateProfileRequest;
import com.skillgapguide.sgg.Dto.UserDTO;
import com.skillgapguide.sgg.Dto.UserSubscriptionDTO;
import com.skillgapguide.sgg.Service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class ProfileController {
    private final UserService userService;

    @GetMapping("/viewprofile")
    public ResponseEntity<UserDTO> getProfile() {
        UserDTO dto = userService.getCurrentUserProfile();
        return ResponseEntity.ok(dto);
    }


    @PutMapping("/update")
    public ResponseEntity<String> updateProfile(@RequestBody UpdateProfileRequest request) {
        userService.updateCurrentUserProfile(request);
        return ResponseEntity.ok("Cập nhật thông tin cá nhân thành công");
    }

    @GetMapping("/subscription")
    public ResponseEntity<UserSubscriptionDTO> getUserSubscription() {
        UserSubscriptionDTO dto = userService.getUserSubscription();
        return ResponseEntity.ok(dto);
    }
}
