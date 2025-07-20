package com.skillgapguide.sgg.Service;

import com.skillgapguide.sgg.Entity.Cv;
import com.skillgapguide.sgg.Entity.User;
import com.skillgapguide.sgg.Repository.CVRepository;
import com.skillgapguide.sgg.Repository.JobCategoryRepository;
import com.skillgapguide.sgg.Repository.JobRepository;
import com.skillgapguide.sgg.Repository.SpecializationRepository;
import com.skillgapguide.sgg.Repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.skillgapguide.sgg.Entity.Job;
import com.skillgapguide.sgg.Entity.JobCategory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.transaction.annotation.Transactional;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.JavascriptExecutor;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobScrapingService {
    private final JobRepository jobRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CVRepository cvRepository;
    @Autowired
    private JobDeleteService jobDeleteService;
//    private final JobCategoryRepository jobCategoryRepository;
    private final SpecializationRepository specializationRepository;
    private final JobCategoryRepository jobCategoryRepository;

    @Transactional // Đảm bảo các thao tác DB được thực hiện trong một giao dịch
    public void scrapeAndSaveJob(String jobDetailUrl) {
        if (specializationRepository.existsSpecializationByUrl(jobDetailUrl)) {
            System.out.println(">>> CÔNG VIỆC ĐÃ TỒN TẠI, BỎ QUA: " + jobDetailUrl);
            return; // Dừng thực thi phương thức ngay lập tức.
        }
        System.setProperty("webdriver.chrome.driver", "sgg/drivers/chromedriver.exe"); // Cập nhật đường dẫn đến chromedriver
        // Cấu hình Chrome để tránh bị phát hiện là bot
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-extensions");
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36");
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        WebDriver driver = null;
        try {
            // Khởi tạo trình duyệt Chrome với các cấu hình
            driver = new ChromeDriver(options);
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Ẩn automation indicator để tránh bị phát hiện
            js.executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");

            // 1. Mở URL bằng Selenium
            System.out.println("🔍 Đang truy cập: " + jobDetailUrl);
            driver.get(jobDetailUrl);

            // Chờ trang load với timeout dài hơn
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(25));

            // Đợi cho title xuất hiện
            By titleSelector = By.cssSelector("h1.job-detail__info--title");
            wait.until(ExpectedConditions.visibilityOfElementLocated(titleSelector));

            // Đợi cho category xuất hiện
            By categorySelector = By.cssSelector("div.job-detail__company--information-item.company-field div.company-value");
            wait.until(ExpectedConditions.visibilityOfElementLocated(categorySelector));

            // Đợi cho description xuất hiện - QUAN TRỌNG
            By descriptionSelector = By.cssSelector("div.job-description__item--content");
            wait.until(ExpectedConditions.visibilityOfElementLocated(descriptionSelector));

            // Scroll để trigger lazy loading nếu có
            js.executeScript("window.scrollTo(0, document.body.scrollHeight/2);");
            Thread.sleep(1000);
            js.executeScript("window.scrollTo(0, document.body.scrollHeight);");

            // Thêm thời gian chờ dài hơn để đảm bảo content load đầy đủ
            Thread.sleep(3000); // Tăng từ 2s lên 3s
            // 2. Lấy HTML của trang sau khi đã được render đầy đủ
            String pageSource = driver.getPageSource();

            // === 3. Phân tích và trích xuất dữ liệu với error handling chi tiết ===
            Document doc = Jsoup.parse(pageSource);

            String title = "";
            String company = "";
            String categoryName = "";
            String fullDescription = "";

            try {
                Element titleElement = doc.selectFirst("h1.job-detail__info--title");
                title = titleElement != null ? titleElement.text().trim() : "";
                if (title.isEmpty()) {
                    System.out.println("⚠️ WARNING: Không tìm thấy title cho job: " + jobDetailUrl);
                }
            } catch (Exception e) {
                System.out.println("❌ ERROR: Lỗi khi lấy title: " + e.getMessage());
            }

            try {
                Element companyElement = doc.selectFirst("a.name");
                company = companyElement != null ? companyElement.text().trim() : "";
                if (company.isEmpty()) {
                    System.out.println("⚠️ WARNING: Không tìm thấy company cho job: " + jobDetailUrl);
                }
            } catch (Exception e) {
                System.out.println("❌ ERROR: Lỗi khi lấy company: " + e.getMessage());
            }

            try {
                Element categoryElement = doc.selectFirst("div.job-detail__company--information-item.company-field div.company-value");
                categoryName = categoryElement != null ? categoryElement.text().trim() : "Khác";
                if (categoryName.isEmpty()) {
                    categoryName = "Khác";
                }
            } catch (Exception e) {
                categoryName = "Khác";
                System.out.println("❌ ERROR: Lỗi khi lấy category: " + e.getMessage());
            }

            // === Cải thiện việc lấy description với nhiều fallback strategies ===
            try {
                StringBuilder descriptionBuilder = new StringBuilder();

                // Strategy 1: Selector chính
                Elements descriptionItems = doc.select("div.job-description__item--content p, div.job-description__item--content div, div.job-description__item--content li, div.job-description__item--content span");

                if (descriptionItems.isEmpty()) {
                    // Strategy 2: Fallback selector
                    descriptionItems = doc.select("div.job-description p, div.job-description div, div.job-description li");
                }

                if (descriptionItems.isEmpty()) {
                    // Strategy 3: Selector tổng quát hơn
                    descriptionItems = doc.select("[class*=job-description] p, [class*=job-description] div, [class*=job-description] li");
                }

                if (descriptionItems.isEmpty()) {
                    // Strategy 4: Lấy toàn bộ job-description container
                    Element descElement = doc.selectFirst("div[class*=job-description]");
                    if (descElement != null) {
                        fullDescription = descElement.html().trim();
                    }
                } else {
                    // Xử lý từng element và filter content có ý nghĩa
                    for (Element item : descriptionItems) {
                        String itemHtml = item.html().trim();
                        // Chỉ lấy content có ý nghĩa (> 10 chars và không phải whitespace)
                        if (!itemHtml.isEmpty() && itemHtml.length() > 10 && !itemHtml.matches("\\s*")) {
                            descriptionBuilder.append(itemHtml).append("\n");
                        }
                    }
                    fullDescription = descriptionBuilder.toString().trim();
                }

                // Logging chi tiết để debug
                if (fullDescription.isEmpty()) {
                    System.out.println("⚠️ WARNING: Description TRỐNG cho job: " + title + " | URL: " + jobDetailUrl);
                    // Debug info
                    Elements debugElements = doc.select("div[class*=description]");
                    System.out.println("🔍 DEBUG: Tìm thấy " + debugElements.size() + " elements chứa 'description'");
                    if (!debugElements.isEmpty()) {
                        Element first = debugElements.first();
                        System.out.println("🔍 DEBUG: Class đầu tiên: " + first.className());
                        String preview = first.text();
                        if (preview.length() > 100) {
                            System.out.println("🔍 DEBUG: Preview text: " + preview.substring(0, 100) + "...");
                        } else {
                            System.out.println("🔍 DEBUG: Preview text: " + preview);
                        }
                    }
                } else {
                    System.out.println("✅ INFO: Job '" + title + "' - Description: " + fullDescription.length() + " ký tự");
                }

            } catch (Exception e) {
                System.out.println("❌ ERROR: Lỗi khi lấy description cho job '" + title + "': " + e.getMessage());
                e.printStackTrace();
                fullDescription = "";
            }

            // === 4. Lưu vào database nếu có đủ thông tin ===
            if (!title.isEmpty() && !company.isEmpty()) {
                String finalCategoryName = categoryName;
                String email = SecurityContextHolder.getContext().getAuthentication().getName(); // lấy từ JWT
                Integer userId = userRepository.findByEmail(email).map(User::getUserId).orElseThrow(() -> new RuntimeException("User not found"));
                Cv cv = cvRepository.findByUserId(userId);
                if (cv == null) {
                    throw new IllegalStateException("Chưa upload cv");
                }
                jobDeleteService.deleteJob();
                Job job = new Job();
                job.setTitle(title);
                job.setCvId(cv.getId());
                job.setCompany(company);
                job.setDescription(fullDescription);
                job.setStatus("ACTIVE");
                job.setSourceUrl(jobDetailUrl);
                jobRepository.save(job);

                System.out.println("✅ ĐÃ LƯU THÀNH CÔNG: " + title + " | " + company + " | Description: " + fullDescription.length() + " ký tự");
            } else {
                System.out.println("❌ KHÔNG THỂ LƯU: Thiếu thông tin cơ bản cho " + jobDetailUrl);
                System.out.println("   - Title: " + (title.isEmpty() ? "THIẾU" : "✓"));
                System.out.println("   - Company: " + (company.isEmpty() ? "THIẾU" : "✓"));
            }

        } catch (Exception e) {
            System.err.println("❌ LỖI NGHIÊM TRỌNG khi cào job: " + jobDetailUrl);
            e.printStackTrace();
        } finally {
            // Rất quan trọng: Luôn đóng trình duyệt sau khi dùng xong để giải phóng bộ nhớ
            if (driver != null) {
                driver.quit();
            }
        }
    }

    public List<String> scrapeJobLinksFromListPage(String listPageUrl) {
        System.setProperty("webdriver.chrome.driver", "sgg/drivers/chromedriver.exe"); // Đường dẫn chromedriver của bạn
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36");

        WebDriver driver = null;
        List<String> jobLinks = new ArrayList<>();

        try {
            driver = new ChromeDriver(options);
            driver.get(listPageUrl);

            // Đợi cho đến khi danh sách job hiển thị (chỉ cần 1 job là đủ)
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("div.job-item-search-result.bg-highlight.job-ta div.body div.body-box div.body-content div.title-block div h3.title a")
            ));

            // Lấy tất cả các link job
            List<WebElement> linkElements = driver.findElements(
                    By.cssSelector("div.job-item-search-result.bg-highlight.job-ta div.body div.body-box div.body-content div.title-block div h3.title a")

            );

            jobLinks = linkElements.stream()
                    .map(e -> e.getAttribute("href"))
                    .filter(href -> href != null && !href.isEmpty())
                    .distinct()
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("Lỗi khi lấy danh sách link job từ: " + listPageUrl);
            e.printStackTrace();
        } finally {
            if (driver != null) driver.quit();
        }
        return jobLinks;
    }

    @Transactional
    public void scrapeAndSaveTop10JobsByCategory(String categoryListUrl) {
        int n = 10;
        // 1. Lấy danh sách link job từ trang category (danh sách việc làm theo lĩnh vực)
        List<String> jobLinks = scrapeJobLinksFromListPage(categoryListUrl);

        if (jobLinks == null || jobLinks.isEmpty()) {
            System.out.println("Không tìm thấy job nào ở URL: " + categoryListUrl);
            return;
        }

        // 2. Chỉ lấy tối đa n link đầu tiên
        final List<String> topNJobLinks = jobLinks.stream().limit(n).collect(Collectors.toList());
        System.out.println("🚀 BẮT ĐẦU CÀO " + topNJobLinks.size() + " JOBS");

        // 3. Lặp và crawl từng job với delay ngẫu nhiên
        int count = 0;
        for (final String jobUrl : topNJobLinks) {
            try {
                System.out.println("\n" + "=".repeat(80));
                System.out.println("📝 CÀO JOB " + (count + 1) + "/" + topNJobLinks.size() + ": " + jobUrl);
                System.out.println("=".repeat(80));

                scrapeAndSaveJob(jobUrl);
                count++;

                // Thêm delay ngẫu nhiên để tránh pattern detection
                if (count < topNJobLinks.size()) {
                    int randomDelay = 3000 + (int) (Math.random() * 2000); // 3-5 giây ngẫu nhiên
                    System.out.println("⏳ Chờ " + (randomDelay / 1000) + " giây trước khi cào job tiếp theo...");
                    Thread.sleep(randomDelay);
                }
            } catch (Exception e) {
                System.err.println("❌ Lỗi khi crawl job: " + jobUrl + " - " + e.getMessage());
                e.printStackTrace();
                // Delay dài hơn khi có lỗi để tránh bị chặn
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        System.out.println("\n🎉 HOÀN THÀNH: Đã crawl " + count + "/" + topNJobLinks.size() + " jobs từ: " + categoryListUrl);

    }

    @Transactional
    public void scrapeAndSaveTop10JobsBySpecialization(String specializationName) {
        var specialization = specializationRepository.findByNameIgnoreCase(specializationName)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vị trí chuyên môn: " + specializationName));
        String url = specialization.getUrl();
        if (url == null || url.isEmpty()) {
            throw new RuntimeException("vị trí chuyên môn không tồn tại hoặc không có URL: " + specializationName);
        }
        scrapeAndSaveTop10JobsByCategory(url);
    }
}