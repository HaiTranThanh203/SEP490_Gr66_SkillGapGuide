package com.skillgapguide.sgg.Controller;

import com.skillgapguide.sgg.Dto.CourseDTO;
import com.skillgapguide.sgg.Entity.Course;
import com.skillgapguide.sgg.Response.EHttpStatus;
import com.skillgapguide.sgg.Response.Response;
import com.skillgapguide.sgg.Service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;
    private final OAuth2AuthorizedClientService authorizedClientService;
    @GetMapping("/findById/{courseId}")
    public Response<Course> getCourseById(@PathVariable Integer courseId) {
        return new Response<>(EHttpStatus.OK, "Lấy thông tin khóa học thành công", courseService.getCourseById(courseId));
    }
    @GetMapping("/findAllFavoriteCourses/{userId}")
    public Response<?> findAllFavoriteCourses(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
        return new Response<>(EHttpStatus.OK, "Lấy danh sách tất cả khóa học yêu thích thành công",
                courseService.getFavoriteCoursesByUserId(userId, pageNo, pageSize));
    }
    @PostMapping("/addCourseToFavorites/{userId}/{courseId}")
    public Response<?> addCourseToFavorites(@PathVariable Integer userId, @PathVariable Integer courseId) {
        return new Response<>(EHttpStatus.OK, "Thêm khóa học vào danh sách yêu thích thành công",
                courseService.addCourseToFavorites(userId, courseId));
    }
    @PostMapping("/hideCourse/{courseId}")
    public Response<?> hideCourse(@PathVariable Integer courseId) {
        courseService.hideCourse(courseId);
        return new Response<>(EHttpStatus.OK, "Khóa học đã được ẩn thành công", "HIDDEN");
    }
    @PostMapping("/showCourse/{courseId}")
    public Response<?> showCourse(@PathVariable Integer courseId) {
        courseService.showCourse(courseId);
        return new Response<>(EHttpStatus.OK, "Khóa học đã được hiển thị thành công", "AVAILABLE");
    }
    @PostMapping("/addCourseManually")
    public Response<Course> addCourseManually(@RequestBody CourseDTO course) {
        Course addedCourse = courseService.addCourseManually(course);
        return new Response<>(EHttpStatus.OK, "Thêm khóa học thành công", addedCourse);
    }
    @DeleteMapping("/deleteFavoriteCourse/{userId}/{courseId}")
    public Response<?> deleteFavoriteCourse(@PathVariable Integer userId, @PathVariable Integer courseId) {
        courseService.removeFavoriteCourse(userId, courseId);
        return new Response<>(EHttpStatus.OK, "Xóa khóa học khỏi danh sách yêu thích thành công", null);
    }
    @DeleteMapping("/deleteAllFavoriteCourse/{userId}")
    public Response<?> deleteAllFavoriteCourse(@PathVariable Integer userId) {
        courseService.removeAllFavoriteCourses(userId);
        return new Response<>(EHttpStatus.OK, "Xóa tất cả khóa học khỏi danh sách yêu thích thành công", null);
    }

}
