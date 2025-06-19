package com.skillgapguide.sgg.Controller;

import com.skillgapguide.sgg.Dto.CreateAdminRequest;
import com.skillgapguide.sgg.Dto.UserRoleUpdateRequest;
import com.skillgapguide.sgg.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/systemadmin")
@RequiredArgsConstructor
public class SystemAdminController {
    private final UserService userService;

    @PostMapping("/change-role")
    public ResponseEntity<?> updateUserRole(@RequestBody UserRoleUpdateRequest request) {
        String result = userService.updateUserRole(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/create-admin")
    public ResponseEntity<String> createAdmin(@RequestBody CreateAdminRequest request) {
        String result = userService.createAdmin(request);
        return ResponseEntity.ok(result);
    }
}
