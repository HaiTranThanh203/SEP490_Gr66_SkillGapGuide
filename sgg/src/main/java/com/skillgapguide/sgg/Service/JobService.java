package com.skillgapguide.sgg.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.skillgapguide.sgg.Dto.ExtractJDSkillDTO;
import com.skillgapguide.sgg.Entity.*;
import com.skillgapguide.sgg.Repository.*;
import com.skillgapguide.sgg.Response.EHttpStatus;
import com.skillgapguide.sgg.Response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class JobService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmbedService embedService;
    @Autowired
    private JobDesFileRepository jobDesFileRepository;
    @Autowired
    private JobDeleteService jobDeleteService;
    @Autowired
    private JobDesSkillsRepository jobDesSkillsRepository;
    @Autowired
    private CVRepository cvRepository;
    @Autowired
    private JobRepository jobRepository;
    private final String UPLOAD_DIR = "D:/JdData/";
    public List<String> loadMultiFile(MultipartFile[] files){
        jobDeleteService.deleteJob();
        List<String> uploadedIds = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new IllegalStateException("Có file trống, vui lòng kiểm tra lại");
            }
            String fileName = file.getOriginalFilename();
            String fileExtension = (fileName != null && fileName.contains("."))
                    ? fileName.substring(fileName.lastIndexOf('.') + 1)
                    : "";
            if (!fileExtension.equalsIgnoreCase("pdf") && !fileExtension.equalsIgnoreCase("docx")) {
                throw new IllegalStateException("Chỉ chấp nhận file PDF");
            }
            // 3. Gọi service để lưu từng tệp và lưu lại ID/trạng thái
            String id = uploadJd(fileName, fileExtension, file);
            uploadedIds.add(id);
        }
        return uploadedIds;
    }
    public String uploadJd(String fileName, String fileExtension, MultipartFile file) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName(); // lấy từ JWT
            Integer userId = userRepository.findByEmail(email).map(User::getUserId).orElseThrow(() -> new RuntimeException("User not found"));
            Cv cv = cvRepository.findByUserId(userId);
            if (cv == null) {
                throw new IllegalStateException("Chưa upload cv");
            }
            Files.createDirectories(Paths.get(UPLOAD_DIR));
            Path path = Paths.get(UPLOAD_DIR + fileName);
            Files.write(path, file.getBytes());

            extractJd(path.toAbsolutePath().toString(), userId, fileName, fileExtension);

            return "File JD đã được upload thành công: " + fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return "Upload file JD thất bại";
        }
    }

    public static String extractTextFromPdf(String filePath) throws IOException {
        try {
            StringBuilder text = new StringBuilder();
            PdfDocument pdfDoc = new PdfDocument(new PdfReader(filePath));
            int pages = pdfDoc.getNumberOfPages();
            for (int i = 1; i <= pages; i++) {
                text.append(PdfTextExtractor.getTextFromPage(pdfDoc.getPage(i)));
            }
            pdfDoc.close();
            return text.toString();
        } catch (IOException ioException) {
            throw new IOException(ioException.getMessage());
        }
    }

    public void extractJd(String filePath, int userId, String fileName, String fileExtension) throws IOException {
        String text = extractTextFromPdf(filePath);
            try {
                String prompt = "Hãy phân tích job description dưới đây và trích xuất tất cả các yêu cầu kỹ năng của ứng viên, title, description, company. Chỉ trả về kết quả dưới dạng JSON theo mẫu sau, không thêm bất kỳ nội dung nào khác:\n" +
                        "{\n" +
                        "\"skills\": [\n" +
                        "],\n" +
                        "\"title\": \"\",\n" +
                        "\"description\": \"\",\n" +
                        "\"company\": \"\"" +
                        "}\n" +
                        "JD:\n" + text;

                LMStudioService service = new LMStudioService(WebClient.builder());
                String content = service.callLMApi(prompt).block();
                try {
                    saveJobSkillsToDb(content, userId, fileName, fileExtension, filePath);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
    public void saveJobSkillsToDb(String aiResponseJson, int userId, String fileName, String fileExtension, String path) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ExtractJDSkillDTO response = mapper.readValue(aiResponseJson, ExtractJDSkillDTO.class);
        Cv cv = cvRepository.findByUserId(userId);
        Job job = new Job();
        job.setTitle(response.getTitle());
        job.setCompany(response.getCompany());
        job.setDescription(response.getDescription());
        job.setStatus("ACTIVE");
        job.setCvId(cv.getId());
        jobRepository.save(job);
        List<String> skills = response.getSkills();
        for (String skill : skills) {
            JobDesSkills jobDesSkills = new JobDesSkills();
            jobDesSkills.setSkill(skill);
            jobDesSkills.setJobId(job.getJobId());
            jobDesSkillsRepository.save(jobDesSkills);
            embedService.getJobDesSkillEmbedding(skill);
        }
        JobDesFile jobMetadata = new JobDesFile();
        jobMetadata.setUserId(userId);
        jobMetadata.setJobId(job.getJobId());
        jobMetadata.setFileName(fileName);
        jobMetadata.setFilePath(path);
        jobMetadata.setFileType(fileExtension);
        jobMetadata.setUploadDate(LocalDateTime.now());
        jobDesFileRepository.save(jobMetadata);
    }
    public List<JobDesSkills> getJobSkill(int jobId) {
        return jobDesSkillsRepository.findByJobId(jobId);
    }

    public List<Job> getJobList() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName(); // lấy từ JWT
        Integer userId = userRepository.findByEmail(email).map(User::getUserId).orElseThrow(() -> new RuntimeException("User not found"));
        Cv cv = cvRepository.findByUserId(userId);
        if (cv == null) {
            throw new IllegalStateException("Chưa upload cv");
        }
        return jobRepository.getJobsByCvId(cv.getId());
    }
}
