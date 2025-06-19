package com.skillgapguide.sgg.Controller;

import com.skillgapguide.sgg.Dto.ChangePasswordRequest;
import com.skillgapguide.sgg.Dto.ForgotPasswordRequest;
import com.skillgapguide.sgg.Dto.RegisterRequest;
import com.skillgapguide.sgg.Dto.UserDTO;
import com.skillgapguide.sgg.Response.EHttpStatus;
import com.skillgapguide.sgg.Response.Response;
import com.skillgapguide.sgg.Service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    @Value("${application.frontend-url}")
    private String frontendUrl;
    private final UserService userService;

    @PostMapping("/forgot-password")
    public Response<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        String email = userService.forgotPassword(request);
        return new Response<>(
                EHttpStatus.OK,
                "Email đặt lại mật khẩu đã được gửi",
                "Email đặt lại mật khẩu đã được gửi đến " + email
        );
    }

    @GetMapping("/reset-password")
    public Response<String> resetPassword(
            @RequestParam("token") String token,
            @RequestParam String newPassword
    ) {
        userService.resetPassword(token, newPassword);
        return new Response<>(
                EHttpStatus.OK,
                "Mật khẩu đã được đặt lại thành công",
                "Vui lòng đăng nhập với mật khẩu mới"
        );
    }
    @PostMapping("/changePassword")
    @SecurityRequirement(name = "Bearer Authentication")
    public Response<String> changePassword(
            @RequestBody ChangePasswordRequest request
    ) {
        userService.changePassword(request);
        return new Response<>(
                EHttpStatus.OK,
                "Mật khẩu đã được thay đổi thành công",
                "Vui lòng đăng nhập với mật khẩu mới"
        );
    }
}
