package com.skillgapguide.sgg.Controller;

import com.skillgapguide.sgg.Dto.UserFileHistoryDTO;
import com.skillgapguide.sgg.Response.EHttpStatus;
import com.skillgapguide.sgg.Response.Response;
import com.skillgapguide.sgg.Service.FileHistoryService;
import com.skillgapguide.sgg.Service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/file")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class FileHistoryController {
    private final FileHistoryService fileHistoryService;
    private final UserService userService;

    @GetMapping("/history")
    public ResponseEntity<Response<UserFileHistoryDTO>> getCurrentUserFileHistory() {
        Integer userId = userService.getUserIdFromContext();
        UserFileHistoryDTO result = fileHistoryService.getUserFileHistory(userId);
        return ResponseEntity.ok(new Response<>(EHttpStatus.OK, "Tìm kiếm thành công", result));
    }
}
