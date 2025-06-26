package com.skillgapguide.sgg.Service;


import com.skillgapguide.sgg.Dto.CourseDTO;
import com.skillgapguide.sgg.Entity.Course;
import com.skillgapguide.sgg.Entity.UserFavoriteCourse;
import com.skillgapguide.sgg.Repository.CourseRepository;
import com.skillgapguide.sgg.Repository.FavoriteCourseRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final FavoriteCourseRepository favoriteCourseRepository;

    public Course getCourseById(Integer courseId) {
        return courseRepository.findCourseByCourseId(courseId);
    }

    public Page<UserFavoriteCourse> getFavoriteCoursesByUserId(Integer userId, int pageNo, int pageSize) {
        Pageable pageable = Pageable.ofSize(pageSize).withPage(pageNo);
        return favoriteCourseRepository.findByUserId(userId, pageable); // Hoặc findByIdUserId nếu dùng composite key
    }
    public UserFavoriteCourse addCourseToFavorites(Integer userId, Integer courseId) {
        // Kiểm tra xem khóa học có tồn tại không
        Course course = courseRepository.findCourseByCourseId(courseId);
        if (course == null) {
            throw new IllegalArgumentException("Khóa học không tồn tại");
        }

        // Kiểm tra xem cặp userId và courseId đã tồn tại trong danh sách yêu thích chưa
        Optional<UserFavoriteCourse> existingFavorite = favoriteCourseRepository.findByUserIdAndCourseId(userId, courseId);
        if (existingFavorite.isPresent()) {
            throw new IllegalStateException("Khóa học đã có trong danh sách yêu thích");
        }

        // Tạo bản ghi UserFavoriteCourse mới
        UserFavoriteCourse favoriteCourse = new UserFavoriteCourse();
        favoriteCourse.setUserId(userId);
        favoriteCourse.setCourse(course);
        favoriteCourse.setStatus("ACTIVE");
        favoriteCourse.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        // Lưu vào cơ sở dữ liệu
        return favoriteCourseRepository.save(favoriteCourse);
    }
    public void hideCourse(Integer courseId) {
        Course course = courseRepository.findCourseByCourseId(courseId);
        if (course == null) {
            throw new IllegalArgumentException("Khóa học không tồn tại");
        }
        course.setStatus("HIDDEN");
        courseRepository.save(course);
    }
    public void showCourse(Integer courseId) {
        Course course = courseRepository.findCourseByCourseId(courseId);
        if (course == null) {
            throw new IllegalArgumentException("Khóa học không tồn tại");
        }
        course.setStatus("AVAILABLE");
        courseRepository.save(course);
    }
    public Course addCourseManually(CourseDTO course) {
        Logger logger = LoggerFactory.getLogger(CourseService.class);

        // Input validation
        if (course == null ||
                course.getTitle() == null || course.getTitle().trim().isEmpty() ||
                course.getUrl() == null || course.getUrl().trim().isEmpty() ||
                course.getProvider() == null || course.getProvider().trim().isEmpty()) {
            throw new IllegalArgumentException("Course title, URL, and provider must not be empty");
        }

        // Check for existing course
        Optional<Course> existingCourse = courseRepository.findCourseByUrl(course.getUrl());
        if (existingCourse.isPresent()) {
            logger.warn("Attempt to add duplicate course with URL: {}", course.getUrl());
            throw new IllegalStateException("Course already exists");
        }

        Course newCourse = new Course();
        newCourse.setTitle(course.getTitle());
        newCourse.setRating(course.getRating());
        newCourse.setDifficulty(course.getDifficulty());
        newCourse.setDescription(course.getDescription());
        newCourse.setProvider(course.getProvider());
        newCourse.setUrl(course.getUrl());
        newCourse.setStatus("AVAILABLE");
        newCourse.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        logger.info("Adding new course: {}", newCourse.getTitle());
        return courseRepository.save(newCourse);
    }
    // At the top of the class
    private static final Logger logger = LoggerFactory.getLogger(CourseService.class);

    public void removeFavoriteCourse(Integer userId, Integer courseId) {
        Optional<UserFavoriteCourse> existingFavorite = favoriteCourseRepository.findByUserIdAndCourseId(userId, courseId);
        if (existingFavorite.isEmpty()) {
            logger.warn("Attempted to remove non-existent favorite: userId={}, courseId={}", userId, courseId);
            throw new IllegalStateException("Favorite course not found");
        }
        favoriteCourseRepository.delete(existingFavorite.get());
        logger.info("Removed favorite course: userId={}, courseId={}", userId, courseId);
    }

    public void removeAllFavoriteCourses(Integer userId) {
        Page<UserFavoriteCourse> favoriteCourses = favoriteCourseRepository.findByUserId(userId, Pageable.unpaged());
        favoriteCourseRepository.deleteAll(favoriteCourses.getContent());
        logger.info("Removed all favorite courses for userId={}", userId);
    }
    @Transactional
    public void scrapeAndSaveCourses(int numPages, int numItems) {
        System.setProperty("webdriver.chrome.driver", "sgg/drivers/chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36");

        WebDriver driver = null;
        try {
            driver = new ChromeDriver(options);
            for (int i = 1; i <= numPages; i++) {
                String url = "https://www.coursera.org/courses?page=" + i + "&index=prod_all_products_term_optimization";
                driver.get(url);
                String pageSource = driver.getPageSource();
                Document doc = Jsoup.parse(pageSource);

                Elements titles = doc.select("h3.cds-CommonCard-title.css-6ecy9b");
                Elements providers = doc.select("p.cds-ProductCard-partnerNames.css-vac8rf");
                Elements ratings = doc.select("div.cds-RatingStat-sizeLabel.css-1i7bybc");
                Elements diffs = doc.select("div.cds-CommonCard-metadata");
                Elements links = doc.select("a.cds-CommonCard-titleLink");
                Elements des = doc.select("div.cds-CommonCard-bodyContent");
                int count = Math.min(numItems, titles.size());
                for (int j = 0; j < count; j++) {
                    String courseTitle = titles.get(j).text();
                    String courseProvider = providers.size() > j ? providers.get(j).text() : "";
                    String courseRating = ratings.size() > j ? ratings.get(j).text() : "";
                    String courseDiff = diffs.size() > j ? diffs.get(j).text() : "";
                    String courseUrl = links.size() > j ? "https://www.coursera.org" + links.get(j).attr("href") : "";
                    String description = des.size() > j ? des.get(j).text() : "";
                    if (courseRepository.existsCourseByUrl(courseUrl)) {
                        logger.info("Khóa học đã tồn tại: {}", courseTitle);
                        continue; // Bỏ qua khóa học đã tồn tại
                    };

                    Course course = new Course();
                    course.setTitle(courseTitle);
                    course.setProvider(courseProvider);
                    course.setRating(courseRating);
                    course.setDifficulty(courseDiff);
                    course.setDescription(description);
                    course.setStatus("ACTIVE");
                    course.setUrl(courseUrl);
                    course.setCreatedAt(Timestamp.from(Instant.now()));

                    courseRepository.save(course);
                }
                 Thread.sleep(1000); // Nếu cần sleep giữa các lần lặp
            }
        } catch (Exception e) {
            // Nên dùng logger thay vì printStackTrace trong production
            e.printStackTrace();
        } finally {
            if (driver != null) driver.quit();
        }
    }
}
