package com.skillgapguide.sgg.Controller;

import com.skillgapguide.sgg.Dto.CourseDTO;
import com.skillgapguide.sgg.Dto.ScrapeResultDTO;
import com.skillgapguide.sgg.Entity.Course;
import com.skillgapguide.sgg.Response.EHttpStatus;
import com.skillgapguide.sgg.Response.Response;
import com.skillgapguide.sgg.Service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;
    @GetMapping("/getAllCourses")
    public Response<?> getAllCourses(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
        return new Response<>(EHttpStatus.OK, "Lấy danh sách tất cả khóa học thành công",
                courseService.getAllCourses(pageNo, pageSize));
    }
    @GetMapping("/findById/{courseId}")
    public Response<Course> getCourseById(@PathVariable Integer courseId) {
        return new Response<>(EHttpStatus.OK, "Lấy thông tin khóa học thành công", courseService.getCourseById(courseId));
    }

    @GetMapping("/findAllFavoriteCourses/{userId}")
    public Response<?> findAllFavoriteCourses(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "1") int pageNo,
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
    @PostMapping("/changeFavoriteCourseStatus/{courseId}/{userId}/{status}")
    public Response<?> changeFavoriteCourseStatus(@PathVariable Integer courseId,@PathVariable Integer userId, @PathVariable String status) {
        courseService.changeFavoriteCourseStatus(courseId,userId, status);
        return new Response<>(EHttpStatus.OK, "Thay đổi trạng thái khóa học thành công", null);
    }
    @PostMapping("/scrape")
    public Response<?> scrapeCourses(@RequestParam(defaultValue = "1") int numPages,
                                     @RequestParam(defaultValue = "9") int numItems,
                                     @RequestParam int cvId) {
        try {
            ScrapeResultDTO result = courseService.scrapeAndSaveCoursesByCvId(numPages, numItems, cvId);
            return new Response<>(EHttpStatus.OK, "Đã cào và lưu thành công course", result);
        } catch (Exception e) {
            return new Response<>(EHttpStatus.BAD_REQUEST, "Lỗi xảy ra khi cào dữ liệu: " + e.getMessage(), null);
        }
    }
}
