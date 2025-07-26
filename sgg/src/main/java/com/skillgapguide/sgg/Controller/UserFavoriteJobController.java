package com.skillgapguide.sgg.Controller;

import com.skillgapguide.sgg.Dto.FavoriteJobDTO;
import com.skillgapguide.sgg.Dto.ViewFavoriteJobDTO;
import com.skillgapguide.sgg.Response.EHttpStatus;
import com.skillgapguide.sgg.Response.Response;
import com.skillgapguide.sgg.Service.FavoriteJobService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorite-job")
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
public class UserFavoriteJobController {
    private final FavoriteJobService favoriteJobService;

    @PostMapping("/add/{jobId}")
    public ResponseEntity<Response<FavoriteJobDTO>> addFavoriteJob(@PathVariable Integer jobId) {
        try {
            FavoriteJobDTO dto = favoriteJobService.addFavoriteJob(jobId);
            return ResponseEntity.ok(new Response<>(EHttpStatus.OK, "Đã thêm vào danh sách yêu thích", dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new Response<>(EHttpStatus.BAD_REQUEST, e.getMessage(), null));
        }
    }

    @GetMapping("/list")
    public ResponseEntity<Response<List<ViewFavoriteJobDTO>>> listFavoriteJob() {
        try {
            List<ViewFavoriteJobDTO> favoriteJobs = favoriteJobService.getFavoriteJobs();
            return ResponseEntity.ok(new Response<>(EHttpStatus.OK, "Hiển thị danh sách yêu thích thành công", favoriteJobs));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new Response<>(EHttpStatus.BAD_REQUEST, e.getMessage(), null));
        }
    }

    @DeleteMapping("/remove/{jobId}")
    public ResponseEntity<Response<Void>> removeFavoriteJob(@PathVariable Integer jobId) {
        try {
            favoriteJobService.removeFavoriteJob(jobId);
            return ResponseEntity.ok(new Response<>(EHttpStatus.OK, "Xoá công việc khỏi danh sách yêu thích thành công", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new Response<>(EHttpStatus.BAD_REQUEST, e.getMessage(), null));
        }
    }



}
