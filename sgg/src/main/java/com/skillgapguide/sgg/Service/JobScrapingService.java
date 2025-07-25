// Java
package com.skillgapguide.sgg.Service;

import com.skillgapguide.sgg.Entity.Cv;
import com.skillgapguide.sgg.Entity.Job;
import com.skillgapguide.sgg.Entity.User;
import com.skillgapguide.sgg.Repository.CVRepository;
import com.skillgapguide.sgg.Repository.JobCategoryRepository;
import com.skillgapguide.sgg.Repository.JobRepository;
import com.skillgapguide.sgg.Repository.SpecializationRepository;
import com.skillgapguide.sgg.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.github.mabinogi233.undetected_chromedriver.ChromeDriverBuilder;

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
    private final SpecializationRepository specializationRepository;
    private final JobCategoryRepository jobCategoryRepository;

    private static final String CHROME_DRIVER_PATH = "sgg/drivers/chromedriver.exe";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36";
    private static final int TIMEOUT_SECONDS = 25;

    private WebDriver createChromeDriver() {
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--headless=new",
                "--disable-gpu",
                "--window-size=1920,1080",
                "--disable-blink-features=AutomationControlled",
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-extensions",
                "--user-agent=" + USER_AGENT
        );
        return new ChromeDriverBuilder().build(options, CHROME_DRIVER_PATH);
    }

    private void loadPageAndWait(WebDriver driver, WebDriverWait wait, String url) {
        driver.get(url);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h1.job-detail__info--title")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.job-detail__company--information-item.company-field div.company-value")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.job-description__item--content")));
        scrollToLoadContent(driver);
    }

    private void scrollToLoadContent(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0, 1000);");
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        js.executeScript("window.scrollBy(0, 1000);");
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    public List<String> scrapeJobLinksFromListPage(String listPageUrl) {
        WebDriver driver = null;
        List<String> jobLinks = new ArrayList<>();
        try {
            driver = createChromeDriver();
            driver.get(listPageUrl);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("div.job-item-search-result.bg-highlight.job-ta div.body div.body-box div.body-content div.title-block div h3.title a")
            ));
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
    private static final int DESCRIPTION_MAX_LENGTH = 7000;
    // Java
    @Transactional
    public boolean scrapeAndSaveJob(String jobDetailUrl) {
//        if (specializationRepository.existsSpecializationByUrl(jobDetailUrl)) {
//            System.out.println(">>> CÔNG VIỆC ĐÃ TỒN TẠI, BỎ QUA: " + jobDetailUrl);
//            return false;
//        }
        WebDriver driver = null;
        boolean saved = false;
        try {
            driver = createChromeDriver();
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_SECONDS));
            loadPageAndWait(driver, wait, jobDetailUrl);

            String pageSource = driver.getPageSource();
            Document doc = Jsoup.parse(pageSource);

            String title = "";
            String company = "";
            String categoryName = "Khác";
            String fullDescription = "";

            try {
                Element titleElement = doc.selectFirst("h1.job-detail__info--title");
                title = titleElement != null ? titleElement.text().trim() : "";
            } catch (Exception e) {
                System.out.println("❌ ERROR: Lỗi khi lấy title: " + e.getMessage());
            }

            try {
                Element companyElement = doc.selectFirst("a.name");
                company = companyElement != null ? companyElement.text().trim() : "";
            } catch (Exception e) {
                System.out.println("❌ ERROR: Lỗi khi lấy company: " + e.getMessage());
            }

            try {
                Element categoryElement = doc.selectFirst("div.job-detail__company--information-item.company-field div.company-value");
                categoryName = categoryElement != null ? categoryElement.text().trim() : "Khác";
                if (categoryName.isEmpty()) categoryName = "Khác";
            } catch (Exception e) {
                categoryName = "Khác";
                System.out.println("❌ ERROR: Lỗi khi lấy category: " + e.getMessage());
            }

            try {
                StringBuilder descriptionBuilder = new StringBuilder();
                Elements descriptionItems = doc.select("div.job-description__item--content p, div.job-description__item--content div, div.job-description__item--content li, div.job-description__item--content span");
                if (descriptionItems.isEmpty()) {
                    descriptionItems = doc.select("div.job-description p, div.job-description div, div.job-description li");
                }
                if (descriptionItems.isEmpty()) {
                    descriptionItems = doc.select("[class*=job-description] p, [class*=job-description] div, [class*=job-description] li");
                }
                if (descriptionItems.isEmpty()) {
                    Element descElement = doc.selectFirst("div[class*=job-description]");
                    if (descElement != null) {
                        fullDescription = descElement.html().trim();
                    }
                    if (fullDescription.length() > DESCRIPTION_MAX_LENGTH) {
                        System.out.println("❌ BỎ QUA: Description quá dài (" + fullDescription.length() + " ký tự) cho " + jobDetailUrl);
                        return false;
                    }
                } else {
                    for (Element item : descriptionItems) {
                        String itemHtml = item.html().trim();
                        if (!itemHtml.isEmpty() && itemHtml.length() > 10 && !itemHtml.matches("\\s*")) {
                            descriptionBuilder.append(itemHtml).append("\n");
                        }
                    }
                    fullDescription = descriptionBuilder.toString().trim();
                }
            } catch (Exception e) {
                System.out.println("❌ ERROR: Lỗi khi lấy description cho job '" + title + "': " + e.getMessage());
                fullDescription = "";
            }

            Integer cvId = null;
            try {
                String email = SecurityContextHolder.getContext().getAuthentication().getName();
                Integer userId = userRepository.findByEmail(email)
                        .map(User::getUserId)
                        .orElse(null);
                Cv cv = userId != null ? cvRepository.findByUserId(userId) : null;
                if (cv != null) {
                    cvId = cv.getId();
                }
            } catch (Exception e) {
                System.out.println("❌ ERROR: Lỗi khi lấy user/cv: " + e.getMessage());
            }


            if (!title.isEmpty() && !company.isEmpty() && cvId != null) {
                try {
                    Job job = new Job();
                    job.setTitle(title);
                    job.setCvId(cvId);
                    job.setCompany(company);
                    job.setDescription(fullDescription);
                    job.setStatus("ACTIVE");
                    job.setSourceUrl(jobDetailUrl);
                    jobRepository.save(job);
                    saved = true;
                } catch (Exception e) {
                    System.err.println("❌ Lỗi khi lưu job vào database: " + jobDetailUrl + " - " + e.getMessage());
                }
            } else {
                System.out.println("❌ BỎ QUA: Thiếu thông tin cơ bản cho " + jobDetailUrl);
            }
        } catch (Exception e) {
            System.err.println("❌ LỖI NGHIÊM TRỌNG khi cào job: " + jobDetailUrl + " - " + e.getMessage());
        } finally {
            if (driver != null) driver.quit();
        }
        return saved;
    }

    // Refactored: sleep only between jobs, handle errors per job, keep selectors
    @Transactional
    public void scrapeJobsWithCvCleanup(String type, Object param) {
        Integer cvId = getCurrentCvId();
        if (cvId != null && jobRepository.existsByCvId(cvId)) {
            jobDeleteService.deleteJobsByCvId(cvId);
        }
        switch (type) {
            case "category":
                scrapeAndSaveTop10JobsByCategory((String) param);
                break;
            case "specialization":
                scrapeAndSaveTop10JobsBySpecialization((String) param);
                break;
            case "multiple":
                scrapeAndSaveTop4JobsFromMultipleCategories((List<String>) param);
                break;
            default:
                throw new IllegalArgumentException("Unknown scrape type: " + type);
        }
    }

    // Helper to get current user's cvId
    public Integer getCurrentCvId() {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            Integer userId = userRepository.findByEmail(email)
                    .map(User::getUserId)
                    .orElse(null);
            Cv cv = userId != null ? cvRepository.findByUserId(userId) : null;
            return cv != null ? cv.getId() : null;
        } catch (Exception e) {
            System.out.println("❌ ERROR: Lỗi khi lấy user/cv: " + e.getMessage());
            return null;
        }
    }

    // Remove deletion logic from these methods
    @Transactional
    public void scrapeAndSaveTop10JobsByCategory(String categoryListUrl) {
        int n = 4;
        List<String> jobLinks = scrapeJobLinksFromListPage(categoryListUrl);
        if (jobLinks == null || jobLinks.isEmpty()) {
            System.out.println("Không tìm thấy job nào ở URL: " + categoryListUrl);
            return;
        }
        final List<String> topNJobLinks = jobLinks.stream().limit(n).collect(Collectors.toList());
        int count = 0;
        for (int i = 0; i < topNJobLinks.size(); i++) {
            String jobUrl = topNJobLinks.get(i);
            try {
                boolean saved = scrapeAndSaveJob(jobUrl);
                if (saved) {
                    count++;
                    System.out.println("✅ Đã lưu thành công: " + jobUrl);
                } else {
                    System.out.println("❌ Không lưu được job: " + jobUrl);
                }
            } catch (Exception e) {
                System.err.println("❌ Lỗi khi crawl job: " + jobUrl + " - " + e.getMessage());
            }
            if (i < topNJobLinks.size() - 1) {
                int randomDelay = 3000 + (int) (Math.random() * 2000);
                try { Thread.sleep(randomDelay); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
            }
        }
        System.out.println("\n🎉 HOÀN THÀNH: Đã crawl " + count + "/" + topNJobLinks.size() + " jobs từ: " + categoryListUrl);
    }

    @Transactional
    public void scrapeAndSaveTop4JobsFromMultipleCategories(List<String> categoryUrls) {
        int maxLinks = 5;
        int totalJobs = 0;
        List<String> urlsToProcess = categoryUrls.stream().limit(maxLinks).collect(Collectors.toList());
        for (String url : urlsToProcess) {
            if (url == null || url.isEmpty()) {
                System.out.println("URL bị thiếu hoặc trống, bỏ qua.");
                continue;
            }
            try {
                System.out.println("Bắt đầu cào jobs từ: " + url);
                int before = (int) jobRepository.count();
                scrapeAndSaveTop10JobsByCategory(url);
                int after = (int) jobRepository.count();
                int jobsAdded = after - before;
                totalJobs += jobsAdded;
                System.out.println("Đã cào " + jobsAdded + " jobs từ: " + url);
            } catch (Exception e) {
                System.err.println("Lỗi khi cào jobs từ URL: " + url + " - " + e.getMessage());
            }
        }
        System.out.println("Tổng số jobs đã cào thành công: " + totalJobs);
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