package com.skillgapguide.sgg.Service;

import com.skillgapguide.sgg.Entity.Course;
import com.skillgapguide.sgg.Entity.UserFavoriteCourse;
import com.skillgapguide.sgg.Repository.CourseRepository;
import com.skillgapguide.sgg.Repository.FavoriteCourseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private FavoriteCourseRepository favoriteCourseRepository;

    @InjectMocks
    private CourseService courseService;

    @Test
    void getCourseByIdReturnsCourseWhenExists() {
        Integer courseId = 1;
        Course mockCourse = new Course();
        mockCourse.setCourseId(courseId);

        when(courseRepository.findCourseByCourseId(courseId)).thenReturn(mockCourse);

        Course result = courseService.getCourseById(courseId);

        assertNotNull(result);
        assertEquals(courseId, result.getCourseId());
    }

    @Test
    void getCourseByIdThrowsExceptionWhenNotFound() {
        Integer courseId = 999;

        when(courseRepository.findCourseByCourseId(courseId)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> courseService.getCourseById(courseId));
    }

    @Test
    void getAllCoursesReturnsPagedResults() {
        int pageNo = 1;
        int pageSize = 10;
        Page<Course> mockPage = new PageImpl<>(List.of());

        when(courseRepository.findAll(any(Pageable.class))).thenReturn(mockPage);

        Page<Course> result = courseService.getAllCourses(pageNo, pageSize);

        assertNotNull(result);
        verify(courseRepository).findAll(any(Pageable.class));
    }

    @Test
    void addCourseToFavoritesThrowsExceptionWhenCourseNotFound() {
        Integer userId = 1;
        Integer courseId = 999;

        when(courseRepository.findCourseByCourseId(courseId)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> courseService.addCourseToFavorites(userId, courseId));
    }

    @Test
    void addCourseToFavoritesThrowsExceptionWhenAlreadyFavorited() {
        Integer userId = 1;
        Integer courseId = 1;
        Course mockCourse = new Course();
        mockCourse.setCourseId(courseId);

        when(courseRepository.findCourseByCourseId(courseId)).thenReturn(mockCourse);
        when(favoriteCourseRepository.findByUserIdAndCourseId(userId, courseId)).thenReturn(Optional.of(new UserFavoriteCourse()));

        assertThrows(IllegalStateException.class, () -> courseService.addCourseToFavorites(userId, courseId));
    }

    @Test
    void addCourseToFavoritesAddsCourseSuccessfully() {
        Integer userId = 1;
        Integer courseId = 1;
        Course mockCourse = new Course();
        mockCourse.setCourseId(courseId);
        UserFavoriteCourse mockFavoriteCourse = new UserFavoriteCourse();

        when(courseRepository.findCourseByCourseId(courseId)).thenReturn(mockCourse);
        when(favoriteCourseRepository.findByUserIdAndCourseId(userId, courseId)).thenReturn(Optional.empty());
        when(favoriteCourseRepository.save(any(UserFavoriteCourse.class))).thenReturn(mockFavoriteCourse);

        UserFavoriteCourse result = courseService.addCourseToFavorites(userId, courseId);

        assertNotNull(result);
        verify(favoriteCourseRepository).save(any(UserFavoriteCourse.class));
    }

    @Test
    void hideCourseThrowsExceptionWhenCourseNotFound() {
        Integer courseId = 999;

        when(courseRepository.findCourseByCourseId(courseId)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> courseService.hideCourse(courseId));
    }

    @Test
    void hideCourseUpdatesStatusSuccessfully() {
        Integer courseId = 1;
        Course mockCourse = new Course();
        mockCourse.setCourseId(courseId);

        when(courseRepository.findCourseByCourseId(courseId)).thenReturn(mockCourse);

        courseService.hideCourse(courseId);

        assertEquals("HIDDEN", mockCourse.getStatus());
        verify(courseRepository).save(mockCourse);
    }

    @Test
    void showCourseThrowsExceptionWhenCourseNotFound() {
        Integer courseId = 999;

        when(courseRepository.findCourseByCourseId(courseId)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> courseService.showCourse(courseId));
    }

    @Test
    void showCourseUpdatesStatusSuccessfully() {
        Integer courseId = 1;
        Course mockCourse = new Course();
        mockCourse.setCourseId(courseId);

        when(courseRepository.findCourseByCourseId(courseId)).thenReturn(mockCourse);

        courseService.showCourse(courseId);

        assertEquals("AVAILABLE", mockCourse.getStatus());
        verify(courseRepository).save(mockCourse);
    }
}